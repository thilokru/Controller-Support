package com.mhfs.controller.hotplug;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.gui.GuiControllerSelection;
import com.mhfs.controller.mappings.ControllerMapping;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;

/**
 * @author Sinus
 * This is the hacky way to check removed controllers. This has to be done, because LWJGL v2 and JInput are crappy.</br>
 * </br>
 * JInput does not fire the defined events when controllers are added or removed.</br>
 * LWJGL ignores whether or not the controller is still available.</br>
 * The required fields and methods are non-public</br>
 * (Probably) The LauchClassLoader messes with packages and classes, making reflecting troublesome (putting Controller into Object...)</br>
 * </br>
 * So, the conditions are messed up. So is this method to handle it.
 */
public class HotplugHandler {

	private static Logger LOG = ControllerSupportMod.LOG;
	private static boolean initalized = false;
	private static Object jInputController = null;
	
	public static void preInit() {
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			LOG.error("Error creating controller evironment", e);
		}
	}
	
	/**
	 * @return whether or not the thread just got started.
	 */
	public static boolean init() {
		if(initalized)return false;
		initalized = true;
		GuiControllerSelection.requestController();
		return true;
	}
	
	public static void checkControllerRemoved(Controller controller) {
		if(jInputController == null) {
			jInputController = getController(controller);
		}
		if(!available(jInputController)) {
			LOG.info("Controller disconnected!");
			jInputController = null;
			Config.INSTANCE.setController(null);
			ControllerSupportMod.INSTANCE.handler.deactivate();
			cleanUp();//Hotplug is not supported by the underlying libraries. Sadly, the game must be restarted.
		}
	}

	private static void cleanUp() {
		LOG.info("Hotplug is currently not supported. Saving and stopping Minecraft...");
		if(Minecraft.getMinecraft().theWorld != null) {
			LOG.info("Saving world...");
			Minecraft.getMinecraft().loadWorld(null, "Unsupported Hotplug Emergency Shutdown!");
			LOG.info("World has been saved.");
		}
		LOG.info("Preventing KeyBinding NPE by going back to main menu...");
		Minecraft.getMinecraft().displayGuiScreen(new GuiMainMenu());
		LOG.info("Forcing Minecraft to quit next tick...");
		Minecraft.getMinecraft().shutdown();
		LOG.info("Minecraft is going down safely.");
	}

	/*private static void resetControllerEnvironment() {
		try {
			//Release all the Controllers! (May be catastrophical)
			for(int i = 0; i < Controllers.getControllerCount(); i++) {
				Object controller = getController(Controllers.getController(i));
				releaseController(controller);
			}
			
			//Reset Controllers from JInput
			Field envField = ControllerEnvironment.class.getDeclaredField("defaultEnvironment");
			envField.setAccessible(true);
			Constructor<?> c = Class.forName("net.java.games.input.DefaultControllerEnvironment").getConstructor();
			c.setAccessible(true);
			Object newEnv = c.newInstance();
			envField.set(null, newEnv);
			ControllerEnvironment.getDefaultEnvironment().getControllers();
			
			//Reset Controllers from LWJGL
			Field createdField = Controllers.class.getDeclaredField("created");
			createdField.setAccessible(true);
			createdField.set(null, false);
			Controllers.create();
		} catch (Exception e) {
			throw new RuntimeException("Error resetting controller environment", e);
		}
	}*/

	private static boolean available(Object theShouldBeAController) {
		try {
			Class<?> theClazz = theShouldBeAController.getClass();
			Method pollMethod = theClazz.getMethod("poll");
			pollMethod.setAccessible(true);
			return (boolean) pollMethod.invoke(theClazz.cast(theShouldBeAController));
		} catch (Exception e) {
			throw new RuntimeException("Error reflecting on net.java.games.input.Controller!", e);
		}
	}
	
	private static Object getController(Controller controller) {
		try {
			Class<?> wrapperClazz = Class.forName("org.lwjgl.input.JInputController");
			if(wrapperClazz.isInstance(controller)) {
				Field wrappedField = wrapperClazz.getDeclaredField("target");
				wrappedField.setAccessible(true);
				Object value = wrappedField.get(controller);
				return value;
			}
		} catch (Throwable t) {
			throw new RuntimeException("Error reflecting on org.lwjgl.input.JInputController!", t);
		}
		return null;
	}
	
	public static void loadSelectedController(Controller controller) {
		if(controller == null) {
			LOG.info("The user chose to use mouse and keyboard.");
		} else {
			LOG.info(String.format("The user chose to use the following controller: '%s'", controller.getName()));
		}
		Config.INSTANCE.setController(controller);
		if(Config.INSTANCE.hasController()) {
			ControllerSupportMod.INSTANCE.handler.activate();
			ControllerMapping mapping = ControllerMapping.loadFromConfig();
			Config.INSTANCE.setMapping(mapping);	
			Config.INSTANCE.save();
		} else {
			ControllerSupportMod.INSTANCE.handler.deactivate();
		}
	}
}
