package com.shopme.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.repository.cdi.CdiRepositoryBean;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {

	//for file logging and should be used in the production mode
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
	
	public static void saveFile(String uploadDir, String fileName,
			MultipartFile multipartFile) throws IOException {
		
		//file upload path 
		Path uploadPath = Paths.get(uploadDir);
		
		//create directory if the upload path doesn't exit
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		
		try (InputStream inputStream = multipartFile.getInputStream()) {
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException ex) {
			throw new IOException("Could not save file: " + fileName, ex);
		}
	}
	
	//method to clean image directory before uploading photos
	public static void cleanDir(String dir) {
		Path dirPath = Paths.get(dir);
		
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					}catch(IOException ex) {
						//print the error message in the log file
						
						LOGGER.error("Could not delete file: " + file);
					//	System.out.println("Could not delete file: " + file);
					}
				}
			});
		}catch(IOException ex) {
			//print the error message in the log file
			
			LOGGER.error("Could not list directory: " + dirPath);
		//	System.out.println("Could not list directory: " + dirPath);
		}
	}

	//removes category image directory
	public static void removeDir(String dir) {
		cleanDir(dir);//delete all files in the directory(folder)
		//Path dirPath = Paths.get(dir);
		try {
			Files.delete(Paths.get(dir)); //deletes directory itself (folder)
			
		}catch(IOException e) {
			LOGGER.error("Could not remove directory: " + dir);
		}
	}
	
}
