package com.mhfs.ipc.executor;

import java.util.concurrent.Callable;

public class RequestedExecution<T> implements Runnable{

	private int invocationID;
	private Callable<T> task;
	private IProviderSendHandler sendHandler;
	
	public RequestedExecution(int invID, Callable<T> task, IProviderSendHandler sendHandler) {
		this.invocationID = invID;
		this.task = task;
	}
	
	@Override
	public void run() {
		T retValue = null;
		try {
			retValue = task.call();
		} catch (Exception e) {
			throw new RuntimeException("Error in task!", e);
		}
		sendHandler.sendInvocationResult(invocationID, retValue);
	}

}
