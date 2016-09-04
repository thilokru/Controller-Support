package com.mhfs.controller.mappings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.actions.ActionEmulationHelper;
import com.mhfs.controller.actions.ActionRegistry;
import com.mhfs.controller.actions.IAction;
import com.mhfs.controller.mappings.conditions.GameContext;
import com.mhfs.controller.mappings.conditions.ICondition;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class ControllerMapping implements IResourceManagerReloadListener{
	
	private final static Gson mappingLoader = new GsonBuilder().enableComplexMapKeySerialization().excludeFieldsWithoutExposeAnnotation()
	 .registerTypeHierarchyAdapter(ICondition.class, new TypeAdapter<ICondition>(){

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
	
	@Expose
	private Map<ICondition, Map<ICondition, IAction>> buttonMap;
	@Expose
	private Map<ICondition, Map<Usage, StickConfig>> stickMap;
	
	private volatile ResourceLocation location;
	private volatile GameContext context;
	private volatile Map<Usage, StickConfig> currentStickMap;
	private volatile Map<ICondition, IAction> currentButtonMap;
	
	public void init(GameContext context) {
		this.context = context;
	}
	
	public void apply() {
		if(context.update()) {
			currentStickMap = select(stickMap, context);
			currentButtonMap = select(buttonMap, context);
		}
		applyMouse();
		applyButtons();
	}
	
	private static <T extends ICondition, V> V select(Map<T, V> input, GameContext context) {
		for(Entry<T, V> entry : input.entrySet()) {
			if(entry.getKey().check(context)) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	private void applyButtons() {
		for(Entry<ICondition, IAction> entry : currentButtonMap.entrySet()) {
			if(entry.getKey().check(this.context)) {
				entry.getValue().run();
			} else {
				entry.getValue().notRun();
			}
		}
	}
	
	private void applyMouse() {
		StickConfig cfg = getStick(Usage.MOUSE);
		if(cfg == null) return;
		Pair<Float, Float> input = cfg.getData(context.getController());
		float dx = (float) (Math.pow(input.getLeft(), 3) * 25);
		float dy = (float) (Math.pow(input.getRight(), 3) * 25);
		ActionEmulationHelper.moveMouse(-dx, -dy);
	}
	
	public StickConfig getStick(Usage usage) {
		return currentStickMap.get(usage);
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

}
