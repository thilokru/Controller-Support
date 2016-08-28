package com.mhfs.controller.mappings.conditions;

import java.util.ArrayList;
import java.util.List;

public class StringSplitHelper {

	public static String[] parantheticalLeveledStringSplit(final String arg) {
		int openParan = 0;
		int firstValid = 0;
		List<String> segments = new ArrayList<String>();
		for(int index = 0; index < arg.length(); index ++) {
			char c = arg.charAt(index);
			if(c == '(') {
				openParan++;
			} else if(c == ')') {
				openParan--;
			} else if(c == ',') {
				if(openParan == 0) {
					segments.add(arg.substring(firstValid, index));
					firstValid = index + 1;
				}
			}
		}
		if(firstValid < arg.length()) {
			segments.add(arg.substring(firstValid, arg.length()));
		}
		return segments.toArray(new String[0]);
	}
}
