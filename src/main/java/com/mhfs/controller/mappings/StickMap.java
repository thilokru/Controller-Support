package com.mhfs.controller.mappings;

import java.util.Map;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Mouse;

import com.mhfs.controller.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

public class StickMap {
	
	private Map<String, Usage> axisUsageMap;
	private Map<String, EnumFacing.Axis> axisCoordMap;
	
	public void apply(Minecraft mc, Controller controller) {
		int stickCount = controller.getAxisCount();
		for(int id = 0; id < stickCount; id++) {
			String name = controller.getAxisName(id);
			Usage usage = axisUsageMap.get(name);
			
			if(usage == null)
				continue;
			
			EnumFacing.Axis axis = axisCoordMap.get(name);
			float value = controller.getAxisValue(id) * Config.INSTANCE.getStickSensitivity();
			
			usage.apply(mc, value, axis);
		}
	}
	
	public interface IStick {
		public void apply(Minecraft mc, float value, EnumFacing.Axis axis);
	}
	
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
					Mouse.setCursorPosition(Mouse.getX(), (int) (Mouse.getY() + value));
					break;
				case X:
					Mouse.setCursorPosition((int) (Mouse.getX() + value), Mouse.getY());
					break;
				default:
					break;
				}
			}
		}
	}
}
