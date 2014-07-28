package com.ah.bo.wlan;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
public class Tx11acRateSettings implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public static final short STREAM_TYPE_SINGLE = 1;
	public static final short STREAM_TYPE_TWO = 2;
	public static final short STREAM_TYPE_THREE = 3;
	
	public static final short MIN_MCS_VALUE = 7;
	public static final short MEDIAN_MCS_VALUE = 8;
	public static final short MAX_MCS_VALUE = 9;
	
	public static final String STREAM_SINGLE_RATE_MCS_7 = "325 Mbps";
	public static final String STREAM_SINGLE_RATE_MCS_8 = "390 Mbps";
	public static final String STREAM_SINGLE_RATE_MCS_9 = "433.3 Mbps";
	
	public static final String STREAM_TWO_RATE_MCS_7 = "650 Mbps";
	public static final String STREAM_TWO_RATE_MCS_8 = "780 Mbps";
	public static final String STREAM_TWO_RATE_MCS_9 = "866.7 Mbps";
	
	public static final String STREAM_THREE_RATE_MCS_7 = "975 Mbps";
	public static final String STREAM_THREE_RATE_MCS_8 = "1.17 Gbps";
	public static final String STREAM_THREE_RATE_MCS_9 = "1.3 Gbps";
	
	public static final String VHT_STREAM_SINGLE_RATE_MCS_7 = "72.2 Mbps";
	public static final String VHT_STREAM_SINGLE_RATE_MCS_8 = "86.7 Mbps";
	public static final String VHT_STREAM_SINGLE_RATE_MCS_9 = "86.7 Mbps";
	
	public static final String VHT_STREAM_TWO_RATE_MCS_7 = "144.4 Mbps";
	public static final String VHT_STREAM_TWO_RATE_MCS_8 = "173.3 Mbps";
	public static final String VHT_STREAM_TWO_RATE_MCS_9 = "173.3 Mbps";
	
	public static final String VHT_STREAM_THREE_RATE_MCS_7 = "216.7 Mbps";
	public static final String VHT_STREAM_THREE_RATE_MCS_8 = "260 Mbps";
	public static final String VHT_STREAM_THREE_RATE_MCS_9 = "288.9 Mbps";
	
	private short streamType;
	private boolean streamEnable = true;
	private short mcsValue;
	
	@Transient
	private String dataRate = "0 Mbps";
	
	@Transient
	private String vhtDataRate = "0 Mbps";
	
	public boolean isStreamEnable() {
		return streamEnable;
	}
	public void setStreamEnable(boolean streamEnable) {
		this.streamEnable = streamEnable;
	}
	public short getStreamType() {
		return streamType;
	}
	public void setStreamType(short streamType) {
		this.streamType = streamType;
	}
	public short getMcsValue() {
		return mcsValue;
	}
	public void setMcsValue(short mcsValue) {
		this.mcsValue = mcsValue;
	}
	public String getDataRate() {
		switch(this.getStreamType()){
			case STREAM_TYPE_SINGLE:
				switch(this.mcsValue){
					case MIN_MCS_VALUE:
						return STREAM_SINGLE_RATE_MCS_7;
					case MEDIAN_MCS_VALUE:
						return STREAM_SINGLE_RATE_MCS_8;
					case MAX_MCS_VALUE:
						return STREAM_SINGLE_RATE_MCS_9;
					default:
						return STREAM_SINGLE_RATE_MCS_9;
				}
			case STREAM_TYPE_TWO:
				switch(this.mcsValue){
					case MIN_MCS_VALUE:
						return STREAM_TWO_RATE_MCS_7;
					case MEDIAN_MCS_VALUE:
						return STREAM_TWO_RATE_MCS_8;
					case MAX_MCS_VALUE:
						return STREAM_TWO_RATE_MCS_9;
					default:
						return STREAM_TWO_RATE_MCS_9;
				}
			case STREAM_TYPE_THREE:
				switch(this.mcsValue){
					case MIN_MCS_VALUE:
						return STREAM_THREE_RATE_MCS_7;
					case MEDIAN_MCS_VALUE:
						return STREAM_THREE_RATE_MCS_8;
					case MAX_MCS_VALUE:
						return STREAM_THREE_RATE_MCS_9;
					default:
						return STREAM_THREE_RATE_MCS_9;
				}
			
			default:
				return dataRate;
		}
	}
	public void setDataRate(String dataRate) {
		this.dataRate = dataRate;
	}
	
	public String getVhtDataRate() {
		switch (this.getStreamType()) {
		case STREAM_TYPE_SINGLE:
			switch (this.mcsValue) {
			case MIN_MCS_VALUE:
				return VHT_STREAM_SINGLE_RATE_MCS_7;
			case MEDIAN_MCS_VALUE:
				return VHT_STREAM_SINGLE_RATE_MCS_8;
			case MAX_MCS_VALUE:
				return VHT_STREAM_SINGLE_RATE_MCS_9;
			default:
				return VHT_STREAM_SINGLE_RATE_MCS_9;
			}
		case STREAM_TYPE_TWO:
			switch (this.mcsValue) {
			case MIN_MCS_VALUE:
				return VHT_STREAM_TWO_RATE_MCS_7;
			case MEDIAN_MCS_VALUE:
				return VHT_STREAM_TWO_RATE_MCS_8;
			case MAX_MCS_VALUE:
				return VHT_STREAM_TWO_RATE_MCS_9;
			default:
				return VHT_STREAM_TWO_RATE_MCS_9;
			}
		case STREAM_TYPE_THREE:
			switch (this.mcsValue) {
			case MIN_MCS_VALUE:
				return VHT_STREAM_THREE_RATE_MCS_7;
			case MEDIAN_MCS_VALUE:
				return VHT_STREAM_THREE_RATE_MCS_8;
			case MAX_MCS_VALUE:
				return VHT_STREAM_THREE_RATE_MCS_9;
			default:
				return VHT_STREAM_THREE_RATE_MCS_9;
			}

		default:
			return vhtDataRate;
		}
	}
	
	public void setVhtDataRate(String vhtDataRate) {
		this.vhtDataRate = vhtDataRate;
	}
	
}
