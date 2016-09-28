package com.mhfs.controller.daemon;

import java.net.InetSocketAddress;

import com.google.gson.Gson;
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
			args[i] = new Gson().fromJson(readString(content), clazz);
		}

		provider.invoke(methodID, invID, args);
	}

	@Override
	public void sendInvocationResult(int invocationID, Object result) {
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(invocationID);
		writeString(buf, result.getClass().getCanonicalName());
		String json = new Gson().toJson(result);
		writeString(buf, json);
		
		DatagramPacket msg = new DatagramPacket(buf, sender);
		channel.writeAndFlush(msg);
	}

	public void setProvider(IPCMethodProvider provider) {
		this.provider = provider;
	}

}
