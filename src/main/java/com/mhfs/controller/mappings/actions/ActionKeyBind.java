package com.mhfs.controller.mappings.actions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

public class ActionKeyBind implements IAction{
	
	private KeyBinding binding;
	
	public ActionKeyBind(KeyBinding binding){
		this.binding = binding;
	}

	@Override
	public void run() {
		if(!binding.isKeyDown())
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
		if(binding.isKeyDown())
			KeyBinding.setKeyBindState(binding.getKeyCode(), false);
	}

	@Override
	public String getActionName() {
		return "KB(" + binding.getKeyDescription() + ")";
	}
	
	@Override
	public String getActionDescription() {
		return I18n.format(binding.getKeyDescription());
	}

	public static void registerActions() {
		for (KeyBinding binding : Minecraft.getMinecraft().gameSettings.keyBindings) {
			ActionRegistry.registerAction(new ActionKeyBind(binding));
		}
	}

}
