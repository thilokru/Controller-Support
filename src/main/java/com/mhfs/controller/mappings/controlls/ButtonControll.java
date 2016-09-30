package com.mhfs.controller.mappings.controlls;

import com.mhfs.controller.mappings.ControllInfo;
import com.mhfs.controller.mappings.conditions.GameContext;

public class ButtonControll implements IControll<Void>{
	
	private int buttonID;
	private boolean phantomProtection = false;
	
	public ButtonControll(int buttonID) {
		this.buttonID = buttonID;
	}

	public ButtonControll(String args) {
		this.buttonID = ControllInfo.get().getButtonID(args.trim());
	}

	@Override
	public boolean check(GameContext context) {
		if(!phantomProtection) {
			return check0(context);
		} else {
			if(!check0(context)) {
				this.phantomProtection = false;
			}
			return false;
		}
	}
	
	private boolean check0(GameContext context) {
		return context.getController().isButtonPressed(buttonID);
	}
	
	@Override
	public String toSaveString() {
		return String.format("BUTTON(%s)", getControllName());
	}

	public String getControllName() {
		return ControllInfo.get().getButtonName(buttonID);
	}

	@Override
	public boolean hasAdditionalData() {
		return false;
	}

	@Override
	public Void getData(GameContext context) {
		return null;
	}

	@Override
	public void enablePhantomProtection() {
		this.phantomProtection = true;
	}

	@Override
	public boolean shouldEnablePhantomProtection(IControll<?> controll) {
		return controll instanceof ButtonControll && ((ButtonControll) controll).buttonID == this.buttonID;
	}
}
