package com.mhfs.controller.hotplug;

import java.io.File;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.mhfs.controller.config.Config;
import com.mhfs.controller.daemon.DaemonMain;
import com.mhfs.ipc.InvocationManager;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class DaemonManager {
	
	private static Process process;
	private static Channel channel;

	public static InvocationManager startDaemon() throws Exception {
		int port = findFreePort();
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String libPath = "-Djava.library.path=" + System.getProperty("java.library.path");
		String className = DaemonMain.class.getCanonicalName();
		String args = Config.INSTANCE.shouldDebugInput() ? "debugControllerInput" : "";

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, libPath, className, String.valueOf(port), args);
		builder.inheritIO();

		process = builder.start();
		Thread.sleep(2000);//Waiting for Daemon to start.
		
		ClientNetworkHandler cnw = new ClientNetworkHandler();
		InetSocketAddress adr = new InetSocketAddress("localhost", port);
		
		Bootstrap b = new Bootstrap();
		b.group(new NioEventLoopGroup());
		b.channel(NioDatagramChannel.class);
		b.handler(cnw);
		ChannelFuture future = b.connect(adr).syncUninterruptibly();
		
		cnw.init(future.channel(), adr);
		InvocationManager manager = new InvocationManager(cnw);
		cnw.setInvocationManager(manager);
		manager.init();
		
		Thread t = new Thread(() -> {
			future.channel().closeFuture().syncUninterruptibly();
			b.group().shutdownGracefully();
		});
		t.setDaemon(true);
		t.setName("Netty-IPC Shutdown Waiter");
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> stopDaemon()));
		
		return manager;
	}
	
	private static int findFreePort() throws SocketException {
		DatagramSocket socket = new DatagramSocket();
		int port = socket.getLocalPort();
		socket.close();
		return port;
	}

	public static void stopDaemon() {
		if(process != null) {
			process.destroy();
		}
		if(channel != null) {
			channel.close();
		}
	}
}
