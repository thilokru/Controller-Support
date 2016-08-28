package com.mhfs.controller.mappings.conditions;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ConditionSerializationHelper;

import net.minecraft.client.Minecraft;

public class OrCondition implements ICondition{

	private ICondition[] conditions;
	
	public OrCondition(ICondition... conditions) {
		this.conditions = conditions;
	}
	
	public OrCondition(String args) {
		String[] subConditions = StringSplitHelper.parantheticalLeveledStringSplit(args);
		this.conditions = new ICondition[subConditions.length];
		for(int i = 0; i < subConditions.length; i++) {
			String subCon = subConditions[i].trim();
			this.conditions[i] = ConditionSerializationHelper.fromString(subCon);
		}
	}

	@Override
	public boolean check(Minecraft mc, Controller controller) {
		boolean check = false;
		for(ICondition condition : conditions) {
			check = check || condition.check(mc, controller);
		}
		return check;
	}
	
	@Override
	public String toSaveString() {
		StringBuilder builder = new StringBuilder();
		builder.append("OR(");
		for(ICondition condition : conditions) {
			builder.append(condition.toSaveString());
			builder.append(",");
		}
		builder.deleteCharAt(builder.lastIndexOf(","));
		builder.append(")");
		return builder.toString();
	}
}
