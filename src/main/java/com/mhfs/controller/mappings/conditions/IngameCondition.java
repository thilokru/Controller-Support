package com.mhfs.controller.mappings.conditions;

public class IngameCondition implements ICondition{

	@Override
	public boolean check(GameContext context) {
		return context.isIngame();
	}

	@Override
	public String toSaveString() {
		return "INGAME";
	}

}
