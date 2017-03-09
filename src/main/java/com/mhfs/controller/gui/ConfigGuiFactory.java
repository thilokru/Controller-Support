package com.mhfs.controller.gui;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.controller.config.State;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ConfigGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
		
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return ConfigGui.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		return null;
	}
	
	public static class ConfigGui extends GuiConfig {
		
		public ConfigGui(GuiScreen parrent) {
			super(parrent, getEntries(), ControllerSupportMod.MODID, false, false, "gui.config.controllerSupport");
		}

		private static List<IConfigElement> getEntries() {
			Map<String, Configuration> configMap = getConfigMap();
			Configuration config = configMap.get(State.configFile.getAbsolutePath());
			return new ConfigElement(config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements();
		}

		@SuppressWarnings("unchecked")
		private static Map<String, Configuration> getConfigMap() {
			try {
				Field field = ConfigManager.class.getDeclaredField("CONFIGS");
				field.setAccessible(true);
				return (Map<String, Configuration>) field.get(null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
}
