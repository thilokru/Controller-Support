package com.mhfs.controller.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ActionKeyBind implements IAction{
	
	private KeyBinding binding;
	
	public ActionKeyBind(KeyBinding binding){
		this.binding = binding;
	}

	@Override
	public void run() {
		KeyBinding.setKeyBindState(binding.getKeyCode(), true);
		if(shouldTick()){
			KeyBinding.onTick(binding.getKeyCode());
		}
	}

	private boolean shouldTick() {
		String desc = binding.getKeyDescription();
		if(desc.equals("key.use")){
			return false;
		}
		return true;
	}

	@Override
	public void notRun() {
		KeyBinding.setKeyBindState(binding.getKeyCode(), false);
	}

	@Override
	public String getName() {
		return "KB(" + binding.getKeyDescription() + ")";
	}

	public static void registerActions() {
		for (KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			ActionRegistry.registerAction(new ActionKeyBind(binding));
		}
	}
}
