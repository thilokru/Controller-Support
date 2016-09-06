package com.mhfs.controller.mappings.actions;

public interface IParametrizedAction<T> extends IAction {

	public void run(T arg);
}
