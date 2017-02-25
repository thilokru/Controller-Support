package com.mhfs.controller.hooks;

import org.apache.commons.lang3.tuple.Pair;

import com.mhfs.controller.config.Configuration;
import com.mhfs.controller.config.State;
import com.mhfs.controller.mappings.StickConfig;
import com.mhfs.controller.mappings.Usage;

import net.minecraft.util.MouseHelper;

public class ControllerMouseHelper extends MouseHelper{
	

	@Override
	public void mouseXYChange() {
		super.mouseXYChange();
		State.controller.poll();
		StickConfig cfg = State.mapping.getStick(Usage.VIEW);
		if(cfg == null)return;
		Pair<Float, Float> add = cfg.getData(State.controller);
		float ox = add.getLeft();
		float dx = (float) (Math.pow(add.getLeft() * Configuration.analogSensitivity, 2) * 150);
		if(ox < 0.0F) dx = -dx;
		
		float oy = add.getRight();
		float dy = (float) (Math.pow(add.getRight() * Configuration.analogSensitivity, 2) * 150);
		if(oy < 0.0F) dy = -dy;
		this.deltaX += dx;
		this.deltaY += dy;
	}
}
