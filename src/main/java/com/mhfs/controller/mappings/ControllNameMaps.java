package com.mhfs.controller.mappings;

import java.util.Map;

import com.google.common.collect.HashBiMap;

public class ControllNameMaps {
	

	private Map<String, Integer> buttons;
	private Map<String, Integer> axes;
	
	private volatile Map<Integer, String> inverseButtons;
	private volatile Map<Integer, String> inverseAxes;
	
	public ControllNameMaps(Map<String, Integer> buttons, Map<String, Integer> axes) {
		this.buttons = buttons;
		this.axes = axes;
	}
	
	public void buildInverted() {
		this.inverseButtons = HashBiMap.<String, Integer>create(buttons).inverse();
		this.inverseAxes = HashBiMap.<String, Integer>create(axes).inverse();
	}
	
	public String getButtonName(int controllID) {
		return inverseButtons.get(controllID);
	}
	
	public int getButtonID(String controllName) {
		return buttons.get(controllName);
	}
	
	public String getAxisName(int controllID) {
		return inverseAxes.get(controllID);
	}
	
	public int getAxisID(String axisName) {
		return axes.get(axisName);
	}
}
