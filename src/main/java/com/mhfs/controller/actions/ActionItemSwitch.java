package com.mhfs.controller.actions;

import net.minecraft.client.Minecraft;

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
	public String getName() {
		return down ? "ITEM_DOWN" : "ITEM_UP";
	}
	
	public static void register() {
		ActionRegistry.registerAction(new ActionItemSwitch(true));
		ActionRegistry.registerAction(new ActionItemSwitch(false));
	}
}
