package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.useraccess.MgmtServiceIPTrack;

public class TrackWanProfileImpl extends TrackProfileImpl {
	
	private List<Short> interfaceType;

	public TrackWanProfileImpl(MgmtServiceIPTrack ipTrack) {
		super(ipTrack);
		this.interfaceType = new ArrayList<Short>();
	}
	
	public short getInterfaceType(int index) {
		if((index > (-1)) && (index < 3)){
			return interfaceType.get(index);
		}else{
			return (short)(-1);
		}
	}
	
	public List<Short> getInterfaceType(){
		return this.interfaceType;
	}

	public void addInterfaceType(short interfaceType) {
		this.interfaceType.add(interfaceType);
	}

}
