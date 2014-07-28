/**
 * @filename			HmUpgradeLog.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.2R1
 * 
 * Copyright (c) 2006-2008 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.List;

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
import javax.persistence.Version;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.Navigation;
import com.ah.util.HmException;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 * The class is the data object of upgrade log
 */
@Entity
@Table(name = "HM_UPGRADE_LOG")
@org.hibernate.annotations.Table(appliesTo = "HM_UPGRADE_LOG", indexes = {
		@Index(name = "HM_UPGRADE_LOG_OWNER", columnNames = { "OWNER" })
		})
public class HmUpgradeLog implements HmBo {
	
	private static final long serialVersionUID = 1L;

	private static final short MAX_LOG_LENGTH = 512;
	

	@Column(length = 1024)
	private String formerContent;

	@Column(length = MAX_LOG_LENGTH)
	private String postContent;

	@Column(length = MAX_LOG_LENGTH)
	private String recommendAction;

	@Column(length = MAX_LOG_LENGTH)
	private String annotation = "Click to add an annotation";

	private HmTimeStamp logTimeStamp;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Transient
	private boolean selected;

	@Version
	private Timestamp version;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}
	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getLabel() {
		return "UpgradeLog";
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = true)
	private HmDomain owner;
	
	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getFormerContent() {
		return formerContent;
	}

	public void setFormerContent(String formerContent) {
		this.formerContent = formerContent;
	}

	public String getPostContent() {
		return postContent;
	}

	public void setPostContent(String postContent) {
		this.postContent = postContent;
	}
	
	public String getRecommendAction() {
		return recommendAction;
	}

	public void setRecommendAction(String recommendAction) {
		this.recommendAction = recommendAction;
	}
	
	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public HmTimeStamp getLogTime() {
		return logTimeStamp;
	}

	public void setLogTime(HmTimeStamp logTimeStamp) {
		this.logTimeStamp = logTimeStamp;
	}

	public String getLogTimeString() {
		if (logTimeStamp == null) {
			return "-";
		}

		return AhDateTimeUtil.getFormattedDateTime(logTimeStamp);
	}

	public HmTimeStamp getLogTimeStamp() {
		return logTimeStamp;
	}

	public void setLogTimeStamp(HmTimeStamp logTimeStamp) {
		this.logTimeStamp = logTimeStamp;
	}

	/**
	 * @author Yu Zhang
	 * 
	 */
	@Column
	private boolean needRedirect;// Whether HM need to redirect normal page to
									// upgrade page.
	@Column
	private boolean dismissed;// Whether the error message had been dismissed by
								// customer on upgrade page.

	private boolean isHmUpdate;// HM software update failed data,not DB restore
								// data.

	@Transient
	public static boolean isNeedRedirectPage(HmUser userContext) {
		if (!isHasAccessPermission(userContext)) {
			return false;
		}
		// check user whether have already redirected to upgrade page
		HmUpgradeLog uplog = getHmUpgradeLog(userContext);
		if (null == uplog || !uplog.isNeedRedirect()) {
			return false;
		}
		try {
			uplog.setNeedRedirect(false);
			QueryUtil.updateBo(uplog);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	@Transient
	public static boolean isHasAccessPermission(HmUser userContext) {
		if (null == userContext) {
			return false;
		}
		// only home domain can access HM upgrade feature
		HmDomain hmDomain = userContext.getSwitchDomain() != null ? userContext
				.getSwitchDomain() : userContext.getDomain();
		String domainName = hmDomain.getDomainName();
		if (!HmDomain.HOME_DOMAIN.equals(domainName)) {
			return false;
		}
		// check user whether have HM upgrade permission
		try {
			AccessControl.checkUserAccess(userContext,
					Navigation.L2_FEATURE_UPDATE_SOFTWARE, CrudOperation.READ);
		} catch (HmException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	@Transient
	public static HmUpgradeLog getHmUpgradeLog(HmUser userContext) {
		List<?> list = QueryUtil.executeQuery(HmUpgradeLog.class,
				new SortParams("logTimeStamp.time", false), new FilterParams(
						"isHmUpdate", true), userContext);
		if (list.isEmpty()) {
			return null;
		}
		return (HmUpgradeLog) list.get(0);
	}

	public boolean isNeedRedirect() {
		return needRedirect;
	}

	public void setNeedRedirect(boolean needRedirect) {
		this.needRedirect = needRedirect;
	}

	public boolean isDismissed() {
		return dismissed;
	}

	public void setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
	}

	public boolean isHmUpdate() {
		return isHmUpdate;
	}

	public void setHmUpdate(boolean isHmUpdate) {
		this.isHmUpdate = isHmUpdate;
	}

}