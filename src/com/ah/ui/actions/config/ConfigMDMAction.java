package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.be.config.create.source.ConfigMdmService;
import com.ah.be.config.create.source.impl.ConfigMdmServiceImpl;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.ConfigMDMAirWatchNonCompliance;
import com.ah.bo.hiveap.ConfigTemplateMdm;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.RoutingProfilePolicy;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class ConfigMDMAction extends BaseAction implements QueryBo {
	
    private static final long serialVersionUID = -8565861248792186496L;

    private final ConfigMdmService			configmdmservice		= new ConfigMdmServiceImpl();
	
	public static final int COLUMN_NAME        = 1;

	public static final int COLUMN_DESCRIPTION = 2;
	
	public static final int COLUMN_MDM_TYPE    = 3;
	
	public static final int COLUMN_OS_OBJECT   = 4;
	
	public boolean newflag =false;
	
	private int[] notificationMethods;
	
	
	public boolean isNewflag() {
		return newflag;
	}
	public void setNewflag(boolean newflag) {
		this.newflag = newflag;
	}
	public ConfigMdmService getConfigmdmservice() {
		return configmdmservice;
	}
	protected String getColumnDescription(int id) {
		String code = "";
		switch (id) {
		case COLUMN_NAME:
			code = "config.ssid.advanced.mdm.enrollment.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ssid.advanced.mdm.enrollment.description";
			break;
		case COLUMN_MDM_TYPE:
			code = "config.ssid.advanced.mdm.enrollment.type";
			break;
		case COLUMN_OS_OBJECT:
			code = "config.ssid.advanced.mdm.enrollment.osobj";	
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		columns.add(new HmTableColumn(COLUMN_MDM_TYPE));
		columns.add(new HmTableColumn(COLUMN_OS_OBJECT));
		return columns;
	}
	@Override
	public Collection<HmBo> load(HmBo bo) {
		
		return null;
	}
	
	@Override
	public String execute() throws Exception{
			
		try {
			if(!("new".equals(operation))){
				newflag=false;
			}
			if("new".equals(operation)){
				if (!setTitleAndCheckAccess(getText("config.title.mdm.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				newflag=true;
				setSessionDataSource(new ConfigTemplateMdm());
				initAirWatchNonComplianceSettings();
				
				return getReturnPathWithJsonMode(INPUT, "configmdmJson");
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)){
                if (checkNameExists("policyname", getDataSource().getPolicyname())) {
                    if (isJsonMode()) {
                        jsonObject = new JSONObject();
                        jsonObject.put("ok", false);
                        jsonObject.put("msg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getPolicyname().toString()));
                    }
                    return isJsonMode() ? "configmdmJson" : INPUT;
                }
				
                handleAirWatchNonComplianceSettings();
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					Long id = createBo(dataSource);
					jsonObject.put("configmdmId",id);
					if (id == null) {
						jsonObject.put("ok",false);
						jsonObject.put("msg","id==null");
						return "json";
					} else {
						jsonObject.put("id", id);
						jsonObject.put("name", getDataSource().getPolicyname());
						jsonObject.put("ok",true);
						return "json";
					}
				}
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
					
			} else if("edit".equals(operation)) {
				setSessionDataSource(findBoById(boClass, id, this));
				addLstTitle(getText("config.title.mdm.edit")+ "'" + getChangedName() + "'");
				String strForward = editBo(this);
				initAirWatchNonComplianceSettings();
				return getReturnPathWithJsonMode(strForward, "configmdmJson");
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)){
				handleAirWatchNonComplianceSettings();
				updateBo(dataSource);
				if ("update".equals(operation)) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("id", getDataSource().getId());
						jsonObject.put("name", getDataSource().getPolicyname());
						jsonObject.put("ok",true);
						return "json";
						
					}else {
						return prepareBoList();
					}
					
				} else {
					
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				ConfigTemplateMdm configtemplemdm = (ConfigTemplateMdm) findBoById(boClass, cloneId, this);
				configtemplemdm.setId(null);
				configtemplemdm.setPolicyname("");
				configtemplemdm.setDescription(null);
				configtemplemdm.setVersion(null);
				configtemplemdm.setOwner(null);
				setSessionDataSource(configtemplemdm);
				initAirWatchNonComplianceSettings();
				addLstTitle(getText("config.title.mdm.new"));
				return getReturnPathWithJsonMode(INPUT, "configmdmJson");
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("continue".equals(operation)) {
				return getReturnPathWithJsonMode(INPUT, "configmdmJson");
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

    private void initAirWatchNonComplianceSettings() {
        if(isSupportAirWatchNonCompliance()) {
            if(getDataSource().getAwNonCompliance().isEnabledNonCompliance()) {
                notificationMethods = getDataSource().getAwNonCompliance().initMethods();
            } else {
                getDataSource()
                .getAwNonCompliance()
                .setContent(MgrUtil.getUserMessage("glasgow_10.config.mdm.airwath.noncompliant.defualt.message"));
            }
        }
    }
	
	private void handleAirWatchNonComplianceSettings() {
        if(isSupportAirWatchNonCompliance()) {
            if(getDataSource().getMdmType() == ConfigTemplateMdm.MDM_ENROLL_TYPE_AIRWATCH) {
                ConfigMDMAirWatchNonCompliance awSettings = getDataSource().getAwNonCompliance();
                if(awSettings.isEnabledNonCompliance()) {
                    awSettings.convertMethods(notificationMethods);
                    if(!awSettings.isNotifyViaEmail()) {
                        awSettings.setTitle(null);
                    }
                    if(awSettings.getContent().length() > 140) {
                        awSettings.setContent(awSettings.getContent().substring(0, 140));
                    }
                } else {
                    // reset the fields
                    getDataSource().setAwNonCompliance(new ConfigMDMAirWatchNonCompliance());
                }
            } else {
                // reset the fields
                getDataSource().setAwNonCompliance(new ConfigMDMAirWatchNonCompliance());
            }
        }
    }
	
    public EnumItem[] getAirWatchNotificationMethods() {
        return MgrUtil.enumItems("enum.mdm.noncompliance.method.", new int[] {
                ConfigMDMAirWatchNonCompliance.NOTIFY_METHOD_PUSH,
                ConfigMDMAirWatchNonCompliance.NOTIFY_METHOD_SMS,
                ConfigMDMAirWatchNonCompliance.NOTIFY_METHOD_EMAIL });
    }
	
    public EnumItem[] getMdmTypeList() {
		return ConfigTemplateMdm.ENUM_MDM_ENROLL_TYPE;
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		ConfigTemplateMdm source = QueryUtil.findBoById(ConfigTemplateMdm.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<ConfigTemplateMdm> list = QueryUtil.executeQuery(ConfigTemplateMdm.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (ConfigTemplateMdm profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			ConfigTemplateMdm rp = source.clone();
			if (null == rp) {
				continue;
			}
			//setCloneFields(source, rp);
			rp.setId(profile.getId());
			rp.setPolicyname(profile.getPolicyname());
			rp.setVersion(profile.getVersion());
			rp.setOwner(profile.getOwner());
			hmBos.add(rp);
		}
		return hmBos;
	}
	private void setCloneFields(RoutingProfilePolicy source, RoutingProfilePolicy destination) {
//		List<RoutingProfilePolicyRule> rpRules = new ArrayList<>();
//
//		for (RoutingProfilePolicyRule rpRule : source.getRoutingProfilePolicyRuleList()) {
//			rpRules.add(rpRule);
//		}
//		destination.setRoutingProfilePolicyRuleList(rpRules);
	}
	@Override
	public ConfigTemplateMdm getDataSource() {
		return (ConfigTemplateMdm) dataSource;
	}
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CONFIG_MDM);
		setDataSource(ConfigTemplateMdm.class);
		
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_CONFIG_MDM;
	}
	
	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'",
				"\\'");
	}
    public int[] getNotificationMethods() {
        return notificationMethods;
    }
    public void setNotificationMethods(int[] notificationMethods) {
        this.notificationMethods = notificationMethods;
    }
    
    private boolean wired;

    public boolean isWired() {
        return wired;
    }
    public void setWired(boolean wired) {
        this.wired = wired;
    }
    
    public boolean isSupportAirWatchNonCompliance() {
        //FIXME AirWatch NonCompliance not support wired right now
        return isFullMode() && !isWired();
    }
}
