package com.mhfs.controller.mappings.conditions;

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
		default:
			throw new RuntimeException(String.format("Unknown condition type %s", op));
		}
	}	
}
