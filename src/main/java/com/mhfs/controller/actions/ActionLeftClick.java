package com.mhfs.controller.actions;

import net.minecraft.client.resources.I18n;

public class ActionLeftClick extends ActionToEvent {

	@Override
	public String getActionName() {
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

	@Override
	public String getActionDescription() {
		return I18n.format("action.leftClick");
	}

}
