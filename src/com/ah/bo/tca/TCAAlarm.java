/**   
* @Title: TCAAlarm.java 
* @Package com.ah.bo.tca 
* @author xxu   
* @date 2012-7-31 
* @version V1.0   
*/
package com.ah.bo.tca;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/** 
 * @ClassName: TCAAlarm 
 * @Description: 
 * @author xxu
 * @date 2012-7-31 
 *  
 */

@Entity
@Table(name = "TCA_ALARM")
public class TCAAlarm implements HmBo{

	/** 
	* @Fields serialVersionUID : default value
	*/ 
	private static final long serialVersionUID = 1L;
	
	public final static short NMS_TYPE=10000;
	
	public final static short TCA_ALARM_TYPE=10100;
	
	public final static short DISK_USAGE_ALARM_TYPE=1;
	
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	@Column(name="interval",nullable=false)
	private Long interval;
	
	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	@Column( nullable = true)
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name="highthreshold",nullable=false)
	private Long highThreshold;
	
	/**
	 * @return the highThreshold
	 */
	public Long getHighThreshold() {
		return highThreshold;
	}

	/**
	 * @param highThreshold the highThreshold to set
	 */
	public void setHighThreshold(Long highThreshold) {
		this.highThreshold = highThreshold;
	}

	@Column(name="lowthreshold",nullable=false)
	private Long lowThreshold;

	/**
	 * @return the lowThreshold
	 */
	public Long getLowThreshold() {
		return lowThreshold;
	}

	/**
	 * @param lowThreshold the lowThreshold to set
	 */
	public void setLowThreshold(Long lowThreshold) {
		this.lowThreshold = lowThreshold;
	}
	
	@Column(name="meatureitem", nullable=false)
	private String meatureItem;

	/**
	 * @return the meatureItem
	 */
	public String getMeatureItem() {
		return meatureItem;
	}

	/**
	 * @param meatureItem the meatureItem to set
	 */
	public void setMeatureItem(String meatureItem) {
		this.meatureItem = meatureItem;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getOwner()
	 */
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#setOwner(com.ah.bo.admin.HmDomain)
	 */
	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBoBase#getLabel()
	 */
	@Override
	public String getLabel() {
		return this.meatureItem;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getId()
	 */
	@Override
	public Long getId() {
		return id;
	}


	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}


	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#getVersion()
	 */
	@Override
	public Timestamp getVersion() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setVersion(java.sql.Timestamp)
	 */
	@Override
	public void setVersion(Timestamp version) {
		
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#isSelected()
	 */
	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.ah.bo.HmBo#setSelected(boolean)
	 */
	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub
		
	}
	
	@Transient
	public String getHighThresholdPercentStr() {
		return highThreshold+"%";
	}

	@Transient
	public String getLowThresholdPercentStr() {
		return lowThreshold+"%";
	}

}
