package com.mhfs.ipc.executor;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ReflectiveMethodInvoker implements Callable<Object> {

	private Object implementor;
	private Method invokable;
	private Object[] args;
	
	public ReflectiveMethodInvoker(Object implementor, Method invokable, Object[] args) {
		this.implementor = implementor;
		this.invokable = invokable;
		this.args = args;
	}
	
	@Override
	public Object call() throws Exception {
		invokable.setAccessible(true);
		return invokable.invoke(implementor, args);
	}

}
