package com.mhfs.controller.daemon;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

import com.mhfs.ipc.executor.IPCMethodProvider;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class DaemonMain implements ProvidedMethods{
	
	private static NetworkHandler handler;
	private boolean controllerSelected;
	private Controller selectedController;

	public static void main(String[] args) {
		try {
			Controllers.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		System.out.println("Starting IPC-Server");
		int port = Integer.parseInt(args[0]);
		
		handler = new NetworkHandler();
		IPCMethodProvider provider = new IPCMethodProvider(ProvidedMethods.class, new DaemonMain(), handler);
		handler.setProvider(provider);
		
		EventLoopGroup eventGroup = new NioEventLoopGroup();
		
		Bootstrap b = new Bootstrap();
		b.group(eventGroup);
		b.channel(NioDatagramChannel.class);
		b.handler(handler);
		ChannelFuture future = b.bind(port).syncUninterruptibly();
		
		Thread t = new Thread(() -> {
			future.channel().closeFuture().syncUninterruptibly();
			b.group().shutdownGracefully();
		});
		t.setDaemon(true);
		t.setName("Netty-IPC Shutdown Waiter");
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			future.channel().close();
			Controllers.destroy();
		}));
	}

	@Override
	public ByteBuf getControllerData() {
		return SerializationHelper.serializeControllerData(selectedController);
	}

	@Override
	public boolean hasControllers() {
		return Controllers.getControllerCount() != 0;
	}

	@Override
	public boolean restartRequired() {
		if(selectedController == null) return false;
		return checkControllerRemoved(selectedController);
	}

	@Override
	public void awaitControllerSelection() {
		Controllers.clearEvents();
		while(!controllerSelected) {
			if(Controllers.next()) {
				if(Controllers.isEventButton()) {
					controllerSelected = true;
					selectedController = Controllers.getEventSource();
				}
			}
		}
	}

	@Override
	public void stopControllerSelection() {
		controllerSelected = true;
		selectedController = null;
	}
	
	private static boolean checkControllerRemoved(Controller controller) {
		Object jInputController = getController(controller);
		if(!available(jInputController)) {
			return true;
		}
		return false;
	}

	private static boolean available(Object theShouldBeAController) {
		try {
			Class<?> theClazz = theShouldBeAController.getClass();
			Method pollMethod = theClazz.getMethod("poll");
			pollMethod.setAccessible(true);
			return (boolean) pollMethod.invoke(theClazz.cast(theShouldBeAController));
		} catch (Exception e) {
			throw new RuntimeException("Error reflecting on net.java.games.input.Controller!", e);
		}
	}
	
	private static Object getController(Controller controller) {
		try {
			Class<?> wrapperClazz = Class.forName("org.lwjgl.input.JInputController");
			if(wrapperClazz.isInstance(controller)) {
				Field wrappedField = wrapperClazz.getDeclaredField("target");
				wrappedField.setAccessible(true);
				Object value = wrappedField.get(controller);
				return value;
			}
		} catch (Throwable t) {
			throw new RuntimeException("Error reflecting on org.lwjgl.input.JInputController!", t);
		}
		return null;
	}

	@Override
	public void setDeadZone(int index, float zone) {
		selectedController.setDeadZone(index, zone);
	}

	@Override
	public void setRumblerStrength(int index, float strength) {
		selectedController.setRumblerStrength(index, strength);
	}
}
