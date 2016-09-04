package com.mhfs.controller.textures;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class TextureHelper implements IResourceManagerReloadListener {
	
	private final static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ResourceLocation.class, new TypeAdapter<ResourceLocation>(){
		@Override
		public void write(JsonWriter out, ResourceLocation value) throws IOException {
			out.value(value.toString());
		}
		@Override
		public ResourceLocation read(JsonReader in) throws IOException {
			return new ResourceLocation(in.nextString());
		}
	}).create();
	private final static Map<ResourceLocation, TextureHelper> cache = new HashMap<ResourceLocation, TextureHelper>();
	private static Logger log = LogManager.getLogger("TextureHelper");
	
	private ResourceLocation textureFile;
	private int textureSizeX, textureSizeY;
	private Map<String, SubTexture> subTextures;
	
	private transient ResourceLocation location;
	
	protected TextureHelper(){
		this.subTextures =  new HashMap<String, SubTexture>();
	}
	
	public TextureHelper(ResourceLocation texture, Map<String, SubTexture> nameToSubTextureMap) {
		this();
		this.textureFile = texture;
		this.subTextures.putAll(nameToSubTextureMap);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		try {
			TextureHelper helper = loadInternal(resourceManager, location);
			this.subTextures = helper.subTextures;
			this.textureSizeX = helper.textureSizeX;
			this.textureSizeY = helper.textureSizeY;
			this.textureFile = helper.textureFile;
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}
	
	public SubTexture getTextureInfo(String name) {
		return subTextures.get(name);
	}
	
	public void drawTextureAt(String textureName, int x, int y) {
		drawTextureAt(Minecraft.getMinecraft(), textureName, x, y);
	}
	
	public void drawTextureAt(Minecraft mc, String textureName, int x, int y) {
		drawTextureAt(mc, textureName, x, y, 0f, 0f, 1f, 1f);
	}
	
	public void drawTextureAt(Minecraft mc, String textureName, int x, int y, float uOffset, float vOffset, float uPart, float vPart){
		SubTexture sub = subTextures.get(textureName);
		if(sub == null) return;
		mc.getTextureManager().bindTexture(textureFile);
		GlStateManager.enableBlend();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		x += sub.width * uOffset;
		y += sub.height * vOffset;
		int u = (int) (sub.x + sub.width * uOffset);
		int v = (int) (sub.y + sub.height * vOffset);
		int drawWidth = (int) (sub.width * uPart);
		int drawHeight = (int) (sub.height * vPart);
		GuiScreen.drawModalRectWithCustomSizedTexture(x, y, u, v, drawWidth, drawHeight, textureSizeX, textureSizeY);
	}
	
	public static TextureHelper get(ResourceLocation location) {
		return get(Minecraft.getMinecraft().getResourceManager(), location);
	}
	
	public static TextureHelper get(IResourceManager manager, ResourceLocation location) {
		TextureHelper helper = cache.get(location);
		if(helper == null) {
			try {
				helper = loadFromJSON(manager, location);
			} catch (IOException e) {
				log.error("Manual texture error occured!", e);
			}
			cache.put(location, helper);
		}
		return helper;
	}
	
	private static TextureHelper loadFromJSON(IResourceManager manager, ResourceLocation location) throws IOException{
		TextureHelper helper = loadInternal(manager, location);
		((IReloadableResourceManager) manager).registerReloadListener(helper);
		return helper;
	}
	
	private static TextureHelper loadInternal(IResourceManager manager, ResourceLocation location) throws IOException {
		InputStreamReader resourceIn = new InputStreamReader(manager.getResource(location).getInputStream());
		TextureHelper helper = gson.fromJson(resourceIn, TextureHelper.class);
		helper.location = location;
		return helper;
	}
	
	public static class SubTexture {
		private final int x, y;
		private final int width, height;
		
		public SubTexture(int x, int y, int width, int height){
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public int getWidth() {
			return width;
		}
		public int getHeight() {
			return height;
		}
	}
}
