package com.softsol.masker;

import java.util.List;

import org.springframework.util.StringUtils;

public class Imagemask {
	private String converterPath;
	private boolean enabled;
	private List<String> supportedFormats;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getConverterPath() {
		return converterPath;
	}

	public void setConverterPath(String converterPath) {
		
		if(StringUtils.isEmpty(converterPath)) {
			converterPath="";
		}
		this.converterPath = converterPath;
	}

	public List<String> getSupportedFormats() {
		return supportedFormats;
	}

	public void setSupportedFormats(List<String> supportedFormats) {
	
		this.supportedFormats = supportedFormats;
	}

}
