package com.mhfs.controller.mappings.conditions;

import com.mhfs.controller.Config;

public class OptionsCondition implements ICondition{

	@Override
	public boolean check(GameContext context) {
		if(context.getCurrentScreen() == null)return false;
		String checkName = context.getCurrentScreen().getClass().getCanonicalName();
		for(String clazzName : Config.INSTANCE.getOptionsClasses()) {
			if(checkName.matches(clazzName))
				return true;
		}
		return false;
	}

	@Override
	public String toSaveString() {
		return "OPTIONS";
	}

}
