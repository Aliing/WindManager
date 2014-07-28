package com.ah.util.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;


public class BoAssistant {
	
	/**
	 * remove certain type of Bo by id, pass in set of objects, id of object will be fetched out
	 * @param cl :Bo class
	 * @param objects :Bo set
	 * @param owner :domain
	 */
	public static <T extends HmBo> void removeExistObjectsById(Class<T> cl, Set<T> objects, HmDomain owner) {
		if (objects == null
				|| objects.isEmpty()) {
			return;
		}
		List<Long> ids = new ArrayList<>();
		for (T obj : objects) {
			ids.add(obj.getId());
		}
		if (ids.size() > 0) {
			try {
				QueryUtil.bulkRemoveBos(cl, new FilterParams("id", ids), owner.getId());
			} catch (Exception e) {
			}
		}
	}
	
	
	/**
	 * fetch Bo from database with Bo ids, no sort
	 * @param cl :Bo class
	 * @param ids :Bo ids
	 * @param owner :domain
	 * @param queryBo :queryBo for lazy object
	 * @return
	 */
	public static <T extends HmBo> Map<Long, T> getIdObjectMap(Class<T> cl, List<Long> ids, HmDomain owner, QueryBo queryBo) {
		Map<Long, T> result = new HashMap<>();
		if (ids == null
				|| ids.isEmpty()) {
			return result;
		}
		List<T> objs = QueryUtil.executeQuery(cl, 
				null, 
				new FilterParams("id", ids), 
				owner.getId(),
				queryBo);
		if (objs != null
				&& !objs.isEmpty()) {
			for (T obj : objs) {
				result.put(obj.getId(), obj);
			}
		}
		return result;
	}
	
	/**
	 * used to define how to fetch out id from a Bo
	 * @param <T>
	 */
	public static interface BoIdGetterInter<T extends HmBo> {
		public Long getId(T bo);
	}
	/**
	 * construct map of id and Bo from Bo set
	 * @param objects :Bo set
	 * @param idGetter :the method to get id of Bo
	 * @return
	 */
	public static <T extends HmBo> Map<Long, T> getIdObjectMap(Set<T> objects, BoIdGetterInter<T> idGetter) {
		Map<Long, T> result = new HashMap<>();
		if (objects == null
				|| objects.isEmpty()) {
			return result;
		}
		
		if (idGetter == null) {
			idGetter = new BoIdGetterInter<T>() {
				@Override
				public Long getId(T bo) {
					return bo.getId();
				}
			};
		}
		
		if (objects != null
				&& !objects.isEmpty()) {
			for (T object : objects) {
				result.put(idGetter.getId(object), object);
			}
		}
		
		return result;
	}
}
