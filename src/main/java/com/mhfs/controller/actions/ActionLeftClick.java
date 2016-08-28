package com.mhfs.controller.actions;

public class ActionLeftClick implements IAction {

	@Override
	public void run() {
		ActionEmulationHelper.startLeftClick();
	}
	
	@Override
	public void notRun() {
		ActionEmulationHelper.stopLeftClick();
	}

	@Override
	public String getName() {
		return "CLICK_LEFT";
	}

}
