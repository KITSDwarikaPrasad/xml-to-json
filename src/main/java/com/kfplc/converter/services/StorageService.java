package com.kfplc.converter.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.Stack;
import java.util.stream.Stream;

import javax.annotation.Resource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

	@Autowired
	RestClient client;

	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	@Value("${storage.upload_dir}")
	private Path uploadDirPath;

	@Value("${elasticsearch.index}")
	private String index;

	public StorageService() {
		System.out.println("----------------------------> StorageService------");
	}

	public void holdUploadedFile(MultipartFile uploadedFile) throws IOException {
		//		byte[] fileBytes = uploadedFile.getBytes();
		//		Path path = Paths.get(UPLOADED_DIR + uploadedFile.getOriginalFilename());
		//		Files.write(path, fileBytes);
		System.out.println("----------------StorageService.saveUploadedFile()----------------");
		Files.copy(uploadedFile.getInputStream(), this.uploadDirPath.resolve(uploadedFile.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
		System.out.println("--------Copy is done........--------------");
	}

	/**
	 * Add data in XML format to Elasticsearch as JSON strings into an index = rootElement of XML 
	 * @param uploadedFile - An XML file from which data will be stored to Elasticsearch
	 */
	public Response addDataToESinTemplates(MultipartFile uploadedFile) {
		System.out.println("-----------StorageService.addDataToES-------------------");
		Response response = null;
			Stack<String> stack = new Stack<String>();
			StringBuilder xmlStringBuilder = new StringBuilder();
			StringBuilder bulkRequestBodyBuilder = new StringBuilder();
			String rootElement;

			try {
			Path uploadedFilePath = this.uploadDirPath.resolve(uploadedFile.getOriginalFilename());
			rootElement = getrootElementFronXML(uploadedFilePath);
			System.out.println("---------indexType:" + rootElement);
			String actionMetaData = String.format("{ \"index\" : { \"_index\" : \"%s\", \"_type\" : \"%s\" } }%n", index, rootElement);
			Stream<String> stream = Files.lines(uploadedFilePath);

			stream
			//.limit(130)
			.filter( line -> !( line.trim().isEmpty() || line.contains(rootElement) || line.contains("xml version")))
			.sequential()
			.forEach(content -> {
				xmlStringBuilder.append(content.trim());
				if(stack.isEmpty()) {
					stack.push("</" + content.substring(1, content.indexOf('>') + 1));
				}
				if(stack.peek().equals(content.trim())) { // Reached to the end  </Bay>
					String jsonString = getJsonFromXml(xmlStringBuilder.toString());

					bulkRequestBodyBuilder.append(actionMetaData);
					bulkRequestBodyBuilder.append(new JSONObject(jsonString).toString(0)).append("\n");
					xmlStringBuilder.delete(0, xmlStringBuilder.length());
					stack.pop();
				}
			});
			//System.out.println("----bulkRequestBodyBuilder : "+bulkRequestBodyBuilder+"----");

			HttpEntity entity = new NStringEntity(bulkRequestBodyBuilder.toString(), ContentType.APPLICATION_JSON);
			response = client.performRequest("POST", "/"+ index +"/"+ rootElement +"/_bulk", Collections.emptyMap(), entity);
			System.out.println("added to ES"+ entity);
			boolean status =  response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * To get rootElement from XML file, to create index in elasticsearch with the same name
	 * @param uploadedFilePath
	 * @return String RootElement
	 * @throws IOException
	 */
	private String getrootElementFronXML(Path uploadedFilePath) throws IOException {
		String rootElement = "";
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			XMLStreamReader reader = inputFactory.createXMLStreamReader(Files.newInputStream(uploadedFilePath));
			while (reader.hasNext()) {
				int eventType = reader.next();
				if( eventType == XMLStreamReader.START_ELEMENT) {
					rootElement = reader.getLocalName();
					break;
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return rootElement;
	}

	/**
	 * Converts XMLObject to JSON
	 * @param xmlObjectString
	 * @return JSON string for the XMLObject
	 */
	private String getJsonFromXml(String xmlObjectString) {

		JSONObject xmlJSONObj = XML.toJSONObject(xmlObjectString);
		//String jsonString = xmlJSONObj.toString(Integer.parseInt(environment.getProperty("json.pretty_print_indent_factor")));
		String jsonString = xmlJSONObj.toString(Integer.parseInt("4"));
		return jsonString;
	}

	/**
	 * Delete all files in the Upload directory
	 * return nothing
	 */
	public void emptyUploadDir() {
		try {
			Files.walk(uploadDirPath)
			.map(Path::toFile)
			.peek(System.out::println)
			.forEach(File::delete);
			//			System.out.println("All files deleted from UPLOADED_DIR");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
