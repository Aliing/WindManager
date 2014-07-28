package com.ah.apiengine;

import java.util.Collection;

public interface Message extends MessageTypes, ElementTypes {

	int BB_SIZE = 1024;

	int getMsgType();

	String getMsgName();

	void setElements(Collection<Element> elements);

}