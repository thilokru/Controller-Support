package com.mhfs.controller.config;

import java.io.File;

import org.lwjgl.input.Controller;

import com.mhfs.controller.config.IndexData.ControllerCfg;
import com.mhfs.controller.mappings.ControllerMapping;

public class State {

	public static File configFile;
	public static ControllerMapping mapping;
	public static Controller controller;
	public static ControllerCfg controllerConfig;
}