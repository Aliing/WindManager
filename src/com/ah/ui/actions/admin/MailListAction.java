package com.ah.ui.actions.admin;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.admin.MailNotification4VHM;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class MailListAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			baseOperation();
			enableSorting();
			
			return prepareBoList();
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAILLIST);
		dataSource = new MailNotification4VHM();
		boClass = MailNotification4VHM.class;
		keyColumnId = COLUMN_VHMNAME;
		tableId = HmTableColumn.TABLE_MAILLIST;
	}

	@Override
	public MailNotification4VHM getDataSource() {
		return (MailNotification4VHM) dataSource;
	}

	/*
	 * ID of table columns in list view
	 */
	public static final int	COLUMN_VHMNAME	= 1;

	public static final int	COLUMN_TOMAIL1	= 2;

	public static final int	COLUMN_TOMAIL2	= 3;

	public static final int	COLUMN_TOMAIL3	= 4;

	public static final int	COLUMN_TOMAIL4	= 5;

	public static final int	COLUMN_TOMAIL5	= 6;

	/**
	 * get the description of column by id
	 * 
	 * @param id -
	 * @return -
	 */
	@Override
	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_VHMNAME:
			code = "config.domain";
			break;
		case COLUMN_TOMAIL1:
			code = "admin.emailNotify.toEmail1";
			break;
		case COLUMN_TOMAIL2:
			code = "admin.emailNotify.toEmail2";
			break;
		case COLUMN_TOMAIL3:
			code = "admin.emailNotify.toEmail3";
			break;
		case COLUMN_TOMAIL4:
			code = "admin.emailNotify.toEmail4";
			break;
		case COLUMN_TOMAIL5:
			code = "admin.emailNotify.toEmail5";
			break;
		}

		return MgrUtil.getUserMessage(code);
	}

	@Override
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>(6);

		columns.add(new HmTableColumn(COLUMN_VHMNAME));
		columns.add(new HmTableColumn(COLUMN_TOMAIL1));
		columns.add(new HmTableColumn(COLUMN_TOMAIL2));
		columns.add(new HmTableColumn(COLUMN_TOMAIL3));
		columns.add(new HmTableColumn(COLUMN_TOMAIL4));
		columns.add(new HmTableColumn(COLUMN_TOMAIL5));

		return columns;
	}

	@Override
	// for other users of home domain
	public List<? extends HmBo> findBos() throws Exception {
		AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(),
				CrudOperation.READ);
		// Customized to not include an owner filter.
		List<? extends HmBo> bos = paging.executeQuery(sortParams, filterParams);
		
		for (Object bo : bos) {
			if (bo instanceof MailNotification4VHM) {
				MailNotification4VHM mailNotification4VHM = (MailNotification4VHM) bo;
				if (mailNotification4VHM.getOwner() != null) {
					mailNotification4VHM.getOwner().getId();
					mailNotification4VHM.getOwner().getDomainName();
				}
			}
		}
		
		return bos;
	}

}