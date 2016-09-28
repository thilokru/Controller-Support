package com.mhfs.ipc;

public class Method<T> {
	
	private static int currentID = 0;

	private final String name;
	private final Class<?>[] args;
	private final Class<T> returnClazz;
	private final int methodID;
	
	private Method(String name, Class<T> returnValue, Class<?>[] argClazzes) {
		this.methodID = currentID++;
		this.name = name;
		this.returnClazz = returnValue;
		this.args = argClazzes;
	}
	
	public String getName() {
		return name;
	}
	
	public Class<?>[] getArgClasses() {
		return args;
	}
	
	public Class<T> getReturnClass() {
		return returnClazz;
	}
	
	public boolean hasReturnValue() {
		return returnClazz == null || returnClazz.isAssignableFrom(Void.class);
	}
	
	public int getCallID() {
		return methodID;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Method<?>  of(java.lang.reflect.Method method) {
		return new Method(method.getName(), method.getReturnType(), method.getParameterTypes());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Method<?> of(String name, Class<?> retType, Class<?>... argTypes) {
		return new Method(name, retType, argTypes);
	}
}
