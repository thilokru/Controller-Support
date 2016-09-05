package com.mhfs.controller.mappings.actions;

import java.lang.reflect.Field;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class ActionButtonChange extends ActionToEvent{
	
	private Direction direction;
	private Field screenButtonListField;

	@Override
	public void buttonDown() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		List<GuiButton> buttonList = reflectiveButtonListRetrieve(screen);
		GuiButton button = findNextButton(buttonList, screen.width, screen.height);
		moveMouse(button);
	}

	private void moveMouse(GuiButton button) {
		int xOff = button.width / 2;
		int yOff = button.height / 2;
		int x = button.xPosition + xOff;
		int y = button.yPosition + yOff;
		Mouse.setCursorPosition(x, y);
	}

	private GuiButton findNextButton(List<GuiButton> buttonList, int screenWidth, int screenHeight) {
		int mouseX = Mouse.getX();
		int mouseY = Mouse.getY();
		int step = 20 - 1;
		
		int currentX = mouseX;
		int currentY = mouseY;
		
		while(runCondition(currentX, currentY, screenWidth, screenHeight)) {
			currentX += step * direction.getDX();
			currentY += step * direction.getDY();
			for(GuiButton button : buttonList) {
				if(button.mousePressed(Minecraft.getMinecraft(), currentX, currentY)) {
					return button;
				}
			}
		}
		return null;
	}

	private boolean runCondition(int currentX, int currentY, int screenWidth, int screenHeight) {
		return currentX >= 0 && currentX <= screenWidth && currentY >= 0 && currentY <= screenHeight;
	}

	@Override
	public void buttonUp() {}
	
	@Override
	public String getActionName() {
		return direction.getAction();
	}

	@Override
	public String getActionDescription() {
		return direction.getDescription();
	}
	
	@SuppressWarnings("unchecked")
	private List<GuiButton> reflectiveButtonListRetrieve(GuiScreen screen) {
		if(screenButtonListField == null) {
			try {
				screenButtonListField = GuiScreen.class.getDeclaredField("buttonList");
			} catch (Exception e) {
				throw new RuntimeException("Error reflecting on GuiScreen class!", e);
			}
			screenButtonListField.setAccessible(true);
		}
		try {
			return (List<GuiButton>) screenButtonListField.get(screen);
		} catch (Exception e) {
			throw new RuntimeException("Error reflectivly retrieving the buttonList field!", e);
		}
	}
	
	private enum Direction {
		UP("UP", "gui.button.up", 0, -1),
		DOWN("DOWN", "gui.button.down", 0, 1),
		LEFT("LEFT", "gui.button.left", -1, 0),
		RIGHT("RIGHT", "gui.button.right", 1, 0);
		
		private String action, desc;
		private int dx, dy;
		
		private Direction(String action, String desc, int dx, int dy) {
			this.action = action;
			this.desc = desc;
			this.dx = dx;
			this.dy = dy;
		}
		
		public String getAction() {
			return String.format("BUTTON_CHANGE(%s)", action);
		}
		
		public String getDescription() {
			return I18n.format(desc);
		}
		
		public int getDX() {
			return dx;
		}
		
		public int getDY() {
			return dy;
		}
	}

}
