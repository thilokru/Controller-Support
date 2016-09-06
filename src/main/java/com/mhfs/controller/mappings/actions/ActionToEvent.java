package com.mhfs.controller.mappings.actions;

/**
 * @author Thilo
 *
 * @param <T> If you don't require extra values, just make it Object.
 */
public abstract class ActionToEvent<T> implements IParametrizedAction<T> {
	
	private boolean allowToggleOn = true, allowToggleOff = false;

	@Override
	public final void run() {
		if(allowToggleOn)
			buttonDown();
		this.allowToggleOn = false;
		this.allowToggleOff = true;
	}
	
	public final void run(T arg) {
		if(allowToggleOn)
			if(!buttonDown(arg))
				buttonDown();
		this.allowToggleOn = false;
		this.allowToggleOff = true;
	}

	@Override
	public final void notRun() {
		if(allowToggleOff)
			buttonUp();
		this.allowToggleOn = true;
		this.allowToggleOff = false;
	}
	
	public abstract void buttonDown();
	
	/**
	 * If you want the parameter, you need to override this method and return true.
	 * @param arg the given argument / parameter
	 * @return if you have implemented this method.
	 */
	public boolean buttonDown(T arg) {
		return false;
	}

	public abstract void buttonUp();
}
