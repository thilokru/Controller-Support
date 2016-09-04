package com.mhfs.controller.mappings.conditions;

import com.mhfs.controller.mappings.ControllInfo;

public class ButtonCondition implements ICondition{
	
	private int buttonID;
	
	public ButtonCondition(int buttonID) {
		this.buttonID = buttonID;
	}

	public ButtonCondition(String args) {
		this.buttonID = ControllInfo.get().getButtonID(args.trim());
	}

	@Override
	public boolean check(GameContext context) {
		return context.getController().isButtonPressed(buttonID);
	}
	
	@Override
	public String toSaveString() {
		return String.format("BUTTON(%s)", getButtonName());
	}

	public String getButtonName() {
		return ControllInfo.get().getButtonName(buttonID);
	}

}
