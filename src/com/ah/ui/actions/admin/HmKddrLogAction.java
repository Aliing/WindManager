package com.ah.ui.actions.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ah.be.common.AhDirTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmKddrLog;
import com.ah.bo.admin.HmTableColumn;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

import edu.emory.mathcs.backport.java.util.Collections;

public class HmKddrLogAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("downloadFile".equals(operation)) {
				File file = new File(getLogFilePath());
				if (file.exists() && file.isFile()) {
					return "download";
				} else {
					addActionError(MgrUtil.getUserMessage("error.downloadFile",
							fileName));
				}
			} else if ("removeFile".equals(operation)) {
				File file = new File(getLogFilePath());
				if (file.isFile() && file.exists()) {
					file.delete();
				}
			}
			baseOperation();
			return preparenLogFiles();
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public String preparenLogFiles() throws Exception {
		if (sortParams == null) {
			enableSorting();
			if ("id".equals(sortParams.getOrderBy())) {
				sortParams.setOrderBy("logTimeStamp");
				sortParams.setAscending(false);
			}
		}
		String result = prepareBoList();
		return result;
	}

	public static final int COLUMN_DEVICENAME = 1;

	public static final int COLUMN_FILENAME = 2;

	public static final int COLUMN_TIME = 3;

	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_DEVICENAME));
		columns.add(new HmTableColumn(COLUMN_FILENAME));
		columns.add(new HmTableColumn(COLUMN_TIME));
		return columns;
	}

	public final String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_DEVICENAME:
			code = "admin.kddrLog.deviceName";
			break;
		case COLUMN_FILENAME:
			code = "admin.kddrLog.fileName";
			break;
		case COLUMN_TIME:
			code = "admin.kddrLog.time";
			break;
		}
		return MgrUtil.getUserMessage(code);
	}

	protected void setTableColumns() {
		selectedColumns = getUserContext().getTableViews().get(tableId);

		if (selectedColumns == null) {
			selectedColumns = getDefaultSelectedColums();
		}

		setColumnDescription(selectedColumns);
		availableColumns = getDefaultSelectedColums();
		setColumnDescription(availableColumns);
		availableColumns.removeAll(selectedColumns);
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_KDDRLOG);
		setDataSource(HmKddrLog.class);
		tableId = HmTableColumn.TABLE_KDDRLLOG;
	}

	public List<? extends HmBo> findBos() throws Exception {
		List<HmKddrLog> pageList = new ArrayList<HmKddrLog>();
		File filePath = new File(getLogFileDir());
		List<HmKddrLog> kddrLogList = new ArrayList<HmKddrLog>();
		getKDDRlLogList(filePath.listFiles(), kddrLogList);
		if (kddrLogList.isEmpty()) {
			return pageList;
		}
		paging.setRowCount(kddrLogList.size());
		Collections.sort(kddrLogList, new Comparator<HmKddrLog>() {
			@Override
			public int compare(HmKddrLog o1, HmKddrLog o2) {
				int compareValue = 0;
				if ("logTimeStamp".equals(sortParams.getOrderBy())) {
					long timeStamp1 = o1.getLogTimeStamp();
					long TimeStamp2 = o2.getLogTimeStamp();
					compareValue = (int) (timeStamp1 - TimeStamp2);
				} else {
					String n1 = "";
					String n2 = "";
					if ("deviceName".equals(sortParams.getOrderBy())) {
						n1 = o1.getDeviceName();
						n2 = o2.getDeviceName();
					} else if ("fileName".equals(sortParams.getOrderBy())) {
						n1 = o1.getFileName();
						n2 = o2.getFileName();
					} else if ("parentFileName".equals(sortParams.getOrderBy())) {
						n1 = o1.getParentFileName();
						n2 = o2.getParentFileName();
					}
					compareValue = n1.compareToIgnoreCase(n2);
				}
				if (!sortParams.isAscending()) {
					compareValue = -compareValue;
				}
				return compareValue;
			}
		});
		int startIndex = paging.getFirstResult();
		int endIndex = startIndex + (paging.getPageSize());
		if (endIndex >= kddrLogList.size()) {
			endIndex = kddrLogList.size();
		}
		for (int i = startIndex; i < endIndex; i++) {
			pageList.add(kddrLogList.get(i));
		}
		return pageList;
	}

	private void getKDDRlLogList(File[] files, List<HmKddrLog> kddrLogList) {
		if (null == files) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				getKDDRlLogList(file.listFiles(), kddrLogList);
			} else {
				String fileName = file.getName();
				long logTimeStamp = file.lastModified();
				String parentFileName = file.getParentFile().getName();
				String deviceName = fileName.split("__")[0];
				/*SimpleHiveAp hiveAP = CacheMgmt.getInstance().getSimpleHiveAp(
						deviceName);
				if (null != hiveAP) {
					deviceName = hiveAP.getHostname();
				}*/
				HmKddrLog kddrLog = new HmKddrLog();
				kddrLog.setDeviceName(deviceName);
				kddrLog.setFileName(fileName);
				kddrLog.setLogTimeStamp(logTimeStamp);
				kddrLog.setLogTimeZone(getUserTimeZone());
				kddrLog.setParentFileName(parentFileName);
				kddrLog.setOwner(getDomain());
				kddrLogList.add(kddrLog);
			}
		}
	}

	public String fileName;
	public String parentFileName;

	private String getLogFileDir() {
		if (getShowDomain()) {
			return AhDirTools.getNetdumpUploadDir();
		}
		return AhDirTools.getNetdumpUploadDir() + getDomain().getDomainName();
	}

	private String getLogFilePath() {
		return AhDirTools.getNetdumpUploadDir() + parentFileName
				+ File.separator + fileName;
	}

	public InputStream getInputStream() throws Exception {
		return new FileInputStream(getLogFilePath());
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getParentFileName() {
		return parentFileName;
	}

	public void setParentFileName(String parentFileName) {
		this.parentFileName = parentFileName;
	}

}
