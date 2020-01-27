package com.softsol.masker.constants;

public class Constants {
	public static final String REGEX_XML_ENTITY_FORMAT = "&(\\w+;)|&#(\\d+;)";
	public static final String REGEX_WORD_FOLLOW_DOT = "\\w+\\.";
	public static final String REGEX_WORD_FOLLOW_COMMA = "\\w+\\,";
	public static final String REGEX_WORD_FOLLOW_QUESTION = "\\w+\\?";
	public static final String REGEX_WORD_FOLLOW_EXCLAMATION = "\\w+\\!";
	public static final String REGEX_ONLY_WORDS = "[a-zA-Z]+";
	public static final String REGEX_ONLY_DIGITS = "[0-9]+";
	public static final String REGEX_WHITESPACE = "\\s";
	// Especially for JAVA8
	public static final String REGEX_NEWLINE = "\\R";

	private Constants() {
		
	}
	
}
