/**
 *@filename		AhLayer3RoamingConfigRequest.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event.request.server;

// aerohive import
import com.ah.be.capwap.AhCapwapDecodeException;
import com.ah.be.capwap.AhCapwapEncodeException;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public class AhLayer3RoamingConfigRequest extends AhCapwapServerRequest {

	private static final long	serialVersionUID	= 1L;

	public AhLayer3RoamingConfigRequest() {
		super(LAYER3_ROAMING_CONFIG_REQUEST);
	}

	public AhLayer3RoamingConfigRequest(String serialNum) {
		super(LAYER3_ROAMING_CONFIG_REQUEST, serialNum);
	}

	@Override
	public byte[] buildPacket() throws AhCapwapEncodeException {
		return null;
	}

	@Override
	public void parsePacket() throws AhCapwapDecodeException {

	}

	@Override
	public String getRequestName() {
		return "Layer3 Roaming Config Request";
	}

}