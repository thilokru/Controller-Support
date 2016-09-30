package com.mhfs.controller.mappings.controlls;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.conditions.GameContext;

public class DPadControll implements IControll<Pair<Float, Float>> {
	
	private boolean phantomProtection = false;

	@Override
	public boolean check(GameContext context) {
		if(!phantomProtection) {
			return check0(context.getController());
		} else {
			if(!check0(context.getController())) {
				phantomProtection = false;
			}
			return false;
		}
	}
	
	private boolean check0(Controller controller) {
		return controller.getPovX() != 0.0 || controller.getPovY() != 0.0;
	}

	@Override
	public String toSaveString() {
		return "DPAD";
	}

	@Override
	public boolean hasAdditionalData() {
		return true;
	}

	@Override
	public Pair<Float, Float> getData(GameContext context) {
		Controller controller = context.getController();
		return Pair.of(controller.getPovX(), controller.getPovY());
	}

	@Override
	public String getControllName() {
		return "DPAD";
	}

	@Override
	public void enablePhantomProtection() {
		this.phantomProtection = true;
	}

	@Override
	public boolean shouldEnablePhantomProtection(IControll<?> controll) {
		return controll instanceof DPadControll;
	}

}
