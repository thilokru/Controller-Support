package com.mhfs.controller.mappings.conditions;

public interface ICondition {

	public boolean check(GameContext context);
	
	public String toSaveString();
}
