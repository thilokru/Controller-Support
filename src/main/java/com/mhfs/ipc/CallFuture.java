package com.mhfs.ipc;

import java.util.Optional;

public class CallFuture<T> {
	
	private static int currentID = 0;

	private int invocationID;
	private boolean finished;
	private Optional<T> result;
	private Method<T> method;
	
	private CallFuture(Method<T> method) {
		this.invocationID = currentID++;
		this.method = method;
	}
	
	public CallFuture() {
		this(null);
	}

	@SuppressWarnings("unchecked")
	void onReturn(Object result) {
		synchronized(this) {
			this.result = Optional.ofNullable((T)result);
			this.finished = true;
		}
	}
	
	int getID() {
		return invocationID;
	}
	
	Method<T> getMethod() {
		return method;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean hasResult() {
		return result.isPresent();
	}
	
	public T getResult() {
		return result.get();
	}
	
	public CallFuture<T> sync() throws InterruptedException {
		while(!finished) {
			Thread.sleep(0);
		}
		return this;
	}
	
	public CallFuture<T> syncUninteruptable (){
		while(!finished) {
			try {
				sync();
			} catch (InterruptedException e) {}
		}
		return this;
	}
	
	public static <T> CallFuture<T> get(Method<T> method) {
		return new CallFuture<T>(method);
	}

	static CallFuture<Method<?>[]> getExchangeFuture() {
		return new CallFuture<Method<?>[]>();
	}	
}
