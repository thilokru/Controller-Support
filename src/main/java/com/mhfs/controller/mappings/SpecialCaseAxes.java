package com.mhfs.controller.mappings;

import org.lwjgl.input.Controller;

public class SpecialCaseAxes {

	public static float getSpecialCase(int axis, Controller controller) {
		if(axis == -1) {
			return controller.getPovX();
		}else if (axis == -2) {
			return controller.getPovY();
		}
		return 0;
	}
}
