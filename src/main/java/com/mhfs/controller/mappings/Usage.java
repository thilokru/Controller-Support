package com.mhfs.controller.mappings;

import com.mhfs.controller.Config;
import com.mhfs.controller.actions.ActionEmulationHelper;
import com.mhfs.controller.mappings.StickMap.IStick;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

public enum Usage implements IStick{
	MOVEMENT {
		public void apply(Minecraft mc, float value, EnumFacing.Axis axis) {
			switch (axis) {
			case Y:
				mc.thePlayer.moveForward += value; 
				break;
			case X:
				mc.thePlayer.moveStrafing += value;
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
				mc.thePlayer.cameraYaw += value; 
				break;
			case X:
				mc.thePlayer.cameraPitch += value;
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