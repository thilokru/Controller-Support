package com.mhfs.controller.gui;

import java.awt.Color;
import java.io.IOException;

import org.lwjgl.input.Controllers;

import com.mhfs.controller.hotplug.HotplugHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiControllerSelection extends GuiScreen {


	public static void requestController() {
		if(Minecraft.getMinecraft().currentScreen instanceof GuiControllerSelection)return;
		if(Controllers.getControllerCount() == 0)return;
		GuiControllerSelection gui = new GuiControllerSelection(Minecraft.getMinecraft().currentScreen);
		Controllers.clearEvents();
		Minecraft.getMinecraft().displayGuiScreen(gui);
	}
	
	private GuiScreen previous;
	private GuiButton useMouse;
	
	public GuiControllerSelection(GuiScreen previous) {
		this.previous = previous;
	}
	
	@Override
	public void initGui() {
		GuiLabel label = new GuiLabel(this.fontRendererObj, 0, 0, this.height / 2, this.width, this.fontRendererObj.FONT_HEIGHT, Color.WHITE.getRGB());
		label.setCentered();
		label.addLine(I18n.format("gui.controller.pressAnyKey"));
		this.labelList.add(label);
		
		useMouse = new GuiButton(0, this.width / 2 - 100, this.height - 30, I18n.format("gui.controller.useMouse"));
		this.buttonList.add(useMouse);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawWorldBackground(0);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void handleInput() throws IOException {
		super.handleInput();
		Controllers.poll();
		if(Controllers.next()) {
			if(Controllers.isEventButton() && Controllers.getEventButtonState()) {
				HotplugHandler.loadSelectedController(Controllers.getEventSource());
				Minecraft.getMinecraft().displayGuiScreen(previous);
			}
		}
	}
	
	@Override 
	protected void actionPerformed(GuiButton button) {
		if(button == useMouse) {
			HotplugHandler.loadSelectedController(null);
			Minecraft.getMinecraft().displayGuiScreen(previous);
		}
	}
}
