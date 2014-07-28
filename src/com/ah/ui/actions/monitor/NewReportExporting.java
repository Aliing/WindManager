package com.ah.ui.actions.monitor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.ah.be.admin.util.EmailElement;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardLayout;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryCertainBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.common.async.Response;
import com.ah.util.Tracer;
import com.ah.util.bo.dashboard.DaDataCalculateUtil;
import com.ah.util.bo.dashboard.DaExportPdfUtil;
import com.ah.util.bo.dashboard.DaExportPdfUtil.ExportPdfResponse;
import com.ah.util.bo.dashboard.DaExportedData;
import com.ah.util.bo.dashboard.DaExportedSingleData;
import com.ah.util.bo.dashboard.DaHelper;
import com.ah.util.bo.dashboard.ReportDataRequestUtil;
import com.ah.util.bo.dashboard.RequestData;

public class NewReportExporting {
	private static final Tracer log = new Tracer(NewReportExporting.class
			.getSimpleName());
	
	private DaExportedData exportedData = new DaExportedData();
	
	//waiting time for inserting exporting object into task queue
	private  static final long taskTimeout=2000L;
	//max size of task queue
	private  static final int MAX_TASKS=100;
	//number of tasks in the queue
	private int remainTasks=0;
	
	private  String emailAddress;
	
	
	//container for report exporting task
	private static BlockingQueue<NewReportExporting> daExportTaskQueue=new LinkedBlockingQueue<NewReportExporting>(MAX_TASKS);
	
	private static ReentrantLock taskLock=new ReentrantLock();
	
	private static Condition taskCondition=taskLock.newCondition();
	//number of email tasks under processing
	private static int emailTask=0;
	
	
	
	public NewReportExporting(boolean scheduled, AhDashboard dashboard, HmDomain owner, TimeZone timeZone) {
		this.dashboard = dashboard;
		this.timeZone = timeZone;
		this.owner = owner;
		this.scheduled = scheduled;
		this.exportType = EXPORT_TYPE_DASHBOARD;
		
		prepareReportInfoFromDashboard(dashboard);
	}
	
	public NewReportExporting(boolean scheduled, List<ReportDataHolder> reportsData, AhDashboard dashboard, HmDomain owner, TimeZone timeZone) {
		if (reportsData != null) {
			this.reportsData = reportsData;
		} else {
			this.reportsData = new ArrayList<>();
		}
		this.dashboard = dashboard;
		this.timeZone = timeZone;
		this.owner = owner;
		this.scheduled = scheduled;
		this.exportType = EXPORT_TYPE_REPORTSDATA;
		
		prepareReportInfoFromDashboard(dashboard);
	}
	
	private void prepareReportInfoFromDashboard(AhDashboard dashboard) {
		if (dashboard != null) {
			this.exportedData.setDaName(dashboard.getDashName());
			//this.exportedData.setStartTime(dashboard.getCustomStartTime());
			//this.exportedData.setEndTime(dashboard.getCustomEndTime());
		}
	}
	
	public void run(ExportPdfResponse response) {
		this.response = response;
		//insert report object into the end of task queue,if success return true
		boolean flag=addTask(this);
		NewReportExporting exportTask;
		//if task added successful
		while(flag)
		{
			try{
				//if task is scheduled exporting work,delay the process to the specified wait time
					if(scheduled)
					{
						if(remainTasks>1)
						{
						Thread.sleep(120*remainTasks);
						}
					}
				//recieve the head element of task queue
				exportTask=daExportTaskQueue.peek();
				//if task queue is not empty and the head element equals current task
				if(exportTask!=null&&exportTask==this)
				{
//					System.out.println("----------"+Thread.currentThread().getName()+":"+exportTask+"-----------------");
					//if there is report must be sent out, call other threads to wait to generate pdf report in case the old file might be covered by the new one 
					taskLock.lock();
					//remove the head element,start exporting task
					exportTask=daExportTaskQueue.poll();
					if(scheduled||(emailAddress!=null&&!"".equals(emailAddress)))
					{
						++emailTask;
						if(response.getResponse()==null&&emailTask>1)
						{
						taskCondition.await();
						}
					}
					taskLock.unlock();
					exportTask.startTask();
					break;
				}
				//if task queue is empty or the recieved element is not current task
				else{
					//release the unused object,wait a specified time for other threads to process 
					exportTask=null;
					Thread.sleep(500L);
				}
			}catch(Exception e)
			{
				log.error("NewReportExporting run()", e);
			}
			finally{
				flag=false;
				if(taskLock.isLocked())
				{
					try{
						taskLock.unlock();
					}catch(Exception e)
					{
						log.error("NewReportExporting,run() unlock task failed", e);
					}
				}
			}
		}
	}
	public boolean addTask(NewReportExporting newTask)
	{
		boolean boolResult=false;
		try{
			taskLock.lock();
			//insert current export object into task queue,waiting up to the specified wait time 
			//if necessary for space to become available.
			boolResult=daExportTaskQueue.offer(newTask,taskTimeout,TimeUnit.MICROSECONDS);
			remainTasks=daExportTaskQueue.size();
		}catch(Exception e)
		{
			log.error("NewReportExporting,addTask()", e);
		}
		finally{
			taskLock.unlock();
		}
		return boolResult;
	}
	
