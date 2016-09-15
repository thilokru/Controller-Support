package com.mhfs.controller.mappings.conditions;

public class AndCondition implements ICondition {
	
	private ICondition[] conditions;
	
	public AndCondition(ICondition... conditions) {
		super();
		this.conditions = conditions;
	}

	public AndCondition(String args) {
		String[] subConditions = ConditionSerializationHelper.parantheticalLeveledStringSplit(args);
		this.conditions = new ICondition[subConditions.length];
		for(int i = 0; i < subConditions.length; i++) {
			String subCon = subConditions[i].trim();
			this.conditions[i] = ConditionSerializationHelper.fromString(subCon);
		}
	}

	@Override
	public boolean check(GameContext context) {
		boolean check = true;
		for(ICondition condition : conditions) {
			check = check && condition.check(context);
		}
		return check;
	}

	@Override
	public String toSaveString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AND(");
		for(ICondition condition : conditions) {
			builder.append(condition.toSaveString());
			builder.append(",");
		}
		builder.deleteCharAt(builder.lastIndexOf(","));
		builder.append(")");
		return builder.toString();
	}

}
