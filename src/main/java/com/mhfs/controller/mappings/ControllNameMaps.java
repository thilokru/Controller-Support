package com.mhfs.controller.mappings;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.HashBiMap;

public class ControllNameMaps {
	

	private Map<String, Integer> buttons;
	private Map<String, Integer> axes;
	private Map<String, StickConfig.Uncompiled> sticks;
	
	private volatile Map<Integer, String> inverseButtons;
	private volatile Map<Integer, String> inverseAxes;
	private volatile Map<String, StickConfig> sticksCompiled;
	
	public ControllNameMaps(Map<String, Integer> buttons, Map<String, Integer> axes) {
		this.buttons = buttons;
		this.axes = axes;
	}
	
	public void build() {
		this.inverseButtons = HashBiMap.<String, Integer>create(buttons).inverse();
		this.inverseAxes = HashBiMap.<String, Integer>create(axes).inverse();
		
		sticksCompiled = new HashMap<String, StickConfig>();
		for(Entry<String, StickConfig.Uncompiled> entry : sticks.entrySet()) {
			sticksCompiled.put(entry.getKey(), entry.getValue().compile(this));
		}
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
	
	public StickConfig getStick(String name) {
		return sticksCompiled.get(name);
	}
	
	public String getStickName(StickConfig cfg) {
		return HashBiMap.create(sticksCompiled).inverse().get(cfg);
	}
}
