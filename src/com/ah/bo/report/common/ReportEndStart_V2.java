package com.ah.bo.report.common;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import com.ah.common.config.datasource.Connections;
import com.ah.common.schedule.DataBaseUtil;
import com.ah.nms.worker.report.Axis;

public class ReportEndStart_V2 {

    ServletContext context;

    public ReportEndStart_V2( ServletContext _context ) {
	context = _context;

	// init memory DB
	Axis.setHMQuery( new HMQueryImpl( ) );
	Connections.setConnectionFactory( new DataBaseUtil( ) );
    }

    public void run( ) {
	if ( context != null ) {
	    // QuartzStarter_V2.start( context );
	    try {
		Class< ? > clazz = Class
			.forName( "com.ah.nms.worker.report.ExecutorStarter_V2" );
		Method method = clazz.getDeclaredMethod( "start",
			new Class[ ] { javax.servlet.ServletContext.class } );

		method.invoke( null, new Object[ ] { context } );
	    } catch ( Exception e ) {
	    }
	}
    }

    public boolean shutdown( ) {
	if ( context != null ) {
	    // QuartzStarter_V2.stop( );
	    try {
		Class< ? > clazz = Class
			.forName( "com.ah.nms.worker.report.ExecutorStarter_V2" );
		Method method = clazz.getDeclaredMethod( "stop",
			new Class[ ] {} );

		method.invoke( null, new Object[ ] {} );
	    } catch ( Exception e ) {
	    }
	}

	return true;
    }

}
