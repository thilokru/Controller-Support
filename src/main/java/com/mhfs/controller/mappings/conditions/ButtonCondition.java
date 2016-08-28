package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ConditionSerializationHelper;

import net.minecraft.client.Minecraft;

public class ButtonCondition implements ICondition{
	
	private int buttonID;
	
	public ButtonCondition(int buttonID) {
		this.buttonID = buttonID;
	}

	public ButtonCondition(String args) {
		this.buttonID = ConditionSerializationHelper.getNames().getButtonID(args.trim());
	}

	@Override
	public boolean check(Minecraft mc, Controller controller) {
		return controller.isButtonPressed(buttonID);
	}
	
	@Override
	public String toSaveString() {
		return String.format("BUTTON(%s)", ConditionSerializationHelper.getNames().getButtonName(buttonID));
	}

}
