package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.HhmList;
import com.ah.apiengine.response.UpdateHhmListResponse;

public class UpdateHhmListRequest extends AbstractRequest {

	/*
	 * Elements Required:
	 *
	 * o  HHM List
	 */

	private static final long serialVersionUID = 1L;

	/* HHM List */
	private HhmList hhmList;

	public UpdateHhmListRequest() {
		super();
	}

	public HhmList getHhmList() {
		return hhmList;
	}

	public void setHhmList(HhmList hhmList) {
		this.hhmList = hhmList;
	}

	//*********************************************************************************
    // Methods implement Message
    //*********************************************************************************

	@Override
	public int getMsgType() {
		return UPDATE_HHM_LIST_REQUEST;
	}

	@Override
	public String getMsgName() {
		return "Update HHM List Request";
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
						case HHM_LIST:
							hhmList = (HhmList) e;
							break;
						default:
							break;
					}
				}
			}
		}
	}

    //*********************************************************************************
    // Methods for API-Engine to execute API-Client request and build response messages
    //*********************************************************************************

	@Override
	public ByteBuffer execute() throws EncodeException {
		UpdateHhmListResponse response = new UpdateHhmListResponse();

		return response.build(this);
	}

	@Override
	public void callback() {

	}

//    //*********************************************************************************
//    // Methods for API-Client to build request messages to API-Engine.
//    //*********************************************************************************
//
//	@Override
//	public ByteBuffer build() throws EncodeException {
//		ByteBuffer reqBB = super.build();
//
//		try {
//			/* Header */
//			int headerLen = encodeHeader(reqBB);
//
//			/* HHM List */
//			int hlLen = hhmList.encode(reqBB);
//
//			/* Message Elements Length */
//			fillPendingElementsLength(reqBB, headerLen, hlLen);
//
//			return reqBB;
//		} catch (Exception e) {
//			throw new EncodeException("Build '" + getMsgName() + "' Error.", e);
//		}
//	}

}