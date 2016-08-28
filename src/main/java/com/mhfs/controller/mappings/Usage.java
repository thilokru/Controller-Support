package com.mhfs.controller.mappings;

import com.mhfs.controller.Config;
import com.mhfs.controller.actions.ActionEmulationHelper;
import com.mhfs.controller.hooks.ControllerMovementInput;
import com.mhfs.controller.mappings.StickMap.IStick;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovementInput;

public enum Usage implements IStick{
	MOVEMENT {
		public void apply(Minecraft mc, float value, EnumFacing.Axis axis) {
			MovementInput input = mc.thePlayer.movementInput;
			if(!(input instanceof ControllerMovementInput))return;
			ControllerMovementInput cmi = (ControllerMovementInput) input;
			switch (axis) {
			case Y:
				cmi.setForwardMotion(-value);
				break;
			case X:
				cmi.setStrafeMotion(-value);
				break;
			default:
				break;
			}
		}
	},
	VIEW {
		public void apply(Minecraft mc, float value, EnumFacing.Axis axis) {
			boolean invertLookAxis = Config.INSTANCE.hasInvertedLookAxis();
			if(invertLookAxis) {
				value = -value;
			}
			switch (axis) {
			case Y:
				mc.getRenderViewEntity().rotationPitch += value;
				break;
			case X:
				mc.getRenderViewEntity().rotationYaw += value;
				break;
			default:
				break;
			}
		}			
	},
	MOUSE {
		
		public void apply(Minecraft mc, float value, EnumFacing.Axis axis) {
			boolean invertLookAxis = Config.INSTANCE.hasInvertedLookAxis();
			if(invertLookAxis) {
				value = -value;
			}
			switch (axis) {
			case Y:
				ActionEmulationHelper.moveMouse(0, value);
				break;
			case X:
				ActionEmulationHelper.moveMouse(value, 0);
				break;
			default:
				break;
			}
		}
	};
}