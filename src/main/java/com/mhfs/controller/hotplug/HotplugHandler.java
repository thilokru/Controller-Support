package com.mhfs.controller.hotplug;

import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Controller;
import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.gui.GuiControllerSelection;
import com.mhfs.controller.mappings.ControllerMapping;
import com.mhfs.ipc.InvocationManager;

public class HotplugHandler {

	private static Logger LOG = ControllerSupportMod.LOG;
	private static boolean initalized = false;
	private static ExtendedMethods ipcInterface;
	
	/**
	 * @return whether or not the thread just got started.
	 */
	public static boolean init() {
		if(initalized)return false;
		initalized = true;
		restartDaemon();
		GuiControllerSelection.requestController();
		return true;
	}
	
	public static void checkControllerRemoved() {
		if(ipcInterface.restartRequired()) {
			LOG.info("Controller disconnected!");
			restartDaemon();
			GuiControllerSelection.requestController();
		}
	}

	private static void restartDaemon() {
		DaemonManager.stopDaemon();
		try {
			InvocationManager manager = DaemonManager.startDaemon();
			ipcInterface = new MethodStub(manager);
		} catch (Exception e) {
			throw new RuntimeException("Error connecting to daemon!", e);
		}
	}

	public static void loadSelectedController(Controller controller) {
		if(controller == null) {
			LOG.info("The user chose to use mouse and keyboard.");
		} else {
			controller.poll();
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
	
	public static ExtendedMethods getIPCHandler() {
		return ipcInterface;
	}
}
