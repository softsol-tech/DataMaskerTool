package com.softsol.masker.util;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {
	static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	private ImageUtil() {
		
	}
	/**
	 * Gets image dimensions for given file
	 * 
	 * @param imgFile image file
	 * @return dimensions of image
	 * @throws IOException if the file is not a known image
	 */
	public static Dimension getImageDimension(File imgFile) throws IOException {
		int pos = imgFile.getName().lastIndexOf('.');
		if (pos == -1)
			throw new IOException("No extension for file: " + imgFile.getAbsolutePath());
		String suffix = imgFile.getName().substring(pos + 1);
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		while (iter.hasNext()) {
			ImageReader reader = iter.next();
			try (ImageInputStream stream = new FileImageInputStream(imgFile)) {
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} catch (IOException e) {
				logger.error("Error reading: " + imgFile.getAbsolutePath());
			} finally {
				reader.dispose();
			}
		}

		throw new IOException("Not a known image file: " + imgFile.getAbsolutePath());
	}
}
