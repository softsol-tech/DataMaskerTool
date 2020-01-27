package com.softsol.masker;

import static com.softsol.masker.constants.Constants.REGEX_NEWLINE;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.softsol.masker.exception.MaskerConfigurationException;
import com.softsol.masker.util.DataCache;

@Component
public class AppStartupRunner implements ApplicationRunner {

	Logger logger = LoggerFactory.getLogger(AppStartupRunner.class);

	@Autowired
	MaskConfigProperties maskConfigProperties;

	@Autowired
	ResourceLoader resourceLoader;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		loadDataSet();
		prepareElementsToIgnore();
		prepareElementsToAttribute();
		imageMaskDefaultConfig();
		logger.info("Cache loaded Successfully.");
	}

	public void loadDataSet() throws IOException {

		Map<Integer, List<String>> dictionary = DataCache.dictionary;
		String dataSetFileLocation = maskConfigProperties.getDatasetFile();

		logger.info(
				StringUtils.isEmpty(dataSetFileLocation) ? "DataSet not configured externally, Loading default dataset"
						: "Externally Configured Data Set Location = " + dataSetFileLocation);

		try (InputStream inputStream = (StringUtils.isEmpty(dataSetFileLocation)
				? resourceLoader.getResource("classpath:default-dictionary.txt").getInputStream()
				: new FileInputStream(dataSetFileLocation))) {

			String dicitonaryRecords = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
			if (StringUtils.isEmpty(dicitonaryRecords)) {
				throw new MaskerConfigurationException(
						"Configured" + dataSetFileLocation + " Dictionary/DataSet File is Empty.");
			}
			String[] listOfWords = dicitonaryRecords.split(REGEX_NEWLINE);

			Arrays.stream(listOfWords).forEach(word -> {
				String trimWord = word.trim().toLowerCase();

				if (dictionary.containsKey(trimWord.length())) {
					dictionary.get(trimWord.length()).add(trimWord);
				} else {
					List<String> list = new ArrayList<>();
					list.add(trimWord);
					dictionary.put(trimWord.length(), list);
				}
			});
		}

	}

	public void prepareElementsToIgnore() {
		if (!StringUtils.isEmpty(maskConfigProperties.getIgnoreXmlElement())) {
			DataCache.elementsToIgnore
					.addAll(Arrays.asList(maskConfigProperties.getIgnoreXmlElement().trim().split("\\s*,\\s*")));
		}
		logger.info("Elements to be ignored:" + DataCache.elementsToIgnore);

	}

	public void prepareElementsToAttribute() {
		if (!StringUtils.isEmpty(maskConfigProperties.getIgnoreXmlAttribute())) {
			DataCache.attributeToIgnore
					.addAll(Arrays.asList(maskConfigProperties.getIgnoreXmlAttribute().trim().split("\\s*,\\s*")));
		}
		logger.info("Attributes to be ignored:" + DataCache.attributeToIgnore);
	}

	public void imageMaskDefaultConfig() {

		// Setting Default values for ImageMask
		if (maskConfigProperties.getImagemask() == null) {
			maskConfigProperties.setImagemask(new Imagemask());
			logger.info("ImageMasking Configuration Missing..");
		}
		if (CollectionUtils.isEmpty(maskConfigProperties.getImagemask().getSupportedFormats())) {
			maskConfigProperties.getImagemask().setSupportedFormats(Arrays.asList(new String[] { "png", "jpeg", "jpg" }));
			logger.info("Default formats " + maskConfigProperties.getImagemask().getSupportedFormats()
					+ " supported for Image Masking");
		}
		if (StringUtils.isEmpty(maskConfigProperties.getImagemask().getConverterPath())) {
			maskConfigProperties.getImagemask().setConverterPath("");
			logger.info("Image Converter Path not configured.");
		}
	}
}