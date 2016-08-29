package com.mhfs.controller.actions;

import com.mhfs.controller.helper.KeyBindHelper;

public class ActionKeyBind implements IAction{
	
	private String name;
	
	public ActionKeyBind(String name){
		this.name = name;
	}

	@Override
	public void run() {
		KeyBindHelper.setState(name, true);
	}

	@Override
	public void notRun() {
		KeyBindHelper.setState(name, false);
	}

	@Override
	public String getName() {
		return "KEY_BIND(" + name.toUpperCase() + ")";
	}

}
