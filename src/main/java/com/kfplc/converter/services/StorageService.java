package com.kfplc.converter.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {
	
	public final Path UPLOADED_DIR = Paths.get("upload-dir");
	public void saveUploadedFile(MultipartFile uploadedFile) throws IOException {
//		byte[] fileBytes = uploadedFile.getBytes();
//		Path path = Paths.get(UPLOADED_DIR + uploadedFile.getOriginalFilename());
//		Files.write(path, fileBytes);
		
		Files.copy(uploadedFile.getInputStream(), this.UPLOADED_DIR.resolve(uploadedFile.getOriginalFilename()));
		
	}

}
