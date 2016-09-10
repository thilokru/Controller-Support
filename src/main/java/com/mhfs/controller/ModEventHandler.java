package com.mhfs.controller;

import java.util.List;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mhfs.controller.gui.GuiScreenControllerHelp;
import com.mhfs.controller.gui.GuiTextInput;
import com.mhfs.controller.gui.LabelButtonInfo;
import com.mhfs.controller.hooks.ControllerMouseHelper;
import com.mhfs.controller.hooks.ControllerMovementInput;
import com.mhfs.controller.mappings.actions.ActionButtonChange;
import com.mhfs.controller.mappings.actions.ActionButtonChange.Wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
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
	
	private boolean active = false;
	
	public void detectControllers() { //TODO: Better controller detection, dynamic (Handle re- and disconnect) and gui!
		int count = Controllers.getControllerCount();
		Logger log = ControllerSupportMod.LOG;
		log.info(String.format("Found %d controller(s)!", count));
		for(int id = 0; id < count; id++) {
			Controller controller = Controllers.getController(id);
			log.info(String.format("Controller: %s", controller.getName()));
			Config.INSTANCE.setController(controller); //TODO: ask user if he'd likes to use the controller.
			this.active = true;
		}
	}
	
	public void activate() {
		this.active = true;
	}
	
	public void deactivate() {
		this.active = false;
	}
	
	@SubscribeEvent
	public void handlePlayerMovementInput(LivingEvent.LivingUpdateEvent event) {
		if(active) {
			if(!(event.getEntityLiving() instanceof EntityPlayerSP))return;
			if(((EntityPlayerSP) event.getEntityLiving()).movementInput instanceof ControllerMovementInput)return;
			Minecraft.getMinecraft().thePlayer.movementInput = new ControllerMovementInput(Minecraft.getMinecraft().gameSettings);
			Minecraft.getMinecraft().mouseHelper = new ControllerMouseHelper();
		}
	}
	
	@SubscribeEvent
	public void handleMouseInput(MouseInputEvent event) {
		if(active) {
			if(event.getGui() instanceof GuiTextInput)return;
			if(event instanceof MouseInputEvent.Pre) {
				List<GuiTextField> list = ActionButtonChange.reflectiveTextFieldListRetrieve(event.getGui());
				for(GuiTextField field : list) {
					field.setFocused(false);
				}
			} else {
				List<GuiTextField> list = ActionButtonChange.reflectiveTextFieldListRetrieve(event.getGui());
				for(GuiTextField field : list) {
					if(field.isFocused()) {
						Minecraft.getMinecraft().displayGuiScreen(new GuiTextInput(event.getGui(), field));
						field.setFocused(false);
						return;
					}
				}
			}
		}
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
		if(active) {
			Config cfg = Config.INSTANCE;
			Controller controller = cfg.getController();
			controller.poll();
			cfg.getMapping().apply();
			Controllers.clearEvents();
		}
	}
	
	@SubscribeEvent
	public void screenHandler(GuiScreenEvent.InitGuiEvent.Post event) {
		if(active) {
			LabelButtonInfo.inject(event.getGui());
			if(event.getGui() instanceof GuiIngameMenu) {
				event.getButtonList().add(new GuiButton(200, (event.getGui().width / 2) - 100, event.getGui().height - 20, I18n.format("gui.controller")));
			}
			if(event.getButtonList().size() != 0)
				ActionButtonChange.moveMouse(new Wrapper(event.getButtonList().get(0)), event.getGui().width, event.getGui().height);
		}
	}
	
	@SubscribeEvent
	public void handleButtonPress(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if(active) {
			if(event.getGui() instanceof GuiIngameMenu) {
				if(event.getButton().id == 200){
					Minecraft.getMinecraft().displayGuiScreen(new GuiScreenControllerHelp(event.getGui()));
				}
			}
		}
	}
	
	
	@SubscribeEvent
	public void handleConfigChange(ConfigChangedEvent event) {
		Config.INSTANCE.save();
	}
}
