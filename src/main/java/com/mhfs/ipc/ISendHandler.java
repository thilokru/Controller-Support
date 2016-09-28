package com.mhfs.ipc;

public interface ISendHandler {

	public void sendBytes(int methodID, int invocationID, Object[] args) throws Exception;
}
