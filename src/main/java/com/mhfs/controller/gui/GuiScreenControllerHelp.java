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
	
	public GuiScreenControllerHelp(GuiScreen previous) {
		this.previous = previous;
	}
	
	@Override
	public void initGui() {
		this.buttonList.add(new GuiButton(0, 0, 0, I18n.format("gui.back")));
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		LabelButtonInfo helper = new LabelButtonInfo(this.fontRendererObj, 0, 0, 0, 0, 0);
		ControllerMapping mapping = Config.INSTANCE.getMapping();
		List<Pair<String, String>> functions = mapping.getIngameButtonFunctions();
		int height = functions.size() * (LabelButtonInfo.scaledTextureSize + LabelButtonInfo.boundary * 2);
		int width = 0;
		for(Pair<String, String> entry : functions) {
			width = Math.max(width, LabelButtonInfo.scaledTextureSize + this.fontRendererObj.getStringWidth(entry.getKey()) + LabelButtonInfo.boundary * 2);
		}
		
		int startX = (this.width - width) / 2;
		int startY = (this.height - height) / 2;
		for(Pair<String, String> entry : functions) {
			helper.drawSingleItem(entry.getLeft(), entry.getRight(), startX, startY);
			startY += LabelButtonInfo.scaledTextureSize + 3 * LabelButtonInfo.boundary;
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button.id == 0) {
			Minecraft.getMinecraft().displayGuiScreen(this.previous);
		}
	}
}
