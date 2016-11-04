package com.mhfs.controller.mappings.controlls;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.conditions.GameContext;

public class DPadControll implements IControll<Pair<Float, Float>> {
	
	private boolean phantomProtection = false;
	private Direction direction = null;
	
	public DPadControll(String args) {
		args = args.trim();
		if(args.length() == 0) return;
		this.direction = Direction.valueOf(args);
	}

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
		if(direction != null){ 
			return direction.isTriggered(controller.getPovX(), controller.getPovY());
		} else {
			return controller.getPovX() != 0.0 || controller.getPovY() != 0.0;
		}
	}

	@Override
	public String toSaveString() {
		if(direction != null) {
			return String.format("DPAD(%s)", direction.toString());
		}
		return "DPAD()";
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
		if(direction != null) {
			return direction.getControllName();
		} else {
			return "DPAD";
		}
	}

	@Override
	public void enablePhantomProtection() {
		this.phantomProtection = true;
	}

	@Override
	public boolean shouldEnablePhantomProtection(IControll<?> controll) {
		return controll instanceof DPadControll;
	}

	public enum Direction {
		UP(0F, 0.5F, "DU"),
		DOWN(0F, -0.5F, "DR"),
		LEFT(-0.5F, 0F, "DL"),
		RIGHT(0.5F, 0F, "DR");
		
		private float xThresh, yThresh;
		private String controllName;
		
		private Direction(float x, float y, String name) {
			this.xThresh = x;
			this.yThresh = y;
			this.controllName = name;
		}

		public boolean isTriggered(float povX, float povY) {
			boolean result = true;
			if(xThresh > 0 && povX < xThresh) {
				result = false;
			}
			if(xThresh < 0 && povX > xThresh) {
				result = false;
			}
			if(yThresh > 0 && povY < yThresh) {
				result = false;
			}
			if(yThresh < 0 && povY > yThresh) {
				result = false;
			}
			return result;
		}
		
		public String getControllName() {
			return controllName;
		}
	}
}
