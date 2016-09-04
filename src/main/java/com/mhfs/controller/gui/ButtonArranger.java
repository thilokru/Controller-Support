package com.mhfs.controller.gui;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.base.Throwables;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;


public class ButtonArranger {
	
	private static Field guiButtonListField;
	
	public static void arrangeButtonsVertical(GuiScreen screen) {
		List<GuiButton> buttons = getButtonList(screen);
		int minX = Integer.MAX_VALUE, maxX = 0, height = 0, distance = Integer.MAX_VALUE;
		for(int i = 0; i < buttons.size(); i++) {
			GuiButton button = buttons.get(i);
			minX = Math.min(minX, button.xPosition);
			maxX = Math.max(maxX, button.xPosition + button.width);
			height = Math.max(height, button.height);
			
			int nextIndex = i + 1;
			if(nextIndex < buttons.size()) {
				GuiButton next = buttons.get(nextIndex);
				
				if(next.xPosition - button.xPosition < height) continue;
				
				int currentDistance = Math.abs(next.yPosition - (button.yPosition + button.height));
				distance = Math.min(distance, currentDistance);
			}
		}
		
		int totalHeight = buttons.size() * height + (buttons.size() - 1) * distance;
		int screenHeight = screen.height;
		int yStart = (screenHeight - totalHeight) / 2;
		int width = maxX - minX;
		for(int i = 0; i < buttons.size(); i++) {
			GuiButton button = buttons.get(i);
			button.xPosition = minX;
			button.width = width;
			button.yPosition = yStart + i * (height + distance);
			button.height = height;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static List<GuiButton> getButtonList(GuiScreen screen) {
		if(guiButtonListField == null) {
			setupReflection();
		}
		try {
			return (List<GuiButton>) guiButtonListField.get(screen);
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return null;
	}

	private static void setupReflection() {
		try {
			guiButtonListField = GuiScreen.class.getDeclaredField("buttonList");
			guiButtonListField.setAccessible(true);
		} catch (Exception e) {
			Throwables.propagate(e);
		}
	}
}
