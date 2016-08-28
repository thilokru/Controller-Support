package com.mhfs.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controllers;

import com.mhfs.controller.actions.ActionLeftClick;
import com.mhfs.controller.actions.ActionRegistry;
import com.mhfs.controller.actions.ActionRightClick;
import com.mhfs.controller.mappings.ControllerMapping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
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
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			LOG.error("Unable to load controllers", e);
		}
		handler.detectControllers();
		
		ActionRegistry.registerAction(new ActionLeftClick());
		ActionRegistry.registerAction(new ActionRightClick());
		
		ResourceLocation loc = Config.INSTANCE.getActionMappingLocation();
		IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
		ControllerMapping mapping = ControllerMapping.loadFromFile(loc, manager);
		Config.INSTANCE.setMapping(mapping);	
		Config.INSTANCE.save();
	}
	
	/*@EventHandler
	public void deactivate(FMLDeactivationEvent event) {
		e
	}*/

}
