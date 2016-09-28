package com.mhfs.controller.daemon;

import org.lwjgl.input.Controller;

public class BeanController implements Controller {
	
	String name;
	int id;
	String[] axisNames, buttonNames, rumblerNames;
	boolean[] buttonState;
	float[] axisValues, axisDeadZones;
	float povX, povY;
	//Special Axes: X, Y, Z, RX, RY, RZ
	int[] specialID;
	
	ProvidedMethods methodProvider;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getIndex() {
		return id;
	}
	

	@Override
	public void poll() {
		SerializationHelper.sync(this, methodProvider.getControllerData());
	}

	@Override
	public int getButtonCount() {
		return buttonNames.length;
	}

	@Override
	public String getButtonName(int index) {
		return buttonNames[index];
	}

	@Override
	public boolean isButtonPressed(int index) {
		return buttonState[index];
	}
	
	@Override
	public int getAxisCount() {
		return axisNames.length;
	}

	@Override
	public String getAxisName(int index) {
		return axisNames[index];
	}

	@Override
	public float getAxisValue(int index) {
		return axisValues[index];
	}
	
	@Override
	public int getRumblerCount() {
		return rumblerNames.length;
	}

	@Override
	public String getRumblerName(int index) {
		return rumblerNames[index];
	}

	@Override
	public void setRumblerStrength(int index, float strength) {
		methodProvider.setRumblerStrength(index, strength);
		poll();
	}
	
	@Override
	public float getDeadZone(int index) {
		return axisDeadZones[index];
	}

	@Override
	public void setDeadZone(int index, float zone) {
		methodProvider.setDeadZone(index, zone);
		poll();
	}

	@Override
	public float getPovX() {
		return povX;
	}

	@Override
	public float getPovY() {
		return povY;
	}

	@Override
	public float getXAxisValue() {
		return getAxisValue(specialID[0]);
	}

	@Override
	public float getXAxisDeadZone() {
		return getDeadZone(specialID[0]);
	}

	@Override
	public void setXAxisDeadZone(float zone) {
		setDeadZone(specialID[0], zone);
	}

	@Override
	public float getYAxisValue() {
		return getAxisValue(specialID[1]);
	}

	@Override
	public float getYAxisDeadZone() {
		return getDeadZone(specialID[1]);
	}

	@Override
	public void setYAxisDeadZone(float zone) {
		setDeadZone(specialID[1], zone);
	}

	@Override
	public float getZAxisValue() {
		return getAxisValue(specialID[2]);
	}

	@Override
	public float getZAxisDeadZone() {
		return getDeadZone(specialID[2]);
	}

	@Override
	public void setZAxisDeadZone(float zone) {
		setDeadZone(specialID[2], zone);
	}

	@Override
	public float getRXAxisValue() {
		return getAxisValue(specialID[3]);
	}

	@Override
	public float getRXAxisDeadZone() {
		return getDeadZone(specialID[3]);
	}

	@Override
	public void setRXAxisDeadZone(float zone) {
		setDeadZone(specialID[3], zone);
	}

	@Override
	public float getRYAxisValue() {
		return getAxisValue(specialID[4]);
	}

	@Override
	public float getRYAxisDeadZone() {
		return getDeadZone(specialID[4]);
	}

	@Override
	public void setRYAxisDeadZone(float zone) {
		setDeadZone(specialID[4], zone);
	}

	@Override
	public float getRZAxisValue() {
		return getAxisValue(specialID[5]);
	}

	@Override
	public float getRZAxisDeadZone() {
		return getDeadZone(specialID[5]);
	}

	@Override
	public void setRZAxisDeadZone(float zone) {
		setDeadZone(specialID[5], zone);
	}

}
