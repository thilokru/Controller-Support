package com.mhfs.controller.hooks;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

import com.mhfs.controller.config.State;
import com.mhfs.controller.mappings.StickConfig;
import com.mhfs.controller.mappings.Usage;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;

public class ControllerMovementInput extends MovementInputFromOptions {

	public ControllerMovementInput(GameSettings gameSettingsIn) {
		super(gameSettingsIn);
	}
	
	@Override
	public void updatePlayerMoveState(){
		super.updatePlayerMoveState();
		
		Controller controller = State.controller;
		StickConfig cfg = State.mapping.getStick(Usage.MOVEMENT);
		if(cfg == null)return;
		Pair<Float, Float> movement = cfg.getData(controller);
		
		if(this.sneak) {
			this.moveForward /= 0.3;
			this.moveStrafe /= 0.3;
		}
		
		this.moveForward = clamp(this.moveForward + movement.getRight(), -1.0F, 1.0F);
		this.moveStrafe = clamp(this.moveStrafe + movement.getLeft(), -1.0F, 1.0F);
		
		if(this.sneak) {
			this.moveForward *= 0.3;
			this.moveStrafe *= 0.3;
		}
	}
	
	private float clamp(float val, float min, float max) {
		if(val > max) {
			return max;
		} else if(val < min) {
			return min;
		} else {
			return val;
		}
	}
}
