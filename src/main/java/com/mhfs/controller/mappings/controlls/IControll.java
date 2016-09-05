package com.mhfs.controller.mappings.controlls;

import com.mhfs.controller.mappings.conditions.GameContext;
import com.mhfs.controller.mappings.conditions.ICondition;

public interface IControll extends ICondition{
	
	public boolean hasAdditionalData();
	
	public Object getData(GameContext context);
}
