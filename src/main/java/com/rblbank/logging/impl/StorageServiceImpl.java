package com.rblbank.logging.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.rblbank.logging.Exception.FileNotFoundException;
import com.rblbank.logging.Exception.StorageException;
import com.rblbank.logging.service.StorageService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    
    @Autowired
    private Environment enviornment;
    
    @Override
    public String storeContactability(MultipartFile file) {
    	String contactUpload=enviornment.getProperty("contact.root.path");
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
                
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, Paths.get(URI.create(contactUpload)).resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
           
        }

        return "success";
    }
    
    @Override
    public Stream<Path> loadAllContactSuccess() {
        try {
        	
        	String contactSuccessPath=enviornment.getProperty("contact.storage.success");
        	
        
        		return Files.walk(Paths.get(URI.create(contactSuccessPath)), 1)
                        .filter(path -> !path.equals(Paths.get(URI.create(contactSuccessPath))))
                        .map(Paths.get(URI.create(contactSuccessPath))::relativize);
        	
            
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }
    
    @Override
    public Stream<Path> loadAllLeadSuccess() {
        try {
        	
        	String contactSuccessPath=enviornment.getProperty("lead.storage.success");
        	
        	
        		return Files.walk(Paths.get(URI.create(contactSuccessPath)), 1)
                        .filter(path -> !path.equals(Paths.get(URI.create(contactSuccessPath))))
                        .map(Paths.get(URI.create(contactSuccessPath))::relativize);
        	
            
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }
    
    @Override
    public Path contactload(String filename) {
    	
    	Path contactroot=Paths.get(URI.create(enviornment.getProperty("contact.root.path")));
        return contactroot.resolve(filename);
    }
    
    @Override
    public Path leadLoadSuccess(String filename) {
    	
    	Path leadLoadSuccess=Paths.get(URI.create(enviornment.getProperty("lead.storage.success")));
    	
        return leadLoadSuccess.resolve(filename);
    }
    
    @Override
    public Path leadLoadFailure(String filename) {
    	
    	Path leadLoadFailure=Paths.get(URI.create(enviornment.getProperty("lead.storage.failure")));
        return leadLoadFailure.resolve(filename);
    }
    
    @Override
    public Path contactloadSuccess(String filename) {
    	
    	Path contactSuccess=Paths.get(URI.create(enviornment.getProperty("contact.storage.success")));
    	
        return contactSuccess.resolve(filename);
    }
    
    @Override
    public Path contactloadFailure(String filename) {
    	
    	Path contactFailure=Paths.get(URI.create(enviornment.getProperty("contact.storage.failure")));
        return contactFailure.resolve(filename);
    }
    
    @Override
    public Resource loadAsContactResource(String filename) {
        try {
        	
        	 Path file=null;
        	
        	if(filename.contains("Success")) {
        		  file = contactloadSuccess(filename);
        	}
        	else if(filename.contains("Failure")) {
        		  file = contactloadFailure(filename);
        	}
        	else {
        		  file = contactload(filename);
        	}
           
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

   
    @Override
    public Resource loadAsLeadResource(String filename) {
        try {
        	
        	 Path file=null;
        	
        	if(filename.contains("Success")) {
        		  file = leadLoadSuccess(filename);
        	}
        	else if(filename.contains("Failure")) {
        		  file = leadLoadFailure(filename);
        	}
        	else {
        		  file = contactload(filename);
        	}
           
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new FileNotFoundException("Could not read file: " + filename, e);
        }
    }

    
    @Override
    public List<File> getALLFailureContactFiles() {
    		
    	String failureLoc=enviornment.getProperty("contact.storage.failure");
         
    	List<File> files=new ArrayList<File>();
 		try {
 			files = Files.list(Paths.get(URI.create(failureLoc)))
 			        .map(Path::toFile)
 			       .sorted(Comparator.comparingLong(File::lastModified).reversed())
 			        .collect(Collectors.toList());
 			 
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
  
 		return files;
        
    }
    
    @Override
    public List<File> getALLFailureLeadFiles() {
    		
    	String failureLoc=enviornment.getProperty("lead.storage.failure");
         
    	List<File> files=new ArrayList<File>();
 		try {
 			files = Files.list(Paths.get(URI.create(failureLoc)))
 			        .map(Path::toFile)
 			       .sorted(Comparator.comparingLong(File::lastModified).reversed())
 			        .collect(Collectors.toList());
 			 
 		} catch (IOException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
  
 		return files;
        
    }
    
    @Override
    public List<File> getALLSucessContactFiles() {
    	
    	String successLoc=enviornment.getProperty("contact.storage.success");
        
    	List<File> files=new ArrayList<File>();
		try {
			files = Files.list(Paths.get(URI.create(successLoc)))
			        .map(Path::toFile)
			        .sorted(Comparator.comparingLong(File::lastModified).reversed())
			        .collect(Collectors.toList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
       return files;
        
    }
  
    @Override
	public List<File> getALLSucessLeadFiles() {
    	
    	String successLoc=enviornment.getProperty("lead.storage.success");
        
    	List<File> files=new ArrayList<File>();
		try {
			files = Files.list(Paths.get(URI.create(successLoc)))
			        .map(Path::toFile)
			        .sorted(Comparator.comparingLong(File::lastModified).reversed())
			        .collect(Collectors.toList());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
       return files;
        
    }
    
   
}
