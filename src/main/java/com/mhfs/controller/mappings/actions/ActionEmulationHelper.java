package com.mhfs.controller.mappings.actions;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;

public class ActionEmulationHelper {

	private static Robot robot;
	
	static{
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}
	
	public static void moveMouse(float dx, float dy) {
		double x = MouseInfo.getPointerInfo().getLocation().getX() + dx;
		double y = MouseInfo.getPointerInfo().getLocation().getY() + dy;
		robot.mouseMove((int)x, (int)y);
	}
	
	public static void moveMouseInGui(int x, int y, int screenWidth, int screenHeight) {
		int glX = x * Minecraft.getMinecraft().displayWidth / screenWidth;
		int glY = (Minecraft.getMinecraft().displayHeight / screenHeight) * (1 + screenHeight - y);
		Mouse.setCursorPosition(glX, glY);
	}

	public static void startLeftClick() {
		int mask = InputEvent.getMaskForButton(1);
		robot.mousePress(mask);
	}
	
	public static void stopLeftClick() {
		int mask = InputEvent.getMaskForButton(1);
		robot.mouseRelease(mask);
	}

	public static void startRightClick() {
		int mask = InputEvent.getMaskForButton(3);
		robot.mousePress(mask);
	}
	
	public static void stopRightClick() {
		int mask = InputEvent.getMaskForButton(3);
		robot.mouseRelease(mask);
	}
	
	public static void startEscape() {
		robot.keyPress(KeyEvent.VK_ESCAPE);
	}
	
	public static void stopEscape() {
		robot.keyRelease(KeyEvent.VK_ESCAPE);
	}
}
