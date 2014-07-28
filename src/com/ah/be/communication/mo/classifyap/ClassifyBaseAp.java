package com.ah.be.communication.mo.classifyap;

public interface ClassifyBaseAp {
	
	/**
	 * classify AP type
	 */
	public static final byte CLASSIFY_TYPE_ROGUE_TO_FRIEND = 1;
	public static final byte CLASSIFY_TYPE_FRIEND_TO_ROGUE = 2;
	
	/**
	 * flag types in event
	 */
	public static final byte DATA_OPERATION_FLAG_ALL = 0;
	public static final byte DATA_OPERATION_FLAG_ADD = 1;
	public static final byte DATA_OPERATION_FLAG_REMOVE = 2;
	
	/**
	 * use this to get object as array of bytes
	 * @return
	 */
	public byte[] getBytesOfObject();
	
	/**
	 * get total length of bytes of object
	 * @return
	 */
	public short getLength();
	
}
