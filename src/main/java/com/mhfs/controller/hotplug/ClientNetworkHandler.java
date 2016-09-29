package com.mhfs.controller.hotplug;

import java.net.InetSocketAddress;

import org.apache.commons.lang3.ClassUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhfs.controller.ControllerSupportMod;
import com.mhfs.ipc.ISendHandler;
import com.mhfs.ipc.InvocationManager;
import com.mhfs.ipc.Method;
import com.mhfs.ipc.MethodTypeAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import static com.mhfs.controller.daemon.SerializationHelper.*;

public class ClientNetworkHandler extends SimpleChannelInboundHandler<DatagramPacket> implements ISendHandler {

	private Channel channel;
	private InetSocketAddress address;
	private InvocationManager manager;

	private static Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Method.class, new MethodTypeAdapter()).create();

	public void init(Channel channel, InetSocketAddress address) {
		this.channel = channel;
		this.address = address;
	}
	
	public void setInvocationManager(InvocationManager manager) {
		this.manager = manager;
	}

	@Override
	public void sendInvocationData(int methodID, int invocationID, Object[] args) throws Exception {
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(methodID);
		buf.writeInt(invocationID);
		buf.writeInt(args.length);
		for (Object object : args) {
			writeString(buf, object.getClass().getCanonicalName());
			writeString(buf, gson.toJson(object));
		}

		DatagramPacket packet = new DatagramPacket(buf, address);
		channel.writeAndFlush(packet);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
		ByteBuf buf = msg.content();
		int id = buf.readInt();
		String clazzName = readString(buf);
		Class<?> clazz = ClassUtils.getClass(clazzName);
		Object val;
		if(ByteBuf.class.isAssignableFrom(clazz)) {
			val = buf.copy();
		} else {
			String json = readString(buf);
			val = gson.fromJson(json, clazz);
		}
		manager.returnValueCallback(id, val);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ControllerSupportMod.LOG.error("Network Error:", cause);
	}

}
