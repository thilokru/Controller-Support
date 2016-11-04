package com.mhfs.controller.mappings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.mappings.actions.ActionEmulationHelper;
import com.mhfs.controller.mappings.actions.IAction;
import com.mhfs.controller.mappings.actions.IParametrizedAction;
import com.mhfs.controller.mappings.conditions.GameContext;
import com.mhfs.controller.mappings.conditions.ICondition;
import com.mhfs.controller.mappings.controlls.IControll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class ControllerMapping implements IResourceManagerReloadListener {

	private final static Gson gson = GsonHelper.getExposedGson();
	private final static GameContext context = new GameContext();

	@Expose
	private Map<ICondition, Map<IControll<?>, IAction>> buttonMap;
	@Expose
	private Map<ICondition, Map<Usage, StickConfig>> stickMap;

	private volatile ResourceLocation location;
	private volatile Map<Usage, StickConfig> currentStickMap;
	private volatile Map<IControll<?>, IAction> currentButtonMap;
	private volatile boolean forcedPhantomProtection = false;

	public void apply() {
		if (context == null) {
			throw new IllegalStateException("ControllerMapping hasn't been initialized yet! (Hint: Call init() with a GameContext Object)");
		}
		if (context.update()) {
			Map<IControll<?>, IAction> oldButtonMap = currentButtonMap;

			currentButtonMap = select(buttonMap, context);
			if (currentButtonMap == null) {
				currentButtonMap = Maps.<IControll<?>, IAction> newHashMap();
			}

			if (oldButtonMap != null) {
				for (IControll<?> newAction : currentButtonMap.keySet()) {
					if(forcedPhantomProtection) { 
						newAction.enablePhantomProtection();
					}else{
						for (IControll<?> oldAction : oldButtonMap.keySet()) {
							if (newAction.shouldEnablePhantomProtection(oldAction)) {
								newAction.enablePhantomProtection();
							}
						}
					}
				}
			}
			forcedPhantomProtection = false;

			currentStickMap = select(stickMap, context);
			if (currentStickMap == null) {
				currentStickMap = Maps.<Usage, StickConfig> newHashMap();
			}
		} else {
			applyMouse();
			applyButtons();
		}
	}

	private static <T extends ICondition, V> V select(Map<T, V> input, GameContext context) {
		for (Entry<T, V> entry : input.entrySet()) {
			if (entry.getKey().check(context)) {
				return entry.getValue();
			}
		}
		return null;
	}

	private void applyButtons() {
		for (Entry<IControll<?>, IAction> entry : currentButtonMap.entrySet()) {
			IControll<?> controll = entry.getKey();
			IAction action = entry.getValue();
			handleControll(controll, action);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void handleControll(IControll<T> controll, IAction action) {
		if (controll.check(ControllerMapping.context)) {
			if (controll.hasAdditionalData() && action instanceof IParametrizedAction<?>) {
				try {
					((IParametrizedAction<T>) action).run(controll.getData(context));
				} catch (ClassCastException e) {
					throw new RuntimeException("Invalid argument for action! " + action.getActionName() + " " + controll.getControllName(), e);
				}
			} else {
				action.run();
			}
		} else {
			action.notRun();
		}
	}

	private void applyMouse() {
		StickConfig cfg = getStick(Usage.MOUSE);
		if (cfg == null)
			return;
		Pair<Float, Float> input = cfg.getData(context.getController());
		float dx = (float) (Math.pow(input.getLeft(), 3) * 25);
		float dy = (float) (Math.pow(input.getRight(), 3) * 25);
		ActionEmulationHelper.moveMouse(-dx, -dy);
	}

	public StickConfig getStick(Usage usage) {
		return currentStickMap.get(usage);
	}

	public ResourceLocation getLocation() {
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
			ControllInfo.updateButtonMap(cfg.getController(), manager);
			ControllerMapping mapping = gson.fromJson(new InputStreamReader(stream), ControllerMapping.class);
			mapping.location = location;
			return mapping;
		} catch (IOException e) {
			ControllerSupportMod.LOG.error("Exception while loading mapping!", e);
		}
		return null;
	}

	public static ControllerMapping loadFromConfig() {
		ResourceLocation loc = Config.INSTANCE.getActionMappingLocation();
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		return loadFromFile(loc, manager);
	}

	public List<Pair<String, String>> getButtonFunctions() {
		return getButtonFunctions0(currentButtonMap, currentStickMap);
	}

	private List<Pair<String, String>> getButtonFunctions0(Map<IControll<?>, IAction> buttonMap, Map<Usage, StickConfig> stickMap) {
		List<Pair<String, String>> ret = Lists.<Pair<String, String>> newArrayList();
		for (Entry<IControll<?>, IAction> entry : buttonMap.entrySet()) {
			String con = entry.getKey().getControllName();
			ret.add(Pair.of(con, entry.getValue().getActionDescription()));
		}
		for (Entry<Usage, StickConfig> entry : stickMap.entrySet()) {
			ret.add(Pair.of(entry.getValue().getStickName(), entry.getKey().getDescription()));
		}
		return ret;
	}

	public List<Pair<String, String>> getIngameButtonFunctions() {
		GameContext ingame = GameContext.getIngameContext();
		return getButtonFunctions0(select(buttonMap, ingame), select(stickMap, ingame));
	}

	public void forcePhantomProtection() {
		this.forcedPhantomProtection = true;
	}

}
