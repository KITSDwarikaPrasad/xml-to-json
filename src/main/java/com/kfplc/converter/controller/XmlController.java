package com.kfplc.converter.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> getXml() {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST,  consumes="multipart/form-data")
	public ResponseEntity<String> addXml(@RequestParam(value="file", required=true) MultipartFile uploadedFile) {
		
		System.out.println("-----------XmlController.addXml()------------");
		//converter.addXmlDataToES();

		if (uploadedFile.isEmpty()) {
			return new ResponseEntity("please select a file!", HttpStatus.OK);
		}

		try {
			storageService.saveUploadedFile(uploadedFile);
			System.out.println("------------Back to XmlController.addXml()------------------");
			storageService.addDataToES(uploadedFile);
		} catch (IOException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		//return null;
		return new ResponseEntity<>( HttpStatus.CREATED);
	}
	
	

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteXml() {
		return null;
	}
	
}
