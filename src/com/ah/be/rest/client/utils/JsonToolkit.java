package com.ah.be.rest.client.utils;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JsonToolkit {
	
	private static Log log = LogFactory.getLog(JsonToolkit.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T getObjectFromJsonString(String source, Class beanClass,
			String[] excludes) {
		T result = null;
		JsonConfig jsonConfig = new JsonConfig();
		if (excludes != null && excludes.length != 0) {
			jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(excludes);
		}
		try {
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(source,
					jsonConfig);
			result = (T) JSONObject.toBean(jsonObject, beanClass);
		} catch (JSONException ex) {
			log.error(ex);
		}

		return result;
	}
	
	public static DynaBean getObjectFromJsonString(String source,
			String[] excludes) {

		DynaBean result = null;
		JsonConfig jsonConfig = new JsonConfig();
		if (excludes != null && excludes.length != 0) {
			jsonConfig = new JsonConfig();
			jsonConfig.setExcludes(excludes);
		}
		try {

			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(source,
					jsonConfig);
			result = (DynaBean) JSONObject.toBean(jsonObject);
		} catch (JSONException ex) {
			log.error(ex);
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> List<T> getListFromJsonString(String source,
			Class beanClass, String[] excludes) {
		List<T> result = null;
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setRootClass(beanClass);
		if (excludes != null && excludes.length != 0) {
			jsonConfig.setExcludes(excludes);
		}
		try {
			JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(source,
					jsonConfig);
			result = (List<T>) JSONSerializer.toJava(jsonArray, jsonConfig);
		} catch (JSONException ex) {
			log.error(ex);
		}

		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<DynaBean> getListFromJsonString(String source,
			String[] excludes) {
		List<DynaBean> result = null;
		JsonConfig jsonConfig = new JsonConfig();
		if (excludes != null && excludes.length != 0) {
			jsonConfig.setExcludes(excludes);
		}
		try {
			JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(source,
					jsonConfig);

			result = (List<DynaBean>) JSONSerializer.toJava(jsonArray);
		} catch (JSONException ex) {
			log.error(ex);
		}

		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T[] getArrayFromJsonString(String source,
			Class beanClass, String[] excludes) {
		T[] result = null;
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		jsonConfig.setRootClass(beanClass);
		if (excludes != null && excludes.length != 0) {
			jsonConfig.setExcludes(excludes);
		}
		try {
			JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(source,
					jsonConfig);
			result = (T[]) JSONSerializer.toJava(jsonArray, jsonConfig);
		} catch (JSONException ex) {
			log.error(ex);
		}

		return result;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static DynaBean[] getArrayFromJsonString(String source, String[] excludes) {
		DynaBean[] result = null;
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setArrayMode(JsonConfig.MODE_OBJECT_ARRAY);
		if (excludes != null && excludes.length != 0) {
			jsonConfig.setExcludes(excludes);
		}
		try {
			JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(source,
					jsonConfig);
			result = (DynaBean[])JSONSerializer.toJava(jsonArray, jsonConfig);
		} catch (JSONException ex) {
			log.error(ex);
		}

		return result;
	}



}
