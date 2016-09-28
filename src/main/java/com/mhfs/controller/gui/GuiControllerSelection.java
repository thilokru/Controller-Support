package com.mhfs.controller.gui;

import java.awt.Color;
import java.io.IOException;

import com.mhfs.controller.daemon.BeanController;
import com.mhfs.controller.hotplug.HotplugHandler;
import com.mhfs.ipc.CallFuture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiControllerSelection extends GuiScreen {


	public static void requestController() {
		if(Minecraft.getMinecraft().currentScreen instanceof GuiControllerSelection)return;
		if(!HotplugHandler.getIPCHandler().hasControllers())return;
		GuiControllerSelection gui = new GuiControllerSelection(Minecraft.getMinecraft().currentScreen);
		Minecraft.getMinecraft().displayGuiScreen(gui);
	}
	
	private GuiScreen previous;
	private GuiButton useMouse;
	private CallFuture<Boolean> controllerSelection;
	
	public GuiControllerSelection(GuiScreen previous) {
		this.previous = previous;
		controllerSelection = HotplugHandler.getIPCHandler().startControllerSelection();
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
		if(controllerSelection.isFinished()) {
			if(controllerSelection.getResult()) {
				HotplugHandler.loadSelectedController(new BeanController(HotplugHandler.getIPCHandler()));
				Minecraft.getMinecraft().displayGuiScreen(previous);
			}
		}
	}
	
	@Override 
	protected void actionPerformed(GuiButton button) {
		if(button == useMouse) {
			HotplugHandler.loadSelectedController(null);
			Minecraft.getMinecraft().displayGuiScreen(previous);
			HotplugHandler.getIPCHandler().stopControllerSelection();
		}
	}
}
