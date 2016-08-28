package com.mhfs.controller.hooks;

import org.apache.commons.lang3.tuple.Pair;

import com.mhfs.controller.Config;
import com.mhfs.controller.mappings.StickConfig;

import net.minecraft.util.MouseHelper;

public class ControllerMouseHelper extends MouseHelper{
	
	private StickConfig cfg;
	
	public ControllerMouseHelper(StickConfig config) {
		this.cfg = config;
	}

	@Override
	public void mouseXYChange() {
		super.mouseXYChange();
		Pair<Float, Float> add = cfg.getData(Config.INSTANCE.getController());
		float ox = add.getLeft();
		float dx = (float) (Math.pow(add.getLeft() * Config.INSTANCE.getStickSensitivity(), 2) * 50);
		if(ox < 0.0F) dx = -dx;
		
		float oy = add.getRight();
		float dy = (float) (Math.pow(add.getLeft() * Config.INSTANCE.getStickSensitivity(), 2) * 50);
		if(oy < 0.0F) dy = -dy;
		
		this.deltaX += dx;
		this.deltaY += dy;
	}
}
