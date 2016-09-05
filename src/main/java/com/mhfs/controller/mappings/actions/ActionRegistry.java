package com.mhfs.controller.mappings.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {

	private static Map<String, IAction> actions = new HashMap<String, IAction>();
	
	public static void registerAction(IAction action) {
		actions.put(action.getActionName(), action);
	}
	
	public static IAction getAction(String name) {
		return actions.get(name);
	}
}
