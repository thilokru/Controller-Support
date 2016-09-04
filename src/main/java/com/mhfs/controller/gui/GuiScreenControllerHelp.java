package com.mhfs.controller.gui;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mhfs.controller.Config;
import com.mhfs.controller.mappings.ControllerMapping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenControllerHelp extends GuiScreen {
	
	private GuiScreen previous;
	private int startX;
	private int lineWidth;
	private int columns = 1;
	
	private List<Pair<String, String>> functions;
	
	public GuiScreenControllerHelp(GuiScreen previous) {
		this.previous = previous;
	}
	
	@Override
	public void initGui() {
		this.startX = 0;
		this.lineWidth = 0;
		this.columns = 1;
		ControllerMapping mapping = Config.INSTANCE.getMapping();
		functions = mapping.getIngameButtonFunctions();
		this.buttonList.add(new GuiButton(0, (this.width / 2) - 100, this.height - 20, I18n.format("gui.back")));
		int currentY = 50;
		for(Pair<String, String> entry : functions) {
			lineWidth = Math.max(lineWidth, LabelButtonInfo.scaledTextureSize + this.fontRendererObj.getStringWidth(entry.getValue()) + LabelButtonInfo.boundary * 2);
			currentY += LabelButtonInfo.scaledTextureSize + 3 * LabelButtonInfo.boundary;
			if(currentY > this.height - 50) {
				currentY = 50;
				columns++;
			}
		}
		this.startX = (this.width - columns * lineWidth) / 2;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		LabelButtonInfo helper = new LabelButtonInfo(this.fontRendererObj, 0, 0, 0, 0, 0);
		int currentY = 50;
		int currentX = startX;
		for(Pair<String, String> entry : functions) {
			helper.drawSingleItem(entry.getLeft(), entry.getRight(), currentX, currentY);
			currentY += LabelButtonInfo.scaledTextureSize + 3 * LabelButtonInfo.boundary;
			if(currentY > this.height - 50) {
				currentY = 50;
				currentX += lineWidth;
			}
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			Minecraft.getMinecraft().displayGuiScreen(this.previous);
		}
	}
}
