package com.mhfs.controller.gui;

import java.lang.reflect.Field;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Throwables;
import com.mhfs.controller.Config;
import com.mhfs.controller.mappings.ControllInfo;
import com.mhfs.controller.textures.TextureHelper;
import com.mhfs.controller.textures.TextureHelper.SubTexture;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class LabelButtonInfo extends GuiLabel {
	
	public final static float buttonScale = 0.2F;
	public final static int scaledTextureSize = 20;
	public final static int boundary = 2;
	
	private final FontRenderer fr;

	public LabelButtonInfo(FontRenderer fr, int id, int x, int y, int width, int height) {
		super(fr, id, x, y, width, height, 0xFFFFFF);
		this.fr = fr;
	}
	
	@Override
	public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
		List<Pair<String, String>> buttonFunctions = Config.INSTANCE.getMapping().getButtonFunctions();
		
		int width = 0;
		for(Pair<String, String> entry : buttonFunctions) {
			width += scaledTextureSize + fr.getStringWidth(entry.getValue()) + boundary;
		}
		int height = Math.max(fr.FONT_HEIGHT, scaledTextureSize);
		
		GlStateManager.enableBlend();
		drawRect(x - boundary, y - boundary, x + width + 2 * boundary, y + height + boundary, 0xAA000000);
		
		int xCursor = x;
		for(Pair<String, String> entry : buttonFunctions) {
			xCursor = drawSingleItem(entry.getKey(), entry.getValue(), xCursor, y);
		}
	}
	
	public int drawSingleItem(String buttonName, String desc, int x, int y) {
		int textYOffset = (scaledTextureSize - fr.FONT_HEIGHT) / 2;
		GlStateManager.enableBlend();
		x += drawButton(x, y, buttonName);
		GlStateManager.disableBlend();
		x = fr.drawString(desc, x, y + textYOffset, 0xFFFFFF);
		x += boundary;
		return x;
	}

	private static int drawButton(int x, int y, String key) {
		TextureHelper helper = ControllInfo.get().getTextureHelper();
		SubTexture info = helper.getTextureInfo(key);
		helper.drawScaledTextureAt(key, x, y, buttonScale);
		return (int) (info.getWidth() * buttonScale);
	}
	
	@SuppressWarnings("unchecked")
	public static void inject(GuiScreen screen) {
		try {
			Field fieldLabelList = GuiScreen.class.getDeclaredField("labelList");
			fieldLabelList.setAccessible(true);
			List<GuiLabel> labelList = (List<GuiLabel>) fieldLabelList.get(screen);
			for(GuiLabel label : labelList) {
				if(label instanceof LabelButtonInfo) {
					return;
				}
			}
			labelList.add(new LabelButtonInfo(Minecraft.getMinecraft().fontRendererObj, -100, 2, 2, 0, 0));
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}
}
