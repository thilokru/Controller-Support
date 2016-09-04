package com.mhfs.controller.mappings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Throwables;
import com.google.common.collect.HashBiMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhfs.controller.Config;
import com.mhfs.controller.textures.TextureHelper;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class ControllInfo {
	
	private final static Gson nameLoader = GsonHelper.getGson(new GsonBuilder());
	
	private static ControllInfo INSTANCE;
	
	private Map<String, Integer> buttons;
	private Map<String, Integer> axes;
	private Map<String, StickConfig.Uncompiled> sticks;
	private ResourceLocation texture;
	
	private volatile Map<Integer, String> inverseButtons;
	private volatile Map<Integer, String> inverseAxes;
	private volatile Map<String, StickConfig> sticksCompiled;
	private volatile TextureHelper textureHelper;
	
	public ControllInfo(Map<String, Integer> buttons, Map<String, Integer> axes) {
		this.buttons = buttons;
		this.axes = axes;
	}
	
	private void build() {
		this.inverseButtons = HashBiMap.<String, Integer>create(buttons).inverse();
		this.inverseAxes = HashBiMap.<String, Integer>create(axes).inverse();
		
		sticksCompiled = new HashMap<String, StickConfig>();
		for(Entry<String, StickConfig.Uncompiled> entry : sticks.entrySet()) {
			sticksCompiled.put(entry.getKey(), entry.getValue().compile(this));
		}
		this.textureHelper = TextureHelper.get(texture);
	}
	
	public static ControllInfo load(ResourceLocation location, IResourceManager manager) {
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(manager.getResource(Config.INSTANCE.getButtonNameMapLocation()).getInputStream());
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		@SuppressWarnings("serial")
		Type type = new TypeToken<ControllInfo>(){}.getType();
		ControllInfo data = null;
		try {
			data = nameLoader.fromJson(isr, type);
		} catch (Exception e) {
			throw new RuntimeException("Error loading controller info (aka button names):", e);
		}
		data.build();
		INSTANCE = data;
		return data;
	}
	
	public static ControllInfo get() {
		if(INSTANCE != null){
			return INSTANCE;
		} else {
			throw new RuntimeException("ControllInfo hasn't been loaded yet but is already querried!");
		}
	}
	
	public TextureHelper getTextureHelper() {
		return textureHelper;
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