	public  void startTask()
	{
		try{
		if (this.exportType == EXPORT_TYPE_DASHBOARD) {
			runWithDashboard();
		} else if (this.exportType == EXPORT_TYPE_REPORTSDATA) {
			runWithReportsData();
		}
		if (response != null) {
			if(scheduled||(emailAddress!=null&&!"".equals(emailAddress)))
			{
				mailPdfFile();
				
			}
			response.respond();
		}
		
		}catch(Exception e)
		{
			log.error("NewReportExporting,startTask failed",e);
		}
		finally{
			if(emailTask>0)
			{
				--emailTask;
				//if running emailTask is greater than 0,then wake up other threads 
				if(emailTask>0){
					taskCondition.signalAll();
					}
				
			}
		}
	}
	
	private void runWithDashboard() {
		prepareDataHolderMapForDashboard();
		runWithPreparedData();
	}
	private void runWithReportsData() {
		prepareDataHolderMapForReportsData();
		runWithPreparedData();
	}
	
	private boolean blnTest = false;
	private void runWithPreparedData() {
		if (dataHolderMap != null
				&& !dataHolderMap.isEmpty()) {
			final Map<Long, Integer> widgetKeyOrders = this.getWidgetKeyOrders();
			List<Map.Entry<String, ReportDataHolder>> keysData = new ArrayList<>(dataHolderMap.entrySet());
			Collections.sort(keysData, new Comparator<Map.Entry<String, ReportDataHolder>>() {

				@Override
				public int compare(Entry<String, ReportDataHolder> o1,
						Entry<String, ReportDataHolder> o2) {
					int order1 = 0,
						order2 = 0;
					if (widgetKeyOrders.containsKey(o1.getValue().getWidgetId())) {
						order1 = widgetKeyOrders.get(o1.getValue().getWidgetId());
					}
					if (widgetKeyOrders.containsKey(o2.getValue().getWidgetId())) {
						order2 = widgetKeyOrders.get(o2.getValue().getWidgetId());
					}
					return order1 - order2;
				}
			});
			//for (final String key : dataHolderMap.keySet()) {
			for (Map.Entry<String, ReportDataHolder> entry : keysData) {
				final String key = entry.getKey();
				ReportDataHolder dataHolder = dataHolderMap.get(key);
				if (this.exportType == EXPORT_TYPE_DASHBOARD) {
					final RequestData data = encapReportRequest(dataHolder);
					dataHolder.setSample(data.getSample());
					if (data != null) {
						if (blnTest) {
							if (dataHolder != null
									&& response != null) {
								dataHolder.setResult("0");
								dataHolder.setException("");
								dataHolder.setXaxis(data.getAxis());
								String testCode = "line";
								if (data.isBlnOvertime()) {
									testCode = "tb";
								}
								dataHolder.setData(ReportDataAction.getFakeDataForTestReturn_static(data, testCode));
							}
						} else {
							try {
								ReportDataRequestUtil.requestForDashboard(data.formatData().getData(), 
										new Response.Handler(){
											public void responded (Response response) {
												finishAReportDataRequest();
												if (response != null) {
													encapWithReportResponse(key, data.getAxis(), response);
												}
											}
										}
								);
								addAReportDataRequest();
							} catch (Exception e) {
								log.error("Failed to request report backend data for widget: " + dataHolderMap.get(key).getWidgetId(), e);
							}
						}
					}
				}
			}
			
			int iCount = 0;
			int waitTime = 120 * this.reportRequestCount;
			while (isStillInRequesting() && iCount++ < waitTime) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					log.error("Failed to sleep this thread[NewReportExporting]", e);
				}
				continue;
			}
			
