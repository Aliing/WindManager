package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.CLIBlob;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.Vlan;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SupplementalCLIAction extends BaseAction implements QueryBo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * ID of table columns in list view
	 */
	public static final int COLUMN_NAME = 1;
	
	public static final int COLUMN_DESCRIPTION = 2;
	
	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(2);
		
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}
	
	@Override
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.macFilter.name";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.macFilter.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}
	
	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("hollywood_02.cli.blob.title.new"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new CLIBlob());
				return isJsonMode() ? "cliBlobDlg" : INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("supplementalName", getDataSource().getSupplementalName())) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				
				if (checkContentLength()) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				

				if(checkIpVlanObject()){
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				
				if (isJsonMode()) {
					try {
						jsonObject = new JSONObject();
						id = createBo(dataSource);
						setUpdateContext(true);
						jsonObject.put("id", id);
						jsonObject.put("parentDomID",getParentDomID());
						jsonObject.put("name", getDataSource().getSupplementalName());
						jsonObject.put("resultStatus",true);
					}catch (Exception e) {
						jsonObject.put("resultStatus", false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.unknown"));
						return "json";
					}
					return "json";
				} else {
					if ("create".equals(operation)) {
						return createBo();
					} else {
						id = createBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else if (("cancel" + getLstForward()).equals(operation)) {
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				addLstTitle(getText("hollywood_02.cli.blob.title.edit") + " '" + getChangedProfileName() + "'");
				return isJsonMode() ? "cliBlobDlg" : returnWord;
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				CLIBlob profile = (CLIBlob) findBoById(
						boClass, cloneId, this);
				profile.setId(null);
				profile.setSupplementalName("");
				profile.setVersion(null);
				profile.setOwner(null);
				setSessionDataSource(profile);
				return INPUT;
			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {

				if (checkContentLength()) {
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				
				if(checkIpVlanObject()){
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg",getActionErrors().toArray()[0].toString());
					}
					return isJsonMode() ? "json" : INPUT ;
				}
				
				if (isJsonMode()) {
					jsonObject = new JSONObject();
					updateBo(dataSource);
					setUpdateContext(true);
					jsonObject.put("resultStatus",true);
					return "json";
				} else {
					if ("update".equals(operation)) {
						return updateBo();
					} else {
						updateBo(dataSource);
						setUpdateContext(true);
						return getLstForward();
					}
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}
	
	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_CLI_BLOB_SETTINGS);
		setDataSource(CLIBlob.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_CLI_BLOB;
	}
	
	public CLIBlob getDataSource() {
		return (CLIBlob) dataSource;
	}

	public String getChangedProfileName() {
		return getDataSource().getSupplementalName().replace("\\", "\\\\").replace(
				"'", "\\'");
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public int getProfileNameLength() {
		return getAttributeLength("supplementalName");
	}
	
	public int getCommentLength() {
		return getAttributeLength("description");
	}
	
	@Override
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		CLIBlob source = QueryUtil.findBoById(CLIBlob.class,
				paintbrushSource, this);
		if (null == source) {
			return null;
		}
		List<CLIBlob> list = QueryUtil.executeQuery(CLIBlob.class, null,
				new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<>(list.size());
		for (CLIBlob profile : list) {
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			CLIBlob vs = source.clone();
			if (null == vs) {
				continue;
			}
			
			vs.setId(profile.getId());
			vs.setVersion(profile.getVersion());
			vs.setSupplementalName(profile.getSupplementalName());
			//vs.setContentAera(profile.getContentAera());
			vs.setOwner(profile.getOwner());
			
			hmBos.add(vs);
		}
		return hmBos;
	}
	
	private boolean checkContentLength(){
		if(null != getDataSource() && null != getDataSource().getContentAera()){
			if(getDataSource().getContentAera().trim().length() == 0){
				addActionError(MgrUtil.getUserMessage("hollywood_02.cli.content.empty"));
				return true;
			}
			if(getDataSource().getContentAera().length() > CLIBlob.MAX_CLI_CONTENT_LENGTH){
				addActionError(MgrUtil.getUserMessage("hollywood_02.cli.content.max.length",String.valueOf(CLIBlob.MAX_CLI_CONTENT_LENGTH)));
				return true;
			}
		}
		return false;
	}
	
	private boolean checkIpVlanObject(){
		if(null == getDataSource().getContentAera()){
			return false;
		}
		
		String content = getDataSource().getContentAera();
		
		String regExString = "\\$\\{ip:.*?\\}|\\$\\{vlan:.+?\\}";
		Pattern pattern = Pattern.compile(regExString,Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(content);
		boolean result = matcher.find();
		if(result){
			Matcher ipMatcher = Pattern.compile("\\$\\{ip:.+?\\}",Pattern.CASE_INSENSITIVE).matcher(content);
			while(ipMatcher.find()){
				int endOffSet = ipMatcher.end() -1;
				int startOffSet = ipMatcher.start()+5;
			    String ipObjectName = content.substring(startOffSet,endOffSet);
			    if(null != ipObjectName && !"".equals(ipObjectName.trim())){
			    	IpAddress ipAddress = QueryUtil.findBoByAttribute(IpAddress.class, "addressName", ipObjectName.trim(), domainId);
				    if(null == ipAddress){
				    	addActionError(MgrUtil.getUserMessage("hollywood_02.cli.object.ip.no.exist", ipObjectName));
						return true;
				    }
			    }
			}
			
			Matcher vlanMatcher = Pattern.compile("\\$\\{vlan:.+?\\}",Pattern.CASE_INSENSITIVE).matcher(content);
			while(vlanMatcher.find()){
				int endOffSet = vlanMatcher.end() -1;
				int startOffSet = vlanMatcher.start()+7;
			    String vlanObjectName = content.substring(startOffSet,endOffSet);
			    if(null != vlanObjectName && !"".equals(vlanObjectName.trim())){
			    	Vlan vlanObje = QueryUtil.findBoByAttribute(Vlan.class, "vlanName", vlanObjectName.trim(), domainId);
				    if(null == vlanObje){
				    	addActionError(MgrUtil.getUserMessage("hollywood_02.cli.object.vlan.exist", vlanObjectName));
				    	return true;
				    }
			    }
			}
			
		}
		return false;
	}
	
}
