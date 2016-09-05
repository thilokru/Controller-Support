package com.mhfs.controller.mappings.actions;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class ActionButtonChange extends ActionToEvent{
	private Field screenButtonListField;

	@Override
	public void buttonDown() {
		throw new RuntimeException("ActionButtonChange requires a Pair of Floats as argument!");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean buttonDown(Object arg) {
		if(arg instanceof Pair) {
			Pair<Float, Float> vals = (Pair<Float, Float>)arg;
			Direction dir = Direction.fromValues(vals.getLeft(), vals.getRight());
			if(dir == null)return true;
			GuiScreen screen = Minecraft.getMinecraft().currentScreen;
			List<GuiButton> buttonList = reflectiveButtonListRetrieve(screen);
			GuiButton button = findNextButton(buttonList, dir, screen.width, screen.height);
			if(button == null) return true;
			moveMouse(button, screen.width, screen.height);
		} else {
			throw new RuntimeException("ActionButtonChange only accepts Pairs of values as argument!");
		}
		
		return true;
	}

	public static void moveMouse(GuiButton button, int screenWidth, int screenHeight) {
		int xOff = button.width > 20 ? 10 : button.width / 2;
		int yOff = button.height / 2;
		int x = button.xPosition + xOff;
		int y = button.yPosition + yOff;
		int glX = x * Minecraft.getMinecraft().displayWidth / screenWidth;
		int glY = (Minecraft.getMinecraft().displayHeight / screenHeight) * (1 + screenHeight - y);
		Mouse.setCursorPosition(glX, glY);
	}

	private GuiButton findNextButton(List<GuiButton> buttonList, Direction direction, int screenWidth, int screenHeight) {
		int mouseX = Mouse.getEventX() * screenWidth / Minecraft.getMinecraft().displayWidth;
		int mouseY = screenHeight - Mouse.getEventY() * screenHeight / Minecraft.getMinecraft().displayHeight - 1;
		int step = 20 - 1;
		
		int currentX = mouseX;
		int currentY = mouseY;
		
		GuiButton currentButton = null;
		for(GuiButton button : buttonList) {
			boolean tempEnabled = button.enabled;
			button.enabled = true;
			if(button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
				button.enabled = tempEnabled;
				currentButton = button;
				break;
			}
			button.enabled = tempEnabled;
		}
		
		while(runCondition(currentX, currentY, screenWidth, screenHeight)) {
			currentX += step * direction.getDX();
			currentY += step * direction.getDY();
			for(GuiButton button : buttonList) {
				if(button == currentButton) continue;
				boolean tempEnabled = button.enabled;
				button.enabled = true;
				if(button.mousePressed(Minecraft.getMinecraft(), currentX, currentY)) {
					button.enabled = tempEnabled;
					return button;
				}
				button.enabled = tempEnabled;
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
		return "BUTTON_CHANGE";
	}

	@Override
	public String getActionDescription() {
		return I18n.format("gui.button.change");
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
		UP(0, -1),
		DOWN(0, 1),
		LEFT(-1, 0),
		RIGHT(1, 0);
		
		private int dx, dy;
		
		private Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		public int getDX() {
			return dx;
		}
		
		public int getDY() {
			return dy;
		}
		
		public static Direction fromValues(float x, float y) {
			if(x == -1) {
				return LEFT;
			} else if(x == 1) {
				return RIGHT;
			} else if(y == -1) {
				return UP;
			} else if(y == 1) {
				return DOWN;
			}
			return null;
		}
	}

}
