package com.mhfs.controller.mappings.actions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiListWorldSelection;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.resources.I18n;

public class ActionListSelect extends ActionToEvent<Pair<Float, Float>> {

	@Override
	public String getActionName() {
		return "SELECT_LIST";
	}

	@Override
	public String getActionDescription() {
		return I18n.format("gui.list.select");
	}
	
	@Override
	public boolean buttonDown(Pair<Float, Float> arg) {
		float y = arg.getRight();
		if(y == 0) return true;
		GuiListExtended list = reflectiveRetrieve();
		if(list == null)
			throw new RuntimeException("The current gui has no GuiListExtended!");
		int listSize = getListSize(list);
		for(int i = 0; i < listSize; i++) {
			if(isSelected(list, i)) {
				int newID = 0;
				if(y > 0) {
					newID = i + 1;
				} else if (y < 0) {
					newID = i - 1;
				}
				if(newID < 0)
					newID = 0;
				if(newID >= listSize) 
					newID = listSize - 1;
				setSelectedID(list, newID);
				return true;
			}
		}
		setSelectedID(list, 0);
		return true;
	}
	
	private static void setSelectedID(GuiListExtended list, int id) {
		Field field;
		try{
			if(list instanceof GuiListWorldSelection) {
				field = GuiListWorldSelection.class.getDeclaredField("selectedIdx");
			} else {
				field = GuiSlot.class.getDeclaredField("selectedElement");
			}
			field.setAccessible(true);
			field.set(list, id);
			if(list instanceof GuiListWorldSelection) {
				((GuiListWorldSelection) list).selectWorld(id);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error accessing index field!", e);
		}
	}
	
	private static boolean isSelected(GuiListExtended list, int id) {
		try {
			Method method = GuiSlot.class.getDeclaredMethod("isSelected", int.class);
			method.setAccessible(true);
			return (boolean) method.invoke(list, id);
		} catch (Exception e) {
			throw new RuntimeException("Error accessing getSize()-Method!", e);
		}
	}
	
	private static int getListSize(GuiListExtended list) {
		try {
			Method method = GuiSlot.class.getDeclaredMethod("getSize");
			method.setAccessible(true);
			return (int) method.invoke(list);
		} catch (Exception e) {
			throw new RuntimeException("Error accessing getSize()-Method!", e);
		}
	}

	private static GuiListExtended reflectiveRetrieve() {
		GuiScreen screen = Minecraft.getMinecraft().currentScreen;
		for(Field field : screen.getClass().getDeclaredFields()) {
			if(GuiListExtended.class.isAssignableFrom(field.getType())) {
				field.setAccessible(true);
				try {
					return (GuiListExtended) field.get(screen);
				} catch (Exception e) {
					throw new RuntimeException("Couldn't access field " + field.getName() + "!", e);
				}
			}
		}
		return null;
	}

	@Override
	public void buttonDown() {
		throw new RuntimeException("ActionListSelect requires arguments!");
	}

	@Override
	public void buttonUp() {}

}
