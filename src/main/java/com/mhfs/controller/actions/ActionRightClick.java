package com.mhfs.controller.actions;

public class ActionRightClick implements IAction {

	@Override
	public void run() {
		ActionEmulationHelper.startRightClick();
	}
	
	@Override
	public void notRun() {
		ActionEmulationHelper.stopRightClick();
	}

	@Override
	public String getName() {
		return "CLICK_RIGHT";
	}

}
