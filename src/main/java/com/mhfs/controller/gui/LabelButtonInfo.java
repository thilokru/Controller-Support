package com.mhfs.controller.gui;

import java.util.Map;
import java.util.Map.Entry;

import com.mhfs.controller.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;

public class LabelButtonInfo extends GuiLabel {
	
	private Map<String, String> buttonFunctions;
	private final FontRenderer fr;

	public LabelButtonInfo(FontRenderer fr, int id, int x, int y, int width, int height) {
		super(fr, id, x, y, width, height, 0xFFFFFF);
		this.fr = fr;
	}
	
	@Override
	public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
		if(buttonFunctions == null) {
			buttonFunctions = Config.INSTANCE.getMapping().getButtonFunctions();
		}
		int xCursor = this.x;
		for(Entry<String, String> entry : buttonFunctions.entrySet()) {
			xCursor += drawButton(xCursor, y, entry.getKey());
			xCursor += this.fr.drawString(entry.getValue(), xCursor, this.y, 0xFFFFFF);
		}
	}

	private int drawButton(int xCursor, int y, String key) {
		
		return 0;
	}

}
