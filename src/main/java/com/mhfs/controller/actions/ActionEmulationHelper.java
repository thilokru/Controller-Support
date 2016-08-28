package com.mhfs.controller.actions;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;

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
}
