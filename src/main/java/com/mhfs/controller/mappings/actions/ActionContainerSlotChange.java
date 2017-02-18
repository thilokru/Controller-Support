package com.mhfs.controller.mappings.actions;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Mouse;

import com.mhfs.controller.mappings.controlls.DPadControll.Direction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Slot;

public class ActionContainerSlotChange extends ActionToEvent<Pair<Float, Float>> {
	
	public final static int SLOT_WIDTH = 16; //derived from GuiContainer.drawSlot();

	@Override
	public String getActionName() {
		return "SLOT_CHANGE";
	}

	@Override
	public String getActionDescription() {
		return I18n.format("gui.controller.slotChange");
	}

	public boolean buttonDown(Pair<Float,Float> input) {
		GuiContainer gui = (GuiContainer) Minecraft.getMinecraft().currentScreen;
		if(gui == null) return true;
		Slot cs = gui.getSlotUnderMouse();
		int xStart, yStart;
		if(cs == null) {
			xStart = Mouse.getEventX() * gui.width / Minecraft.getMinecraft().displayWidth;
			yStart = gui.height - Mouse.getEventY() * gui.height / Minecraft.getMinecraft().displayHeight - 1;
		} else {
			xStart = cs.xPos + gui.getGuiLeft();
			yStart = cs.yPos + gui.getGuiTop();
		}
		
		float x = input.getLeft();
		float y = input.getRight();
		
		Slot newSlot = null;
		if(Direction.UP.isTriggered(x, y)) {
			newSlot = search(gui, xStart + (SLOT_WIDTH / 2), yStart, 0, -SLOT_WIDTH);
		} else if(Direction.DOWN.isTriggered(x, y)) {
			newSlot = search(gui, xStart + SLOT_WIDTH / 2, yStart + SLOT_WIDTH, 0, SLOT_WIDTH); 
		} else if(Direction.LEFT.isTriggered(x, y)) {
			newSlot = search(gui, xStart, yStart + SLOT_WIDTH / 2, -SLOT_WIDTH, 0);
		} else if(Direction.RIGHT.isTriggered(x, y)){
			newSlot = search(gui, xStart + SLOT_WIDTH, yStart + SLOT_WIDTH / 2, SLOT_WIDTH, 0);
		}
		if(newSlot != null) {
			ActionEmulationHelper.moveMouseInGui(newSlot.xPos + SLOT_WIDTH / 2 + gui.getGuiLeft(), newSlot.yPos + SLOT_WIDTH / 2 + gui.getGuiTop(), gui.width, gui.height);
		}
		return true;
	}
	
	private Slot search(GuiContainer gui, int startX, int startY, int stepX, int stepY) {
		int currentX = startX;
		int currentY = startY;
		while(0 <= currentX && currentX <= gui.width && 0 <= currentY && currentY <= gui.height) {
			currentX += stepX;//do step
			currentY += stepY;
			
			Slot temp = getSlotAt(gui, currentX, currentY);
			
			if(temp != null) {
				return temp;
			}
		}
		return null;
	}
	
	private Slot getSlotAt(GuiContainer gui, int x, int y) {
		for(Slot slot : gui.inventorySlots.inventorySlots) {
			if(slot.canBeHovered()) {
				int slotX = slot.xPos + gui.getGuiLeft();
				int slotY = slot.yPos + gui.getGuiTop();
				if(slotX <= x && x <= slotX + SLOT_WIDTH && slotY <= y && y <= slotY + SLOT_WIDTH) {
					return slot;
				}
			}
		}
		return null;
	}
	
	@Override
	public void buttonDown() {}

	@Override
	public void buttonUp() {}

}
