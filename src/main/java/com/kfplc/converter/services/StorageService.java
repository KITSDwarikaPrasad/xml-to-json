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
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

	//	@Resource
	//    private Environment environment;

	@Autowired
	RestClient client;

	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;

	public StorageService() {
		System.out.println("----------------------------> StorageService------");
	}

	//public final Path UPLOADED_DIR = Paths.get(environment.getProperty("upload-dir"));
	public final Path UPLOADED_DIR = Paths.get("upload-dir");
	
	public void holdUploadedFile(MultipartFile uploadedFile) throws IOException {
		//		byte[] fileBytes = uploadedFile.getBytes();
		//		Path path = Paths.get(UPLOADED_DIR + uploadedFile.getOriginalFilename());
		//		Files.write(path, fileBytes);
		System.out.println("----------------StorageService.saveUploadedFile()----------------");
		Files.copy(uploadedFile.getInputStream(), this.UPLOADED_DIR.resolve(uploadedFile.getOriginalFilename()),StandardCopyOption.REPLACE_EXISTING);
		System.out.println("--------Copy is done........--------------");
	}
	public void addDataToESinTemplates(MultipartFile uploadedFile) {
		try {
			System.out.println("-----------StorageService.addDataToES-------------------");
			Stack<String> stack = new Stack<String>();
			StringBuilder xmlStringBuilder = new StringBuilder();
			StringBuilder bulkRequestBodyBuilder = new StringBuilder();

			int id = 1;
			String index = "datafeed";
			String rootElement;

			Path uploadedFilePath = this.UPLOADED_DIR.resolve(uploadedFile.getOriginalFilename());
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
					// Convert xmlObject to JSONString
					String jsonString = getJsonFromXml(xmlStringBuilder.toString());

					bulkRequestBodyBuilder.append(actionMetaData);
					bulkRequestBodyBuilder.append(new JSONObject(jsonString).toString(0)).append("\n");
					//TO-DO    Send to Elastic Search 
					xmlStringBuilder.delete(0, xmlStringBuilder.length());
					stack.pop();
				}
			});
			//System.out.println("----bulkRequestBodyBuilder : "+bulkRequestBodyBuilder+"----");

			HttpEntity entity = new NStringEntity(bulkRequestBodyBuilder.toString(), ContentType.APPLICATION_JSON);
			Response response = null;
			try {
				response = client.performRequest("POST", "/"+ index +"/"+ rootElement +"/_bulk", Collections.emptyMap(), entity);
				System.out.println("added to ES"+ entity);
			} catch (IOException e) {
				e.printStackTrace();
			}
			boolean status =  response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

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

	private String getJsonFromXml(String xmlObjectString) {

		JSONObject xmlJSONObj = XML.toJSONObject(xmlObjectString);
		//String jsonString = xmlJSONObj.toString(Integer.parseInt(environment.getProperty("json.pretty_print_indent_factor")));
		String jsonString = xmlJSONObj.toString(Integer.parseInt("4"));
		return jsonString;
	}
	public void emptyUploadDir() {
		try {
			Files.walk(UPLOADED_DIR)
			.map(Path::toFile)
			.peek(System.out::println)
			.forEach(File::delete);
//			System.out.println("All files deleted from UPLOADED_DIR");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
