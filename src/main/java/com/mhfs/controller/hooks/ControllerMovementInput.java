package com.mhfs.controller.hooks;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInputFromOptions;

public class ControllerMovementInput extends MovementInputFromOptions {
	
	private float forward, strafe;
	private boolean jumpOv, sneakOv;
	
	public ControllerMovementInput(GameSettings gameSettingsIn) {
		super(gameSettingsIn);
	}
	
	@Override
	public void updatePlayerMoveState(){
		super.updatePlayerMoveState();
		this.jump = this.jump || jumpOv;
		
		if(this.sneak) {
			this.moveForward /= 0.3;
			this.moveStrafe /= 0.3;
		}
		this.sneak = this.sneak || sneakOv;
		
		this.moveForward = clamp(this.moveForward + forward, -1.0F, 1.0F);
		this.moveStrafe = clamp(this.moveStrafe + strafe, -1.0F, 1.0F);
		
		if(this.sneak) {
			this.moveForward *= 0.3;
			this.moveStrafe *= 0.3;
		}
		
		forward = 0.0F;
		strafe = 0.0F;
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

	public void setForwardMotion(float motion) {
		this.forward = motion;
	}
	
	public void setStrafeMotion(float motion) {
		this.strafe = motion;
	}
	
	public void jump(){
		this.jumpOv = true;
	}
	
	public void notJump(){
		this.jumpOv = false;
	}
	
	public void sneak(){
		this.sneakOv = true;
	}
	
	public void notSneak(){
		this.sneakOv = false;
	}

}
