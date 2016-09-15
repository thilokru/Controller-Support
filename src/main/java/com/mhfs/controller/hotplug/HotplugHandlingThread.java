package com.mhfs.controller.hotplug;

import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mhfs.controller.Config;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.mappings.ControllerMapping;
import com.mhfs.controller.mappings.conditions.GameContext;

public class HotplugHandlingThread implements Runnable {

	private static Logger LOG = ControllerSupportMod.LOG;
			
	@Override
	public void run() {
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			LOG.error("Unable to load controllers", e);
		}
		detectControllers();
		ControllerMapping mapping = ControllerMapping.loadFromConfig();
		mapping.init(new GameContext(Config.INSTANCE.getController()));
		Config.INSTANCE.setMapping(mapping);	
		Config.INSTANCE.save();
	}
	
	private void detectControllers() { //TODO: Better controller detection, dynamic (Handle re- and disconnect) and gui!
		int count = Controllers.getControllerCount();
		Logger log = ControllerSupportMod.LOG;
		log.info(String.format("Found %d controller(s)!", count));
		for(int id = 0; id < count; id++) {
			Controller controller = Controllers.getController(id);
			log.info(String.format("Controller: %s", controller.getName()));
			Config.INSTANCE.setController(controller); //TODO: ask user if he'd likes to use the controller.
		}
	}
}
