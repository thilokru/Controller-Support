package com.mhfs.controller.mappings.controlls;

public class ControllSerializationHelper {

	public static String toString(IControll<?> value) {
		return value.toSaveString();
	}

	public static IControll<?> fromString(String serializedCondition) {
		int openingIndex = serializedCondition.indexOf('(');
		int closingIndex = serializedCondition.lastIndexOf(')');
		if(openingIndex < 0 || closingIndex < 0 || openingIndex >= serializedCondition.length() || closingIndex > serializedCondition.length()){
			throw new RuntimeException(String.format("The condition '%s' has faulty parantheses!", serializedCondition));
		}
		String op = serializedCondition.substring(0, openingIndex).toLowerCase();
		String args = serializedCondition.substring(openingIndex + 1, closingIndex);
		switch(op) {
		case "dpad":
			return new DPadControll();
		case "button":
			return new ButtonControll(args);
		case "stick":
			return new StickControll(args);
		default:
			throw new RuntimeException(String.format("Unknown controll type %s", op));
		}
	}
}
