package com.softsol.masker.util;

public class RegExMatch {
	private String value;
	private int startIndex;
	private int endIndex;

	
	public RegExMatch(String value, int startIndex, int endIndex) {
		super();
		this.value = value;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public int getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public int getEndIndex() {
		return endIndex;
	}

	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}

	@Override
	public String toString() {
		return "RegExMatch [value=" + value + ", startIndex=" + startIndex + ", endIndex=" + endIndex + "]";
	}
}
