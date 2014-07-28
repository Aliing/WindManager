package com.ah.bo.network;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

@Entity
@Table(name = "CUSTOM_APPLICATION")
@org.hibernate.annotations.Table(appliesTo = "CUSTOM_APPLICATION", indexes = {
		@Index(name = "CUSTOM_APPLICATION_OWNER", columnNames = { "OWNER" })
		})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomApplication  implements HmBo {
	private static final long serialVersionUID = 1L;

	public static final int CUSTOM_APP_MINIMUM_CODE = 19000;
	public static final int CUSTOM_APP_MAXIMUM_CODE = 19099;
	public static final String CUSTOM_APP_SHORT_NAME_PREFIX = "app";
	public static final String DEFAULT_CUSTOM_APP_GROUP_NAME = "Custom";
	
	@Id
	@GeneratedValue
	private Long id;

	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String customAppName;
	
	@Column(length = DEFAULT_STRING_LENGTH, nullable = false)
	private String customAppShortName;
	
	@Column(length = 1024)
	private String description;
	
	@Column(length = DEFAULT_STRING_LENGTH)
	private String appGroupName;
	
	@Column
	private Integer appCode;
	
	@Column( nullable=true )
	private int idleTimeout;
	
	@Version
	private Timestamp version;
	
	@Column
	private boolean deletedFlag = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;
	
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "CUSTOM_APPLICATION_RULE", joinColumns = @JoinColumn(name = "CUSTOM_APPLICATION_ID", nullable = true))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
	private List<CustomApplicationRule> rules = new ArrayList<CustomApplicationRule>();
	
	@Transient
	private boolean selected;
	
	@Transient
	private Long lastDayUsage = 0L;
	
	@Transient
	private Long lastMonthUsage = 0L; 
	
	@Transient
	private int appType = 1;

	public int getAppType() {
		return appType;
	}

	public void setAppType(int appType) {
		this.appType = appType;
	}
	
	public String getCustomAppName() {
		return customAppName;
	}

	public void setCustomAppName(String customAppName) {
		this.customAppName = customAppName;
	}

	
	public String getCustomAppShortName() {
		return customAppShortName;
	}

	public void setCustomAppShortName(String customAppShortName) {
		this.customAppShortName = customAppShortName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getAppGroupName() {
		return appGroupName;
	}

	public void setAppGroupName(String appGroupName) {
		this.appGroupName = appGroupName;
	}

	public Integer getAppCode() {
		return appCode;
	}

	public void setAppCode(Integer appCode) {
		this.appCode = appCode;
	}
	
	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public List<CustomApplicationRule> getRules() {
		return rules;
	}

	public void setRules(List<CustomApplicationRule> rules) {
		this.rules = rules;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
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
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String getLabel() {
		return customAppName;
	}

	public boolean isDeletedFlag() {
		return deletedFlag;
	}

	public void setDeletedFlag(boolean deletedFlag) {
		this.deletedFlag = deletedFlag;
	}	
	
	public Long getLastDayUsage() {
		return lastDayUsage;
	}
	
	public String getLastDayUsageStr() {
		if (lastDayUsage == 0) {
			return "0.00 KB";
		}
		double d = lastDayUsage * 1.0;
		if (d >= 1024 * 1024 * 1024) {
			d = d / (1024 * 1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " GB";
		}
		else if (d >= 1024 * 1024) {
			d = d / (1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " MB";
		}
		if (d >= 1024) {
			d = d / 1024;
			d = Math.round(d * 100) / 100.0; 
			return d + " KB";
		}
		return lastDayUsage + " B";
	}
	
	public String getLastMonthUsageStr() {
		if (lastMonthUsage == 0) {
			return "0.00 KB";
		}
		double d = lastMonthUsage * 1.0;
		if (d >= 1024 * 1024 * 1024) {
			d = d / (1024 * 1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " GB";
		}
		else if (d >= 1024 * 1024) {
			d = d / (1024 * 1024);
			d = Math.round(d * 100) / 100.0; 
			return d + " MB";
		}
		if (d >= 1024) {
			d = d / 1024;
			d = Math.round(d * 100) / 100.0; 
			return d + " KB";
		}
		return lastMonthUsage + " B";
	}

	public void setLastDayUsage(Long lastDayUsage) {
		this.lastDayUsage = lastDayUsage;
	}

	public Long getLastMonthUsage() {
		return lastMonthUsage;
	}

	public void setLastMonthUsage(Long lastMonthUsage) {
		this.lastMonthUsage = lastMonthUsage;
	}
	
	public static Integer getNextCustomAppCode(HmDomain owner){
		int currentCode = -1;
		String used_appcode_sql = "SELECT MAX(appcode) FROM custom_application where owner="+owner.getId();
		String unused_appcode_sql = "SELECT distinct appcode FROM custom_application WHERE deletedflag = true and owner="+owner.getId()+" order by appcode asc";
		long available_app_num = QueryUtil.findRowCount(CustomApplication.class, new FilterParams("deletedFlag = :s1 and owner.id = :s2",
				new Object[]{false,owner.getId()}));
		long total_app_num = QueryUtil.findRowCount(CustomApplication.class, new FilterParams("owner.id = :s1",new Object[]{owner.getId()}));
		if(available_app_num >= 100){
			currentCode = -1;
		}else{
			if(total_app_num >= 100){
				List<CustomApplication> list = QueryUtil.executeQuery(CustomApplication.class,null,new FilterParams("deletedFlag = :s1 and owner.id = :s2",
						new Object[]{false,owner.getId()}));
				List<Integer> appcodeList = new ArrayList<Integer>();
				if(null != list && !list.isEmpty()){
					for(CustomApplication ca : list){
						appcodeList.add(ca.getAppCode());
					}
				}
				Integer existUnusedMinAppCode = -1;
				List<?> unusedAppCodes = QueryUtil.executeNativeQuery(unused_appcode_sql);
				if(null != unusedAppCodes && !unusedAppCodes.isEmpty()){
					for (Object obj : unusedAppCodes) {
						if(null != obj){
							existUnusedMinAppCode = (int)obj;
							if(!appcodeList.contains(existUnusedMinAppCode)){
								break;
							}
						}
					}
				}
				currentCode = existUnusedMinAppCode;
			}else{
				List<?> appCodes = QueryUtil.executeNativeQuery(used_appcode_sql);
				int existMaxAppCode = CUSTOM_APP_MINIMUM_CODE;
				if(null != appCodes && !appCodes.isEmpty()){
					boolean exist = true;
					for (Object obj : appCodes) {
						if(null == obj){
							exist = false;
							break;
						}else{
							existMaxAppCode = (int)obj;
						}
					}
					if(! exist){
						currentCode = existMaxAppCode;
					}else{
						currentCode = existMaxAppCode + 1;
					}
				}else{
					currentCode = CUSTOM_APP_MINIMUM_CODE;
				}
				
				if(currentCode > CUSTOM_APP_MAXIMUM_CODE){
					currentCode = -1;
				}
			}
		}
		
		return currentCode;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CustomApplication)) {
			return false;
		}
		return null == id ? super.equals(other) : id.equals(((CustomApplication) other).getId());
	}

	@Override
	public int hashCode() {
		return null == id ? super.hashCode() : id.intValue();
	}
	@Override
	public CustomApplication clone() {
		try {
			return (CustomApplication) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
