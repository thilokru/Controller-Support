package com.mhfs.controller.mappings.conditions;

import net.minecraft.client.gui.inventory.GuiContainer;

public class ContainerCondition implements ICondition {

	@Override
	public boolean check(GameContext context) {
		return context.isScreenInstanceOf(GuiContainer.class);
	}

	@Override
	public String toSaveString() {
		return "CONTAINER()";
	}

}
