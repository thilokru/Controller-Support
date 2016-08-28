package com.mhfs.controller.actions;

public class ActionLeftClick implements IAction {

	@Override
	public void run() {
		ActionEmulationHelper.leftClick();
	}

	@Override
	public String getName() {
		return "CLICK_LEFT";
	}

}
