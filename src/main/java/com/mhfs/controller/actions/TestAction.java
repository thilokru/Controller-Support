package com.mhfs.controller.actions;

import com.mhfs.controller.ControllerSupportMod;

public class TestAction implements IAction {

	@Override
	public void run() {
		ControllerSupportMod.LOG.info("Action interpreted");
	}

	@Override
	public String getName() {
		return "TEST";
	}

}
