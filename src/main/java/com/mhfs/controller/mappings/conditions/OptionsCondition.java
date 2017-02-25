package com.mhfs.controller.mappings.conditions;

import com.mhfs.controller.config.Configuration;

public class OptionsCondition implements ICondition{

	@Override
	public boolean check(GameContext context) {
		if(context.getCurrentScreen() == null)return false;
		String checkName = context.getCurrentScreen().getClass().getCanonicalName();
		for(String clazzName : Configuration.optionClasses) {
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
