package com.mhfs.controller.daemon;

import io.netty.buffer.ByteBuf;

public interface ProvidedMethods {

	/**
	 * @return all the data a controller can provide, serialized.
	 */
	public ByteBuf getControllerData();
	
	/**
	 * @return whether controllers are present and can be selected.
	 */
	public boolean hasControllers();
	
	/**
	 * @return whether the daemon needs to be restarted, e.g if a controller was disconnected.
	 */
	public boolean restartRequired();
	
	/**
	 * Returns when either a controller has been selected or {@link #stopControllerSelection()} has been called.
	 * @return true if controller has been selected, false if stopControllerSelection was called.
	 */
	public boolean awaitControllerSelection();
	
	/**
	 * Stops {@link #awaitControllerSelection()}.
	 */
	public void stopControllerSelection();

	//Controller Functions
	public void setDeadZone(int index, float zone);

	public void setRumblerStrength(int index, float strength);
}
