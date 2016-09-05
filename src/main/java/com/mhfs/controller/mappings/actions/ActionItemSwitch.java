package com.mhfs.controller.mappings.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ActionItemSwitch extends ActionToEvent{
	
	private boolean down;
	
	private ActionItemSwitch(boolean down) {
		this.down = down;
	}
	
	@Override
	public void buttonDown() {
		if(Minecraft.getMinecraft().thePlayer != null)
			Minecraft.getMinecraft().thePlayer.inventory.changeCurrentItem(down ? 1 : -1);
	}

	@Override
	public void buttonUp() {}

	@Override
	public String getActionName() {
		return down ? "ITEM_DOWN" : "ITEM_UP";
	}
	
	public static void register() {
		ActionRegistry.registerAction(new ActionItemSwitch(true));
		ActionRegistry.registerAction(new ActionItemSwitch(false));
	}

	@Override
	public String getActionDescription() {
		return I18n.format(down ? "action.prevItem" : "action.nextItem");
	}
}
