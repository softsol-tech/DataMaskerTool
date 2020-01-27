package com.softsol.masker.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataCache {

	
	public static final Map<Integer, List<String>> dictionary = new HashMap<>();
	public static final Set<String> elementsToIgnore = new HashSet<>();
	public static final Set<String> attributeToIgnore =new HashSet<>();
	
	private DataCache() {
		
	}
}
