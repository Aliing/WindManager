package com.ah.bo.report.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ah.util.bo.report.builder.DefaultReportBuilder;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AhReportConfig {
	
	public long id();
	
	@SuppressWarnings("rawtypes")
	public Class builder() default DefaultReportBuilder.class;
	
	public boolean groupCalEnabled() default true;
}
