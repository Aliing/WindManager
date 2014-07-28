package com.ah.be.config.cli.generate.tools.classloader;

import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.jfree.util.Log;

import com.ah.be.parameter.device.DeepCloneUtil;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;

public class ConfigBoContext {
	
	private Map<Class<?>, List<Field>> classFieldMap = new HashMap<>();
	
	private static ConfigBoContext instance = new ConfigBoContext();
	
	private ConfigBoContext(){
		init();
	}
	
	private void init(){
		treeWalkClass(HiveAp.class);
	}
	
	public static ConfigBoContext getInstance(){
		return instance;
	}
	
	public Map<Class<?>, List<Object>> getAllChildObj(Object hmBo){
		Map<Class<?>, List<Object>> resMap = new HashMap<Class<?>, List<Object>>();
		try {
			treeWalkObj(hmBo, resMap);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			Log.error(e.getMessage(), e);
		}
		
		//filter same profile.
		Iterator<Entry<Class<?>, List<Object>>> resItems = resMap.entrySet().iterator();
		Set<Long> tempSet = new HashSet<Long>();
		while(resItems.hasNext()){
			Entry<Class<?>, List<Object>> entryObj = resItems.next();
			if(!HmBo.class.isAssignableFrom(entryObj.getKey())){
				continue;
			}else if(entryObj.getValue() == null || entryObj.getValue().isEmpty()){
				continue;
			}
			
			Iterator<Object> itemObj = entryObj.getValue().iterator();
			tempSet.clear();
			while(itemObj.hasNext()){
				HmBo bo = (HmBo)itemObj.next();
				if(tempSet.contains(bo.getId())){
					itemObj.remove();
				}else{
					tempSet.add(bo.getId());
				}
			}
		}
		
		return resMap;
	}
	
	private void treeWalkClass(Class<?> clazzBo) {
		if(classFieldMap.containsKey(clazzBo)){
			return;
		}else if(DeepCloneUtil.isSimpleClass(clazzBo)){
			return;
		}
		
		classFieldMap.put(clazzBo, new ArrayList<Field>());
		Field[] allFields = clazzBo.getDeclaredFields();
		for(Field field : allFields){
			if (Modifier.isStatic(field.getModifiers()) || 
					Modifier.isFinal(field.getModifiers())){
				continue;
			}else if (DeepCloneUtil.isSimpleClass((Class<?>)field.getType())){
				continue;
			}else if(field.getAnnotation(Transient.class) != null){
				continue;
			}else if(field.getAnnotation(OneToMany.class) != null){
				continue;
			}
			
			field.setAccessible(true);
			classFieldMap.get(clazzBo).add(field);
			
			List<Class<?>> typeClasses = getTypeClass(field.getGenericType());
			for(Class<?> tClass : typeClasses){
				treeWalkClass(tClass);
			}
		}
	}
	
	private void treeWalkObj(Object hmBo, Map<Class<?>, List<Object>> boContext) throws IllegalArgumentException, IllegalAccessException{
		hmBo = getOriginalObject(hmBo);
		
		if(hmBo == null){
			return;
		}else if(DeepCloneUtil.isSimpleObject(hmBo)){
			return;
		}
//		else if(hmBo.getClass().isArray()){
//			int len = Array.getLength(hmBo);
//			for (int i = 0; i < len; i++) {
//				treeWalkObj(Array.get(hmBo, i), boContext);
//			}
//		}
		else if(hmBo instanceof Collection<?>){
			try{
				Collection<?> listObj = (Collection<?>)hmBo;
				for(Object cldObj : listObj){
					treeWalkObj(cldObj, boContext);
				}
			}catch(Exception e){
//				e.printStackTrace();
				return;
			}
		}else if(hmBo instanceof Map<?, ?>){
			try{
				Map<?, ?> mapObj = (Map<?, ?>)hmBo;
				for(Object cldObj : mapObj.values()){
					treeWalkObj(cldObj, boContext);
				}
			}catch(Exception e){
				return;
			}
		}
//		else if(hmBo.getClass().getAnnotation(Entity.class) == null && 
//				hmBo.getClass().getAnnotation(Embeddable.class) == null){
//			return;
//		}
		
		storeObject(hmBo, boContext);
		List<Field> fieldList = classFieldMap.get(hmBo.getClass());
		if(fieldList != null){
			for(Field field : fieldList){
				treeWalkObj(field.get(hmBo), boContext);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getOriginalObject(T candidate){
		if(candidate == null){
			return candidate;
		}
		
		if(candidate instanceof HibernateProxy){
//			Hibernate.initialize(candidate);
			LazyInitializer lazyObj = ((HibernateProxy) candidate).getHibernateLazyInitializer();
			if(lazyObj.isUninitialized()){
				return null;
			}else{
				return (T) lazyObj.getImplementation();
			}
//			return (T) lazyObj.getImplementation();
		}else{
			return candidate;
		}
	}
	
	private void storeObject(Object hmBo, Map<Class<?>, List<Object>> boContext) {
		if(hmBo.getClass().getAnnotation(Entity.class) == null && 
				hmBo.getClass().getAnnotation(Embeddable.class) == null){
			return;
		}
		Class<?> clazz = hmBo.getClass();
		if(!boContext.containsKey(clazz)){
			boContext.put(clazz, new ArrayList<Object>());
		}
		boContext.get(clazz).add(hmBo);
	}
	
	private List<Class<?>> getTypeClass(Type type){
		List<Class<?>> resList = new ArrayList<>();
		if(type instanceof ParameterizedType){
			Type[] pTypes = ((ParameterizedType)type).getActualTypeArguments();
			for(Type t : pTypes){
				if(t instanceof ParameterizedType){
					resList.addAll(getTypeClass((ParameterizedType)t));
				}else{
					resList.add((Class<?>)t);
				}
			}
		}else{
			resList.add((Class<?>)type);
		}
		return resList;
	}
}
