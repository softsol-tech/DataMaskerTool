package com.softsol.masker.service;

import static com.softsol.masker.constants.Constants.REGEX_NEWLINE;
import static com.softsol.masker.constants.Constants.REGEX_ONLY_DIGITS;
import static com.softsol.masker.constants.Constants.REGEX_ONLY_WORDS;
import static com.softsol.masker.constants.Constants.REGEX_WHITESPACE;
import static com.softsol.masker.constants.Constants.REGEX_WORD_FOLLOW_COMMA;
import static com.softsol.masker.constants.Constants.REGEX_WORD_FOLLOW_DOT;
import static com.softsol.masker.constants.Constants.REGEX_WORD_FOLLOW_EXCLAMATION;
import static com.softsol.masker.constants.Constants.REGEX_WORD_FOLLOW_QUESTION;
import static com.softsol.masker.constants.Constants.REGEX_XML_ENTITY_FORMAT;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.softsol.masker.MaskConfigProperties;
import com.softsol.masker.exception.MaskerException;
import com.softsol.masker.util.DataCache;
import com.softsol.masker.util.MaskUtil;
import com.softsol.masker.util.RegExMatch;


@Service
public class XMLMaskerServiceImpl implements MaskerService {
	
	Logger logger = LoggerFactory.getLogger(XMLMaskerServiceImpl.class);
	
	@Autowired
	MaskConfigProperties maskConfigProperties;
	
	/**
	 * @param inputFile
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws Exception
	 */
	public void payloadMask(String inputFile) {

		DocumentBuilderFactory dbf = null;
		Document document = null;

		try (InputStream inputStream = new FileInputStream(inputFile)) {

			dbf = DocumentBuilderFactory.newInstance();
			// DTD Validation Off
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			// external DTD entities restriction
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			document = dbf.newDocumentBuilder().parse(inputStream);
			maskNodeTree(document.getDocumentElement());
		} catch (SAXException e) {
			throw new MaskerException("Exception while parsing file :" + inputFile, e);
		} catch (IOException e1) {
			throw new MaskerException("File Issue" + inputFile, e1);
		} catch (ParserConfigurationException e) {
			throw new MaskerException("XML Configuration Failed for the file :" + inputFile, e);
		}
		try (OutputStream outputStream = new FileOutputStream(inputFile)) {
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) document.getImplementation()
					.getFeature("LS", "3.0");
			LSOutput lsOutput = domImplementationLS.createLSOutput();
			lsOutput.setByteStream(outputStream);
			LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
			lsSerializer.write(document, lsOutput);
		} catch (IOException e) {
			throw new MaskerException("File Issue" + inputFile, e);
		}
		logger.info("XML Masked Successfully. File :" + inputFile);
	}

	/**
	 * A helper method to recursively search for all elements and apply mask
	 * 
	 * @param node The root node of the document.
	 */
	private void maskNodeTree(Node node) {


		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			if (!DataCache.elementsToIgnore.contains(node.getNodeName().toLowerCase())) {
				maskNodeTree(nodeList.item(i));
			}
		}
		if (maskConfigProperties.isEnableAttributeMask() && node.getNodeType() == Node.ELEMENT_NODE) {
			NamedNodeMap attributes = node.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node attribute = attributes.item(i);
				if (!DataCache.attributeToIgnore.contains(attribute.getNodeName().toLowerCase())) {
					attribute.setNodeValue(maskContent(attribute.getNodeValue()));
				}
			}
		}
		
		if (node.getNodeType() == Node.TEXT_NODE) {
			
			node.setTextContent(maskContent(node.getTextContent()));
		}
	}

	/**
	 * Mask content with sentences or multiple lines
	 * 
	 * @param textContent
	 * @return maskedContent
	 */
	private  String maskContent(String textContent) {

		// Regular Expression for Entity,NewLine,WhiteSpace. All will be retained at the
		// same position.
		String mergedRegEx = REGEX_XML_ENTITY_FORMAT + '|' + REGEX_NEWLINE + '|' + REGEX_WHITESPACE;
		StringBuilder maskBuilder = new StringBuilder();
		List<RegExMatch> regExMatchList = MaskUtil.getRegExMatches(textContent, mergedRegEx);

		if (regExMatchList != null && !regExMatchList.isEmpty()) {
			List<String> splitedWordList = MaskUtil.splitByRegEx(textContent, mergedRegEx);

			for (String word : splitedWordList) {
				maskBuilder.append(maskWord(word));
			}

			for (RegExMatch regExMatchText : regExMatchList) {
				maskBuilder.insert(regExMatchText.getStartIndex(), regExMatchText.getValue());
			}
		} else {
			return maskWord(textContent);
		}
		return maskBuilder.toString();
	}
	
	/**
	 * Masking the given text/word, It can be AlphaNumeric, Numbers, Words and also
	 * text With Special character
	 * 
	 * @param word
	 * @return maskedText
	 */
	public String maskWord(String word) {

		int wordLength = word.length();

		String maskedWord = "";
		if (word.matches(REGEX_ONLY_DIGITS)) {

			// Getting same number of random digits as original one
			return MaskUtil.maskNumbers(word);

		} else if (word.matches(REGEX_ONLY_WORDS)) {

			// Gets a masked/alternate word with exact length of original word
			maskedWord = MaskUtil.getRandomWordByLength(wordLength);

			// Converting to upper case or lower case based on original string
			maskedWord = MaskUtil.convertCase(word, maskedWord);

		} else if (word.matches(REGEX_WORD_FOLLOW_COMMA + "|" + REGEX_WORD_FOLLOW_DOT + "|" + REGEX_WORD_FOLLOW_QUESTION
				+ "|" + REGEX_WORD_FOLLOW_EXCLAMATION)) {
			// TODO Can be added to maskContent method
			/**
			 * In the next else block we have a logic, if the text having a special
			 * character, replace each character with a random one except the special
			 * character //EX: HKIAYR124@GMAIL.COM ----> KJHIOU456@KDHDI.JIO //[Here we
			 * don't need any meaningful word since this word might be a mail address or a
			 * kind of ID in some case] But In the below case, we might have a word which
			 * ends up with a . or , or ? EX: We are done. ------> Hi See Kind. [In this
			 * case we need a meaningful word]
			 */
			maskedWord = MaskUtil.getRandomWordByLength(wordLength - 1) + String.valueOf(word.charAt(wordLength - 1));

		} else {
			StringBuilder newMask = new StringBuilder("");

			// iterate for every characters
			for (int i = 0; i < wordLength; ++i) {

				char inputChar = word.charAt(i);

				// CHECKING FOR ALPHABET
				if (inputChar >= 65 && inputChar <= 90) {
					newMask.append(Character.toUpperCase(MaskUtil.getRandomAlphabet()));
				} else if (inputChar >= 97 && inputChar <= 122) {
					newMask.append(MaskUtil.getRandomAlphabet());
				}
				// CHECKING FOR DIGITS
				else if (inputChar >= 48 && inputChar <= 57) {
					newMask.append(MaskUtil.getRandomNumber());
				} else {
					// No Replacement for Special Character
					newMask.append(inputChar);
				}
				maskedWord = newMask.toString();
			}
		}
		return maskedWord;

	}
}
