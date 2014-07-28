package com.ah.be.communication.mo.classifyap;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

public class ClassifyAps {
	protected final int			BUFFER_CAPACITY		= 5000;
	
	public ClassifyAps() {
		
	}
	
	public ClassifyAps(Set<ClassifyBaseAp> containedAps, byte classifyType) {
		this(containedAps, classifyType, ClassifyBaseAp.DATA_OPERATION_FLAG_ALL);
	}
	
	public ClassifyAps(byte classifyType, byte opFlag) {
		this(null, classifyType, opFlag);
	}
	
	public ClassifyAps(Set<ClassifyBaseAp> containedAps, byte classifyType, byte opFlag) {
		if (containedAps != null) {
			this.containedAps = containedAps;
		}
		this.classifyType = classifyType;
		this.opFlag = opFlag;
	}
	
	public void addListOfClassifyAp(Set<ClassifyBaseAp> aps) {
		containedAps.addAll(aps);
	}
	
	public void addClassifyAp(ClassifyBaseAp ap) {
		containedAps.add(ap);
	}
	
	public void removeClassifyAp(ClassifyBaseAp ap) {
		containedAps.remove(ap);
	}
	
	public byte[] getBytesOfObject() {
		ByteBuffer buf = ByteBuffer.allocate(getLength());
		//classify type
		buf.put(classifyType);
		//flag
		buf.put(opFlag);
		//number of APs
		buf.putShort(getNumberOfAps());
		//AP details info
		for (ClassifyBaseAp baseAp : containedAps) {
			buf.put(baseAp.getBytesOfObject());
		}
		
		buf.flip();
		
		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}
	
	public int getLength() {
		return (int)(1+1+2+getDataLength());
	}
	
	private int getDataLength() {
		int allApsLength = 0;
		for (ClassifyBaseAp baseAp : containedAps) {
			allApsLength += baseAp.getLength();
		}
		return allApsLength;
	}
	
	private byte classifyType = ClassifyBaseAp.CLASSIFY_TYPE_FRIEND_TO_ROGUE;
	
	private byte opFlag = ClassifyBaseAp.DATA_OPERATION_FLAG_ALL;
	
	private Set<ClassifyBaseAp> containedAps = new HashSet<ClassifyBaseAp>();
	
	public byte getClassifyType() {
		return classifyType;
	}

	public void setClassifyType(byte classifyType) {
		this.classifyType = classifyType;
	}

	public byte getOpFlag() {
		return opFlag;
	}

	public void setOpFlag(byte opFlag) {
		this.opFlag = opFlag;
	}

	public short getNumberOfAps() {
		return (short)containedAps.size();
	}

	public Set<ClassifyBaseAp> getContainedAps() {
		return containedAps;
	}

	public void setContainedAps(Set<ClassifyBaseAp> containedAps) {
		this.containedAps = containedAps;
	}

}
