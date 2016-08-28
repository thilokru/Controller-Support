package com.mhfs.controller.mappings;

import java.util.Map;

import org.lwjgl.input.Controller;
import com.mhfs.controller.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;

public class StickMap {
	
	private Map<String, Usage> axisUsageMap;
	private Map<String, EnumFacing.Axis> axisCoordMap;
	
	public void apply(Minecraft mc, Controller controller) {
		int stickCount = controller.getAxisCount();
		for(int id = 0; id < stickCount; id++) {
			String name = ConditionSerializationHelper.getNames().getAxisName(id);
			Usage usage = axisUsageMap.get(name);
			
			if(usage == null)
				continue;
			
			EnumFacing.Axis axis = axisCoordMap.get(name);
			float orig = controller.getAxisValue(id);
			float value = (float) (Math.pow(orig * Config.INSTANCE.getStickSensitivity(), 2) * 50);
			if(orig < 0.0F) value = -value;
			
			usage.apply(mc, value, axis);
		}
	}
	
	public interface IStick {
		public void apply(Minecraft mc, float value, EnumFacing.Axis axis);
	}
}
