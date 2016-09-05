package com.mhfs.controller;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mhfs.controller.gui.GuiScreenControllerHelp;
import com.mhfs.controller.gui.LabelButtonInfo;
import com.mhfs.controller.hooks.ControllerMouseHelper;
import com.mhfs.controller.hooks.ControllerMovementInput;
import com.mhfs.controller.mappings.actions.ActionButtonChange;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
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
	
	public void detectControllers() {
		int count = Controllers.getControllerCount();
		Logger log = ControllerSupportMod.LOG;
		log.info(String.format("Found %d controller(s)!", count));
		for(int id = 0; id < count; id++) {
			Controller controller = Controllers.getController(id);
			log.info(String.format("Controller: %s", controller.getName()));
			Config.INSTANCE.setController(controller); //TODO: ask user if he'd likes to use the controller.
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
	public void handleClientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.START){
			handleTick();
		}
	}
	
	public void handleTick() {
		Config cfg = Config.INSTANCE;
		if(!(cfg.hasController()))return;
		Controller controller = cfg.getController();
		controller.poll();
		cfg.getMapping().apply();
		Controllers.clearEvents();
	}
	
	@SubscribeEvent
	public void screenHandler(GuiScreenEvent.InitGuiEvent.Post event) {
		LabelButtonInfo.inject(event.getGui());
		if(event.getGui() instanceof GuiIngameMenu) {
			event.getButtonList().add(new GuiButton(200, (event.getGui().width / 2) - 100, event.getGui().height - 20, I18n.format("gui.controller")));
		}
		ActionButtonChange.moveMouse(event.getButtonList().get(0), event.getGui().width, event.getGui().height);
	}
	
	@SubscribeEvent
	public void handleButtonPress(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if(event.getGui() instanceof GuiIngameMenu) {
			if(event.getButton().id == 200){
				Minecraft.getMinecraft().displayGuiScreen(new GuiScreenControllerHelp(event.getGui()));
			}
		}
	}
	
	
	@SubscribeEvent
	public void handleConfigChange(ConfigChangedEvent event) {
		Config.INSTANCE.save();
	}
}
