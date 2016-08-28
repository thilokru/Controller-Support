package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import net.minecraft.client.Minecraft;

public class IngameCondition implements ICondition{

	@Override
	public boolean check(Minecraft mc, Controller controller) {
		return mc.inGameHasFocus;
	}

	@Override
	public String toSaveString() {
		return "INGAME";
	}

}
