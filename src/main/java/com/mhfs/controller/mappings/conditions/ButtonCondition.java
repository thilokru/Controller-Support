package com.mhfs.controller.mappings.conditions;

import com.mhfs.controller.mappings.ConditionSerializationHelper;

public class ButtonCondition implements ICondition{
	
	private int buttonID;
	
	public ButtonCondition(int buttonID) {
		this.buttonID = buttonID;
	}

	public ButtonCondition(String args) {
		this.buttonID = ConditionSerializationHelper.getNames().getButtonID(args.trim());
	}

	@Override
	public boolean check(GameContext context) {
		return context.getController().isButtonPressed(buttonID);
	}
	
	@Override
	public String toSaveString() {
		return String.format("BUTTON(%s)", ConditionSerializationHelper.getNames().getButtonName(buttonID));
	}

}
