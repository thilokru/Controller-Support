package com.mhfs.controller.mappings.actions;

public abstract class ActionToEvent implements IAction {
	
	private boolean allowToggleOn = true, allowToggleOff = false;

	@Override
	public final void run() {
		if(allowToggleOn)
			buttonDown();
		this.allowToggleOn = false;
		this.allowToggleOff = true;
	}

	@Override
	public final void notRun() {
		if(allowToggleOff)
			buttonUp();
		this.allowToggleOn = true;
		this.allowToggleOff = false;
	}
	
	public abstract void buttonDown();

	public abstract void buttonUp();
}
