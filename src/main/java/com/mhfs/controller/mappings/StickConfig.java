package com.mhfs.controller.mappings;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

public class StickConfig {

	private int xID, yID;
	private boolean xInverted, yInverted;
	
	public StickConfig(int xID, int yID) {
		this(xID, yID, false, false);
	}
	
	public StickConfig(int xID, int yID, boolean xInverted, boolean yInverted) {
		this.xID = xID;
		this.yID = yID;
		this.xInverted = xInverted;
		this.yInverted = yInverted;
	}
	
	public Pair<Float, Float> getData(Controller controller) {
		controller.poll();
		float x = controller.getAxisValue(xID);
		if(xInverted) x = -x;
		
		float y = controller.getAxisValue(yID);
		if(yInverted) y = -y;
		
		return Pair.of(x, y);
	}
}
