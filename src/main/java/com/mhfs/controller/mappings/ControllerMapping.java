package com.mhfs.controller.mappings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.actions.ActionEmulationHelper;
import com.mhfs.controller.actions.ActionRegistry;
import com.mhfs.controller.actions.IAction;
import com.mhfs.controller.mappings.conditions.ICondition;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class ControllerMapping implements IResourceManagerReloadListener{
	
	private final static Gson mappingLoader = new GsonBuilder().registerTypeHierarchyAdapter(ICondition.class, new TypeAdapter<ICondition>(){

		@Override
		public void write(JsonWriter out, ICondition value) throws IOException {
			out.value(ConditionSerializationHelper.toString(value));
		}

		@Override
		public ICondition read(JsonReader in) throws IOException {
			return ConditionSerializationHelper.fromString(in.nextString());
		}
		
	}).registerTypeHierarchyAdapter(IAction.class, new TypeAdapter<IAction>(){

		@Override
		public void write(JsonWriter out, IAction value) throws IOException {
			out.value(value.getName());
		}

		@Override
		public IAction read(JsonReader in) throws IOException {
			String actionName = in.nextString();
			IAction res = ActionRegistry.getAction(actionName);
			if(res == null) {
				throw new RuntimeException("Unknown action: " + actionName);
			}
			return res;
		}
		
	}).registerTypeHierarchyAdapter(StickConfig.class, new TypeAdapter<StickConfig>(){
		@Override
		public void write(JsonWriter out, StickConfig value) throws IOException {
			out.value(ConditionSerializationHelper.getNames().getStickName(value));
		}
		@Override
		public StickConfig read(JsonReader in) throws IOException {
			return ConditionSerializationHelper.getNames().getStick(in.nextString());
		}
	}).create();
	
	private Map<ICondition, IAction> buttonMap;
	private Map<ICondition, Map<Usage, StickConfig>> stickMap;
	private volatile ResourceLocation location;
	
	public void applyButtons(Minecraft mc, Controller controller) {
		for(Entry<ICondition, IAction> entry : buttonMap.entrySet()) {
			if(entry.getKey().check(mc, controller)) {
				entry.getValue().run();
			} else {
				entry.getValue().notRun();
			}
		}
	}
	
	public void applyMouse(Minecraft mc, Controller controller) {
		StickConfig cfg = getStick(mc, controller, Usage.MOUSE);
		if(cfg == null) return;
		Pair<Float, Float> input = cfg.getData(controller);
		ActionEmulationHelper.moveMouse(input.getLeft(), input.getRight());
	}
	
	public ResourceLocation getLocation(){
		return location;
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		ControllerMapping newMapping = loadFromFile(location, resourceManager);
		this.buttonMap = newMapping.buttonMap;
		this.stickMap = newMapping.stickMap;
	}
	
	public static ControllerMapping loadFromFile(ResourceLocation location, IResourceManager manager) {
		try {
			InputStream stream = manager.getResource(location).getInputStream();
			Config cfg = Config.INSTANCE;
			ConditionSerializationHelper.updateButtonMap(cfg.getController(), manager);
			ControllerMapping mapping = mappingLoader.fromJson(new InputStreamReader(stream), ControllerMapping.class);
			mapping.location = location;
			return mapping;
		} catch (IOException e) {
			ControllerSupportMod.LOG.error("Exception while loading mapping!", e);
		}
		return null;
	}

	public StickConfig getStick(Minecraft mc, Controller controller, Usage usage) {
		for(Entry<ICondition, Map<Usage, StickConfig>> entry : stickMap.entrySet()) {
			if(entry.getKey().check(mc, controller)) {
				return entry.getValue().get(usage);
			}
		}
		return null;
	}

}
