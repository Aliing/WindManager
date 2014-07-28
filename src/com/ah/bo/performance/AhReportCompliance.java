package com.ah.bo.performance;

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

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

/** 
 * @ClassName: AhReportComplianceResult 
 * @Description: 
 * @author xkzhang
 * @date 2012-9-11 
 *  
 */

@Entity
@Table(name = "ah_report_compliance")
public class AhReportCompliance implements HmBo{

	/** 
	* @Fields serialVersionUID : default value
	*/ 
	private static final long serialVersionUID = 1L;
	
	public static final int COMPLIANCE_POLICY_POOR = 1;
	public static final int COMPLIANCE_POLICY_GOOD = 2;
	public static final int COMPLIANCE_POLICY_EXCELLENT = 3;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;
	
	@Column(name="apmac",nullable=false)
	private String apmac;
	
	@Column(name="status",nullable=false)
	private int status;

	public String getApmac() {
		return apmac;
	}

	public void setApmac(String apmac) {
		this.apmac = apmac;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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
		// TODO Auto-generated method stub
		return null;
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
	

}
