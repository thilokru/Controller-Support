package com.mhfs.controller.mappings.conditions;

import java.util.ArrayList;
import java.util.List;

public class ConditionSerializationHelper {

	public static String toString(ICondition value) {
		return value.toSaveString();
	}

	public static ICondition fromString(String serializedCondition) {
		int openingIndex = serializedCondition.indexOf('(');
		int closingIndex = serializedCondition.lastIndexOf(')');
		if(openingIndex < 0 || closingIndex < 0 || openingIndex >= serializedCondition.length() || closingIndex > serializedCondition.length()){
			throw new RuntimeException(String.format("The condition '%s' has faulty parantheses!", serializedCondition));
		}
		String op = serializedCondition.substring(0, openingIndex).toLowerCase();
		String args = serializedCondition.substring(openingIndex + 1, closingIndex);
		switch(op) {
		case "and":
			return new AndCondition(args);
		case "or":
			return new OrCondition(args);
		case "not":
			return new NotCondition(args);
		case "screen":
			return new ScreenCondition(args);
		case "ingame":
			return new IngameCondition();
		case "options":
			return new OptionsCondition();
		case "container":
			return new ContainerCondition();
		default:
			throw new RuntimeException(String.format("Unknown condition type %s", op));
		}
	}
	
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
