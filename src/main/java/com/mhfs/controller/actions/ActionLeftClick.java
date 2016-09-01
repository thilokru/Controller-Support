package com.mhfs.controller.actions;

public class ActionLeftClick extends ActionToEvent {

	@Override
	public String getName() {
		return "CLICK_LEFT";
	}

	@Override
	public void buttonDown() {
		ActionEmulationHelper.startLeftClick();
	}

	@Override
	public void buttonUp() {
		ActionEmulationHelper.stopLeftClick();
	}

}
