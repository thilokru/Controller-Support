package com.mhfs.controller.mappings.actions;

import org.apache.commons.lang3.tuple.Pair;

public class ActionListSelect extends ActionToEvent<Pair<Float, Float>>{

	@Override
	public String getActionName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActionDescription() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean buttonDown(Pair<Float, Float> Arg) {
		
		return true;
	}

	@Override
	public void buttonDown() {
		throw new RuntimeException("ActionListSelect requires arguments!");
	}

	@Override
	public void buttonUp() {}

}
