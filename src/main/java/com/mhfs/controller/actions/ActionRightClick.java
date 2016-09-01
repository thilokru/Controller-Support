package com.mhfs.controller.actions;

public class ActionRightClick extends ActionToEvent {
	
	@Override
	public void buttonDown() {
		ActionEmulationHelper.startRightClick();
	}
	
	@Override
	public void buttonUp() {
		ActionEmulationHelper.stopRightClick();
	}

	@Override
	public String getName() {
		return "CLICK_RIGHT";
	}

}
