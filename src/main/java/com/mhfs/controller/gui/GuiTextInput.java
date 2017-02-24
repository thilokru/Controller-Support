package com.mhfs.controller.gui;

import java.io.IOException;
import java.lang.reflect.Method;

import org.lwjgl.input.Keyboard;

import com.mhfs.controller.mappings.actions.ActionButtonState;
import com.mhfs.controller.mappings.actions.ActionRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiTextInput extends GuiScreen{
	
	private GuiScreen previous;
	private GuiTextField inputTarget;
	private GuiTextField textDisplay;
	private GuiButton buttonConfirm;
	private GuiCharSelectElement selector;
	private boolean prevDelete, prevSelect;
	
	
	public GuiTextInput(GuiScreen previous, GuiTextField inputTarget) {
		this.previous = previous;
		this.inputTarget = inputTarget;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		buttonConfirm = new GuiButton(0, this.width / 2 + 100, 30, 50, 20, I18n.format("gui.done"));
		this.buttonList.add(buttonConfirm);
		String text = "";
		if(textDisplay != null)
			text = textDisplay.getText();
		textDisplay = new GuiTextField(0, fontRendererObj, this.width / 2 - 100, 30, 200, 20);
		textDisplay.setFocused(true);
		textDisplay.setText(text);
		
		selector = new GuiCharSelectElement(this.width / 2, this.height / 2 + 20);
		selector.handleInput();
		
		prevDelete = prevSelect = false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		selector.draw();
		textDisplay.drawTextBox();
	}	
	
	@Override
	public void handleInput() throws IOException{
		super.handleInput();
		selector.handleInput();
		
		boolean currentDelete = ((ActionButtonState)ActionRegistry.getAction("DELETE_LAST")).getState();
		if(currentDelete && !prevDelete) {
			textDisplay.textboxKeyTyped(' ', Keyboard.KEY_BACK);
		}
		prevDelete = currentDelete;
		
		boolean currentSelect = ((ActionButtonState)ActionRegistry.getAction("SELECT")).getState();
		if(currentSelect && !prevSelect) {
			char selectedChar = selector.getSelectedElement().getResult();
			textDisplay.textboxKeyTyped(selectedChar, 0);
		}
		prevSelect = currentSelect;
	}	
	
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == this.buttonConfirm) {
			Minecraft.getMinecraft().currentScreen = previous;
			inputTarget.setText(textDisplay.getText());
			previous.updateScreen();
			try {
				reflectiveTextfieldUpdate();
			} catch (Exception e) {
				throw new RuntimeException("Error reflecting on previous gui!", e);
			}
		}
	}
	
	private void reflectiveTextfieldUpdate() throws Exception {
		Method method = GuiScreen.class.getDeclaredMethod("keyTyped", char.class, int.class);
		method.setAccessible(true);
		method.invoke(this.previous, ' ', Keyboard.KEY_SPACE);
		method.invoke(this.previous, ' ', Keyboard.KEY_BACK);
	}

	@Override
	public void updateScreen() {
		textDisplay.updateCursorCounter();
	}
}
