package com.mhfs.controller;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import com.mhfs.controller.hooks.ControllerMouseHelper;
import com.mhfs.controller.hooks.ControllerMovementInput;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * Handles the InputEvent (each Tick) to pull data from the controller and input to the game.
 * @author Thilo
 *
 */
public class ModEventHandler {
	
	private Controller controller;
	private boolean alreadyRun = false;
	
	public void detectControllers() {
		int count = Controllers.getControllerCount();
		Logger log = ControllerSupportMod.LOG;
		log.info(String.format("Found %d controller(s)!", count));
		for(int id = 0; id < count; id++) {
			Controller controller = Controllers.getController(id);
			log.info(String.format("Controller: %s", controller.getName()));
			Config.INSTANCE.setController(controller);
			this.controller = controller;//TODO: On first detection, ask user if he wants to use a controller.
		}
	}
	
	@SubscribeEvent
	public void handlePlayerMovementInput(LivingEvent.LivingUpdateEvent event) {
		if(!(event.getEntityLiving() instanceof EntityPlayerSP))return;
		if(((EntityPlayerSP) event.getEntityLiving()).movementInput instanceof ControllerMovementInput)return;
		Minecraft.getMinecraft().thePlayer.movementInput = new ControllerMovementInput(Minecraft.getMinecraft().gameSettings);
		Minecraft.getMinecraft().mouseHelper = new ControllerMouseHelper();
	}

	@SubscribeEvent
	public void handleGuiScreenEvent(GuiScreenEvent.DrawScreenEvent event) {
		handleTick();
	}
	
	@SubscribeEvent
	public void handleMouseTicks(MouseEvent event) {
		if(!alreadyRun) {
			handleTick();
			alreadyRun = true;
		}
	}
	
	@SubscribeEvent
	public void handleClientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END){
			alreadyRun = false;
		}
	}
	
	public void handleTick() {
		Config cfg = Config.INSTANCE;
		if(!(cfg.hasController() && controller != null))return;
		controller.poll();
		cfg.getMapping().applyMouse(Minecraft.getMinecraft(), controller);
		cfg.getMapping().applyButtons(Minecraft.getMinecraft(), controller);
	}
	
	@SubscribeEvent
	public void handleConfigChange(ConfigChangedEvent event) {
		Config.INSTANCE.save();
	}
}
