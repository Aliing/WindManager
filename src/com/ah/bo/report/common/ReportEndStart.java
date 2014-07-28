package com.ah.bo.report.common;


import java.io.File;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.common.config.datasource.Connections;
import com.ah.common.schedule.DataBaseUtil;
import com.ah.common.schedule.QuartzMgr;
import com.ah.common.schedule.tranmit.BasicQueue;
import com.ah.common.schedule.tranmit.DefaultRequestImlp;
import com.ah.nms.worker.report.Axis;
import com.ah.util.HibernateDbConfigTool;
import com.ah.util.Tracer;


public class ReportEndStart extends Thread{
	
	private static final Tracer log = new Tracer(ReportEndStart.class.getSimpleName());
	private static final Log quartzLog = LogFactory.getLog("quartzinfo");
	private ServletContext context;
	public ReportEndStart(ServletContext context){
		this.setName( QuartzMgr.class.getName());
		//D:\WorkSpaces\REPORT\repository\dev\NetworkManager-config\src\main\resources\com\ah\nms\config
		//D:\WorkSpaces\REPORT\repository\dev\common\configuration\src\main\java\com\ah\common\schedule\qzconf
//		String dir = System.getProperty("user.dir");
		this.context = context;
		//temp for debug	
//		QuartzMgr.getInstance().setConfigurePath(RepoConfigure.configurePath);
		
		// No suitable driver found for jdbc:postgresql://jdbc:postgresql://10.155.21.214:5432/hm
		
		QuartzMgr.getInstance().setDbIPAddressURL(HibernateDbConfigTool.getHostAndPort().get(3));
		QuartzMgr.getInstance().setUsername(System.getProperty("hm.connection.username"));
		QuartzMgr.getInstance().setPassword(System.getProperty("hm.connection.password"));
		String configurePath = this.context.getRealPath("/")+"WEB-INF" + File.separator + "report"+File.separator + "qzconf";		
		QuartzMgr.getInstance().setConfigurePath(configurePath);
		QuartzMgr.getInstance().setLogger(quartzLog);
		QuartzMgr.getInstance().setQueueMaxSize(10000);
		QuartzMgr.getInstance().setRequest(new DefaultRequestImlp());
		QuartzMgr.getInstance().setQueue(new BasicQueue());
		
		//init memory DB
		Axis.setHMQuery(new HMQueryImpl());
		Connections.setConnectionFactory( new DataBaseUtil());
		
		quartzLog.info("contextInitialized configure Path : " + configurePath);
		
	}
	public void run(){
		
		QuartzMgr.getInstance().load();
		quartzLog.info("contextInitialized Quartz Start.");
	}

	
	public static void main(String[] args){
//		System.out.println(System.getProperty("user.dir")+ File.separator + "webapps"+File.separator+"WEB-INF" + File.separator + "qzconf");
		Thread pp = new ReportEndStart(null);
		pp.start();
//		new ReceiveFromMQ().receive();
	}
}
