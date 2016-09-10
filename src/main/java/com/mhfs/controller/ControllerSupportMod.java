package com.mhfs.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;

import com.mhfs.controller.mappings.ControllerMapping;
import com.mhfs.controller.mappings.actions.ActionRegistry;
import com.mhfs.controller.mappings.conditions.GameContext;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ControllerSupportMod.MODID, name = ControllerSupportMod.NAME, version = "1.0Alpha", clientSideOnly = true, canBeDeactivated = true)
public class ControllerSupportMod {
	
	public final static String MODID = "controller_support";
	public final static String NAME = "Controller Support";
	
	@Instance("controller_support")
	public static ControllerSupportMod INSTANCE;
	public static Logger LOG = LogManager.getLogger("Controller Support");
	
	public ModEventHandler handler;
	
	public ControllerSupportMod() {
		this.handler = new ModEventHandler();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Config.INSTANCE = new Config(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(handler);
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			LOG.error("Unable to load controllers", e);
		}
		handler.detectControllers();
		if(!Config.INSTANCE.hasController()) {
			return;
		}
		
		ActionRegistry.load();
		
		ControllerMapping mapping = ControllerMapping.loadFromConfig();
		mapping.init(new GameContext(Config.INSTANCE.getController()));
		Config.INSTANCE.setMapping(mapping);	
		Config.INSTANCE.save();
	}
	
	public void postInit(FMLPostInitializationEvent event) {
		if(!Config.INSTANCE.hasController()) {
			return;
		}
		Controllers.clearEvents();
	}
}
