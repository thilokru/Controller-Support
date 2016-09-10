package com.mhfs.controller.mappings.actions;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry {

	private static Map<String, IAction> actions = new HashMap<String, IAction>();
	
	public static void load() {
		ActionRegistry.registerAction(new ActionLeftClick());
		ActionRegistry.registerAction(new ActionRightClick());
		ActionRegistry.registerAction(new ActionEscape());
		ActionRegistry.registerAction(new ActionButtonChange());
		ActionRegistry.registerAction(new ActionListSelect());
		ActionRegistry.registerAction(new ActionButtonState("DELETE_LAST", "gui.input.delete"));
		ActionRegistry.registerAction(new ActionButtonState("CAPITAL", "gui.input.capital"));
		ActionRegistry.registerAction(new ActionButtonState("SPECIAL_CHAR", "gui.input.special"));
		ActionRegistry.registerAction(new ActionButtonState("SELECT_CHAR", "gui.input.select"));
		ActionKeyBind.registerActions();
		ActionItemSwitch.register();
	}
	
	public static void registerAction(IAction action) {
		actions.put(action.getActionName(), action);
	}
	
	public static IAction getAction(String name) {
		if(name.toLowerCase().contains("INVOKE_BUTTON".toLowerCase())) {
			int openParanIndex = name.indexOf('(');
			int closeParanIndex = name.indexOf(')');
			String sub = name.substring(openParanIndex + 1, closeParanIndex);
			int buttonID = Integer.parseInt(sub.trim());
			return new ActionButtonInvoke(buttonID);
		}
		return actions.get(name);
	}
}
