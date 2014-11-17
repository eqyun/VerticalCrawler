package com.pada.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtil {
	/**
	 * 获取当前target的某一属性值
	 * 
	 * @param target
	 * @param fieldName
	 * @return
	 */
	public static <T> T getFieldValue(Object target, String fieldName) {
		if (target == null) {
			return null;
		}
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			boolean flag = field.isAccessible();
			field.setAccessible(true);
			Object obj = field.get(target);
			field.setAccessible(flag);
			return (T) obj;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setFieldValue(Object target, String fieldName,
			Object fieldValue) {
		if (target == null) {
			return;
		}
		try {
			Field field = target.getClass().getDeclaredField(fieldName);
			if (field == null)
				return;
			boolean flag = field.isAccessible();
			field.setAccessible(true);
			field.set(target, fieldValue);
			field.setAccessible(flag);
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, Object> getfieldsValueMap(Object target) {
		Field[] fields = target.getClass().getDeclaredFields();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : fields) {
			String key = field.getName();
			Object value = getFieldValue(target, key);
			if (value != null)
				map.put(key, value);
		}
		return map;
	}
}