package com.mhfs.controller.mappings.controlls;

import com.mhfs.controller.mappings.ControllInfo;
import com.mhfs.controller.mappings.conditions.GameContext;

public class ButtonControll implements IControll{
	
	private int buttonID;
	
	public ButtonControll(int buttonID) {
		this.buttonID = buttonID;
	}

	public ButtonControll(String args) {
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

	@Override
	public boolean hasAdditionalData() {
		return false;
	}

	@Override
	public Object getData(GameContext context) {
		return null;
	}

}
