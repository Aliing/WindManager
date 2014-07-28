package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.ApiString;
import com.ah.apiengine.response.QueryVhmMovingStatusResponse;

public class QueryVhmMovingStatusRequest extends AbstractRequest {

	private static final long	serialVersionUID	= 1L;

	public static final String	CLEAR_STATUS		= "1";
	public static final String	QUERY_STATUS		= "2";

	private ApiString				apiString;

	public ApiString getApiString() {
		return apiString;
	}

	public void setApiString(ApiString apiString) {
		this.apiString = apiString;
	}

	@Override
	public void callback() {
	}

	@Override
	public ByteBuffer execute() throws EncodeException {
		QueryVhmMovingStatusResponse response = new QueryVhmMovingStatusResponse();
		return response.build(this);
	}

	@Override
	public String getMsgName() {
		return "Query VHM moving status";
	}

	@Override
	public int getMsgType() {
		return QUERY_VHM_MOVING_STATUS_REQUEST;
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
					case API_STRING:
						apiString = (ApiString) e;
						break;
					default:
						break;
					}
				}
			}
		}
	}

}
