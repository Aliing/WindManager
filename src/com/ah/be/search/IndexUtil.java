/**
 * @filename			IndexOutputStream.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.Paging;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.mgmt.impl.PagingImpl;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;
import com.ah.ui.actions.Navigation;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * utiilties for index
 */
public class IndexUtil {

	public static final Tracer	log						= new Tracer(IndexUtil.class
																.getSimpleName());

	public static final int		COMPRESSION_MAX_SIZE	= 5000000;

	public static List<Object> read(String fileName) {
		if (fileName == null) {
			return null;
		}

		File file = new File(fileName);

		if (!file.exists()) {
			return null;
		}

		List<Object> list = new ArrayList<Object>();
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);

			Object temp;

			while ((temp = ois.readObject()) != null) {
				list.add(temp);
			}
		} catch (EOFException eof) {
			// end of the file
		} catch (Exception e) {
			list = null;
			log.error("Error in reading file: " + fileName, e);
		} finally {
			if (ois != null) {
				try {
					ois.close();
					fis.close();
				} catch (IOException e) {
					log.error("read", "error in closing file: " + fileName, e);
				}
			}
		}

		return list;
	}

	public synchronized static void save(Object object, String fileName) {
		if (object == null || fileName == null) {
			return;
		}

		File file = new File(fileName);
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;

		try {
			fos = new FileOutputStream(file, true);

			if (!file.exists() || file.length() < 1) {
				oos = new ObjectOutputStream(fos);
			} else {
				oos = new IndexOutputStream(fos);
			}

			oos.writeObject(object);
			oos.flush();
		} catch (FileNotFoundException e) {
			log.error("save", "file: " + fileName + " is not found", e);
		} catch (IOException e) {
			log.error("save", "error in saving map to file: " + fileName, e);
		} catch (Exception e) {
		    log.error("save", "error in saving map to file: " + fileName, e);
        } finally {
			if (oos != null) {
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					log.error("read", "error in closing file: " + fileName, e);
				}
			}
		}
	}

	public static byte[] compress(String source) {
		if (source == null) {
			return null;
		}

		try {
			byte[] input = source.getBytes("UTF-8");
			byte[] output = new byte[COMPRESSION_MAX_SIZE];

			Deflater compresser = new Deflater();
			compresser.setInput(input);
			compresser.finish();

			int compressedDataLength = compresser.deflate(output);

			return Arrays.copyOf(output, compressedDataLength);

		} catch (UnsupportedEncodingException e) {
			log.error("compress", "error in compressing string: " + source, e);
		}

		return null;
	}

	public static String decompress(byte[] input) {
		// Decompress the bytes
		Inflater decompresser = new Inflater();
		decompresser.setInput(input, 0, input.length);
		byte[] result = new byte[COMPRESSION_MAX_SIZE];
		int resultLength;
		try {
			resultLength = decompresser.inflate(result);
			decompresser.end();

			// Decode the bytes into a String
			return new String(result, 0, resultLength, "UTF-8");
		} catch (DataFormatException e) {
			log.error("compress", "error in decompressing", e);
		} catch (UnsupportedEncodingException e) {
			log.error("compress", "error in decompressing", e);
		}

		return null;
	}

	public static List<ResultEntry> convertResult(List<Target> targets, String searchKey) {
		if (targets == null) {
			return null;
		}

		List<ResultEntry> resultList = new ArrayList<ResultEntry>(targets.size());
		
		for (Target target : targets) {
			ResultEntry entry = new ResultEntry();
			// url
			entry.setUrl(getUrl(target));

			// description
			entry.setDescription(getDescription(target));

			//
			entry.setTarget(target);
			entry.setSearchKey(searchKey);

			/*
			 * event and alarm
			 * get field name and value from database table
			 */
			if(target.getType() == SearchParameter.TYPE_FAULT) {
				setFaultField(target, searchKey);
			}
			
			resultList.add(entry);

			log.debug("convertResult", entry.toString());
		}

		return resultList;
	}

	private static String getUrl(Target target) {
		if (target == null) {
			return null;
		}

		StringBuffer url = new StringBuffer(target.getAction());
		url.append(".action");

		// params
		if (target.getUrlParams() != null) {
			url.append("?").append(target.getUrlParams());
		}
		
		/*
		 * operation - new
		 */
		if(target instanceof FieldTarget) {
			if (target.getUrlParams() == null) {
				url.append("?operation=new");
			} else {
				if(url.indexOf("operation=") == -1) {
					url.append("&operation=new");
				}
			}
		}

		// operation - edit/redirect(topology)
		if (target instanceof EntityTarget) {
			EntityTarget entity = (EntityTarget) target;
			String operation = "edit";

			/*
			 * operation
			 * normally, operation is 'edit'.
			 * however, there are unique ones
			 */
			if (entity.getType() == SearchParameter.TYPE_TOPO) {
				operation = "redirect";
			} else if (entity.getType() == SearchParameter.TYPE_FAULT) {
				operation = Navigation.OPERATION_GOTO_PAGE;
			}

			if(entity.getFeature().equals("Aerohive APs")) {
				operation = "hiveApDetails";
			}
			
			if (entity.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_AUDITLOG)) {
			    // for audit log
				operation = Navigation.OPERATION_GOTO_PAGE;
			} else if (entity.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_CONFIGURATION_TEMPLATE)) {
			    // temporary avoid edit the operation for Network Policy, need to update the filter
			    operation = "listView";
			}
			
			if (target.getUrlParams() == null) {
				url.append("?operation=").append(operation);
			} else {
				if(url.indexOf("operation=") == -1) {
					url.append("&operation=").append(operation);
				}
			}

			if (entity.getType() == SearchParameter.TYPE_FAULT) {
				url.append("&gotoPage=").append(
						queryPageIndex(entity, entity.getBoId()));
			} else if (entity.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_AUDITLOG)) {
				url.append("&gotoPage=").append(
						queryPageIndex(entity, entity.getBoId()));
			} else if (entity.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_SYSTEMLOG)) {
				url.append("&gotoPage=").append(
						queryPageIndex(entity, entity.getBoId()));
			} else {
				url.append("&id=").append(entity.getBoId());
			}

			if(entity.getBoDomainId() != null) {
			url.append("&domainId=").append(entity.getBoDomainId());
			}
			
			url.append("&removeAllLstTitle=true");
		}
		
		if (url.toString().contains("?")){
			url.append("&searchFlg=true");
		} else {
			url.append("?searchFlg=true");
		}

		return url.toString();
	}

	private static int queryPageIndex(EntityTarget target, Long boID) {
		String tableName;
		String pagingSessionKey;
		Class<? extends HmBo> boClass;
		if (target.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_EVENTS)) {
			tableName = "ah_event";
			pagingSessionKey = AhEvent.class.getSimpleName() + "Paging";
			boClass = AhEvent.class;
		} else if (target.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_ALARMS)) {
			tableName = "ah_alarm";
			pagingSessionKey = AhAlarm.class.getSimpleName() + "Paging";
			boClass = AhAlarm.class;
		} else if (target.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_AUDITLOG)) {
			tableName = "hm_auditlog";
			pagingSessionKey = HmAuditLog.class.getSimpleName() + "Paging";
			boClass = HmAuditLog.class;
		} else if (target.getAction().equalsIgnoreCase(Navigation.L2_FEATURE_SYSTEMLOG)) {
			tableName = "hm_systemlog";
			pagingSessionKey = HmSystemLog.class.getSimpleName() + "Paging";
			boClass = HmSystemLog.class;
		} else {
			return 0;
		}

		int boIndex = queryRecordIndex(tableName, boID, target.getBoDomainId());
		Paging paging = (Paging) MgrUtil.getSessionAttribute(pagingSessionKey);
		if (paging == null) {
			paging = new PagingImpl(boClass);
			paging.clearNext();
			MgrUtil.setSessionAttribute(pagingSessionKey, paging);
			paging.executeQuery(new SortParams("id",false), null, target.getBoDomainId());
		}
		
		int pageSize = paging.getPageSize();
