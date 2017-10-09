package com.kfplc.converter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/xml", produces="application/json")
public class XmlController {
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> getXml() {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/xml")
	public ResponseEntity<String> addXml() {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteXml() {
		return null;
	}
}
