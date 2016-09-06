package com.mhfs.controller.mappings.controlls;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.conditions.GameContext;

public class DPadControll implements IControll<Pair<Float, Float>> {

	@Override
	public boolean check(GameContext context) {
		return context.getController().getPovX() != 0.0 || context.getController().getPovY() != 0.0;
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

}
