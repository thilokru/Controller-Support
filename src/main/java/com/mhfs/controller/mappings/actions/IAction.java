package com.mhfs.controller.mappings.actions;

public interface IAction {

	public void run();
	
	public void notRun();
	
	/**
	 * For Serialization and loading. E.g. "LEFT_CLICK".
	 * @return a string representing the action when serialized.
	 */
	public String getActionName();
	
	/**
	 * Returns a human readable description, to be displayed on screen.
	 * @return a human readable description, to be displayed on screen.
	 */
	public String getActionDescription();
}
