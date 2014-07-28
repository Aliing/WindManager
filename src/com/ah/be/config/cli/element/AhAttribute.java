package com.ah.be.config.cli.element;

public class AhAttribute implements AhNode {
	private String name;
	private String value;
	private AhElement element;
	
	public AhAttribute(String name, String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public AhElement getElement() {
		return element;
	}
	public void setElement(AhElement element) {
		this.element = element;
	}
}
