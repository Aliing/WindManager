package com.ah.be.parameter.device;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeepCloneUtil {

	public static List<Field> getAllFieads(Object o) {

		List<Field> fields = new ArrayList<Field>();

		if (null == o)
			return fields;

		Class<?> type = o.getClass();
		do {
			for (Field f : type.getDeclaredFields()) {
				fields.add(f);
			}
			type = type.getSuperclass();
		} while (null != type);

		return fields;

	}
	
	public static boolean isSimpleClass(Class<?> type) {
		if (type.isPrimitive()) {
			return true;
		}

		if (type.equals(String.class))
			return true;
		if (type.equals(Long.class))
			return true;
		if (type.equals(Boolean.class))
			return true;
		if (type.equals(Short.class))
			return true;
		if (type.equals(Integer.class))
			return true;
		if (type.equals(Character.class))
			return true;

		if (type.equals(Float.class))
			return true;

		if (type.equals(Double.class))
			return true;
		if (type.equals(Byte.class))
			return true;

		return false;
	}

	public static boolean isSimpleObject(Object o) {
		return isSimpleClass(o.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T> T cloneObject(Class<T> clazz, Object o) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		if (null == o)
			return null;

		Map<Object, Object> map = new HashMap<Object, Object>();
		return (T)cloneObject(o, map);
	}

	private static Object cloneObject(Object o, Map<Object, Object> map)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		if (null == o)
			return null;
		Object newInstance = null;

		newInstance = map.get(o);
		if (null != newInstance) {
			return newInstance;
		}

		if (isSimpleObject(o))
			return o;

		if (o.getClass().isArray()) {
			return cloneArray(o, map);
		}

		Class<?> type = o.getClass();
		newInstance = type.newInstance();
		map.put(o, newInstance);

		cloneFields(o, newInstance, map);

		return newInstance;
	}

	private static Object cloneArray(Object o, Map<Object, Object> map)
			throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		if (null == o)
			return null;

		if (!o.getClass().isArray()) {
			return cloneObject(o, map);
		}

		int len = Array.getLength(o);

		Object array = Array.newInstance(o.getClass().getComponentType(), len);
		map.put(o, array);

		for (int i = 0; i < len; i++) {
			Array.set(array, i, cloneObject(Array.get(o, i), map));
		}

		return array;
	}

	private static void cloneFinalObject(Object object, Object newObject,
			Map<Object, Object> map) throws IllegalArgumentException,
			IllegalAccessException, InstantiationException {
		if (object == null || newObject == null || object == newObject
				|| !newObject.getClass().equals(newObject.getClass()))
			return;

		if (null != map.get(newObject)) {
			return;
		}
		map.put(newObject, newObject);

		cloneFields(object, newObject, map);

		return;
	}

	private static void cloneFields(Object object, Object newObject,
			Map<Object, Object> map) throws SecurityException,
			IllegalArgumentException, IllegalAccessException,
			InstantiationException {

		if (null == object || null == newObject) {
			return;
		}
		List<Field> fields = getAllFieads(object);

		for (Field f : fields) {
			if (Modifier.isStatic(f.getModifiers()))
				continue;

			if (Modifier.isFinal(f.getModifiers())) {
				cloneFinalObject(f.get(object), f.get(newObject), map);
			} else {
				f.setAccessible(true);
				f.set(newObject, cloneObject(f.get(object), map));
			}

		}
	}
}