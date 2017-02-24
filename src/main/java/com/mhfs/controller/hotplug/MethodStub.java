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
		return manager.<ByteBuf>invoke("getControllerData").syncUninteruptable().getResult();
	}

	@Override
	public boolean hasControllers() {
		return manager.<Boolean>invoke("hasControllers").syncUninteruptable().getResult();
	}

	@Override
	public boolean restartRequired() {
		return manager.<Boolean>invoke("restartRequired").syncUninteruptable().getResult();
	}

	@Override
	public boolean awaitControllerSelection() {
		return manager.<Boolean>invoke("awaitControllerSelection").syncUninteruptable().getResult();
	}
	
	@Override
	public CallFuture<Boolean> startControllerSelection() {
		return manager.<Boolean>invoke("awaitControllerSelection");
	}

	@Override
	public void stopControllerSelection() {
		manager.<Void>invoke("stopControllerSelection").syncUninteruptable();
	}

	@Override
	public void setDeadZone(int index, float zone) {
		manager.<Void>invoke("setDeadZone", index, zone).syncUninteruptable();
	}

	@Override
	public void setRumblerStrength(int index, float strength) {
		manager.<Void>invoke("setRumblerStrength", index, strength).syncUninteruptable();
	}

}
