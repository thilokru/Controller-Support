package com.mhfs.controller.mappings.controlls;

import com.mhfs.controller.mappings.conditions.GameContext;

public interface IControll {
	
	public boolean check(GameContext context);
	
	public String toSaveString();
	
	public boolean hasAdditionalData();
	
	public Object getData(GameContext context);

	public String getControllName();
}
