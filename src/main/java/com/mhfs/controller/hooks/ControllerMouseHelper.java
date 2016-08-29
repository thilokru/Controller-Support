package com.mhfs.controller.hooks;

import org.apache.commons.lang3.tuple.Pair;

import com.mhfs.controller.Config;
import com.mhfs.controller.mappings.StickConfig;
import com.mhfs.controller.mappings.Usage;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;

public class ControllerMouseHelper extends MouseHelper{
	

	@Override
	public void mouseXYChange() {
		super.mouseXYChange();
		Config.INSTANCE.getController().poll();
		StickConfig cfg = Config.INSTANCE.getMapping().getStick(Minecraft.getMinecraft(), Config.INSTANCE.getController(), Usage.VIEW);
		Pair<Float, Float> add = cfg.getData(Config.INSTANCE.getController());
		float ox = add.getLeft();
		float dx = (float) (Math.pow(add.getLeft() * Config.INSTANCE.getStickSensitivity(), 2) * 150);
		if(ox < 0.0F) dx = -dx;
		
		float oy = add.getRight();
		float dy = (float) (Math.pow(add.getRight() * Config.INSTANCE.getStickSensitivity(), 2) * 150);
		if(oy < 0.0F) dy = -dy;
		System.out.println(dx + " " + dy);
		this.deltaX += dx;
		this.deltaY += dy;
	}
}
