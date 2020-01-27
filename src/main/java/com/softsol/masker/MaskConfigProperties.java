package com.softsol.masker;

import javax.validation.constraints.NotNull;

import org.jboss.logging.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "masker")
public class MaskConfigProperties {
	Logger logger = Logger.getLogger(MaskConfigProperties.class);

	@NotNull
	private String uploadDir;
	private String datasetFile;
	private String ignoreXmlElement;
	private String ignoreXmlAttribute;
	private boolean isEnableAttributeMask;

	private Imagemask imagemask;

	public Imagemask getImagemask() {
		return imagemask;
	}

	public void setImagemask(Imagemask imagemask) {
		this.imagemask = imagemask;
	}

	public boolean isEnableAttributeMask() {
		return isEnableAttributeMask;
	}

	public void setEnableAttributeMask(boolean isEnableAttributeMask) {
		this.isEnableAttributeMask = isEnableAttributeMask;
	}

	public String getIgnoreXmlElement() {
		return ignoreXmlElement;
	}

	public void setIgnoreXmlElement(String ignoreXmlElement) {
		this.ignoreXmlElement = ignoreXmlElement;
	}

	public String getIgnoreXmlAttribute() {
		return ignoreXmlAttribute;
	}

	public void setIgnoreXmlAttribute(String ignoreXmlAttribute) {
		this.ignoreXmlAttribute = ignoreXmlAttribute;
	}

	public String getDatasetFile() {
		return datasetFile;
	}

	public void setDatasetFile(String datasetFile) {
		this.datasetFile = datasetFile;
	}

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}

}
