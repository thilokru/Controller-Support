package com.mhfs.controller.daemon;

import java.lang.reflect.Field;

import org.lwjgl.input.Controller;

import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class SerializationHelper {

	public static void writeString(ByteBuf buf, String string) {
		buf.writeInt(string.length());
		buf.writeBytes(string.getBytes());
	}
	
	public static String readString(ByteBuf buf) {
		int length = buf.readInt();
		byte[] data = new byte[length];
		buf.readBytes(data);
		return new String(data);
	}
	
	public static void sync(BeanController controller, ByteBuf buf) {
		controller.id = buf.readInt();
		controller.name = readString(buf);
		
		int buttonCount = buf.readInt();
		if(controller.buttonNames == null || controller.buttonNames.length != buttonCount)
			controller.buttonNames = new String[buttonCount];
		if(controller.buttonState == null || controller.buttonState.length != buttonCount)
			controller.buttonState = new boolean[buttonCount];
		for(int i = 0; i < buttonCount; i++) {
			controller.buttonNames[i] = readString(buf);
			controller.buttonState[i] = buf.readBoolean();
		}
		
		int axisCount = buf.readInt();
		if(controller.axisNames == null || controller.axisNames.length != axisCount)
			controller.axisNames = new String[axisCount];
		if(controller.axisValues == null || controller.axisValues.length != axisCount)
			controller.axisValues = new float[axisCount];
		if(controller.axisDeadZones == null || controller.axisDeadZones.length != axisCount)
			controller.axisDeadZones = new float[axisCount];
		for(int i = 0; i < axisCount; i++) {
			controller.axisNames[i] = readString(buf);
			controller.axisValues[i] = buf.readFloat();
			controller.axisDeadZones[i] = buf.readFloat();
		}
		
		int rumblerCount = buf.readInt();
		if(controller.rumblerNames == null || controller.rumblerNames.length != rumblerCount)
			controller.rumblerNames = new String[rumblerCount];
		for(int i = 0; i < rumblerCount; i++) {
			controller.rumblerNames[i] = readString(buf);
		}
		
		controller.povX = buf.readFloat();
		controller.povY = buf.readFloat();
		
		int specialIDCount = buf.readInt();
		if(controller.specialID == null || controller.specialID.length != specialIDCount)
			controller.specialID = new int[specialIDCount];
		for(int i = 0; i < specialIDCount; i++) {
			controller.specialID[i] = buf.readInt();
		}
	}
	
	public static ByteBuf serializeControllerData(Controller controller) {
		ByteBuf buf = Unpooled.buffer();
		buf.writeInt(controller.getIndex()); //Controller ID
		writeString(buf, controller.getName()); //Controller Name
		
		buf.writeInt(controller.getButtonCount()); //Buttons
		for(int i = 0; i < controller.getButtonCount(); i++) {
			writeString(buf, controller.getButtonName(i));
			buf.writeBoolean(controller.isButtonPressed(i));
		}
		
		buf.writeInt(controller.getAxisCount()); //Axes
		for(int i = 0; i < controller.getAxisCount(); i++) {
			writeString(buf, controller.getAxisName(i));
			buf.writeFloat(controller.getAxisValue(i));
			buf.writeFloat(controller.getDeadZone(i));
		}
		
		buf.writeInt(controller.getRumblerCount()); //Rumblers
		for(int i = 0; i < controller.getRumblerCount(); i++) {
			writeString(buf, controller.getRumblerName(i));
		}
		
		buf.writeFloat(controller.getPovX());
		buf.writeFloat(controller.getPovY());
		
		int[] specialIds = getSpecialIds(controller);
		buf.writeInt(specialIds.length);
		for(int id : specialIds) {
			buf.writeInt(id);
		}
		return buf;
	}

	private static int[] getSpecialIds(Controller controller) {
		try {
			Class<?> targetClazz = Class.forName("org.lwjgl.input.JInputController");
			if(targetClazz.isInstance(controller)) {
				int[] ret = new int[6];
				ret[0] = getFieldValue(controller, "xaxis");
				ret[1] = getFieldValue(controller, "yaxis");
				ret[2] = getFieldValue(controller, "zaxis");
				ret[3] = getFieldValue(controller, "rxaxis");
				ret[4] = getFieldValue(controller, "ryaxis");
				ret[5] = getFieldValue(controller, "rzaxis");
				return ret;
			}
		} catch (Exception e) {
			Throwables.propagate(e);
		}
		return null;
	}

	private static int getFieldValue(Controller controller, String name) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> targetClazz = controller.getClass();
		Field field = targetClazz.getDeclaredField(name);
		field.setAccessible(true);
		return field.getInt(controller);
	}
}
