package com.mhfs.controller.gui;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;
import org.lwjgl.opengl.GL11;

import com.mhfs.controller.Config;
import com.mhfs.controller.mappings.ControllerMapping;
import com.mhfs.controller.mappings.StickConfig;
import com.mhfs.controller.mappings.Usage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public abstract class GuiSelectElement<T> extends Gui {
	
	private Pair<Float, Float> selectionInput;
	private int middleX, middleY;
	
	public GuiSelectElement(int middleX, int middleY) {
		this.middleX = middleX;
		this.middleY = middleY;
	}
	
	public void draw() {
		ISelectableElement<T>[] elements = getElements();
		ISelectableElement<T> currentActive = getSelectedElement();
		for(int i = 0; i < elements.length; i++) {
			drawSegment(middleX, middleY, elements.length, i, elements[i], elements[i] == currentActive);
		}
	}
	
	public void handleInput() {
		Controller controller = Config.INSTANCE.getController();
		ControllerMapping mapping = Config.INSTANCE.getMapping();
		StickConfig input = mapping.getStick(Usage.TEXT);
		if(input != null) {
			this.selectionInput = input.getData(controller);
		} else {
			this.selectionInput = Pair.of(0F, 0F);
		}
	}
	
	private void drawSegment(int centerX, int centerY, int segmentCount, int segmentID, ISelectableElement<T> element, boolean active) {
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
		
		FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
		
		int fX = (int) (centerX + (Math.sin(startAngle) * innerDistance + Math.sin(startAngle) * outerDistance + Math.sin(stopAngle) * outerDistance + Math.sin(stopAngle) * innerDistance) / 4 - fr.getStringWidth(element.getDisplayString()) / 2);
		int fY = (int) (centerY - (Math.cos(startAngle) * innerDistance + Math.cos(startAngle) * outerDistance + Math.cos(stopAngle) * outerDistance + Math.cos(stopAngle) * innerDistance) / 4 - fr.FONT_HEIGHT / 2);
		fr.drawString(element.getDisplayString(), fX, fY, 0xFFFFFF);
	}
	
	public ISelectableElement<T> getSelectedElement() {
		ISelectableElement<T>[] elements = getElements();
		int elementCount = elements.length;
		double charAngel = 360.0F / elementCount;
		double selectAngel = currentAngel();
		int selectIndex = (int) (selectAngel / charAngel);
		return elements[selectIndex];
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

	protected abstract ISelectableElement<T>[] getElements();
	
	public static interface ISelectableElement<T> {
		
		public String getDisplayString();
		
		public T getResult();
	}
}
