package com.mhfs.ipc.executor;

public interface IProviderSendHandler {

	public void sendInvocationResult(int invocationID, Object result);
}
