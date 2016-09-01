package com.mhfs.controller.actions;

public class ActionEscape extends ActionToEvent{

	@Override
	public void buttonDown() {
		ActionEmulationHelper.startEscape();
	}

	@Override
	public void buttonUp() {
		ActionEmulationHelper.stopEscape();
	}

	@Override
	public String getName() {
		return "ESCAPE";
	}

}
