package com.mhfs.controller.mappings.conditions;

import com.google.common.base.Throwables;

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
	public boolean check(GameContext context) {
		return context.isScreenInstanceOf(screenClazz);
	}
	
	@Override
	public String toSaveString() {
		return String.format("SCREEN(%s)", screenClazz.getCanonicalName());
	}

}
