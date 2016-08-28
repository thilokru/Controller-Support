package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ConditionSerializationHelper;
import com.mhfs.controller.mappings.SpecialCaseAxes;

import net.minecraft.client.Minecraft;

public class StickCondition implements ICondition {
	
	private int axis;
	private float threshold;
	private boolean smallerThan;
	
	public StickCondition(int axis, float threshold, boolean smallerThan) {
		this.axis = axis;
		this.threshold = threshold;
		this.smallerThan = smallerThan;
	}

	public StickCondition(String args) {
		String[] sub = args.split(",");
		this.axis = ConditionSerializationHelper.getNames().getAxisID(sub[0].trim());
		this.threshold = Float.parseFloat(sub[1].trim());
		this.smallerThan = Boolean.parseBoolean(sub[2].trim());
	}

	@Override
	public boolean check(Minecraft mc, Controller controller) {
		float value;
		
		if(axis >= 0) {
			value = controller.getAxisValue(axis);
		} else {
			value = SpecialCaseAxes.getSpecialCase(axis, controller);
		}
		if(smallerThan) {
			return value < threshold;
		} else {
			return value > threshold;
		}
	}
	
	@Override
	public String toSaveString() {
		return String.format("STICK(%s,%f,%s)", ConditionSerializationHelper.getNames().getAxisName(axis), threshold, Boolean.toString(smallerThan));
	}

}
