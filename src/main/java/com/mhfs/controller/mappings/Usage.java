package com.mhfs.controller.mappings;

import net.minecraft.client.resources.I18n;

public enum Usage{
	MOVEMENT("usage.movement"),
	VIEW("usage.view"),
	MOUSE("usage.mouse"),
	TEXT("usage.text");
	
	private String translateKey;
	
	private Usage(String translateKey) {
		this.translateKey = translateKey;
	}

	public String getDescription() {
		return I18n.format(translateKey);
	}
}