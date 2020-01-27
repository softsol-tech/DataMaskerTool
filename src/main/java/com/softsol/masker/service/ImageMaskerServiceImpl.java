package com.softsol.masker.service;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.softsol.masker.MaskConfigProperties;
import com.softsol.masker.exception.MaskerException;
import com.softsol.masker.exception.MaskerImageException;
import com.softsol.masker.util.ImageUtil;

@Service
public class ImageMaskerServiceImpl implements MaskerService {

	Logger logger = LoggerFactory.getLogger(ImageMaskerServiceImpl.class);

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	MaskConfigProperties maskConfigProperties;

	public void payloadMask(String imageFile) {

		String converterPath = maskConfigProperties.getImagemask().getConverterPath();
		Dimension dim;
		try {
			dim = ImageUtil.getImageDimension(new File(imageFile));
		} catch (IOException e) {
			throw new MaskerException("IO Issue while getting image dimensions of file : " + imageFile, e);
		}
		String dimensions = dim.getHeight() + "x" + dim.getWidth();

		ProcessBuilder processBuilder = null;
		Process process = null;
		try {
			processBuilder = new ProcessBuilder(
					resourceLoader.getResource("classpath:maskImage.bat").getFile().getAbsolutePath(), dimensions,
					imageFile, converterPath);

			process = processBuilder.start();
		} catch (IOException e) {
			throw new MaskerException("IO Issue while stating the imageConverter process. File: " + imageFile, e);
		}

		StringBuilder output = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line.trim() + "\n");
			}
			int exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println(output.toString());
				logger.debug("ProcessBuilder - Commands Executed :: " + output);
			} else {
				System.out.println(output.toString());
				throw new MaskerImageException("Image Masking failed for file :: " + imageFile);
			}
		} catch (IOException e) {
			throw new MaskerException("IO Issue while reading output of batch process. File : " + imageFile, e);
		} catch (InterruptedException e) {
			throw new MaskerException("Interrupted Exception while waiting for the batch process. File : " + imageFile,
					e);
		}

	}

}
