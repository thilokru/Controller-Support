package com.mhfs.controller.gui;

import java.io.IOException;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Throwables;
import com.mhfs.controller.Config;
import com.mhfs.controller.mappings.ControllerMapping;
import com.mhfs.controller.mappings.StickConfig;
import com.mhfs.controller.mappings.Usage;
import com.mhfs.controller.mappings.actions.ActionButtonState;
import com.mhfs.controller.mappings.actions.ActionRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;

public class GuiTextInput extends GuiScreen{
	
	private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz ", SPECIAL_CHARS = "!\"§$%&/()=?*+'#:.;,";
	private static final char[] LOWERCASE_ALPHABET, UPPERCASE_ALPHABET, SPECIAL_ALPHABET;
	
	static {
		LOWERCASE_ALPHABET = ALPHABET.toCharArray();
		UPPERCASE_ALPHABET = ALPHABET.toUpperCase().toCharArray();
		SPECIAL_ALPHABET = SPECIAL_CHARS.toCharArray();
	}
	
	private GuiScreen previous;
	private GuiTextField inputTarget;
	private GuiTextField textDisplay;
	private GuiButton buttonConfirm;
	private Pair<Float, Float> selectionInput;
	private boolean capital, special, prevDelete, prevSelect, inputPresent;
	
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
		capital = special = prevDelete = prevSelect = inputPresent = false;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		textDisplay.drawTextBox();
		if(!inputPresent) {
			try {
				this.handleInput();
			} catch (IOException e) {
				Throwables.propagate(e);
			}
		}
		char[] charset = charsFromState();
		char currentActive = getSelectedChar();
		for(int i = 0; i < charset.length; i++) {
			drawSegment(this.width / 2, this.height / 2 + 20, charset.length, i, charset[i], charset[i] == currentActive);
		}
	}
	
	private void drawSegment(int centerX, int centerY, int segmentCount, int segmentID, char c, boolean active) {
		int innerDistance = 40;
		int outerDistance = active ? 70 : 55;
		double startAngle = ((float)segmentID / segmentCount) * 2 * Math.PI;
		double stopAngle = ((float)(segmentID + 1) / segmentCount) * 2 * Math.PI;
		
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.disableCull();
		GlStateManager.color(0F, 0F, 0F, 0.8F);
		vertexbuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(centerX + Math.sin(startAngle) * innerDistance, centerY - Math.cos(startAngle) * innerDistance, 0D).endVertex();
		vertexbuffer.pos(centerX + Math.sin(startAngle) * outerDistance, centerY - Math.cos(startAngle) * outerDistance, 0D).endVertex();
		vertexbuffer.pos(centerX + Math.sin(stopAngle) * outerDistance, centerY - Math.cos(stopAngle) * outerDistance, 0D).endVertex();
		vertexbuffer.pos(centerX + Math.sin(stopAngle) * innerDistance, centerY - Math.cos(stopAngle) * innerDistance, 0D).endVertex();
		tessellator.draw();
		GlStateManager.color(0.5F, 0.5F, 0.5F);
		vertexbuffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		vertexbuffer.pos(centerX + Math.sin(startAngle) * innerDistance, centerY - Math.cos(startAngle) * innerDistance, 0D).endVertex();
		vertexbuffer.pos(centerX + Math.sin(startAngle) * outerDistance, centerY - Math.cos(startAngle) * outerDistance, 0D).endVertex();
		vertexbuffer.pos(centerX + Math.sin(stopAngle) * outerDistance, centerY - Math.cos(stopAngle) * outerDistance, 0D).endVertex();
		vertexbuffer.pos(centerX + Math.sin(stopAngle) * innerDistance, centerY - Math.cos(stopAngle) * innerDistance, 0D).endVertex();
		tessellator.draw();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
		
		int fX = (int) (centerX + (Math.sin(startAngle) * innerDistance + Math.sin(startAngle) * outerDistance + Math.sin(stopAngle) * outerDistance + Math.sin(stopAngle) * innerDistance) / 4 - this.fontRendererObj.getCharWidth(c) / 2);
		int fY = (int) (centerY - (Math.cos(startAngle) * innerDistance + Math.cos(startAngle) * outerDistance + Math.cos(stopAngle) * outerDistance + Math.cos(stopAngle) * innerDistance) / 4 - this.fontRendererObj.FONT_HEIGHT / 2);
		this.fontRendererObj.drawString(c + "", fX, fY, 0xFFFFFF);
	}
	
	@Override
	public void handleInput() throws IOException{
		super.handleInput();
		Controller controller = Config.INSTANCE.getController();
		ControllerMapping mapping = Config.INSTANCE.getMapping();
		StickConfig input = mapping.getStick(Usage.TEXT);
		this.selectionInput = input.getData(controller);
		
		this.capital = ((ActionButtonState)ActionRegistry.getAction("CAPITAL")).getState();
		this.special = ((ActionButtonState)ActionRegistry.getAction("SPECIAL_CHAR")).getState();
		
		boolean currentDelete = ((ActionButtonState)ActionRegistry.getAction("DELETE_LAST")).getState();
		if(currentDelete && !prevDelete) {
			textDisplay.textboxKeyTyped(' ', Keyboard.KEY_BACK);
		}
		prevDelete = currentDelete;
		
		boolean currentSelect = ((ActionButtonState)ActionRegistry.getAction("SELECT_CHAR")).getState();
		if(currentSelect && !prevSelect) {
			char selectedChar = getSelectedChar();
			textDisplay.textboxKeyTyped(selectedChar, 0);
		}
		prevSelect = currentSelect;
		this.inputPresent = true;
	}
	
	private char getSelectedChar() {
		char[] charset = charsFromState();
		int charcount = charset.length;
		double charAngel = 360.0F / charcount;
		double selectAngel = currentAngel();
		int selectIndex = (int) (selectAngel / charAngel);
		return charset[selectIndex];
	}
	
	private double currentAngel() {
		double selectAngel = (-Math.atan(selectionInput.getLeft() / selectionInput.getRight())) * (180 / (Math.PI));
		if(selectionInput.getRight() < 0) {
			selectAngel += 180;
		}
		if(selectAngel < 0) {
			selectAngel += 360;
		}
		while(selectAngel > 360) {
			selectAngel -= 360;
		}
		//Some Bug causes these mirrored values. Ugly fix.
		if(selectAngel == 270) {
			return 90;
		}
		if(selectAngel == 90) {
			return 270;
		}
		return selectAngel;
	}

	private char[] charsFromState() {
		if(this.capital) {
			return UPPERCASE_ALPHABET;
		} else if(this.special) {
			return SPECIAL_ALPHABET;
		} else {
			return LOWERCASE_ALPHABET;
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if(button == this.buttonConfirm) {
			Minecraft.getMinecraft().currentScreen = previous;
			inputTarget.setText(textDisplay.getText());
		}
	}
	
	@Override
	public void updateScreen() {
		textDisplay.updateCursorCounter();
	}
}
