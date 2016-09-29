package com.mhfs.controller.mappings.controlls;

import com.mhfs.controller.mappings.conditions.GameContext;

public interface IControll<T> {
	
	public boolean check(GameContext context);
	
	public String toSaveString();
	
	public boolean hasAdditionalData();
	
	public T getData(GameContext context);

	public String getControllName();
	
	public void enablePhantomProtection();
	
	public boolean shouldEnablePhantomProtection(IControll<?> controll);
}
