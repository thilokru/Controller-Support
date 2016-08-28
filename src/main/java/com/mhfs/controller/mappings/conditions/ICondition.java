package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import net.minecraft.client.Minecraft;

public interface ICondition {

	public boolean check(Minecraft mc, Controller controller);
	
	public String toSaveString();
}
