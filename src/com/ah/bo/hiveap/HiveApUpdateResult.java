package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

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

import org.hibernate.annotations.Index;

import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HIVE_AP_UPDATE_RESULT")
@org.hibernate.annotations.Table(appliesTo = "HIVE_AP_UPDATE_RESULT", indexes = {
		@Index(name = "HIVE_AP_UPDATE_RESULT_OWNER", columnNames = { "OWNER" }),
		@Index(name = "HIVE_AP_UPDATE_RESULT_MAC", columnNames = { "NODEID" })
		})
public class HiveApUpdateResult implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Version
	private Timestamp version;

	private String hostname;

	private String nodeId;

	private String ipAddress;

	private long startTime;

	private long finishTime;

	private short updateType;

	private short result;

	private short state;

	private short level = UpdateParameters.LEVEL_IMAGE_YES;

	private float downloadRate;

	@Column(length = 1024)
	private String description;

	public static final int TAG_AUTO_PROVISION = 1;
	public static final int TAG_IMAGE_DISTRIBUTION = 2;
	// The tag used for indicating auto provisioning, image distributing, etc.
	private int tag;

	private short actionType;

	public static final short DEFAULT_STAGED_TIME = 3;

	private int stagedTime = DEFAULT_STAGED_TIME;

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "HIVE_AP_UPDATE_RESULT_ITEM", joinColumns = @JoinColumn(name = "HIVE_AP_ID", nullable = true))
	private List<HiveApUpdateItem> items = new ArrayList<HiveApUpdateItem>();

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		if ("".equals(ipAddress)) {
			// avoid error when sort by inet function, fix bug 27305
			ipAddress = "0.0.0.0";
		}
		this.ipAddress = ipAddress;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(long finishTime) {
		this.finishTime = finishTime;
	}

	public short getResult() {
		return result;
	}

	public void setResult(short result) {
		this.result = result;
	}

	public short getState() {
		return state;
	}

	public void setState(short state) {
		this.state = state;
	}

	public float getDownloadRate() {
		return downloadRate;
	}

	public void setDownloadRate(float downloadRate) {
		this.downloadRate = downloadRate;
	}

	public String getDescription() {
		if (null == description) {
			return "";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public short getUpdateType() {
		return updateType;
	}

	public void setUpdateType(short updateType) {
		this.updateType = updateType;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public String getLabel() {
		return hostname;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Transient
	public String getStateString() {
		String color;
		switch (state) {
		case UpdateParameters.PROCESS_NOT_START:
			color = "black";
			break;
		case UpdateParameters.PROCESS_LOADING:
		case UpdateParameters.PROCESS_EXTRACTING:
		case UpdateParameters.PROCESS_RETRIEVING:
		case UpdateParameters.PROCESS_GENERATING:
		case UpdateParameters.PROCESS_COMPARING:
			color = "blue";
			break;
		default:
			color = "";
		}

		String cancel_box = "";
		if ("".equals(color)) {
			return "";
		} else {
			int queueNum = -1;
			if(state == UpdateParameters.PROCESS_NOT_START){
				queueNum = HmBeConfigUtil.getUpdateManager().getUploadWaitingQueueNum(this.nodeId);
			}
			if(queueNum > 0){
				String msg = MgrUtil.getUserMessage("info.hiveAp.update.queue.position",
						new String[]{String.valueOf(queueNum)});
				return "<span class='currentState'><font>"+ msg + "</font></span>";
			}else{
				return "<span class='currentState'><font color='"
					+ color
					+ "'>"
					+ MgrUtil.getEnumString("enum.hiveAp.update.process."
							+ state) + "</font></span>" + cancel_box;
			}
		}
	}

	@Transient
	public String getResultString() {
		String color;
		switch (result) {
		case UpdateParameters.UPDATE_SUCCESSFUL:
		case UpdateParameters.REBOOT_SUCCESSFUL:
			color = "green";
			if (UpdateParameters.LEVEL_IMAGE_RISK == level) {
				color = "brown";
			}
			break;
		case UpdateParameters.UPDATE_CANCELED:
			color = "green";
			break;
		case UpdateParameters.UPDATE_FAILED:
		case UpdateParameters.UPDATE_TIMEOUT:
		case UpdateParameters.WARNING:
			color = "red";
			break;
		case UpdateParameters.UPDATE_ABORT:
			color = "black";
			break;
		case UpdateParameters.UPDATE_STAGED:
		case UpdateParameters.REBOOTING:
			color = "purple";
			break;
		default:
			color = "";
		}

		String reboot_tag = "";
		if ("".equals(color)) {
			return "";
		} else {
			if (needShowReboot()) {
				reboot_tag = "<font color='red'>*</font>";
			}
			if(result == UpdateParameters.UPDATE_STAGED){
				return "<font color='"
						+ color
						+ "' title='"+this.getStagedTimes()+"'><b>"
						+ MgrUtil.getEnumString("enum.hiveAp.update.result."
								+ result) + "</b></font>" + reboot_tag;
			}else{
				String msg = MgrUtil.getEnumString("enum.hiveAp.update.result." + result);
				if (UpdateParameters.LEVEL_IMAGE_RISK == level) msg = "Warning<font color='red'>&#33;</font>";
				return "<font color='"
						+ color
						+ "'><b>"
						+ msg + "</b></font>" + reboot_tag;
			}
		}
	}

	@Transient
	public String getUpdateTypeHtmlString() {
		String str = getUpdateTypeString(updateType);
		if (isAutoProvision()) {
			str += "<span class='note'>&#8224; </span>";
		} else if (isImageDistribution()) {
			str += "<span class='note'>&#8225; </span>";
		}
		return str;
	}

	@Transient
	public static String getUpdateTypeString(short type) {
		switch (type) {
		case UpdateParameters.AH_DOWNLOAD_SCRIPT_WIZARD:
		case UpdateParameters.AH_DOWNLOAD_BOOTSTRAP:
		case UpdateParameters.AH_DOWNLOAD_SCRIPT:
		case UpdateParameters.AH_DOWNLOAD_IMAGE:
		case UpdateParameters.AH_DOWNLOAD_CWP:
		case UpdateParameters.AH_DOWNLOAD_RADIUS_CERTIFICATE:
		case UpdateParameters.AH_DOWNLOAD_VPN_CERTIFICATE:
		case UpdateParameters.AH_DOWNLOAD_COUNTRY_CODE:
		case UpdateParameters.AH_DOWNLOAD_PSK:
		case UpdateParameters.AH_DOWNLOAD_POE:
		case UpdateParameters.AH_DOWNLOAD_OS_DETECTION:
		case UpdateParameters.AH_DOWNLOAD_NET_DUMP:
		case UpdateParameters.AH_DOWNLOAD_IP_NETMASK_GATEWAY_DNS:
		case UpdateParameters.AH_DOWNLOAD_OUTDOORSTTINGS:
		case UpdateParameters.AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE:
		case UpdateParameters.AH_DOWNLOAD_DS_CONFIG:
		case UpdateParameters.AH_DOWNLOAD_DS_USER_CONFIG:
		case UpdateParameters.AH_DOWNLOAD_DS_AUDIT_CONFIG:
		case UpdateParameters.AH_DOWNLOAD_L7_SIGNATURE:
		case UpdateParameters.AH_DOWNLOAD_REBOOT:
			return MgrUtil.getEnumString("enum.hiveAp.update." + type);
		default:
			return "Unknown";
		}
	}

	@Transient
	public boolean isAutoProvision() {
		return tag == TAG_AUTO_PROVISION;
	}

	@Transient
	public boolean isImageDistribution() {
		return tag == TAG_IMAGE_DISTRIBUTION;
	}

	@Transient
	private String actionTypeString;

	public String getActionTypeString() {
		return actionTypeString;
	}

	public void setActionTypeString(boolean hideAction) {
		String str = "";
		HmUser us = BaseAction.getSessionUserContext();
		HmPermission permisson =  us.getUserGroup()
									.getFeaturePermissions()
									.get(Navigation.L2_FEATURE_HIVEAP_UPDATE_RESULTS);
		boolean isread = permisson == null ? true
				:permisson.getOperations() == HmPermission.OPERATION_READ;
		if (!hideAction) {
			switch (actionType) {
			case UpdateParameters.ACTION_CANCEL:
				boolean isCanceling = HmBeConfigUtil.getUpdateManager().isCanceling(
						nodeId, id, updateType)
						|| HmBeConfigUtil.getImageDistributor().isCanceling(nodeId,
								id, updateType);
				if (isCanceling) {
					str = "<a id='t"
							+ id
							+ "' class='actionType'><img title='Canceling' src='images/waitingSquare.gif' hspace=2 class='dinl'></a>";
				} else {
					if(isread){
						str = "<a class='actionType'>Cancel</a>";
					}else{
						str = "<a id='f"
								+ id
								+ "' class='actionType' href='javascript:requestCancel("
								+ id
								+ ");'>Cancel</a>"
								+ "<a id='t"
								+ id
								+ "' class='actionType' style='display:none;'><img title='Canceling' src='images/waitingSquare.gif' hspace=2 class='dinl'></a>";
					}
					
				}
				break;
			case UpdateParameters.ACTION_REBOOT:
				if(isread){
					str = "<a class='actionType'>Reboot</a>";
				}else{
					str = "<a class='actionType' href='javascript:requestReboot("
							+ id + ");'>Reboot</a>";
				}
				
				break;
			case UpdateParameters.ACTION_RETRY:
				if(isread){
					str = "<a class='actionType'>Retry</a>";
				}else{
					str = "<a class='actionType' href='javascript:requestRetry("
							+ id + ");'>Retry</a>";
				}
				
				break;
			}
		}
		this.actionTypeString = str;
	}

	@Transient
	private String userTimeZone;

	public void setUserTimeZone(String userTimeZone) {
		this.userTimeZone = userTimeZone;
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}

	@Transient
	public String getStartTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (startTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(startTime, TimeZone
							.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(startTime, getOwner()
							.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (startTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(startTime, TimeZone
							.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(startTime, getOwner()
							.getTimeZone());
				}
			}
		}
		
		return "";
	}

	@Transient
	public String getFinishTimeString() {
		if(BaseAction.getSessionUserContext() != null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			if (finishTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(finishTime, TimeZone
							.getTimeZone(userTimeZone), loginUser != null ? loginUser : owner);
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(finishTime, getOwner()
							.getTimeZone(), loginUser != null ? loginUser : owner);
				}
			}
		}else{
			if (finishTime > 0) {
				if (null != userTimeZone && !"".equals(userTimeZone)) {
					return AhDateTimeUtil.getSpecifyDateTime(finishTime, TimeZone
							.getTimeZone(userTimeZone));
				} else {
					return AhDateTimeUtil.getSpecifyDateTime(finishTime, getOwner()
							.getTimeZone());
				}
			}
		}
		
		return "";
	}

	@Transient
	public String getDownloadRateString() {
		return String.valueOf((int) (downloadRate * 100));
	}

	@Transient
	public String getDescriptionValue() {
		if (description == null) {
			return "";
		} else if (description.length() >= 39) {
			return description.substring(0, 39) + "...";
		} else {
			return description;
		}
	}

	@Transient
	public String getDescriptionTitle() {
		if (description == null) {
			return "";
		} else {
			return description.replace("'", "&acute;").replace("\n", "<br>")
					.replace("/", "/&#8203;");
		}
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public short getActionType() {
		return actionType;
	}

	public void setActionType(short actionType) {
		this.actionType = actionType;
	}

	public List<HiveApUpdateItem> getItems() {
		return items;
	}

	public void setItems(List<HiveApUpdateItem> items) {
		this.items = items;
	}

	@Transient
	public HiveApUpdateItem getItem(short type) {
		if (null != items) {
			for (HiveApUpdateItem item : items) {
				if (item.getUpdateType() == type) {
					return item;
				}
			}
		}
		return null;
	}

	@Transient
	public boolean isLastItem(HiveApUpdateItem item) {
		if (null != items && !items.isEmpty()) {
			HiveApUpdateItem lastItem = items.get(items.size() - 1);
			return lastItem.getUpdateType() == item.getUpdateType();
		}
		return false;
	}

	@Transient
	public int getItemCount() {
		if (null != items) {
			return items.size();
		}
		return 0;
	}

	@Transient
	public int getItemIndex(HiveApUpdateItem item) {
		if (null != items) {
			for (int i = 0; i < items.size(); i++) {
				if (items.get(i).getUpdateType() == item.getUpdateType()) {
					return i;
				}
			}
		}
		return -1;
	}

	@Transient
	public boolean isAllItemActived() {
		boolean actived = true;
		if (null != items) {
			//if exists reboot item no need active.
			for (HiveApUpdateItem item : items) {
				if (item.getUpdateType() == UpdateParameters.AH_DOWNLOAD_REBOOT) {
					return true;
				}
			}
			
			for (HiveApUpdateItem item : items) {
				if (!item.isActived()) {
					actived = false;
					break;
				}
			}
		}
		return actived;
	}

	@Transient
	public boolean needShowReboot() {
		return actionType == UpdateParameters.ACTION_REBOOT;
	}
	
	public String getStagedTimes(){
		return "Remaining Staged Times: " + stagedTime;
	}

	public int getStagedTime() {
		return stagedTime;
	}

	public void setStagedTime(int stagedTime) {
		this.stagedTime = stagedTime;
	}

	public short getLevel() {
		return level;
	}

	public void setLevel(short level) {
		this.level = level;
	}

}