			for (String key : dataHolderMap.keySet()) {
				DaExportedSingleData daData=DaDataCalculateUtil.doDataCalculate(dataHolderMap.get(key), owner, this.timeZone);
				if(daData!=null)
				this.exportedData.addData(daData);
			}
			
			if (this.exportedData != null
					&& this.exportedData.getData() != null) {
//				exportedData.setExportTime(dashboard.getDashPDFReportTimeString());
				exportedData.setDashboard(dashboard);
				DaExportPdfUtil.excutePerformance(exportedData, scheduled, owner, timeZone, this.response);
			}
		}
	}
	
	private Map<Long, Integer> getWidgetKeyOrders() {
		Map<Long, Integer> result = null;
		if (dataHolderMap != null
				&& !dataHolderMap.isEmpty()) {
			result = new HashMap<>();
			List<Long> widgetIds = new ArrayList<>();
			for (ReportDataHolder dataHolder : dataHolderMap.values()) {
				widgetIds.add(dataHolder.getWidgetId());
			}
			
			List<AhDashboardWidget> widgetBos = QueryUtil
					.executeQuery(AhDashboardWidget.class, 
						null, 
						new FilterParams("id", widgetIds), 
						this.owner.getId(),
						new QueryCertainBo<AhDashboardWidget>() {
	
							@Override
							public Collection<HmBo> loadBo(AhDashboardWidget bo) {
								if (bo.getWidgetConfig() != null) {
									bo.getWidgetConfig().getId();
								}
								return null;
							}
					
				});
			
			if (widgetBos != null
					&& !widgetBos.isEmpty()) {
				for (AhDashboardWidget daWidget : widgetBos) {
					if (daWidget.getWidgetConfig() != null) {
						result.put(daWidget.getId(), DaHelper.getReportKeySequence(daWidget.getWidgetConfig().getKey()));
					} else {
						result.put(daWidget.getId(), DaHelper.getReportKeySequence(-1));
					}
				}
			}
		}
		
		return result;
	}
	
	private void encapWithReportResponse(String key, String xaxis, Response response) {
		ReportDataHolder dataHolder = dataHolderMap.get(key);
		if (dataHolder != null
				&& response != null) {
			dataHolder.setResult(String.valueOf(response.result));
			dataHolder.setException(response.exception);
			dataHolder.setXaxis(xaxis);
			if (response.data != null) {
				dataHolder.setData(response.data.toString());
			}
		}
	}
	
	private RequestData encapReportRequest(ReportDataHolder dataHolder) {
		if (dataHolder == null) {
			return null;
		}
		
		RequestData data = null;
		if (dataHolder.getWidgetId() != null) {
			data = ReportDataRequestUtil.prepareDataWithWidgetID(this.dashboard, dataHolder.getWidgetId(), this.timeZone);
		}
		
		if (data == null) {
			return null;
		}
		
		return data;
	}
	
	private void prepareDataHolderMapForDashboard() {
		if (this.dashboard == null) {
			return;
		}
		List<?> layoutsTmp = QueryUtil.executeQuery("select id, itemOrder from " + AhDashboardLayout.class.getSimpleName(), 
				null, 
				new FilterParams("dashboard.id", this.dashboard.getId()), 
				this.dashboard.getOwner().getId());
		Map<Long, Byte> layouts = new HashMap<>();
		if (layoutsTmp != null
				&& !layoutsTmp.isEmpty()) {
			for (Object obj : layoutsTmp) {
				Object[] objs = (Object[])obj;
				layouts.put(Long.valueOf(objs[0].toString()), Byte.valueOf(objs[1].toString()));
			}
		}
		
		if (layouts != null
				&& !layouts.isEmpty()) {
			List<?> widgetsTmp = QueryUtil.executeQuery("select id, itemOrder, daLayout.id from " + AhDashboardWidget.class.getSimpleName(), 
					null, 
					new FilterParams("daLayout.id", layouts.keySet()), 
					this.dashboard.getOwner().getId());
			if (widgetsTmp != null
					&& !widgetsTmp.isEmpty()) {
				for (Object obj : widgetsTmp) {
					Object[] objs = (Object[])obj;
					ReportDataHolder dataHolder = new ReportDataHolder();
					dataHolder.setColumn(layouts.get(Long.valueOf(objs[2].toString())));
					dataHolder.setOrder(Integer.valueOf(objs[1].toString()));
					dataHolder.setWidgetId(Long.valueOf(objs[0].toString()));
					dataHolderMap.put(getDataIdentifier(dataHolder.getColumn(), dataHolder.getOrder()), dataHolder);
				}
			}
		}
	}
	private void prepareDataHolderMapForReportsData() {
		if (this.reportsData == null
				|| this.reportsData.isEmpty()) {
			return;
		}
		
		for (ReportDataHolder dataHolder : this.reportsData) {
			if (dataHolder == null) {
				continue;
			}
			dataHolderMap.put(getDataIdentifier(dataHolder.getColumn(), dataHolder.getOrder()), dataHolder);
		}
	}
	private void mailPdfFile() {
		try {
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			sf.setTimeZone(dashboard.getTz());
			String mailFileName;
			mailFileName = "Reporting" + "_"  + dashboard.getDashName() + "_" + sf.format(new Date()) + ".pdf";
			String filePath;
			if (scheduled) {
				emailAddress=dashboard.getReEmailAddress();
				filePath= DaExportPdfUtil.fileDirPath + File.separator + dashboard.getOwner().getDomainName() + File.separator + mailFileName;
			} else {
				filePath=DaExportPdfUtil.fileDirPathCurrent + File.separator + dashboard.getOwner().getDomainName() + File.separator + mailFileName;
			}
			
			File tmpFile = new File(filePath);
			if (tmpFile.exists()) {
				EmailElement email = new EmailElement();
				email.setDomainName(dashboard.getOwner().getDomainName());
				email.setToEmail(emailAddress);
				if (scheduled) {
					email.setSubject("Schedule Network Summary Report--" + dashboard.getDashName());
				} else {
					email.setSubject("Current Network Summary Report--" + dashboard.getDashName());
				}
				email.setMailContent(mailFileName);
				List<String> fileList = new ArrayList<String>();
				fileList.add(filePath);
				email.setDetachedFileList(fileList);
				email.setMustBeSent(true);
				
				HmBeAdminUtil.sendEmail(email);
			}
		} catch (Exception e) {
			log.error("NetReportExporting.mailPdfFile(): Failed send mail! ",e);
		}
	}
	
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}


	public static class ReportDataHolder {
		private String result;
		private String exception;
		private String data;
		private Long widgetId;
		private int column;
		private int order;
		private String xaxis;
		private int sample;
		
		public ReportDataHolder(){
			
		}
		
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
		public String getException() {
			return exception;
		}
		public void setException(String exception) {
			this.exception = exception;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public int getColumn() {
			return column;
		}
		public void setColumn(int column) {
			this.column = column;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public Long getWidgetId() {
			return widgetId;
		}
		public void setWidgetId(Long widgetId) {
			this.widgetId = widgetId;
		}
		public String getXaxis() {
			return xaxis;
		}
		public void setXaxis(String xaxis) {
			this.xaxis = xaxis;
		}
		public int getSample() {
			return sample;
		}
		public void setSample(int sample) {
			this.sample = sample;
		}
	}
	
	private Map<String, ReportDataHolder> dataHolderMap = new HashMap<>();
	private String getDataIdentifier(int column, int order) {
		return column + "_" + order;
	}
	
	private static byte EXPORT_TYPE_DASHBOARD = 1;
	private static byte EXPORT_TYPE_REPORTSDATA = 2;
	private byte exportType = EXPORT_TYPE_DASHBOARD;
	
	private AhDashboard dashboard;
	private List<ReportDataHolder> reportsData;
	private ExportPdfResponse response;
	private TimeZone timeZone;
	private HmDomain owner;
	private boolean scheduled;
	
	private int reportRequestCount = 0;
	private boolean isStillInRequesting() {
		return reportRequestCount > 0;
	}
	private void addAReportDataRequest() {
		reportRequestCount++;
	}
	private void finishAReportDataRequest() {
		reportRequestCount--;
	}
	
	public AhDashboard getDashboard() {
		return dashboard;
	}
	public void setDashboard(AhDashboard dashboard) {
		this.dashboard = dashboard;
	}
	public List<ReportDataHolder> getReportsData() {
		return reportsData;
	}
	public void setReportsData(List<ReportDataHolder> reportsData) {
		this.reportsData = reportsData;
	}
	
}
