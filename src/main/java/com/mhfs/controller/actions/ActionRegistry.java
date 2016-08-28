package com.mhfs.controller.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {

	private static Map<String, IAction> actions = new HashMap<String, IAction>();
	
	public static void registerAction(IAction action) {
		actions.put(action.getName(), action);
	}
	
	public static IAction getAction(String name) {
		return actions.get(name);
	}
}
