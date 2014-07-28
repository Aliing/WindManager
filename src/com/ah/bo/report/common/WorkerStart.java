package com.ah.bo.report.common;


import java.io.File;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;


import com.ah.common.schedule.QuartzMgr;
import com.ah.nms.worker.report.rowup.calculate.WorkerMgr;
import com.ah.nms.worker.report.rowup.calculate.tranmit.DefaultResponseImlp;

public class WorkerStart extends Thread{

	
	private ServletContext context;
	final protected Logger logger = Logger.getLogger( getClass() );
	private static  Log quartzLog = LogFactory.getLog("quartzinfo");
	
	public WorkerStart(ServletContext context){
		this.setName( WorkerStart.class.getName());
		String dir = System.getProperty("user.dir");
		WorkerMgr.getInstance().setLogger(quartzLog);
		this.context = context;
		WorkerMgr.getInstance().setQueue(QuartzMgr.getInstance().getQueue());
		WorkerMgr.getInstance().setReceiveFactory(new DefaultResponseImlp());
//		String configurePath = this.context.getServletContext().getRealPath("/")+"WEB-INF" + File.separator + "MetricFactory.xml";		
		WorkerMgr.getInstance().setConfigurePath(QuartzMgr.getInstance().getConfigurePath() + File.separator + "metricFactory.xml");
		quartzLog.info("MetricFactory.xml Load Path : " + QuartzMgr.getInstance().getConfigurePath());
		quartzLog.info("Worker Init.");
	}
	public void run(){
		
		
		quartzLog.info("contextInitialized Worker Start.");
		WorkerMgr.getInstance().receive(null);	
	}
	
	
	public static void main(String[] args){
//		System.out.println(System.getProperty("user.dir")+ File.separator + "webapps"+File.separator+"WEB-INF" + File.separator + "qzconf");
		
		Thread wr = new WorkerStart(null);
		wr.start();
//		new ReceiveFromMQ().receive();
	}
}
