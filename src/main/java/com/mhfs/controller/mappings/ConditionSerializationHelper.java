package com.mhfs.controller.mappings;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import org.lwjgl.input.Controller;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	
	private final static Gson nameLoader = new GsonBuilder().enableComplexMapKeySerialization().create();
	
	private static ControllNameMaps names;

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
		try {
			InputStreamReader isr = new InputStreamReader(manager.getResource(Config.INSTANCE.getButtonNameMapLocation()).getInputStream());
			@SuppressWarnings("serial")
			Type type = new TypeToken<ControllNameMaps>(){}.getType();
			names = nameLoader.fromJson(isr, type);
			names.build();
		} catch (Exception e) {
			ControllerSupportMod.LOG.error("Error loading button map!", e);
		}
	}
	
	private static void setMap(String mapName) {
		Config.INSTANCE.setButtonNameMapLocation(new ResourceLocation(ControllerSupportMod.MODID, String.format("maps/%s.map", mapName)));
	}
	
	public static ControllNameMaps getNames() {
		return names;
	}
	
}
