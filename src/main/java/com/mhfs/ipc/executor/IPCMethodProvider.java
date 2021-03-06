package com.mhfs.ipc.executor;

import java.util.ArrayList;
import java.util.List;

import com.mhfs.ipc.Method;

public class IPCMethodProvider {
	
	private List<Method<?>> providableMethods;
	private Object implementor;
	private IProviderSendHandler sender;
	
	public IPCMethodProvider(Class<?> wrappable, Object implementor, IProviderSendHandler sender) {
		if(wrappable == null || !wrappable.isInterface()) {
			throw new IllegalArgumentException("The given class is not an interface!");
		}
		if(!wrappable.isInstance(implementor)) {
			throw new IllegalArgumentException("The given object does not implement the interface represented by the given class!");
		}
		
		providableMethods = new ArrayList<Method<?>>();
		this.implementor = implementor;
		this.sender = sender;
		
		providableMethods.add(Method.of("getMethods", Method[].class));
		for(java.lang.reflect.Method method : wrappable.getDeclaredMethods()) {
			providableMethods.add(Method.of(method));
		}
	}
	
	public void invoke(int methodID, int invocationID, Object[] args) {
		try {
			invoke0(methodID, invocationID, args);
		} catch (Throwable t) {
			throw new RuntimeException("Error invoking method!", t);
		}
	}
	
	private void invoke0(int methodID, int invocationID, Object[] arguments) throws NoSuchMethodException, SecurityException {
		if(methodID == 0) {
			sender.sendInvocationResult(invocationID, getWrappedMethods().toArray(new Method[0]));
			return;
		}
		for(Method<?> method : providableMethods) {
			if(method.getCallID() == methodID) {
				java.lang.reflect.Method actual = implementor.getClass().getDeclaredMethod(method.getName(), method.getArgClasses());
				Thread t = new Thread(new RequestedExecution<Object>(invocationID, new ReflectiveMethodInvoker(this.implementor, actual, arguments), sender));
				t.setDaemon(true);
				t.setName("IPC Method Executor");
				t.start();
				return;
			}
		}
		throw new RuntimeException("The requested method couldn't be found!");
	}
	
	public List<Method<?>> getWrappedMethods() {
		return providableMethods;
	}
}
