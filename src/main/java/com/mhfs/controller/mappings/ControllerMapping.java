package com.mhfs.controller.mappings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.mappings.actions.ActionEmulationHelper;
import com.mhfs.controller.mappings.actions.IAction;
import com.mhfs.controller.mappings.conditions.GameContext;
import com.mhfs.controller.mappings.conditions.ICondition;
import com.mhfs.controller.mappings.controlls.ButtonControll;
import com.mhfs.controller.mappings.controlls.StickControll;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class ControllerMapping implements IResourceManagerReloadListener{
	
	private final static Gson gson = GsonHelper.getExposedGson();
	
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
			ControllerMapping mapping = gson.fromJson(new InputStreamReader(stream), ControllerMapping.class);
			mapping.location = location;
			return mapping;
		} catch (IOException e) {
			ControllerSupportMod.LOG.error("Exception while loading mapping!", e);
		}
		return null;
	}

	public List<Pair<String, String>> getButtonFunctions() {
		return getButtonFunctions0(currentButtonMap, currentStickMap);
	}
	
	private List<Pair<String, String>> getButtonFunctions0(Map<ICondition, IAction> buttonMap, Map<Usage, StickConfig> stickMap) {
		List<Pair<String, String>> ret = Lists.<Pair<String, String>>newArrayList();
		for(Entry<ICondition, IAction> entry : buttonMap.entrySet()) {
			String con = "";
			if(entry.getKey() instanceof ButtonControll) {
				con = ((ButtonControll)entry.getKey()).getButtonName();
			} else if (entry.getKey() instanceof StickControll) {
				con = ((StickControll)entry.getKey()).getStickName();
			}
			ret.add(Pair.of(con, entry.getValue().getActionDescription()));
		}
		for(Entry<Usage, StickConfig> entry : stickMap.entrySet()) {
			ret.add(Pair.of(entry.getValue().getStickName(), entry.getKey().getDescription()));
		}
		return ret;
	}

	public List<Pair<String, String>> getIngameButtonFunctions() {
		GameContext ingame = GameContext.getIngameContext(this.context.getController());
		return getButtonFunctions0(select(buttonMap, ingame), select(stickMap, ingame));
	}

}
