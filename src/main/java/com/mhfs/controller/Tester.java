package com.mhfs.controller;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

public class Tester {

	public static void test(final Controller controller){
		Thread thread = new Thread(new Runnable() {
			public void run() {
				loop(controller);
			}});
		thread.setDaemon(true);
		thread.start();
	}
	
	public static void loop(Controller controller) {
		while(true) {
			try { Thread.sleep(1000); } catch (Exception e) {}
			Controllers.poll();
			ControllerSupportMod.LOG.info("DPAD-TEST!");
			
			ControllerSupportMod.LOG.info(controller.getPovX());
			
			ControllerSupportMod.LOG.info("DPAD-TEST-END!");
		}
	}
}
