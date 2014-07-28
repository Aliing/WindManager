package com.ah.be.config.cli.generate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface CLIConfig {

	String value() default "";
	
	String platform() default "";
	
	String type() default "";
	
	String version() default "";
	
	String expression() default "";
}
