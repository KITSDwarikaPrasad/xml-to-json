package com.kfplc.converter.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.elasticsearch.client.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kfplc.converter.services.StorageService;


//import com.kfplc.converter.services.ConverterServices;

@RestController
@RequestMapping(value="/xml", produces="application/json")
public class XmlController {

	//	@Autowired
	//	ConverterServices converter;

	@Autowired
	StorageService storageService;

	public XmlController() {
		System.out.println("----------------------------> XmlController------");
	}

	public final Path UPLOADED_DIR = Paths.get("upload-dir");

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> getXml() {
		return null;
	}

	@RequestMapping(method = RequestMethod.POST,  consumes="multipart/form-data")
	public ResponseEntity<String> addXml(@RequestParam(value="file", required=true) MultipartFile uploadedFile) {

		System.out.println("-----------XmlController.addXml()------------");
		Response response = null;
		
		if (uploadedFile.isEmpty()) {
			return new ResponseEntity("please select a file!", HttpStatus.OK);
		}

		try {
			storageService.holdUploadedFile(uploadedFile);
			System.out.println("------------Back to XmlController.addXml()------------------");
			//			storageService.addDataToES(uploadedFile);
			response = storageService.addDataToESinTemplates(uploadedFile);
			storageService.emptyUploadDir();
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		if(response == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		int httpStatusCode = response.getStatusLine().getStatusCode();
		return new ResponseEntity<>( HttpStatus.valueOf(httpStatusCode));
	}



	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteXml() {
		return null;
	}

}
