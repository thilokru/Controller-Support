package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.google.common.base.Throwables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class ScreenCondition implements ICondition{
	
	private Class<? extends GuiScreen> screenClazz;
	
	public ScreenCondition(Class<? extends GuiScreen> screenClazz) {
		this.screenClazz = screenClazz;
	}

	@SuppressWarnings("unchecked")
	public ScreenCondition(String args) {
		try {
			this.screenClazz = (Class<? extends GuiScreen>) Class.forName(args.trim());
		} catch (ClassNotFoundException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public boolean check(Minecraft mc, Controller controller) {
		if(mc == null) return false;
		
		if(mc.ingameGUI == null) {
			if(screenClazz.getCanonicalName().endsWith("GuiIngame")){
				return true;
			} else {
				return false;
			}
		}
		try {
			return screenClazz.isAssignableFrom(mc.currentScreen.getClass());
		} catch (NullPointerException npe) {
			return false;
		}
	}
	
	@Override
	public String toSaveString() {
		return String.format("SCREEN(%s)", screenClazz.getCanonicalName());
	}

}
