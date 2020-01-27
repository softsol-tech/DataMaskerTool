package com.softsol.masker.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

public class MaskUtil {
	private static Random randomGenerator = new Random();
	private MaskUtil() {
		
	}
	/**
	 * This method return number of X based on given length
	 * 
	 * @param length
	 * @return numberOfX
	 */
	public static String getNumberOfMaskedX(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i <= length; i++) {
			builder.append("X");
		}
		return builder.toString();
	}
	/**
	 * This method returns a random alphabet
	 * 
	 * @return randomAlphabet
	 */
	public static char getRandomAlphabet() {
		
		int randomNumber;
		String phrase = "abcdefghijklmnopqrstuvwxyz";
		int length = phrase.length();
		randomNumber = randomGenerator.nextInt(length);
		return phrase.charAt(randomNumber);
	}

	/**
	 * This method returns a random number between 0 to 9
	 * 
	 * @return randomNumber
	 */
	public static char getRandomNumber() {
		int randomNumber = randomGenerator.nextInt(10);
		return String.valueOf(randomNumber).charAt(0);
	}
	
	/**
	 * This method can mask the numbers
	 * 
	 * @param textContent
	 * @return maskedNumber
	 */
	public static String maskNumbers(String textContent) {
		// This logic support for any given length of characters. More than
		// Long.MAX_VALUE = 9,223,372,036,854,775,807
		StringBuilder newMask = new StringBuilder("");

		// iterate for every characters
		for (int i = 0; i < textContent.length(); ++i) {

			newMask.append(getRandomNumber());
		}
		return newMask.toString();
	}
	

	/**
	 * Converting the characters of maskedContent to upper case based on textContent
	 * 
	 * @param textContent
	 * @param maskedContent
	 * @return
	 */
	public static String convertCase(String textContent, String maskedContent) {
		StringBuilder builder = new StringBuilder();

		if (!StringUtils.isEmpty(textContent) && !StringUtils.isEmpty(maskedContent)
				&& textContent.length() == maskedContent.length()) {

			builder.append(maskedContent);
			char[] characters = textContent.toCharArray();

			for (int i = 0; i < characters.length; i++) {
				if (Character.isUpperCase(characters[i])) {
					builder.setCharAt(i, Character.toUpperCase(maskedContent.charAt(i)));
				}
			}
		}
		return builder.toString();

	}
		
	public static List<String> splitByRegEx(String word, String regex) {
		return Arrays.asList(word.split(regex));
	}
	
	/**
	 * 
	 * Find the Regular Expression matches from Text
	 * 
	 * @param text
	 * @param regex
	 * @return listOfRegExMatch
	 */
	public static List<RegExMatch> getRegExMatches(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		// Check all occurrences

		List<RegExMatch> regExMatchList = new ArrayList<>();

		while (matcher.find()) {

			regExMatchList.add(new RegExMatch(matcher.group(), matcher.start(), matcher.end()));
		}

		return regExMatchList;
	}
	/**
	 * This method return a random word in a given length
	 * 
	 * @param length
	 * @return randomWord
	 */
	public static  String getRandomWordByLength(int length) {

		if (DataCache.dictionary.containsKey(length)) {
			List<String> words = DataCache.dictionary.get(length);
			return words.get(randomGenerator.nextInt(words.size()));
		}
		// If word with given length not available in data set or dictionary,replace all
		// the character with X
		return MaskUtil.getNumberOfMaskedX(length);
	}
	
	



}
