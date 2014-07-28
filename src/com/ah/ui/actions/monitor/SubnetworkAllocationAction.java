/**
 * $Id: SubnetworkAllocationAction.java,v 1.6.4.2 2013/02/27 06:46:45 xpei Exp $
 */
package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.SubNetworkResource;
import com.ah.bo.network.VpnNetwork;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.CheckItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * US359: Subnetwork allocation list with export capability
 * 
 * @author Yunzhi Lin
 *
 */
public class SubnetworkAllocationAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 3114352986079101184L;
	
	private static final Tracer log = new Tracer(SubnetworkAllocationAction.class.getSimpleName());
	
	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if("export".equals(operation)) {
				// FIXME Need to change the get list method
				prepareTotalList();
				return "export";
			} else if ("editAp".equals(operation)) {
				addLstForward(L2_FEATURE_SUBNETWORK_ALLOCATIONS);
				selctedOwnerId = getDomainId();
				return operation;
			} else if ("remove".equals(operation)) {
			    return removeOP();
			} else {
				baseOperation();
				String pageStrSucc = prepareBoList();
				List<?> pageList = this.page;
				if (null != pageList && !pageList.isEmpty()) {
					for (Object obj : pageList) {
						SubNetworkResource resource = (SubNetworkResource) obj;
						if (resource.isEnableNat()) {
							resource.setParentLocalNetwork(resource
									.getParentNetwork());
						}else{
							resource.setNetwork("");
						}
					}
				}
				return pageStrSucc;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	// -------------------- Operation method ------------------------- //
	/**
	 * Fixed Bug 16941, Change the remove operation to update operation.
	 */
	@SuppressWarnings("unchecked")
    private String removeOP() throws Exception {
        int count = 0;
        List<Long> hiveAps = null;
        Set<Long> deviceIds = null;
        final String querySql = "select ap.id from " + HiveAp.class.getSimpleName() + " ap, "
                + SubNetworkResource.class.getSimpleName() + " res";
        final String whereBasicStr = "ap.owner.id = :s1 and res.status = :s2 and res.hiveApMac = ap.macAddress and ap.manageStatus = :s3";
        if (allItemsSelected) {
            hiveAps = (List<Long>) QueryUtil.executeQuery(querySql, null, new FilterParams(
                    whereBasicStr, new Object[] { getDomainId(),
                            SubNetworkResource.IP_SUBBLOCKS_STATUS_USED, HiveAp.STATUS_MANAGED }));
            deviceIds = changeList2Set(hiveAps);

            count = QueryUtil.updateBos(SubNetworkResource.class,
                    "hiveApMac = :s1, hiveApMgtx = :s2, status = :s3", "status != :s4",
                    new Object[] { null, (short) -1, SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE,
                            SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE }, getDomainId());

        } else if (selectedIds != null && !selectedIds.isEmpty()) {
            hiveAps = (List<Long>) QueryUtil.executeQuery(querySql, null, new FilterParams(
                    whereBasicStr + " and res.id in (:s4)", new Object[] { getDomainId(),
                            SubNetworkResource.IP_SUBBLOCKS_STATUS_USED, HiveAp.STATUS_MANAGED,
                            selectedIds }));
            deviceIds = changeList2Set(hiveAps);

            count = QueryUtil.updateBos(SubNetworkResource.class,
                    "hiveApMac = :s1, hiveApMgtx = :s2, status = :s3", "id in (:s4)", new Object[] {
                            null, (short) -1, SubNetworkResource.IP_SUBBLOCKS_STATUS_FREE,
                            selectedIds }, getDomainId());

        }

        updateIndicationIcon(deviceIds);

        final String warningMsg = " Please do a complete upload to apply the changed configuration.";
        if (count < 0) {
            addActionMessage(MgrUtil.getUserMessage(SELECT_OBJECT));
        } else if (count == 0) {
            addActionMessage(MgrUtil.getUserMessage(NO_OBJECTS_REMOVED));
        } else if (count == 1) {
            addActionMessage(MgrUtil.getUserMessage(OBJECT_REMOVED) + warningMsg);
        } else {
            addActionMessage(MgrUtil.getUserMessage(OBJECTS_REMOVED, count + "") + warningMsg);
        }
        return prepareBoList();
    }
    private void updateIndicationIcon(Set<Long> deviceIds) throws Exception {
        // update the related devices indication
        if(null != deviceIds) {
            int pendingIndex = ConfigurationResources.CONFIG_HIVEAP_NETWORK_RESOURCE_RECYCLE;
            // String desc = ConfigurationUtils.getDescription(new
            // SubNetworkResource(), null);
            String desc = Navigation.getFeatureName(Navigation.L2_FEATURE_SUBNETWORK_ALLOCATIONS);
            BoMgmt.getHiveApMgmt().updateConfigurationIndication(deviceIds, true, pendingIndex, desc,
                    ConfigurationType.Configuration);
        }
    }
    private Set<Long> changeList2Set(List<Long> hiveAps) {
        Set<Long> deviceIds = null;
        if(null != hiveAps && !hiveAps.isEmpty()) {
            deviceIds = new HashSet<Long>(hiveAps);
        }
        return deviceIds;
    }
	private void prepareTotalList() throws Exception {
		initFilterParams();
		totalList = QueryUtil.executeQuery(SubNetworkResource.class, null, filterParams, getDomainId(), this);
	}
    private void initFilterParams() {
        if (currentNetworkId > 0) {
            filterParams = new FilterParams("networkId = :s1 and status = :s2", new Object[] {
                    currentNetworkId, SubNetworkResource.IP_SUBBLOCKS_STATUS_USED });
            VpnNetwork network = QueryUtil.findBoById(VpnNetwork.class, currentNetworkId, this);
            log.debug("current selected network is " + network);
            if (null != network) {
                this.webSecurityStr = StringUtils.isNotBlank(network.getWebSecurityString()) ? network
                        .getWebSecurityString() : "None";
                this.subNetworkCountStr = network.getSubItems().size() + "";
                this.currentNetworkName = network.getNetworkName();
            }
        } else {
            filterParams = new FilterParams("status", SubNetworkResource.IP_SUBBLOCKS_STATUS_USED);
        }
    }
	// -------------------- Override method -------------------------- //
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SUBNETWORK_ALLOCATIONS);
		keyColumnId = ColumnItem.BRNAME.getKey();
		this.tableId = HmTableColumn.TABLE_SUBNETWORK_ALLOCATION;
		setDataSource(SubNetworkResource.class);
	}
	@Override
	protected void preparePage() throws Exception {
		enableSorting();
		enablePaging();
        initFilterParams();
		page = findBos(this);
	}
	@Override
	public List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> tableColumns = new ArrayList<HmTableColumn>();
		tableColumns.add(new HmTableColumn(ColumnItem.BRNAME.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.NODEID.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.LOCALNETWRK.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.NATNETWORK.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.NETWORK.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.DHCPPOOL.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.GATEWAY.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.TAG1.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.TAG2.getKey()));
		tableColumns.add(new HmTableColumn(ColumnItem.TAG3.getKey()));
		return tableColumns;
	}
	@Override
	protected String getColumnDescription(int id) {
		String code = ColumnItem.getColumnNameCode(id);
		return null == code ? "" : MgrUtil.getUserMessage(code);
	}
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof SubNetworkResource) {
			SubNetworkResource resource = (SubNetworkResource) bo;
			if(null != resource.getVpnNetwork()) {
				resource.getVpnNetwork().getId();
			}
			if(null != resource.getOwner()) {
				resource.getOwner().getId();
			}
		}
		if (bo instanceof VpnNetwork) {
			VpnNetwork network = (VpnNetwork) bo;
			network.getSubItems().size();
		}
		return null;
	}
	// -------------------- Fields -------------------------- //
	private Long selctedOwnerId;
	private Long selctedHiveApId;
	
	private static final String ALL_NETWORK = "All";
	private long currentNetworkId = 0L;
	private String currentNetworkName = ALL_NETWORK;
	private String webSecurityStr = "";
	private String subNetworkCountStr = "";
	
	private List<SubNetworkResource> totalList;
	
	public List<CheckItem> getNetworkList() {
		List<CheckItem> list = new ArrayList<CheckItem>();
		list.add(new CheckItem(0L, ALL_NETWORK));
		SortParams sortParams = new SortParams("networkName");
		sortParams.setPrimaryOrderBy("defaultFlag");
		sortParams.setPrimaryAscending(false);
		List<CheckItem> items = getBoCheckItemsSort("networkName", VpnNetwork.class, new FilterParams(
				"networkType != :s1", new Object[] { VpnNetwork.VPN_NETWORK_TYPE_GUEST }), sortParams,
				CHECK_ITEM_BEGIN_NO, CHECK_ITEM_END_NO);
		if (null != items) {
			list.addAll(items);
		}
		return list;
	}
	/**
	 * Column Enum: key, name
	 * 
	 * @author Yunzhi Lin
	 */
	private enum ColumnItem {
		BRNAME {
			@Override
			int getKey() {
				return 1;
			}

			@Override
			String getName() {
				return "hiveAp.hostName";
			}
		},
		NODEID {
			@Override
			int getKey() {
				return 2;
			}

			@Override
			String getName() {
				return "hiveAp.macaddress";
			}
		},
		LOCALNETWRK {
			@Override
			int getKey() {
				return 3;
			}

			@Override
			String getName() {
				return "monitor.subnetwork.localNetwork";
			}
		},
		NETWORK {
			@Override
			int getKey() {
				return 4;
			}

			@Override
			String getName() {
				return "config.userprofile.network.object";
			}
		},
		DHCPPOOL {
			@Override
			int getKey() {
				return 5;
			}

			@Override
			String getName() {
				return "monitor.subnetwork.allocation.dhcppool";
			}
		},
		GATEWAY {
			@Override
			int getKey() {
				return 6;
			}

			@Override
			String getName() {
				return "hiveAp.gateway";
			}
		},
		TAG1 {
			@Override
			int getKey() {
				return 7;
			}

			@Override
			String getName() {
				return "hiveAp.classification.tag1";
			}
		},
		TAG2 {
			@Override
			int getKey() {
				return 8;
			}

			@Override
			String getName() {
				return "hiveAp.classification.tag2";
			}
		},
		TAG3 {
			@Override
			int getKey() {
				return 9;
			}

			@Override
			String getName() {
				return "hiveAp.classification.tag3";
			}
		},
		NATNETWORK {
			@Override
			int getKey() {
				return 10;
			}

			@Override
			String getName() {
				return "monitor.subnetwork.natNetwork";
			}
		};
		
		abstract int getKey();
		abstract String getName();
		
		public static String getColumnNameCode(int key) {
			for (ColumnItem element : values()) {
				if(element.getKey() == key) {
					return element.getName();
				} 
			}
			return null;
		}
	}
	
	// -------------------- Getter/Setter -------------------------- //
	public long getCurrentNetworkId() {
		return currentNetworkId;
	}
	
	public void setCurrentNetworkId(long currentNetworkId) {
		this.currentNetworkId = currentNetworkId;
	}

	public String getWebSecurityStr() {
		return webSecurityStr;
	}

	public void setWebSecurityStr(String webSecurityStr) {
		this.webSecurityStr = webSecurityStr;
	}

	public String getSubNetworkCountStr() {
		return subNetworkCountStr;
	}

	public void setSubNetworkCountStr(String subNetworkCountStr) {
		this.subNetworkCountStr = subNetworkCountStr;
	}
	
	public String getCurrentNetworkName() {
		return currentNetworkName;
	}

	public void setCurrentNetworkName(String currentNetworkName) {
		this.currentNetworkName = currentNetworkName;
	}

	public Long getSelctedOwnerId() {
		return selctedOwnerId;
	}

	public void setSelctedOwnerId(Long selctedOwnerId) {
		this.selctedOwnerId = selctedOwnerId;
	}

	public Long getSelctedHiveApId() {
		return selctedHiveApId;
	}

	public void setSelctedHiveApId(Long selctedHiveApId) {
		this.selctedHiveApId = selctedHiveApId;
	}

	public List<SubNetworkResource> getTotalList() {
		return totalList;
	}

	public void setTotalList(List<SubNetworkResource> totalList) {
		this.totalList = totalList;
	}
}
