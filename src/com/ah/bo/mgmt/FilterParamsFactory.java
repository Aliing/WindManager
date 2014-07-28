package com.ah.bo.mgmt;

import java.util.Collection;

/**
 * Make it easier to initialize FilterParams
 * @author xianwang
 *
 */
public class FilterParamsFactory {
	
	private FilterParamsFactory() {
	}
	
	private static FilterParamsFactory instance = null;
	
	public static FilterParamsFactory getInstance() {
		if (instance == null) {
			instance = new FilterParamsFactory();
		}
		
		return instance;
	}
	
	/**
	 * want to filter a field which is null
	 * @param field
	 * @return
	 */
	public FilterParams fieldIsNull(String field) {
		Object nullObj = null;
		return new FilterParams(field, nullObj);
	}
	
	/**
	 * want to filter a field which is not null
	 * @param field
	 * @return
	 */
	public FilterParams fieldIsNotNull(String field) {
		return new FilterParams(field + " is not null", new Object[0]);
	}
	
	/**
	 * want to filter a field which is equal to a certain value
	 * @param field
	 * @param value
	 * @return
	 */
	public FilterParams fieldIsEqualTo(String field, Object value) {
		return new FilterParams(field, value);
	}
	
	/**
	 * want to filter a field which is not equal to a certain value
	 * @param field
	 * @param value
	 * @return
	 */
	public FilterParams fieldIsNotEqualTo(String field, Object value) {
		Object[] bindings = new Object[]{value};
		return new FilterParams(field + " != :s1", bindings);
	}
	
	/**
	 * want to filter a filed whose value is in values
	 * @param field
	 * @param values
	 * @return
	 */
	public FilterParams fieldIsIn(String field, Collection<?> values) {
		return new FilterParams(field, values);
	}
	
	/**
	 * want to filter a filed whose value is not in values
	 * example to get Array from Collection: collectionItems.toArray(new String[0])
	 * @param field
	 * @param values
	 * @return
	 */
	public FilterParams fieldIsNotIn(String field, Collection<?> values) {
		Object[] bindings = new Object[]{values};
		return new FilterParams(field + " not in (:s1)", bindings);
	}
	
	/**
	 * customized filter parameters, same to new FilterParams(where, bindings)
	 * @param where
	 * @param bindings
	 * @return
	 */
	public FilterParams customizedFilter(String where, Object[] bindings) {
		return new FilterParams(where, bindings);
	}
	
}
