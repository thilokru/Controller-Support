package com.mhfs.ipc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class InvocationManager {

	private List<CallFuture<?>> invocations;
	private ISendHandler sendHandler;

	private List<Method<?>> methods;

	public InvocationManager(ISendHandler sendHandler) {
		this.sendHandler = sendHandler;
		this.invocations = new LinkedList<CallFuture<?>>();
	}

	public void init() throws Exception {
		CallFuture<Method<?>[]> future = CallFuture.getExchangeFuture();
		invocations.add(future);
		sendHandler.sendInvocationData(0, future.getID(), new Object[0]);
		future.syncUninteruptable();
		methods = new ArrayList<Method<?>>();
		for (Method<?> method : future.getResult()) {
			methods.add(method);
		}
	}

	public CallFuture<?> invoke(String name, Object... args) {
		for (Method<?> method : methods) {
			if (method.getName().equals(name)) {
				return invoke(method, args);
			}
		}
		return null;
	}

	public <T> CallFuture<T> invoke(Method<T> method, Object... args) {
		preInvocationChecks(method, args);
		CallFuture<T> future = CallFuture.get(method);
		synchronized (invocations) {
			invocations.add(future);
		}
		try {
			sendHandler.sendInvocationData(method.getCallID(), future.getID(), args);
		} catch (Exception e) {
			throw new RuntimeException("Error sending invocation data!", e);
		}
		return future;
	}

	public void returnValueCallback(int futureID, Object value) {
		synchronized (invocations) {
			Iterator<CallFuture<?>> it = invocations.iterator();
			while (it.hasNext()) {
				CallFuture<?> future = it.next();
				if (future.getID() == futureID) {
					future.onReturn(value);
					it.remove();
					return;
				}
			}
		}
		throw new RuntimeException(String.format("The invocation with the call id '%d' couldn't be found!", futureID));
	}

	private void preInvocationChecks(Method<?> method, Object[] args) {
		Class<?>[] validArgs = method.getArgClasses();
		if (validArgs == null) {
			throw new NullPointerException(String.format("Method '%s' has a NULL arg class list!", method.getName()));
		}
		if (validArgs.length != args.length) {
			throw new IllegalArgumentException(String.format("Method '%s' accepts %d arguments, but got %d!", method.getName(), validArgs.length, args.length));
		}
		for (int i = 0; i < validArgs.length; i++) {
			if (!validArgs[i].isAssignableFrom(args[i].getClass())) {
				if(checkPrimitive(validArgs[i], args[i])) {
					continue;
				}
				throw new ClassCastException(String.format("Method '%s' expected '%s' as argument at index %d, but got '%s'!", method.getName(), validArgs[i].getCanonicalName(), i, args[i].getClass().getCanonicalName()));
			}
		}
	}
	
	private boolean checkPrimitive(Class<?> validArg, Object arg) {
		if(int.class.equals(validArg) && arg instanceof Integer) {
			return true;
		} else if(byte.class.equals(validArg) && (arg instanceof Byte)) {
			return true;
		} else if(long.class.equals(validArg) && (arg instanceof Long)) {
			return true;
		} else if(boolean.class.equals(validArg) && (arg instanceof Boolean)) {
			return true;
		} else if(double.class.equals(validArg) && (arg instanceof Double)) {
			return true;
		} else if(float.class.equals(validArg) && (arg instanceof Float)) {
			return true;
		}
		return false;
	}
}
