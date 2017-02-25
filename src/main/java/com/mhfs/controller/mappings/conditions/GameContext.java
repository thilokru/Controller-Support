package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.mhfs.controller.config.State;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GameContext {

	private boolean ingame;
	private GuiScreen currentScreen;
	
	public boolean update() {
		boolean tmpIngame = Minecraft.getMinecraft().inGameHasFocus;
		GuiScreen tmpScreen = Minecraft.getMinecraft().currentScreen;
		boolean changes = tmpIngame != ingame || tmpScreen != currentScreen;
		this.ingame = tmpIngame;
		this.currentScreen = tmpScreen;
		return changes;
	}
	
	public boolean isIngame() {
		return ingame;
	}
	
	public GuiScreen getCurrentScreen() {
		return currentScreen;
	}
	
	public boolean isScreenInstanceOf(Class<? extends GuiScreen> clazz) {
		if(currentScreen == null) {
			return false;
		}
		return clazz.isAssignableFrom(currentScreen.getClass());
	}
	
	public Controller getController() {
		return State.controller;
	}
	
	public static GameContext getIngameContext() {
		GameContext gc = new GameContext();
		gc.currentScreen = null;
		gc.ingame = true;
		return gc;
	}
}
