package com.mhfs.controller.gui;

import java.lang.reflect.Method;

import com.mhfs.controller.mappings.actions.ActionButtonState;
import com.mhfs.controller.mappings.actions.ActionRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiButtonSelector extends GuiSelectElement<GuiButton> {

	private ISelectableElement<GuiButton>[] elements;
	
	public GuiButtonSelector(int middleX, int middleY, int innerRadius, GuiButton[] buttons) {
		super(middleX, middleY, innerRadius);
		elements = new ButtonElementImpl[buttons.length];
		for(int i = 0; i < elements.length; i++) {
			elements[i] = new ButtonElementImpl(buttons[i]);
		}
	}
	
	@Override
	public void handleInput() {
		super.handleInput();
		if(((ActionButtonState)ActionRegistry.getAction("SELECT")).getState()) {
			activateSelectedButton();
		}
	}
	
	private void activateSelectedButton() {
		GuiButton button = this.getSelectedElement().getResult();
		reflectiveActionPerformed(button, Minecraft.getMinecraft().currentScreen);
	}

	private void reflectiveActionPerformed(GuiButton button, GuiScreen currentScreen) {
		try{
			Method method = GuiScreen.class.getDeclaredMethod("actionPerformed", GuiButton.class);
			method.setAccessible(true);
			method.invoke(currentScreen, button);
		} catch (Throwable t) {
			throw new RuntimeException("Error reflecting on GuiScreen to invoke actionPerformed()!", t);
		}
	}

	@Override
	protected ISelectableElement<GuiButton>[] getElements() {
		return elements;
	}

	private static class ButtonElementImpl implements ISelectableElement<GuiButton> {
		
		private GuiButton button;
		
		public ButtonElementImpl(GuiButton button) {
			this.button = button;
		}
		
		@Override
		public String getDisplayString() {
			if(button instanceof GuiButtonLanguage) {
				return I18n.format("gui.button.language");
			}
			return button.displayString;
		}

		@Override
		public GuiButton getResult() {
			return button;
		}
	}
}
