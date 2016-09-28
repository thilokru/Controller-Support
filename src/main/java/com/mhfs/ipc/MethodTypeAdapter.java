package com.mhfs.ipc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MethodTypeAdapter extends TypeAdapter<Method<?>> {

	@Override
	public void write(JsonWriter out, Method<?> value) throws IOException {
		out.beginObject();
		
		out.name("id");
		out.value(value.getCallID());
		
		out.name("name");
		out.value(value.getName());
		
		out.name("returnType");
		out.value(value.getReturnClass().getCanonicalName());
		
		out.name("argumentTypes");
		out.beginArray();
		for(Class<?> clazz : value.getArgClasses()) {
			out.value(clazz.getCanonicalName());
		}
		out.endArray();
		
		out.endObject();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Method<?> read(JsonReader in) throws IOException {
		int id = 0;
		String name = "";
		Class<?> retValue = null;
		Class<?>[] argValues = null;
		
		in.beginObject();
		while(in.hasNext()) {
			SWITCH: switch(in.nextName()) {
			case "id":
				id = in.nextInt();
				break SWITCH;
			case "name":
				name = in.nextString();
				break SWITCH;
			case "returnType":
				try {
					String clazzName = in.nextString();
					if(clazzName.equals("void")) {
						retValue = Void.class;
					} else {
						retValue = ClassUtils.getClass(clazzName);
					}
				} catch (ClassNotFoundException e) {
					throw new IOException(e);
				}
				break SWITCH;
			case "argumentTypes":
				List<Class<?>> list = new LinkedList<Class<?>>();
				in.beginArray();
				while(in.hasNext()) {
					try {
						list.add(ClassUtils.getClass(in.nextString()));
					} catch (ClassNotFoundException e) {
						throw new IOException(e);
					}
				}
				argValues = list.toArray(new Class<?>[0]);
				in.endArray();
				break SWITCH;
			}
		}
		in.endObject();
		return new Method(id, name, retValue, argValues);
	}

}
