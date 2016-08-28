package com.mhfs.controller;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ControllerMapping;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Config extends Configuration{
	
	public static Config INSTANCE;
	
	private float analogSensitivity = 0.5F;
	private boolean invertedLookAxes;
	private ControllerMapping mapping;
	private Controller controller;
	private ResourceLocation buttonNameMap;
	private ResourceLocation mappingLocation;
	
	public Config(FMLPreInitializationEvent event) {
		super(event.getSuggestedConfigurationFile());
		this.load();
		this.save();
	}
	
	@SubscribeEvent
	public void handleConfigChange(ConfigChangedEvent event) {
		this.save();
	}
	
	public void load(){
		super.load();
		this.analogSensitivity = this.getFloat("analogSensitivity", CATEGORY_GENERAL, 0.5F, 0F, 1.0F, "The sensitivity of the analog sticks");
		this.invertedLookAxes = this.getBoolean("invertedAxes", CATEGORY_GENERAL, false, "Whether the view axes are inverted");
		this.mappingLocation = new ResourceLocation(this.getString("mappingLocation", CATEGORY_GENERAL, "controller_support:maps/mapping.cfg", "From where to load the buttons config."));
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

	public void setButtonNameMapLocation(ResourceLocation loc) {
		this.buttonNameMap = loc;
	}
	
	public ResourceLocation getButtonNameMapLocation(){
		return this.buttonNameMap;
	}

	public ResourceLocation getActionMappingLocation() {
		return mappingLocation;
	}

	public void setActionMappingLocation(ResourceLocation mappingLocation) {
		this.mappingLocation = mappingLocation;
		this.get(CATEGORY_GENERAL, "mappingLocation", "controller_support:maps/mapping.cfg").set(mappingLocation.toString());
	}

}
