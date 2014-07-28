package com.ah.util.bo.report.interceptor;

import org.apache.commons.lang.StringUtils;

import com.ah.util.bo.report.AhReportInvokerInterface;

public class AhPerformanceInterceptor implements AhReportInterceptor {

	@Override
	public String intercept(AhReportInvokerInterface invoker) throws Exception {
		// these are all test code now
		String result = beforeInvoke();
		if (!StringUtils.isBlank(result)) {
			return result;
		}
		invoker.invoke();
		afterInvoke();
		
		return null;
	}
	
	private String beforeInvoke() {
		System.out.println("called before runing real report calculate.");
		return null;
	}
	
	private void afterInvoke() {
		System.out.println("called after runing report, finished.");
	}

}
