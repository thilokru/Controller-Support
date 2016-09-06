package com.mhfs.controller.mappings.actions;

import net.minecraft.client.resources.I18n;

public class ActionEscape extends ActionToEvent<Object>{

	@Override
	public void buttonDown() {
		ActionEmulationHelper.startEscape();
	}

	@Override
	public void buttonUp() {
		ActionEmulationHelper.stopEscape();
	}

	@Override
	public String getActionName() {
		return "ESCAPE";
	}

	@Override
	public String getActionDescription() {
		return I18n.format("action.escape");
	}

}
