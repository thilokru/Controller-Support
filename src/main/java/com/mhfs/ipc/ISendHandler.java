package com.mhfs.ipc;

public interface ISendHandler {

	public void sendInvocationData(int methodID, int invocationID, Object[] args) throws Exception;
}
