package com.mhfs.controller.hotplug;

import com.mhfs.controller.daemon.ProvidedMethods;
import com.mhfs.ipc.InvocationManager;

import io.netty.buffer.ByteBuf;

public class MethodStub implements ProvidedMethods{
	
	private InvocationManager manager;
	
	public MethodStub(InvocationManager manager) {
		this.manager = manager;
	}

	@Override
	public ByteBuf getControllerData() {
		return (ByteBuf) manager.invoke("getControllerData").syncUninteruptable().getResult();
	}

	@Override
	public boolean hasControllers() {
		return (boolean) manager.invoke("hasControllers").syncUninteruptable().getResult();
	}

	@Override
	public boolean restartRequired() {
		return (boolean) manager.invoke("restartRequired").syncUninteruptable().getResult();
	}

	@Override
	public void awaitControllerSelection() {
		manager.invoke("awaitControllerSelection").syncUninteruptable();
	}

	@Override
	public void stopControllerSelection() {
		manager.invoke("stopControllerSelection").syncUninteruptable();
	}

	@Override
	public void setDeadZone(int index, float zone) {
		manager.invoke("setDeadZone", index, zone).syncUninteruptable();
	}

	@Override
	public void setRumblerStrength(int index, float strength) {
		manager.invoke("setRumblerStrength", index, strength).syncUninteruptable();
	}

}
