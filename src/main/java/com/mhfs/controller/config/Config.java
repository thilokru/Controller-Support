package com.mhfs.controller.config;

import org.lwjgl.input.Controller;

import com.mhfs.controller.config.IndexData.ControllerCfg;
import com.mhfs.controller.mappings.ControllerMapping;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config extends Configuration{
	
	public static Config INSTANCE;
	private final static String[] DEFAULT_OPTION_CLASSES = new String[]{
			"net\\.minecraft\\.client\\.gui\\.((.*Options.*)|(.*Settings.*)|(.*Customize.*)|GuiControls|GuiLanguage|GuiScreenResourcePacks)",
			"net.minecraftforge.fml.client.GuiModList",
			"net.minecraftforge.fml.client.config.GuiConfig",
			"net.minecraftforge.fml.client.config.GuiEditArray",
			"net.minecraftforge.fml.client.config.GuiSelectString"
		};
	
	private float analogSensitivity = 0.5F;
	private boolean invertedLookAxes;
	private ControllerMapping mapping;
	private Controller controller;
	private ControllerCfg controllerConfig;
	private String[] optionClasses;
	private boolean debugController;
	
	public Config(FMLPreInitializationEvent event) {
		super(event.getSuggestedConfigurationFile());
		this.load();
		this.save();
	}
	
	@SubscribeEvent
	public void handleConfigChange(ConfigChangedEvent event) {
		this.save();
		this.load();
	}
	
	public void load(){
		super.load();
		this.analogSensitivity = this.getFloat("analogSensitivity", CATEGORY_GENERAL, 0.5F, 0F, 1.0F, "The sensitivity of the analog sticks");
		this.invertedLookAxes = this.getBoolean("invertedAxes", CATEGORY_GENERAL, false, "Whether the view axes are inverted");
		this.optionClasses = this.getStringList("optionClasses", CATEGORY_GENERAL, DEFAULT_OPTION_CLASSES, "The classes that trigger the OPTIONS()-condition. Has regex-support.");
		this.debugController = this.getBoolean("debugControllerInput", CATEGORY_GENERAL, false, "When set to true, button and axis ids are printed into the logfiles.");
	}
	
	public float getStickSensitivity() {
		return analogSensitivity;
	}
	
	public void setStickSensitivity(float sensitivity) {
		this.analogSensitivity = sensitivity;
		this.get(CATEGORY_GENERAL, "analogSensitity", 0.5F).set(sensitivity);
	}

	public ControllerMapping getMapping() {
		return mapping;
	}
	
	public void setMapping(ControllerMapping mapping) {
		this.mapping = mapping;
	}

	public boolean hasInvertedLookAxis() {
		return invertedLookAxes;
	}
	
	public void setHasInvertedLookAxis(boolean inverted) {
		this.invertedLookAxes = inverted;
		this.get(CATEGORY_GENERAL, "invertedAxes", false).set(inverted);
	}

	public Controller getController() {
		return controller;
	}
	
	public boolean hasController() {
		return controller != null;
	}
	
	public void setController(Controller controller) {
		this.controller = controller;
	}

	public void setControllerConfig(ControllerCfg cfg) {
		this.controllerConfig = cfg;
	}
	
	public ControllerCfg getControllerConfig() {
		return this.controllerConfig;
	}
	
	public String[] getOptionsClasses() {
		return this.optionClasses;
	}

	public boolean shouldDebugInput() {
		return debugController;
	}

}
