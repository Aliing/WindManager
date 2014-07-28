package com.ah.ui.actions.monitor.enrolledclient.tools;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.io.xml.DomDriver;

@XStreamAlias("content")
public class DeviceIdList {
	
	@XStreamAlias("CustomerId")
	private String customId;
	
	@XStreamAlias("DeviceIdList")
	private List<String> idList;

	@XStreamAlias("Action")
	private String actionName;
	
	@XStreamAsAttribute
	private String version;
	
	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public List<String> getIdList() {
		return idList;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setIdList(List<String> idList) {
		this.idList = idList;
	}
	
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public static void main(String [] args){
		DeviceIdList d = new DeviceIdList();
		List l = new ArrayList<String>();
		l.add("123");
		l.add("2324");
		d.setIdList(l);
		d.setCustomId("woshihe");
		XStream x =new XStream(new DomDriver());
		x.processAnnotations(DeviceIdList.class);
		x.alias("DeviceId",String.class);
		System.out.println(x.toXML(d).toString());
	}
}
