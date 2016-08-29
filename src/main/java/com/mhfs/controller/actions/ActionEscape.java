package com.mhfs.controller.actions;

public class ActionEscape implements IAction{

	@Override
	public void run() {
		ActionEmulationHelper.startEscape();
	}

	@Override
	public void notRun() {
		ActionEmulationHelper.stopEscape();
	}

	@Override
	public String getName() {
		return "ESCAPE";
	}

}
