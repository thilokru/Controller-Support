package com.mhfs.controller.mappings.controlls;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ControllInfo;
import com.mhfs.controller.mappings.SpecialCaseAxes;
import com.mhfs.controller.mappings.conditions.GameContext;

public class StickControll implements IControll<Float> {
	
	private int axis;
	private float threshold;
	private boolean smallerThan;
	private String controllName;
	private boolean phantomProtection;
	
	public StickControll(int axis, float threshold, boolean smallerThan) {
		this.axis = axis;
		this.threshold = threshold;
		this.smallerThan = smallerThan;
	}
	
	/**
	 * New format STICK( ^name^ < ^value^, ^icon^)
	 * @param args
	 */
	public StickControll(String args) {
		String[] sub = args.split(",");
		String unequation = sub[0];
		if(unequation.contains("<")) {
			this.smallerThan = true;
			String[] uneqArgs = unequation.split("<");
			this.axis = ControllInfo.get().getAxisID(uneqArgs[0].trim());
			this.threshold = Float.parseFloat(uneqArgs[1].trim());
		} else if(unequation.contains(">")) {
			this.smallerThan = false;
			String[] uneqArgs = unequation.split(">");
			this.axis = ControllInfo.get().getAxisID(uneqArgs[0].trim());
			this.threshold = Float.parseFloat(uneqArgs[1].trim());
		} else {
			throw new RuntimeException(String.format("Arguments '%s' did not provide a valid comparison!", args));
		}
		if(sub.length > 1) this.controllName = sub[1].trim();
	}
	
	@Override
	public boolean check(GameContext context) {
		if(!phantomProtection) {
			return check0(context);
		} else {
			if(!check0(context)) {
				this.phantomProtection = false;
			}
			return false;
		}
	}

	private boolean check0(GameContext context) {
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
			return String.format("STICK(%s %s %f, %s)", getControllName(), smallerThan ? "<" : ">", threshold, controllName);
		} else {
			return String.format("STICK(%s %s %f)", getControllName(), smallerThan ? "<" : ">", threshold);
		}
	}

	public String getControllName() {
		return controllName == null ? ControllInfo.get().getAxisName(axis) : controllName;
	}

	@Override
	public boolean hasAdditionalData() {
		return true;
	}

	@Override
	public Float getData(GameContext context) {
		return context.getController().getAxisValue(axis);
	}

	@Override
	public void enablePhantomProtection() {
		this.phantomProtection = true;
	}

	@Override
	public boolean shouldEnablePhantomProtection(IControll<?> controll) {
		if(controll instanceof StickControll) {
			StickControll cont = (StickControll) controll;
			if(cont.axis == this.axis) {
				return true;
			}
		}
		return false;
	}

}
