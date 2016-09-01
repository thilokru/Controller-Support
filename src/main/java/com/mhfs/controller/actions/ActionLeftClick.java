package com.mhfs.controller.actions;

public class ActionLeftClick implements IAction {
	
	private boolean allowToggleOn = true, allowToggleOff = false;

	@Override
	public void run() {
		if(allowToggleOn)
			ActionEmulationHelper.startLeftClick();
		this.allowToggleOn = false;
		this.allowToggleOff = true;
	}
	
	@Override
	public void notRun() {
		if(allowToggleOff)
			ActionEmulationHelper.stopLeftClick();
		this.allowToggleOn = true;
		this.allowToggleOff = false;
	}

	@Override
	public String getName() {
		return "CLICK_LEFT";
	}

}
