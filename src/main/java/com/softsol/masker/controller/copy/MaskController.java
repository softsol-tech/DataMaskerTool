package com.softsol.masker.controller.copy;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.softsol.masker.MaskConfigProperties;
import com.softsol.masker.exception.MaskerThrowingConsumer;
import com.softsol.masker.exception.UploadException;
import com.softsol.masker.pojo.UploadFileResponse;
import com.softsol.masker.service.FileStorageService;
import com.softsol.masker.service.MaskerService;
import com.softsol.masker.util.ZipUtil;

@RestController
@RequestMapping("mask/xml")
public class MaskController {

	static final Logger logger = LoggerFactory.getLogger(MaskController.class);

	@Autowired
	@Qualifier("XMLMaskerServiceImpl")
	private MaskerService xMLMaskerServiceImpl;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private MaskConfigProperties maskConfigProperties;

	@Autowired
	@Qualifier("imageMaskerServiceImpl")
	private MaskerService imageMaskerServiceImpl;

	@PostMapping("/upload")
	public UploadFileResponse maskXML(@RequestParam("file") MultipartFile uploadFile) {

		// Validation
		String filename = StringUtils.cleanPath(uploadFile.getOriginalFilename());
		if (uploadFile.isEmpty()) {
			throw new UploadException("Failed to store empty file " + filename);
		}
		if (filename.contains("..")) {
			// This is a security check
			throw new UploadException("Cannot store file with relative path outside current directory " + filename);
		}

		int pos = uploadFile.getOriginalFilename().lastIndexOf('.');
		if (pos == -1)
			throw new UploadException("Expecting a file extension for the file : " + uploadFile.getOriginalFilename());

		String uploadedFileLocation = fileStorageService.storeFile(uploadFile);

		logger.debug(uploadFile.getOriginalFilename() + " file uploaded to " + uploadedFileLocation + "successfully.");

		String actualDir = uploadedFileLocation.substring(0, uploadedFileLocation.lastIndexOf('.')) + File.separator
				+ uploadFile.getOriginalFilename().substring(0, uploadFile.getOriginalFilename().lastIndexOf('.'));

		logger.debug("Extract Directory : " + actualDir);

		ZipUtil.deCompress(uploadedFileLocation, actualDir);

		logger.debug(uploadedFileLocation + " file extracted successfully.");

		File directory = new File(actualDir);
		Collection<File> listOfXMLFiles = FileUtils.listFiles(directory, new String[] { "xml" }, true);

		if (CollectionUtils.isEmpty(listOfXMLFiles)) {

			logger.error("No XML files to Mask.");
		}

		// Masking all xml files one by one
		listOfXMLFiles.forEach(item -> xMLMaskerServiceImpl.payloadMask(item.getAbsolutePath()));

		if (maskConfigProperties.getImagemask().isEnabled()) {
			Collection<File> listOfImages = FileUtils.listFiles(directory,
					maskConfigProperties.getImagemask().getSupportedFormats().stream().toArray(String[]::new), true);
			if (CollectionUtils.isEmpty(listOfImages)) {
				logger.error("No Image files to Mask.");
			}

			listOfImages.forEach(item -> imageMaskerServiceImpl.payloadMask(item.getAbsolutePath()));

		}

		// Create a ZIP file from the output directory
		File outputZip = new File(directory.getAbsolutePath() + ".zip");

		ZipUtil.compress(directory, outputZip);

		logger.debug("Zip Compression Completed Successfully, File Path: " + outputZip.getAbsolutePath());

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/mask/xml/download/")
				.path(outputZip.getParentFile().getName() + "/" + outputZip.getName()).toUriString();

		logger.info("URI for download : " + fileDownloadUri);

		return new UploadFileResponse(outputZip.getName(), fileDownloadUri, uploadFile.getContentType(),
				uploadFile.getSize());
	}

	@GetMapping("/download/{location}/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String location, @PathVariable String fileName,
			HttpServletRequest request) {
		// Load file as Resource
		Resource resource = fileStorageService.loadFileAsResource(location + File.separator + fileName);

		// Try to determine file's content type
		String contentType = null;
		try {
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException ex) {
			logger.info("Could not determine file type.");
		}

		// Fallback to the default content type if type could not be determined
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.header(HttpHeaders.COOKIE, "fileDownload=true", "path=/").body(resource);
	}

}
