package com.ah.be.config.cli.element;

import java.util.ArrayList;
import java.util.List;

public class AhElement implements AhNode {
	
	public static final String ATTR_NAME = "name";

	private String name;
	private List<AhAttribute> attributes;
	private List<AhElement> childs;
	private AhElement parent;
	private int priority;
	
	public AhElement(){}
	
	public AhElement(String name){
		this.name = name;
	}
	
	public String getPath(boolean withName){
		String path = getCurrentPath(withName);
		if(this.getParent() != null){
			path = this.getParent().getPath(withName) + path;
		}
		
		return path;
	}
	
	private String getCurrentPath(boolean withName){
		String curPath = "/" + this.name;
		if(withName && attributes != null){
			for(AhAttribute attrObj : attributes){
				if(ATTR_NAME.equals(attrObj.getName())){
					curPath += "[@" + ATTR_NAME + "='"+attrObj.getValue()+"']";
					break;
				}
			}
		}
		return curPath;
	}
	
	public void addChildElement(AhElement childElement){
		if(childElement == null){
			return;
		}
		if(childs == null){
			childs = new ArrayList<>();
		}
		childElement.setParent(this);
		childs.add(childElement);
	}
	
	public void addAttribute(String name, String value){
		AhAttribute attribute = new AhAttribute(name, value);
		addAttribute(attribute);
	}
	
	public void addAttribute(AhAttribute attribute){
		if(attribute == null){
			return;
		}
		if(attributes == null){
			attributes = new ArrayList<>();
		}
		attributes.add(attribute);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public AhElement getParent() {
		return parent;
	}
	public void setParent(AhElement parent) {
		this.parent = parent;
	}

	public List<AhAttribute> getAttributes() {
		return attributes;
	}

	public List<AhElement> getChilds() {
		return childs;
	}

	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
