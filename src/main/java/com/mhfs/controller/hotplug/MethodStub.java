package com.mhfs.controller.hotplug;

import com.mhfs.ipc.CallFuture;
import com.mhfs.ipc.InvocationManager;

import io.netty.buffer.ByteBuf;

public class MethodStub implements ExtendedMethods{
	
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
	public boolean awaitControllerSelection() {
		return (boolean) manager.invoke("awaitControllerSelection").syncUninteruptable().getResult();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public CallFuture<Boolean> startControllerSelection() {
		return (CallFuture<Boolean>) manager.invoke("awaitControllerSelection");
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