//		int pageCount = paging.getPageCount();

		// mark: because default order is descend
		return (boIndex / pageSize) + ((boIndex % pageSize > 0) ? 1 : 0);
	}

	/**
	 * query record index of given table with id.
	 * 
	 * @param tableName -
	 * @param boID -
	 * @param domainID -
	 * @return -
	 */
	private static int queryRecordIndex(String tableName, Long boID, Long domainID) {
		try {
			List<?> resultList = QueryUtil.executeNativeQuery(" select count(*) from " + tableName
					+ " where id>=" + boID + " and owner="+domainID);
			if (resultList.isEmpty()) {
				return 0;
			}

			Object index = resultList.get(0);
			boolean isInt = Pattern.matches("\\d+", String.valueOf(index));
			if (isInt) {
				return Integer.valueOf(String.valueOf(index));
			} else {
				return 0;
			}
		} catch (Exception e) {
			log.debug("queryRecordIndex", "Query index of " + tableName + "(ID:" + boID
					+ ") catch exception", e);
			return 0;
		}
	}

	private static String getDescription(Target target) {
		if (target == null) {
			return null;
		}

		// StringBuffer description = new StringBuffer();
		//
		// // // feature name
		// // description.append("Feature: " + target.getFeature());
		// //
		// String column = null;
		// String value = null;
		//
		// // colomn name
		// if (target instanceof ColumnTarget) {
		// ColumnTarget colTarget = (ColumnTarget) target;
		//
		// column = "Column";
		// value = colTarget.getColumn();
		// }
		//
		// // entity
		// if (target instanceof EntityTarget) {
		// EntityTarget entityTarget = (EntityTarget) target;
		//
		// column = entityTarget.getFieldName();
		// value = entityTarget.getFieldValue();
		// }
		//
		// if (value == null) {
		// column = "Feature";
		// value = target.getFeature();
		// }
		//			
		// value = value.replaceAll(searchParameter.getKeyword(), "<font
		// style=\"color:#FF0000\"><b>"
		// + searchParameter.getKeyword() + "</b></font>");
		//
		// description.append("<font style=\"color:#535353\">");
		// description.append(target.getFeature());
		// description.append("</font>");
		// description.append("[");
		// description.append(column + ": ");
		// description.append("<font style=\"color:#FFFFFF\">");
		// description.append(value);
		// description.append("</font>");
		// description.append("]");
		//
		// return description.toString();
		return target.getFeature();
	}
	
	private static void setFaultField(Target target, String keyword) {
		EntityTarget entityTarget;
		
		try {
			entityTarget = (EntityTarget)target;
		} catch(Exception e) {
			return ;
		}
		
		if(target.getAction().equalsIgnoreCase("events")) {
			setEventField(entityTarget, keyword);
		} else if(target.getAction().equalsIgnoreCase("alarms")) {
			setAlarmField(entityTarget,keyword);
		}
	}
	
	private static void setEventField(EntityTarget target, String keyword) {
		AhEvent event = QueryUtil.findBoById(AhEvent.class, target.getBoId());
		
		if (event == null) {
			return ;
		}
		
		// node id
		if(event.getApId().toLowerCase().contains(keyword)) {
			updateField(target, "Node ID", event.getApId());
			return ;
		}
		
		// host name
		if(event.getApName().toLowerCase().contains(keyword)) {
			updateField(target, "Host Name", event.getApName());
			return ;
		}
		
		// occurred
		if(event.getTrapTimeString().toLowerCase().contains(keyword)) {
			updateField(target, "Occurred", event.getTrapTimeString());
			return ;
		}
		
		// description
		if(event.getTrapDesc().toLowerCase().contains(keyword)) {
			updateField(target, "Description", event.getTrapDesc());
			return ;
		}
		
		// component
		if(event.getObjectName().toLowerCase().contains(keyword)) {
			updateField(target, "Component", event.getObjectName());
			return ;
		}
		
		// domain name
		if(event.getOwner().getDomainName().toLowerCase().contains(keyword)) {
			updateField(target, "Virtual HM", event.getOwner().getDomainName());
			return ;
		}
	}
	
    private static void setAlarmField(EntityTarget target, String keyword) {
    	AhAlarm alarm = QueryUtil.findBoById(AhAlarm.class, target.getBoId());
		
		if (alarm == null) {
			return ;
		}
		
		// node id
		if(alarm.getApId().toLowerCase().contains(keyword)) {
			updateField(target, "Node ID", alarm.getApId());
			return ;
		}
		
		// host name
		if(alarm.getApName().toLowerCase().contains(keyword)) {
			updateField(target, "Host Name", alarm.getApName());
			return ;
		}
		
		// severity
		if(alarm.getSeverityString().toLowerCase().contains(keyword)) {
			updateField(target, "Severity", alarm.getSeverityString());
			return ;
		}
		
		// occurred
		if(alarm.getTrapTimeString().toLowerCase().contains(keyword)) {
			updateField(target, "Occurred", alarm.getTrapTimeString());
			return ;
		}
		
		// cleared
		if(alarm.getClearTimeString().toLowerCase().contains(keyword)) {
			updateField(target, "Cleared", alarm.getClearTimeString());
			return ;
		}
		
		// description
		if(alarm.getTrapDesc().toLowerCase().contains(keyword)) {
			updateField(target, "Description", alarm.getTrapDesc());
			return ;
		}
		
		// component
		if(alarm.getObjectName().toLowerCase().contains(keyword)) {
			updateField(target, "Component", alarm.getObjectName());
			return ;
		}
		
		// domain name
		if(alarm.getOwner().getDomainName().toLowerCase().contains(keyword)) {
			updateField(target, "Virtual HM", alarm.getOwner().getDomainName());
			return ;
		}
	}
    
    private static void updateField(EntityTarget target, String fieldName, String fieldValue) {
    	target.setFieldName(fieldName);
    	target.setFieldValue(fieldValue);
    }

}