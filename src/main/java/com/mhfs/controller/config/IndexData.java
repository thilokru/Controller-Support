package com.mhfs.controller.config;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mhfs.controller.mappings.GsonHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class IndexData {

	private static IndexData INSTANCE;
	
	private Map<String, ControllerCfg> regexToController;
	private IndexData(){}
	
	public static IndexData get() {
		if(INSTANCE == null) {
			try {
				INSTANCE = IndexData.load(new ResourceLocation("controller_support:maps/mappingmap.map"), Minecraft.getMinecraft().getResourceManager());
			} catch (IOException e) {
				Throwables.propagate(e);
			}
		}
		return INSTANCE;
	}
	
	private static IndexData load(ResourceLocation resourceLocation, IResourceManager resourceManager) throws IOException {
		Gson gson = GsonHelper.getGson(new GsonBuilder());
		IndexData data = new IndexData();
		Reader reader = new InputStreamReader(resourceManager.getResource(resourceLocation).getInputStream());
		Type type = new TypeToken<Map<String, ControllerCfg>>(){}.getType();
		data.regexToController = gson.fromJson(reader, type);
		return data;
	}
	
	public ControllerCfg controller(String name) {
		for(String regex : regexToController.keySet()) {
			Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
			if(pattern.matcher(name).find()) {
				return regexToController.get(regex);
			}
		}
		throw new IllegalStateException(String.format("Unknown controller type: '%s'. Contact the mod author or add the controller to the configs.", name));
	}

	public class ControllerCfg {
		private ResourceLocation controlls;
		private Map<String, ResourceLocation> mappings;
		private String selected;
		
		public ResourceLocation getActionMapping() {
			return mappings.get(selected);
		}
		
		public ResourceLocation getButtonIDMapping() {
			return controlls;
		}
	}
}
