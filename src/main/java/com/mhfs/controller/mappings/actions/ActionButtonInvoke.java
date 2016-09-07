package com.mhfs.controller.mappings.actions;

import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class ActionButtonInvoke extends ActionToEvent<Object> {

	private int buttonID;
	
	public ActionButtonInvoke(int id) {
		this.buttonID = id;
	}
	
	@Override
	public String getActionName() {
		return "INVOKE_BUTTON";
	}

	@Override
	public String getActionDescription() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		if(screen == null)return "<ERROR>";
		for(GuiButton button : ActionButtonChange.reflectiveButtonListRetrieve(screen)) {
			if(button.id == buttonID) {
				return button.displayString;
			}
		}
		return "";
	}

	@Override
	public void buttonDown() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		for(GuiButton button : ActionButtonChange.reflectiveButtonListRetrieve(screen)) {
			if(button.id == buttonID) {
				try{
					Method method = GuiScreen.class.getDeclaredMethod("actionPerformed", GuiButton.class);
					method.setAccessible(true);
					method.invoke(screen, button);
				} catch (Exception e) {
					throw new RuntimeException("Unable to reflect on GuiScreen!", e);
				}
			}
		}
	}

	@Override
	public void buttonUp() {}

}
