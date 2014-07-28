package com.ah.ui.actions.teacherView;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;

import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.bo.teacherView.TvComputerCartMacName;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class TvComputerCartAction extends BaseAction implements QueryBo{

	private static final long	serialVersionUID	= 1L;

	private String				cartName;

	private String				clientMacs;
	private String 				clientNames;
	/*
	 * ID of table columns in list view
	 */
	public static final int		COLUMN_NAME			= 1;

	public static final int		COLUMN_DESCRIPTION	= 2;

	/**
	 * get the description of column by id
	 * 
	 * @param id
	 *            -
	 * @return
	 * @author Joseph Chen
	 */
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_NAME:
			code = "config.tv.cartName";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.hp.description";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_NAME));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		return columns;
	}

	public String execute() throws Exception {
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.tv.computerCart"))) {
					return getLstForward();
				}
				setSessionDataSource(new TvComputerCart());
				hideCreateItem = "";
				hideNewButton = "none";
				return INPUT;
			} else if ("create".equals(operation) || ("create" + getLstForward()).equals(operation)) {
				if (checkNameExists("cartName", getDataSource().getCartName())) {
					return INPUT;
				}
				String macCountSql = "select count(stumac) from tv_computer_cart_mac aa "
					 	+ "inner join tv_computer_cart bb on aa.tv_cart_id=bb.id and bb.owner="
						+ getDomainId();
				List<?> macCount = QueryUtil.executeNativeQuery(macCountSql);
				if (Long.parseLong(macCount.get(0).toString())+ getDataSource().getItems().size()>TvStudentRosterAction.MAX_NUMBER_TV_STUDENT) {
					addActionError(MgrUtil.getUserMessage("error.maxSupportNumber",
							new String[]{"computers",String.valueOf(TvStudentRosterAction.MAX_NUMBER_TV_STUDENT)}));
					return INPUT;
				}
				if ("create".equals(operation)) {
					return createBo();
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("edit".equals(operation)) {
				String returnWord = editBo(this);
				if (dataSource == null) {
					return prepareBoList();
				} else {
					addLstTitle(getText("config.title.tv.computerCart.edit") + " '"
							+ getChangedName() + "'");
					return returnWord;
				}
			} else if ("update".equals(operation) || ("update" + getLstForward()).equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				}
				
				String macCountSql = "select count(stumac) from tv_computer_cart_mac aa "
				 	+ "inner join tv_computer_cart bb on aa.tv_cart_id=bb.id and bb.owner="
					+ getDomainId() + " and bb.id <> " + getDataSource().getId();
				List<?> macCount = QueryUtil.executeNativeQuery(macCountSql);
				if (Long.parseLong(macCount.get(0).toString())+ getDataSource().getItems().size()>TvStudentRosterAction.MAX_NUMBER_TV_STUDENT) {
					addActionError(MgrUtil.getUserMessage("error.maxSupportNumber",
							new String[]{"computers",String.valueOf(TvStudentRosterAction.MAX_NUMBER_TV_STUDENT)}));
					return INPUT;
				}
				
				if ("update".equals(operation)) {
					return updateBo();
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				TvComputerCart profile = (TvComputerCart) findBoById(boClass, cloneId,this);
				profile.setId(null);
				profile.setCartName("");
				profile.setOwner(null);
				profile.setVersion(null);
				List<TvComputerCartMacName> items = new ArrayList<TvComputerCartMacName>();
				items.addAll(profile.getItems());
				profile.setItems(items);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {
				setUpdateContext(true);
				return getLstForward();
			} else if ("import".equals(operation)) {
				addLstForward("tvComputerCart");
				clearErrorsAndMessages();
				return operation;
			} else if ("addMac".equals(operation)) {
				if (dataSource == null) {
					return prepareBoList();
				} else {
					if (!addSingleMac()) {
						addActionError(MgrUtil.getUserMessage("error.addObjectExists"));
					}
					sortLst();
					return INPUT;
				}
			} else if ("removeMac".equals(operation) || "removeMacNone".equals(operation)) {
				hideCreateItem = "removeMacNone".equals(operation) ? "" : "none";
				hideNewButton = "removeMacNone".equals(operation) ? "none" : "";
				if (dataSource == null) {
					return prepareBoList();
				} else {
					removeSelectedMacs();
					sortLst();
					return INPUT;
				}
			} else if ("addClients".equals(operation)) {
				TvComputerCart cart = QueryUtil.findBoByAttribute(
						TvComputerCart.class, "cartName", cartName, getDomainId());
				if (cart == null) {
					addActionError(MgrUtil.getUserMessage("action.error.add.client"));
					return SUCCESS;
				}
				id = cart.getId();
				setSessionDataSource(findBoById(boClass, id,this));

				if (dataSource == null) {
					addActionError(MgrUtil.getUserMessage("action.error.add.client"));
					return getLstForward();
				} else {
					addLstTitle(getText("config.title.tv.computerCart.edit") + " '"
							+ getChangedName() + "'");

					//
					String[] macs = clientMacs.split(",");
					String[] names = clientNames.split(",");
					if (macs!=null && names!=null){
						for(int i=0; i<macs.length;i++){
							TvComputerCartMacName newCart = new TvComputerCartMacName();
							newCart.setStuMac(macs[i]);
							newCart.setStuName(names[i]);
							if (!getDataSource().getItems().contains(newCart)) {
								TvComputerCartMacName tmpMacName = new TvComputerCartMacName();
								tmpMacName.setStuMac(macs[i]);
								tmpMacName.setStuName(names[i]);
								getDataSource().getItems().add(tmpMacName);
							}
						}
						sortLst();
					}
					return INPUT;
				}
			} else {
				baseOperation();
				return prepareBoList();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_TV_COMPUTERCART);
		setDataSource(TvComputerCart.class);
		keyColumnId = COLUMN_NAME;
		this.tableId = HmTableColumn.TABLE_TV_COMPUTERCART;
	}

	public TvComputerCart getDataSource() {
		return (TvComputerCart) dataSource;
	}

	private String				macAddStr		= "";
	private String				nameAddStr		= "";
	private Collection<String>	macIndices;

	private String				hideNewButton	= "";
	private String				hideCreateItem	= "none";

	public String getHideCreateItem() {
		return hideCreateItem;
	}

	public String getHideNewButton() {
		return hideNewButton;
	}

	public String getChangedName() {
		return getDataSource().getLabel().replace("\\", "\\\\").replace("'", "\\'");
	}

	public String getUpdateDisabled() {
		if ("".equals(getWriteDisabled())) {
			return "";
		}
		return "disabled";
	}

	public int getGridCount() {
		return getDataSource().getItems().size() == 0 ? 3 : 0;
	}
	
	public void sortLst(){
		if (!getDataSource().getItems().isEmpty()){
			Collections.sort(getDataSource().getItems(), new Comparator<TvComputerCartMacName>() {
				@Override
				public int compare(TvComputerCartMacName o1, TvComputerCartMacName o2) {
					return o1.getStuMac().compareToIgnoreCase(o2.getStuMac());
				}
			});
		}
	}

	protected boolean addSingleMac() throws Exception {
		for (TvComputerCartMacName single : getDataSource().getItems()) {
			if (single.getStuMac().equalsIgnoreCase(macAddStr)) {
				hideCreateItem = "";
				hideNewButton = "none";
				return false;
			}
		}
		TvComputerCartMacName tmpMacName = new TvComputerCartMacName();
		tmpMacName.setStuMac(macAddStr);
		tmpMacName.setStuName(nameAddStr);
		getDataSource().getItems().add(tmpMacName);
		macAddStr="";
		nameAddStr="";
		return true;
	}

	protected void removeSelectedMacs() {
		if (macIndices != null) {
			Collection<TvComputerCartMacName> removeList = new Vector<TvComputerCartMacName>();
			for (String serviceIndex : macIndices) {
				try {
					int index = Integer.parseInt(serviceIndex);
					if (index < getDataSource().getItems().size()) {
						removeList.add(getDataSource().getItems().get(index));
					}
				} catch (NumberFormatException e) {
					// Bug in struts, shouldn't create a 'false' entry when no
					// check boxes checked.
					return;
				}
			}
			getDataSource().getItems().removeAll(removeList);
		}
	}

	/**
	 * @param macAddStr
	 *            the macAddStr to set
	 */
	public void setMacAddStr(String macAddStr) {
		this.macAddStr = macAddStr.replaceAll(":", "").replaceAll("-", "");
	}

	/**
	 * @param macIndices
	 *            the macIndices to set
	 */
	public void setMacIndices(Collection<String> macIndices) {
		this.macIndices = macIndices;
	}

	public String getCartName() {
		return cartName;
	}

	public void setCartName(String cartName) {
		this.cartName = cartName;
	}

	public String getClientMacs() {
		return clientMacs;
	}

	public void setClientMacs(String clientMacs) {
		this.clientMacs = clientMacs;
	}

	/**
	 * @return the clientNames
	 */
	public String getClientNames() {
		return clientNames;
	}

	/**
	 * @param clientNames the clientNames to set
	 */
	public void setClientNames(String clientNames) {
		this.clientNames = clientNames;
	}

	/**
	 * @param nameAddStr the nameAddStr to set
	 */
	public void setNameAddStr(String nameAddStr) {
		this.nameAddStr = nameAddStr;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof TvComputerCart) {
			dataSource = bo;
			if (getDataSource().getItems() != null) {
				getDataSource().getItems().size();
			}
		}
		return null;
	}

	/**
	 * @return the macAddStr
	 */
	public String getMacAddStr() {
		return macAddStr;
	}

	/**
	 * @return the nameAddStr
	 */
	public String getNameAddStr() {
		return nameAddStr;
	}

}
