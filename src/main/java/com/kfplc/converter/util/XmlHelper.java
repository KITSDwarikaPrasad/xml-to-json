package com.kfplc.converter.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Component;

@Component
public class XmlHelper {
	
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;
	public static void main(String[] args) {
		
		String fileName = "C:\\Users\\prasad01\\OneDrive - Kingfisher PLC\\work\\KAPIs\\KAPI-2817-Configuring Elastic Search\\ProductLocation\\SAPECCtoProductLocationAPI_part0_20171003120232-0000.xml";
		
		XmlHelper xmlHelper = new XmlHelper();
		xmlHelper.parseXml2(fileName);
	}

	public void parseXml2(String fileName) {
		try {
			Stack<String> stack = new Stack<String>();
			StringBuilder xmlStringBuilder = new StringBuilder();
			Stream<String> stream = Files.lines(Paths.get(fileName));
			
			stream
			.skip(1)
			.limit(1000)
			.forEach(content -> {
				
				xmlStringBuilder.append(content);
				if(stack.isEmpty()) {
					stack.push("</" + content.substring(1, content.indexOf('>') + 1));
					//System.out.println(stack);
				}
				if(stack.peek().equals(content)) {
					// Convert xmlObject to JSONString
					String jsonString = getJsonFromXml(xmlStringBuilder.toString());
					System.out.println(jsonString);
					
					//TO-DO    Send to Elastic Search 
					String response = sendJsonToElastic(jsonString);
					xmlStringBuilder.delete(0, xmlStringBuilder.length());
					stack.pop();
				}
				
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private String sendJsonToElastic(String jsonString) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getJsonFromXml(String xmlObjectString) {
		
		JSONObject xmlJSONObj = XML.toJSONObject(xmlObjectString);
		String jsonString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		return jsonString;
	}

}
