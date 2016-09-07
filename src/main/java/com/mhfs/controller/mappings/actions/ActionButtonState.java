package com.mhfs.controller.mappings.actions;

import net.minecraft.client.resources.I18n;

public class ActionButtonState implements IAction {

	private boolean state;
	private String name, desc;
	
	public ActionButtonState(String name, String desc) {
		this.state = false;
		this.name = name;
	}
	
	@Override
	public void run() {
		this.state = true;
	}

	@Override
	public void notRun() {
		this.state = false;
	}

	@Override
	public String getActionName() {
		return name;
	}

	@Override
	public String getActionDescription() {
		return I18n.format(desc);
	}
	
	public boolean getState() {
		return state;
	}
}
