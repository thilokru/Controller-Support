package com.mhfs.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mhfs.controller.mappings.actions.ActionRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
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
		ActionRegistry.load();
	}
}
