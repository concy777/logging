package com.rblbank.logging.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.rblbank.logging.service.StorageService;

@Controller
public class UploadController {
	
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

   @Autowired 
   StorageService storageService;
    
        
    @GetMapping("/api/lead/logs")
    public String loadAllFilesNew(Model model) {

    	model.addAttribute("failureList", storageService.getALLFailureLeadFiles());
    	model.addAttribute("successList", storageService.getALLSucessLeadFiles());
        
        return "upload";
    }
    
    
    @GetMapping("/api/contact/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> contactDownloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadAsContactResource(filename);
        
     

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping("/api/lead/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> leadDownloadFile(@PathVariable String filename) {

        Resource resource = storageService.loadAsLeadResource(filename);
               return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    
    @PostMapping("/api/contact/upload")
    public ResponseEntity<?> uploadContactibility(@RequestParam("file") MultipartFile file,Model model) {
        
        try {
        	  storageService.storeContactability(file);
          } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
          } 
          return ResponseEntity.ok("File uploaded successfully.");
        

       
    }
    
    @GetMapping("/api/contact/logs")
    public String uploadMain(Model model) {
    	model.addAttribute("failureList", storageService.getALLFailureContactFiles());
    	model.addAttribute("successList", storageService.getALLSucessContactFiles());
        return "contactUpload";

       
    }
    
    
}
