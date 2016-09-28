package com.mhfs.controller.hotplug;

import java.net.InetSocketAddress;

import com.google.gson.Gson;
import com.mhfs.ipc.ISendHandler;
import com.mhfs.ipc.InvocationManager;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import static com.mhfs.controller.daemon.SerializationHelper.*;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<DatagramPacket> implements ISendHandler{

	private Channel channel;
	private InetSocketAddress address;
	private InvocationManager manager;
	
	public void init(Channel channel, InetSocketAddress address) {
		this.channel = channel;
		this.address = address;
	}
	
	@Override
	public void sendInvocationData(int methodID, int invocationID, Object[] args) throws Exception {
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(methodID);
		buf.writeInt(invocationID);
		buf.writeInt(args.length);
		for(Object object : args) {
			writeString(buf, object.getClass().getCanonicalName());
			writeString(buf, new Gson().toJson(object));
		}
		
		DatagramPacket packet = new DatagramPacket(buf, address);
		channel.writeAndFlush(packet);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		ByteBuf buf = msg.content();
		int id = buf.readInt();
		Class<?> clazz = Class.forName(readString(buf));
		Object val = new Gson().fromJson(readString(buf), clazz);
		manager.returnValueCallback(id, val);
	}

}
