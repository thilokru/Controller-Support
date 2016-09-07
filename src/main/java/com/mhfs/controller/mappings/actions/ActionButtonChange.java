package com.mhfs.controller.mappings.actions;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class ActionButtonChange extends ActionToEvent<Pair<Float, Float>>{
	private static Field screenButtonListField;

	@Override
	public void buttonDown() {
		throw new RuntimeException("ActionButtonChange requires a Pair of Floats as argument!");
	}
	
	@Override
	public boolean buttonDown(Pair<Float, Float> arg) {
		Pair<Float, Float> vals = (Pair<Float, Float>)arg;
		Direction dir = Direction.fromValues(vals.getLeft(), vals.getRight());
		if(dir == null)return true;
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		List<Wrapper> buttonList = getMergedElementList(screen);
		Wrapper button = findNextGuiElement(buttonList, dir, screen.width, screen.height);
		if(button == null) return true;
		moveMouse(button, screen.width, screen.height);
		return true;
	}

	public static void moveMouse(Wrapper button, int screenWidth, int screenHeight) {
		int xOff = button.width > 20 ? 10 : button.width / 2;
		int yOff = button.height / 2;
		int x = button.x + xOff;
		int y = button.y + yOff;
		int glX = x * Minecraft.getMinecraft().displayWidth / screenWidth;
		int glY = (Minecraft.getMinecraft().displayHeight / screenHeight) * (1 + screenHeight - y);
		Mouse.setCursorPosition(glX, glY);
	}

	private Wrapper findNextGuiElement(List<Wrapper> buttonList, Direction direction, int screenWidth, int screenHeight) {
		int mouseX = Mouse.getEventX() * screenWidth / Minecraft.getMinecraft().displayWidth;
		int mouseY = screenHeight - Mouse.getEventY() * screenHeight / Minecraft.getMinecraft().displayHeight - 1;
		int step = 20 - 1;
		
		int currentX = mouseX;
		int currentY = mouseY;
		
		Gui current = null;
		for(Wrapper wrapper : buttonList) {
			if(wrapper.isVisible && wrapper.mouseInside(mouseX, mouseY)) {
				current = wrapper.obj;
				break;
			}
		}
		
		while(runCondition(currentX, currentY, screenWidth, screenHeight)) {
			currentX += step * direction.getDX();
			currentY += step * direction.getDY();
			for(Wrapper wrapper : buttonList) {
				if(wrapper.obj == current) continue;
				if(wrapper.isVisible && wrapper.mouseInside(currentX, currentY)) {
					return wrapper;
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
		return "BUTTON_CHANGE";
	}

	@Override
	public String getActionDescription() {
		return I18n.format("gui.button.change");
	}
	
	private static List<Wrapper> getMergedElementList(GuiScreen screen) {
		List<Wrapper> ret = Lists.newArrayList();
		for(GuiTextField field : reflectiveTextFieldListRetrieve(screen)) {
			ret.add(new Wrapper(field));
		}
		for(GuiButton button : reflectiveButtonListRetrieve(screen)) {
			ret.add(new Wrapper(button));
		}
		return ret;
	}
	
	public static List<GuiTextField> reflectiveTextFieldListRetrieve(GuiScreen screen) {
		List<GuiTextField> list = Lists.newArrayList();
		for(Field field : screen.getClass().getDeclaredFields()) {
			if(GuiTextField.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					list.add((GuiTextField) field.get(screen));
				} catch(Exception e) {
					throw new RuntimeException("Error reflecting on GuiScreen class!", e);
				}
			}
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static List<GuiButton> reflectiveButtonListRetrieve(GuiScreen screen) {
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
	
	public static class Wrapper {
		
		public int width, height, x, y;
		public boolean isVisible;
		public Gui obj;
		
		public Wrapper(GuiButton button) {
			obj = button;
			this.width = button.width;
			this.height = button.height;
			this.x = button.xPosition;
			this.y = button.yPosition;
			this.isVisible = button.visible;
		}

		public Wrapper(GuiTextField field) {
			obj = field;
			this.width = field.width;
			this.height = field.height;
			this.x = field.xPosition;
			this.y = field.yPosition;
			this.isVisible = field.getVisible();
		}
		
		public boolean mouseInside(int currentX, int currentY) {
			return currentX > x && currentY > y && currentX < x + width && currentY < y + height;
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
