package com.mhfs.controller.mappings.conditions;

import com.mhfs.controller.mappings.ConditionSerializationHelper;

public class NotCondition implements ICondition{

	private ICondition condition;
	
	public NotCondition(ICondition condition) {
		this.condition = condition;
	}
	
	public NotCondition(String args) {
		this.condition = ConditionSerializationHelper.fromString(args.trim());
	}

	@Override
	public boolean check(GameContext context) {
		return !condition.check(context);
	}
	
	@Override
	public String toSaveString() {
		return String.format("NOT(%s)", condition.toSaveString());
	}
}
