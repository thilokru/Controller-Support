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
	
	private final static float buttonScale = 0.2F;
	private final static int scaledTextureSize = 20;
	private final static int boundary = 2;
	
	private List<Pair<String, String>> buttonFunctions;
	private final FontRenderer fr;

	public LabelButtonInfo(FontRenderer fr, int id, int x, int y, int width, int height) {
		super(fr, id, x, y, width, height, 0xFFFFFF);
		this.fr = fr;
	}
	
	@Override
	public void drawLabel(Minecraft mc, int mouseX, int mouseY) {
		if(buttonFunctions == null) {
			buttonFunctions = Config.INSTANCE.getMapping().getButtonFunctions();
		}
		
		int width = 0;
		for(Pair<String, String> entry : buttonFunctions) {
			width += scaledTextureSize + fr.getStringWidth(entry.getValue());
		}
		int height = Math.max(fr.FONT_HEIGHT, scaledTextureSize);
		
		GlStateManager.enableBlend();
		drawRect(x - boundary, y - boundary, x + width + 3 * boundary, y + height + boundary, 0xAA000000);
		
		int textYOffset = (scaledTextureSize - fr.FONT_HEIGHT) / 2;
		int xCursor = this.x;
		for(Pair<String, String> entry : buttonFunctions) {
			GlStateManager.enableBlend();
			xCursor += drawButton(xCursor, y, entry.getKey());
			GlStateManager.disableBlend();
			xCursor += this.fr.drawString(entry.getValue(), xCursor, this.y + textYOffset, 0xFFFFFF);
		}
	}

	private int drawButton(int x, int y, String key) {
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
			labelList.add(new LabelButtonInfo(Minecraft.getMinecraft().fontRendererObj, -100, 2, 2, 0, 0));
		} catch (Throwable t) {
			Throwables.propagate(t);
		}
	}

}
