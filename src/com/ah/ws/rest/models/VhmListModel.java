package com.ah.ws.rest.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ah.ws.rest.models.dto.Vhm;

@XmlRootElement
public class VhmListModel {

	private List<Vhm> vhmList = new ArrayList<Vhm>();

	@XmlElement
	public List<Vhm> getVhmList() {
		return vhmList;
	}

	public void setVhmList(List<Vhm> vhmList) {
		this.vhmList = vhmList;
	}
}
