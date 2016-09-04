package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GameContext {

	private boolean ingame;
	private GuiScreen currentScreen;
	private Controller controller;
	
	public GameContext(Controller controller) {
		this.controller = controller;
	}
	
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
		return clazz.isAssignableFrom(currentScreen.getClass());
	}
	
	public Controller getController() {
		return controller;
	}
}
