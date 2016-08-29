package com.mhfs.controller.helper;

import java.util.Map;

import com.mhfs.controller.actions.ActionKeyBind;
import com.mhfs.controller.actions.ActionRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class KeyBindHelper {
	
	private static Map<String, KeyBinding> bindings;
	
	private static void init() {
		for(KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			bindings.put(binding.getKeyDescription(), binding);
		}
	}
	
	public static void setState(String name, boolean pressed) {
		KeyBinding binding = bindings.get(name);
		KeyBinding.setKeyBindState(binding.getKeyCode(), pressed);
	}

	public static void registerActions() {
		init();
		for(String name : bindings.keySet()) {
			ActionRegistry.registerAction(new ActionKeyBind(name));
		}
	}

}
