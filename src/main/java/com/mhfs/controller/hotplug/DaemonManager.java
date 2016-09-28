package com.mhfs.controller.hotplug;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

import com.mhfs.controller.daemon.DaemonMain;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class DaemonManager {
	
	private static Process process;
	private static Channel channel;

	public static void startDaemon() {
		int port = 53356; //TODO find free udp port;
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String libPath = "-Djava.library.path=" + System.getProperty("java.library.path");
		String className = DaemonMain.class.getCanonicalName();

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, libPath, className, String.valueOf(port));
		builder.inheritIO();

		try {
			process = builder.start();
		} catch (IOException e) {
			throw new RuntimeException("Error starting daemon!", e);
		}
		
		ClientNetworkHandler cnw = new ClientNetworkHandler();
		
		InetSocketAddress adr = new InetSocketAddress("localhost", port);
		
		Bootstrap b = new Bootstrap();
		b.group(new NioEventLoopGroup());
		b.channel(NioDatagramChannel.class);
		b.handler(cnw);
		ChannelFuture future = b.connect(adr).syncUninterruptibly();
		cnw.init(future.channel(), adr);

		Thread t = new Thread(() -> {
			future.channel().closeFuture().syncUninterruptibly();
			b.group().shutdownGracefully();
		});
		t.setDaemon(true);
		t.setName("Netty-IPC Shutdown Waiter");
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> stopDaemon()));
	}
	
	public static void stopDaemon() {
		if(process != null) {
			process.destroy();
			channel.close();
		}
	}
}
