package com.ah.apiengine.request;

import java.nio.ByteBuffer;
import java.util.Collection;

import com.ah.apiengine.AbstractRequest;
import com.ah.apiengine.Element;
import com.ah.apiengine.EncodeException;
import com.ah.apiengine.element.StringList;
import com.ah.apiengine.response.SendEmailListResponse;

public class SendEmailListRequest extends AbstractRequest {

	private static final long	serialVersionUID	= 1L;

	private StringList			strlist;

	public StringList getStrlist() {
		return strlist;
	}

	public void setStrlist(StringList strlist) {
		this.strlist = strlist;
	}

	@Override
	public void callback() {
	}

	@Override
	public ByteBuffer execute() throws EncodeException {
		SendEmailListResponse response = new SendEmailListResponse();

		return response.build(this);
	}

	@Override
	public String getMsgName() {
		return "send deny mail list request";
	}

	@Override
	public int getMsgType() {
		return SEND_DENY_MAIL_LIST_REQUEST;
	}

	@Override
	public void setElements(Collection<Element> elements) {
		if (elements != null) {
			for (Element e : elements) {
				if (e != null) {
					switch (e.getElemType()) {
					case STRING_LIST:
						strlist = (StringList) e;
						break;
					default:
						break;
					}
				}
			}
		}
	}

//	@Override
//	public ByteBuffer build() throws EncodeException {
//		ByteBuffer reqBB = super.build();
//
//		try {
//			/* Header */
//			int headerLen = encodeHeader(reqBB);
//
//			int hlLen = strlist.encode(reqBB);
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
