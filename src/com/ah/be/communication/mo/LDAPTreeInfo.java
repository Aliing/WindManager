/**
 * LDAP tree info structure
 */
package com.ah.be.communication.mo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cchen
 *
 */
public class LDAPTreeInfo {
	
	private		String 							dn;	// for build full tree
	
	private		List<String> 					rDns;
	
	private 	Map<String, List<String>> 		attrNames;
	
	private 	Map<String, List<String>> 		attrValues;
	
	private 	Map<String, LDAPTreeInfo> 		childTreeInfo;// use rDn as key
	
	public LDAPTreeInfo() {
		dn = null;
		rDns = new ArrayList<String>();
		attrNames = new HashMap<String, List<String>>();
		attrValues = new HashMap<String, List<String>>();
		childTreeInfo = null;// for cache tree info usage
	}
	
	public short getRDNCount(){
		return (short)rDns.size();
	}
	
	public List<String> getAttrNamesByRDN(String rDn){
		return this.attrNames.get(rDn);
	}
	
	public List<String> getAttrValuesByName(String name){
		return this.attrValues.get(name);
	}
	
	public void setAttrName(String rDn, String name){
		List<String> attrNames = getAttrNamesByRDN(rDn);
		
		if (attrNames == null) {
			List<String> names = new ArrayList<String>();
			names.add(name);
			this.attrNames.put(rDn, names);
		} else {
			if (!attrNames.contains(name)) {
				attrNames.add(name);
			}
		}
	}
	
	public void setAttrNameValue(String rDn, String name, String value){
		setAttrName(rDn, name);
		List<String> attrValues = getAttrValuesByName(name);
		
		if(attrValues == null){
			List<String> values = new ArrayList<String>();
			values.add(value);
			this.attrValues.put(name, values);
		} else {
			if (!attrValues.contains(value)) {
				attrValues.add(value);
			}
		}
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public List<String> getrDns() {
		return rDns;
	}

	public void setrDns(List<String> rDns) {
		this.rDns = rDns;
	}

	public Map<String, List<String>> getAttrNames() {
		return attrNames;
	}

	public void setAttrNames(Map<String, List<String>> attrNames) {
		this.attrNames = attrNames;
	}

	public Map<String, List<String>> getAttrValues() {
		return attrValues;
	}

	public void setAttrValues(Map<String, List<String>> attrValues) {
		this.attrValues = attrValues;
	}

	public Map<String, LDAPTreeInfo> getChildTreeInfo() {
		return childTreeInfo;
	}

	public void setChildTreeInfo(Map<String, LDAPTreeInfo> childTreeInfo) {
		this.childTreeInfo = childTreeInfo;
	}
}