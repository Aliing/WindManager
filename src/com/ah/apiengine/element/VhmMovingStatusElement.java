package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;

public class VhmMovingStatusElement extends AbstractElement {

	private static final long			serialVersionUID	= 1L;

	private static final Tracer			log					= new Tracer(VhmMovingStatusElement.class);

	private Collection<MvResponseInfo>	vhmMovingInfos;

	public Collection<MvResponseInfo> getVhmMovingInfos() {
		return vhmMovingInfos;
	}

	public void setVhmMovingInfos(Collection<MvResponseInfo> vhmMovingInfos) {
		this.vhmMovingInfos = vhmMovingInfos;
	}

	@Override
	public int decode(ByteBuffer bb, int msgLen) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.info("decode", "Start Position: " + startPos);

			short domainSize = bb.getShort();
			vhmMovingInfos = new ArrayList<MvResponseInfo>();
			for (int i = 0; i < domainSize; i++) {
				MvResponseInfo info = new MvResponseInfo();
				info.setDomainName(Tool.getString(bb));
				info.setSrcIp(Tool.getString(bb));
				info.setDestIp(Tool.getString(bb));
				info.setMVStatus(bb.getShort());
				info.setProcessStatus(bb.getShort());
				boolean result = bb.get() == 1 ? true : false;
				info.setResult(result);
				info.setMsg(Tool.getString(bb));
			}

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
		if (vhmMovingInfos == null) {
			throw new EncodeException("Encoding '" + getElemName()
					+ "' Error. vhmMovingInfos error");
		}
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.info("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.info("encode", "Start Position: " + startPos);

			bb.putShort((short) vhmMovingInfos.size());
			for (MvResponseInfo info : vhmMovingInfos) {
				Tool.putString(bb, info.getDomainName());
				Tool.putString(bb, info.getSrcIp());
				Tool.putString(bb, info.getDestIp());
				bb.putShort((short) info.getMVStatus());
				bb.putShort((short) info.getProcessStatus());

				if (info.getResult()) {
					bb.put((byte) 1);
				} else {
					bb.put((byte) 0);
				}
				Tool.putString(bb, info.getMsg());
			}

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
		return "VHM moving status";
	}

	@Override
	public short getElemType() {
		return QUERY_VHM_MOVING_STATUS;
	}

}
