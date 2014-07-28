package com.ah.be.topo;

import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhNeighbor;
import com.ah.bo.performance.AhRadioAttribute;
import com.ah.bo.performance.AhXIf;

public class StatisticResultsObject {

	private HiveAp owner;

	private List<AhNeighbor> neighbors;

	private List<AhXIf> xifs;

	private List<AhRadioAttribute> radioAttributes;

	private List<AhAssociation> associations;

	public StatisticResultsObject(HiveAp owner) {
		this.owner = owner;
	}

	public HiveAp getOwner() {
		return owner;
	}

	public void setOwner(HiveAp owner) {
		this.owner = owner;
	}

	public List<AhNeighbor> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<AhNeighbor> neighbors) {
		this.neighbors = neighbors;
	}

	public List<AhXIf> getXifs() {
		return xifs;
	}

	public void setXifs(List<AhXIf> xifs) {
		this.xifs = xifs;
	}

	public List<AhRadioAttribute> getRadioAttributes() {
		return radioAttributes;
	}

	public void setRadioAttributes(List<AhRadioAttribute> radioAttributes) {
		this.radioAttributes = radioAttributes;
	}

	public List<AhAssociation> getAssociations() {
		return associations;
	}

	public void setAssociations(List<AhAssociation> associations) {
		this.associations = associations;
	}

}
