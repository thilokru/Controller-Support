package com.mhfs.controller.daemon;

import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhfs.ipc.Method;
import com.mhfs.ipc.MethodTypeAdapter;
import com.mhfs.ipc.executor.IPCMethodProvider;
import com.mhfs.ipc.executor.IProviderSendHandler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import static com.mhfs.controller.daemon.SerializationHelper.*;

public class NetworkHandler extends SimpleChannelInboundHandler<DatagramPacket> implements IProviderSendHandler {

	private static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Method.class, new MethodTypeAdapter()).create();
	private IPCMethodProvider provider;
	private Channel channel;
	private InetSocketAddress sender;
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		channel = ctx.channel();
		sender = msg.sender();

		ByteBuf content = msg.content();
		
		int methodID = content.readInt();
		int invID = content.readInt();
		int argCount = content.readInt();
		Object[] args = new Object[argCount];
		for(int i = 0; i < argCount; i++) {
			Class<?> clazz = Class.forName(readString(content));
			args[i] = gson.fromJson(readString(content), clazz);
		}

		provider.invoke(methodID, invID, args);
	}

	@Override
	public void sendInvocationResult(int invocationID, Object result) {
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(invocationID);
		writeString(buf, result.getClass().getCanonicalName());
		if(result instanceof ByteBuf) {
			ByteBuf res = (ByteBuf) result;
			buf.writeBytes(res);
		} else {
			String json = gson.toJson(result);
			writeString(buf, json);
		}
		
		DatagramPacket msg = new DatagramPacket(buf, sender);
		channel.writeAndFlush(msg);
	}

	public void setProvider(IPCMethodProvider provider) {
		this.provider = provider;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("Daemon Network Error:");
		cause.printStackTrace();
	}
}
