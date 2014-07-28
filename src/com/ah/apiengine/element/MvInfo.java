package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;

public class MvInfo extends AbstractElement {

	private static final long	serialVersionUID	= 1L;

	private static final Tracer	log					= new Tracer(MvInfo.class);

	public static final short	HHM_TYPE_HM_EVAL	= 1;

	public static final short	HHM_TYPE_PLAN_EVAL	= 2;

	public static final short	HHM_TYPE_PRODUCT	= 3;

	private List<String>	strDomainNameList;

	private String				strDestVersion;

	private String				strSrcIpAddress;

	private String				strDestIpAddress;

	private String				strUsrName;

	private String				strPasswd;

	private short				sHHMtype;

	public void setDomainNameList(List<String> strNameList) {
		this.strDomainNameList = strNameList;
	}

	public List<String> getDomainNameList() {
		return this.strDomainNameList;
	}

	public void setDestVersion(String strVersion) {
		this.strDestVersion = strVersion;
	}

	public String getDestVersion() {
		return this.strDestVersion;
	}

	public void setSrcIpaddress(String strAddress) {
		this.strSrcIpAddress = strAddress;
	}

	public String getSrcIpaddress() {
		return this.strSrcIpAddress;
	}

	public void setDestIpaddress(String strAddress) {
		this.strDestIpAddress = strAddress;
	}

	public String getDestIpaddress() {
		return this.strDestIpAddress;
	}

	public void setUserName(String strName) {
		this.strUsrName = strName;
	}

	public String getUsername() {
		return this.strUsrName;
	}

	public void setPasswd(String strPsd) {
		this.strPasswd = strPsd;
	}

	public String getPasswd() {
		return this.strPasswd;
	}

	public void setHHMType(short sType) {
		this.sHHMtype = sType;
	}

	public short getHHMType() {
		return this.sHHMtype;
	}

	@Override
	public int decode(ByteBuffer bb, int msgLen) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.info("decode", "Start Position: " + startPos);

			short domainSize = bb.getShort();
			strDomainNameList = new ArrayList<String>(domainSize);
			for (int i = 0; i < domainSize; i++) {
				strDomainNameList.add(Tool.getString(bb));
			}

			strDestVersion = Tool.getString(bb);
			strSrcIpAddress = Tool.getString(bb);
			strDestIpAddress = Tool.getString(bb);
			strUsrName = Tool.getString(bb);
			strPasswd = Tool.getString(bb);

			sHHMtype = bb.getShort();
			
			// End Position
			int endPos = bb.position();
			log.info("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException("Incorrect element length '" + msgLen + "' for "
					+ getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		if (strDomainNameList == null || strDomainNameList.size() == 0) {
			throw new EncodeException("Encoding '" + getElemName()
					+ "' Error. strDomainNameList error");
		}
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.info("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.info("encode", "Start Position: " + startPos);

			bb.putShort((short) strDomainNameList.size());
			for (String domainName : strDomainNameList) {
				Tool.putString(bb, domainName);
			}

			Tool.putString(bb, strDestVersion);
			Tool.putString(bb, strSrcIpAddress);
			Tool.putString(bb, strDestIpAddress);
			Tool.putString(bb, strUsrName);
			Tool.putString(bb, strPasswd);
			bb.putShort(sHHMtype);

			// End Position
			int endPos = bb.position();
			log.info("encode", "End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.info("encode", "Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	@Override
	public String getElemName() {
		return "Move VHM info";
	}

	@Override
	public short getElemType() {
		return MOVE_VHM;
	}
}
