package com.mhfs.controller;

import java.util.List;
import org.lwjgl.input.Controller;

import com.mhfs.controller.config.Config;
import com.mhfs.controller.gui.GuiButtonSelector;
import com.mhfs.controller.gui.GuiScreenControllerHelp;
import com.mhfs.controller.gui.GuiTextInput;
import com.mhfs.controller.gui.LabelButtonInfo;
import com.mhfs.controller.hooks.ControllerMouseHelper;
import com.mhfs.controller.hooks.ControllerMovementInput;
import com.mhfs.controller.hotplug.HotplugHandler;
import com.mhfs.controller.mappings.actions.ActionButtonChange;
import com.mhfs.controller.mappings.actions.ActionButtonChange.Wrapper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

/**
 * Handles the InputEvent (each Tick) to pull data from the controller and input
 * to the game.
 * 
 * @author Thilo
 *
 */
public class ModEventHandler {

	private boolean active = false;
	private GuiButtonSelector selector;

	public void activate() {
		this.active = true;
		Minecraft.getMinecraft().gameSettings.realmsNotifications = false;
	}

	public void deactivate() {
		this.active = false;
		GuiScreen gs = Minecraft.getMinecraft().currentScreen;
		if (gs != null) {
			LabelButtonInfo.remove(gs);
			if (gs instanceof GuiMainMenu) {
				Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
			}
		}
		if(Minecraft.getMinecraft().thePlayer != null) {
			Minecraft.getMinecraft().thePlayer.movementInput = new MovementInputFromOptions(Minecraft.getMinecraft().gameSettings);
		}
		Minecraft.getMinecraft().mouseHelper = new MouseHelper();
		Minecraft.getMinecraft().gameSettings.realmsNotifications = true;
	}

	@SubscribeEvent
	public void onPlayerLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		if (active) {
			if (!(event.getEntityLiving() instanceof EntityPlayerSP))
				return;
			if (((EntityPlayerSP) event.getEntityLiving()).movementInput instanceof ControllerMovementInput)
				return;
			Minecraft.getMinecraft().thePlayer.movementInput = new ControllerMovementInput(Minecraft.getMinecraft().gameSettings);
			Minecraft.getMinecraft().mouseHelper = new ControllerMouseHelper();
		}
	}

	@SubscribeEvent
	public void onMouseInput(MouseInputEvent event) {
		if (active) {
			if (event.getGui() instanceof GuiTextInput)
				return;
			if (event instanceof MouseInputEvent.Pre) {
				List<GuiTextField> list = ActionButtonChange.reflectiveTextFieldListRetrieve(event.getGui());
				for (GuiTextField field : list) {
					field.setFocused(false);
				}
			} else {
				List<GuiTextField> list = ActionButtonChange.reflectiveTextFieldListRetrieve(event.getGui());
				for (GuiTextField field : list) {
					if (field.isFocused()) {
						Minecraft.getMinecraft().displayGuiScreen(new GuiTextInput(event.getGui(), field));
						field.setFocused(false);
						return;
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onDrawGuiScreen(GuiScreenEvent.DrawScreenEvent event) {
		handleTick();
		if (event.getGui() instanceof GuiMainMenu) {
			if (HotplugHandler.init())
				return;
			if (active) {
				selector.handleInput();
				selector.draw();
			}
		}
	}

	@SubscribeEvent
	public void handleClientTickEnd(ClientTickEvent event) {
		if (event.phase == Phase.START) {
			handleTick();
		}
	}

	public void handleTick() {
		if (active) {
			Config cfg = Config.INSTANCE;
			Controller controller = cfg.getController();
			controller.poll();
			cfg.getMapping().apply();
		}
		HotplugHandler.checkControllerManagerRestart();
	}

	@SubscribeEvent
	public void onInitGuiScreen(GuiScreenEvent.InitGuiEvent.Post event) {
		if (active) {
			LabelButtonInfo.inject(event.getGui());
			if (event.getGui() instanceof GuiIngameMenu) {
				event.getButtonList().add(new GuiButton(200, (event.getGui().width / 2) - 100, event.getGui().height - 20, I18n.format("gui.controller")));
			}
			if (event.getGui() instanceof GuiMainMenu) {
				this.selector = new GuiButtonSelector(event.getGui().width / 2, event.getGui().height / 2 + 30, 40, event.getButtonList().toArray(new GuiButton[0]));
				event.getButtonList().clear();
			}
			if (event.getButtonList().size() != 0)
				ActionButtonChange.moveMouse(new Wrapper(event.getButtonList().get(0)), event.getGui().width, event.getGui().height);
		}
	}

	@SubscribeEvent
	public void onGuiScreenButtonPress(GuiScreenEvent.ActionPerformedEvent.Post event) {
		if (active) {
			if (event.getGui() instanceof GuiIngameMenu) {
				if (event.getButton().id == 200) {
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
