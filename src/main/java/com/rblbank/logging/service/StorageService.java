package com.rblbank.logging.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public interface StorageService {

       
    List<File> getALLFailureContactFiles();
    
    List<File> getALLSucessContactFiles();
  
	Stream<Path> loadAllContactSuccess();

	String storeContactability(MultipartFile file);

	Path contactloadSuccess(String filename);

	Path contactloadFailure(String filename);

	Path contactload(String filename);

	Resource loadAsContactResource(String filename);

	Resource loadAsLeadResource(String filename);

	Path leadLoadSuccess(String filename);

	Path leadLoadFailure(String filename);

	Stream<Path> loadAllLeadSuccess();

	List<File> getALLSucessLeadFiles();

	List<File> getALLFailureLeadFiles();

}