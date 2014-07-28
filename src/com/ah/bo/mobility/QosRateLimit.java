package com.ah.bo.mobility;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class QosRateLimit implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private short qosClass;
	private short schedulingType;
	@Range(min=0,max=1000)
	private int schedulingWeight;
	@Range(min=0,max=54000)
	private int policingRateLimit;
	@Range(min=0,max=2000000)
	private int policing11nRateLimit;
	@Range(min=0,max=2000000)
	private int policing11acRateLimit;
	
	public int getPolicing11nRateLimit()
	{
		return policing11nRateLimit;
	}

	public void setPolicing11nRateLimit(int policing11nRateLimit)
	{
		this.policing11nRateLimit = policing11nRateLimit;
	}
	
	public int getPolicing11acRateLimit()
	{
		return policing11acRateLimit;
	}

	public void setPolicing11acRateLimit(int policing11acRateLimit)
	{
		this.policing11acRateLimit = policing11acRateLimit;
	}

	@Transient
	public String[] getFieldValues(){
		String[] fieldValues ={"QOS_RATE_LIMIT_ID","qosClass",
				"schedulingType","schedulingWeight","policingRateLimit","policing11nRateLimit","policing11acRateLimit"};
		return fieldValues;
	}
	
	public static final short STRICT=1;
	public static final short WEIGHTED_ROUND_ROBIN=2;//Weighted Round Robin
	
	public static EnumItem[] ENUM_SCHEDULING_TYPE = MgrUtil.enumItems(
			"enum.qosSchedulingType.", new int[] {STRICT,WEIGHTED_ROUND_ROBIN}
			);

	public int getPolicingRateLimit() {
		return policingRateLimit;
	}

	public void setPolicingRateLimit(int policingRateLimit) {
		this.policingRateLimit = policingRateLimit;
	}

	public short getQosClass() {
		return qosClass;
	}

	public void setQosClass(short qosClass) {
		this.qosClass = qosClass;
	}

	public short getSchedulingType() {
		return schedulingType;
	}

	public void setSchedulingType(short schedulingType) {
		this.schedulingType = schedulingType;
	}

	public int getSchedulingWeight() {
		return schedulingWeight;
	}

	public void setSchedulingWeight(int schedulingWeight) {
		this.schedulingWeight = schedulingWeight;
	}

}
