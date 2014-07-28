package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.MvInfo;
import com.ah.apiengine.response.MoveVhmResponse;
//import com.ah.be.admin.hhmoperate.HHMmove;

public class MoveVhmRequest extends AbstractRequest {

	private static final long	serialVersionUID	= 1L;

	private MvInfo				mvInfo;

	public MvInfo getMvInfo() {
		return mvInfo;
	}

	public void setMvInfo(MvInfo mvInfo) {
		this.mvInfo = mvInfo;
	}

	@Override
	public void callback() {
		//HHMmove.moveHHM(mvInfo);
		//do not used
	}

	@Override
	public ByteBuffer execute() throws EncodeException {
		MoveVhmResponse response = new MoveVhmResponse();
		return response.build(this);
	}

	@Override
	public String getMsgName() {
		return "move VHM request";
	}

	@Override
	public int getMsgType() {
		return MOVE_VHM_REQUEST;
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
					case MOVE_VHM:
						mvInfo = (MvInfo) e;
						break;
					default:
						break;
					}
				}
			}
		}
	}

}
