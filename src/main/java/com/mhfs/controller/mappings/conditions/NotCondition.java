package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ConditionSerializationHelper;

import net.minecraft.client.Minecraft;

public class NotCondition implements ICondition{

	private ICondition condition;
	
	public NotCondition(ICondition condition) {
		this.condition = condition;
	}
	
	public NotCondition(String args) {
		this.condition = ConditionSerializationHelper.fromString(args.trim());
	}

	@Override
	public boolean check(Minecraft mc, Controller controller) {
		return !condition.check(mc, controller);
	}
	
	@Override
	public String toSaveString() {
		return String.format("NOT(%s)", condition.toSaveString());
	}
}
