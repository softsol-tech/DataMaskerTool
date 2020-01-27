package com.softsol.masker.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.softsol.masker.MaskConfigProperties;
import com.softsol.masker.exception.MaskerFileNotFoundException;
import com.softsol.masker.exception.MaskerFileStorageException;

@Service
public class FileStorageService {
	
	private final Path fileStorageLocation;
	
	@Autowired
    public FileStorageService(MaskConfigProperties maskConfigProperties) {
        this.fileStorageLocation = Paths.get(maskConfigProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new MaskerFileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
	
	public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new MaskerFileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            // Copy file to the target location (Replacing existing file with the same name))
            Path targetLocation = this.fileStorageLocation.resolve(System.currentTimeMillis()+"-"+Math.abs(System.nanoTime())+fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new MaskerFileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }
	
	public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new MaskerFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MaskerFileNotFoundException("File not found " + fileName, ex);
        }
    }

}
