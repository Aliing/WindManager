package com.ah.util.bo.report.interceptor;

import com.ah.util.bo.report.AhReportInvokerInterface;

public interface AhReportInterceptor {
	public String intercept(AhReportInvokerInterface invoker) throws Exception;
}
