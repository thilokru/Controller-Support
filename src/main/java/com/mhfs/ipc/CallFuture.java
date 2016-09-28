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
	
	@SuppressWarnings("unchecked")
	void onReturn(Object result) {
		this.finished = true;
		this.result = Optional.ofNullable((T)result);
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
	
	public void sync() throws InterruptedException {
		while(!finished) {
			Thread.sleep(1);
		}
	}
	
	public void syncUninteruptable (){
		while(!finished) {
			try {
				sync();
			} catch (InterruptedException e) {}
		}
	}
	
	public static <T> CallFuture<T> get(Method<T> method) {
		return new CallFuture<T>(method);
	}	
}
