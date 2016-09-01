package com.mhfs.controller.actions;

public class ActionRightClick implements IAction {
	
	private boolean allowToggleOn = true, allowToggleOff = false;

	@Override
	public void run() {
		if(allowToggleOn)
			ActionEmulationHelper.startRightClick();
		this.allowToggleOn = false;
		this.allowToggleOff = true;
	}
	
	@Override
	public void notRun() {
		if(allowToggleOff)
			ActionEmulationHelper.stopRightClick();
		this.allowToggleOn = true;
		this.allowToggleOff = false;
	}

	@Override
	public String getName() {
		return "CLICK_RIGHT";
	}

}
