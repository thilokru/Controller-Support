package com.mhfs.controller.config;

import com.mhfs.controller.ControllerSupportMod;
import net.minecraftforge.common.config.Config;

@Config(modid = ControllerSupportMod.MODID)
public class Configuration {
	
	private final static String[] DEFAULT_OPTION_CLASSES = new String[]{
			"net\\.minecraft\\.client\\.gui\\.((.*Options.*)|(.*Settings.*)|(.*Customize.*)|GuiControls|GuiLanguage|GuiScreenResourcePacks)",
			"net.minecraftforge.fml.client.GuiModList",
			"net.minecraftforge.fml.client.config.GuiConfig",
			"net.minecraftforge.fml.client.config.GuiEditArray",
			"net.minecraftforge.fml.client.config.GuiSelectString"
		};
	
	@Config.LangKey("gui.config.analogSensitivity")
	@Config.Comment("The sensitivity of the analog sticks")
	public static float analogSensitivity = 0.5F;
	
	@Config.LangKey("gui.config.invertedLookAxes")
	@Config.Comment("Whether the view axes are inverted")
	public static boolean invertedLookAxes = false;
	
	@Config.LangKey("gui.config.optionClasses")
	@Config.Comment("The classes that trigger the OPTIONS()-condition. Has regex-support.")
	public static String[] optionClasses = DEFAULT_OPTION_CLASSES;
	
	@Config.LangKey("gui.config.debugController")
	@Config.Comment("When set to true, button and axis ids are printed into the logfiles.")
	public static boolean debugController = false;	
}
