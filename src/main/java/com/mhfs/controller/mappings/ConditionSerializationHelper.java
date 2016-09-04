package com.mhfs.controller.mappings;

import org.lwjgl.input.Controller;

import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.mappings.conditions.AndCondition;
import com.mhfs.controller.mappings.conditions.ButtonCondition;
import com.mhfs.controller.mappings.conditions.ICondition;
import com.mhfs.controller.mappings.conditions.IngameCondition;
import com.mhfs.controller.mappings.conditions.NotCondition;
import com.mhfs.controller.mappings.conditions.OrCondition;
import com.mhfs.controller.mappings.conditions.ScreenCondition;
import com.mhfs.controller.mappings.conditions.StickCondition;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ConditionSerializationHelper {
	
	private static ControllInfo names;

	public static String toString(ICondition value) {
		return value.toSaveString();
	}

	public static ICondition fromString(String serializedCondition) {
		int openingIndex = serializedCondition.indexOf('(');
		int closingIndex = serializedCondition.lastIndexOf(')');
		if(openingIndex < 0 || closingIndex < 0 || openingIndex >= serializedCondition.length() || closingIndex > serializedCondition.length()){
			throw new RuntimeException(String.format("The condition '%s' has faulty parantheses!", serializedCondition));
		}
		String op = serializedCondition.substring(0, openingIndex).toLowerCase();
		String args = serializedCondition.substring(openingIndex + 1, closingIndex);
		switch(op) {
		case "and":
			return new AndCondition(args);
		case "or":
			return new OrCondition(args);
		case "not":
			return new NotCondition(args);
		case "screen":
			return new ScreenCondition(args);
		case "button":
			return new ButtonCondition(args);
		case "stick":
			return new StickCondition(args);
		case "ingame":
			return new IngameCondition();
		default:
			throw new RuntimeException(String.format("Unknown condition type %s", op));
		}
	}

	public static void updateButtonMap(Controller controller, IResourceManager manager) {
		String name = controller.getName();
		if(name.toLowerCase().contains("xbox")) {
			setMap("xbox");
		} else {
			setMap(name);
		}
		names = ControllInfo.load(Config.INSTANCE.getButtonNameMapLocation(), manager);
	}
	
	private static void setMap(String mapName) {
		Config.INSTANCE.setButtonNameMapLocation(new ResourceLocation(ControllerSupportMod.MODID, String.format("maps/%s.map", mapName)));
	}
	
	public static ControllInfo getNames() {
		return names;
	}
	
}
