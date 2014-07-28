package com.ah.bo.mgmt;

import java.util.Collection;

/*
 * @author Chris Scheers
 * 
 * Generic filter params class, but for now just pass in filter query
 */

public class FilterParams {

	private String name;

	private Object value = null;

	private Collection<?> values = null;

	private String where = null;

	private Object[] bindings;

	public FilterParams() {
	}

	public FilterParams(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public FilterParams(String name, Collection<?> values) {
		this.name = name;
		this.values = values;
	}

	public FilterParams(String where, Object[] bindings) {
		this.where = where;
		this.bindings = bindings;
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
	}

	public Collection<?> getValues() {
		return values;
	}

	public Object[] getBindings() {
		return bindings;
	}

	public String getWhere() {
		return where;
	}
}