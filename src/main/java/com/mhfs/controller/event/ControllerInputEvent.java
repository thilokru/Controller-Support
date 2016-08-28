package com.mhfs.controller.event;

import org.lwjgl.input.Controller;

import com.mhfs.controller.mappings.ControllerMapping;

import net.minecraftforge.fml.common.gameevent.InputEvent;

public class ControllerInputEvent extends InputEvent{
	
	private final ControllerMapping mapping;
	private final Controller controller;

	public ControllerInputEvent(ControllerMapping mapping, Controller controller){
		super();
		this.mapping = mapping;
		this.controller = controller;
	}
	
	public ControllerMapping getMapping() {
		return mapping;
	}
	
	public Controller getController() {
		return controller;
	}
}
