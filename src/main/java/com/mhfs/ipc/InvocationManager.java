package com.mhfs.ipc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InvocationManager {

	private List<CallFuture<?>> invocations;
	private ISendHandler sendHandler;
	
	public InvocationManager(ISendHandler sendHandler) {
		this.sendHandler = sendHandler;
		this.invocations = new LinkedList<CallFuture<?>>();
	}
	
	public <T> CallFuture<T> invoke(Method<T> method, Object... args) {
		preInvocationChecks(method, args);
		CallFuture<T> future = CallFuture.get(method);
		synchronized(invocations) {
			invocations.add(future);
		}
		try {
			sendHandler.sendBytes(method.getCallID(), future.getID(), args);
		} catch (Exception e) {
			throw new RuntimeException("Error sending invocation data!", e);
		}
		return future;
	}
	
	public void returnValueCallback(int futureID, Object value) {
		Iterator<CallFuture<?>> it = invocations.iterator();
		while (it.hasNext()){
			CallFuture<?> future = it.next();
			if(future.getID() == futureID) {
				future.onReturn(value);
				it.remove();
				return;
			}
		}
		throw new RuntimeException(String.format("The invocation with the call id '%d' couldn't be found!", futureID));
	}

	private void preInvocationChecks(Method<?> method, Object[] args) {
		Class<?>[] validArgs = method.getArgClasses();
		if(validArgs.length != args.length) {
			throw new IllegalArgumentException(String.format("Method '%s' accepts %d arguments, but got %d!", method.getName(), validArgs.length, args.length));
		}
		for(int i = 0; i < validArgs.length; i++) {
			if(!validArgs[i].isAssignableFrom(args[i].getClass())) {
				throw new ClassCastException(String.format("Method '%s' expected '%s' as argument at index %d, but got '%s'!", method.getName(), validArgs[i].getCanonicalName(), i, args[i].getClass().getCanonicalName()));
			}
		}
	}
}
