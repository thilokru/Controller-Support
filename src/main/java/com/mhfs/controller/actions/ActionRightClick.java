package com.mhfs.controller.actions;

import net.minecraft.client.resources.I18n;

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
	public String getActionName() {
		return "CLICK_RIGHT";
	}

	@Override
	public String getActionDescription() {
		return I18n.format("action.rightClick");
	}

}
