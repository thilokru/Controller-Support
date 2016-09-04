package com.mhfs.controller.mappings;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Controller;

public class StickConfig {

	private int xID, yID;
	private boolean xInverted, yInverted;
	
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
	
	public static class Uncompiled {
		private String xName, yName;
		private boolean xInverted, yInverted;
		
		public Uncompiled(String xName, String yName, boolean xInverted, boolean yInverted) {
			this.xName = xName;
			this.yName = yName;
			this.xInverted = xInverted;
			this.yInverted = yInverted;
		}
		
		public StickConfig compile(ControllInfo names) {
			return new StickConfig(names.getAxisID(xName), names.getAxisID(yName), xInverted, yInverted);
		}
	}
}
