package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ControllInfo;
import com.mhfs.controller.mappings.SpecialCaseAxes;

public class StickCondition implements ICondition {
	
	private int axis;
	private float threshold;
	private boolean smallerThan;
	private String controllName;
	
	public StickCondition(int axis, float threshold, boolean smallerThan) {
		this.axis = axis;
		this.threshold = threshold;
		this.smallerThan = smallerThan;
	}

	public StickCondition(String args) {
		String[] sub = args.split(",");
		this.axis = ControllInfo.get().getAxisID(sub[0].trim());
		this.threshold = Float.parseFloat(sub[1].trim());
		this.smallerThan = Boolean.parseBoolean(sub[2].trim());
		if(sub.length > 3) this.controllName = sub[3].trim();
	}

	@Override
	public boolean check(GameContext context) {
		Controller controller = context.getController();
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
		if(controllName != null) {
			return String.format("STICK(%s,%f,%s,%s)", getStickName(), threshold, Boolean.toString(smallerThan), controllName);
		} else {
			return String.format("STICK(%s,%f,%s)", getStickName(), threshold, Boolean.toString(smallerThan));
		}
	}

	public String getStickName() {
		return controllName == null ? ControllInfo.get().getAxisName(axis) : controllName;
	}

}
