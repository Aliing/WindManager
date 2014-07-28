package com.ah.apiengine.agent.subagent.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.apiengine.agent.subagent.CommandLineAgent;
import com.ah.apiengine.element.CommandLine;

public class CommandLineAgentImpl implements CommandLineAgent {

	private static final Log						log			= LogFactory
																		.getLog("tracerlog.CommandAgentImpl");

	private static final Map<String, String>	commandMap	= new HashMap<String, String>(13);

	static {
		commandMap.put("backupvhm", "com.ah.be.apiengine.agent.HmOperation#backupVhm");
		commandMap.put("restorevhm", "com.ah.be.apiengine.agent.HmOperation#restoreVhm");
		commandMap.put("queryprog", "com.ah.be.apiengine.agent.HmOperation#queryCommandProgress");
	}

	@Override
	public String execute(CommandLine command2) {
		String command = command2.getString();
		log.info("command=" + command);
		// analyze command
		String cmd = command.substring(0, command.indexOf(' '));
		String value2 = commandMap.get(cmd);
		String[] value = value2.split("#");
		log.info("value[0]=" + value[0]);
		log.info("value[1]=" + value[1]);

		try {
			Class<?> cls = Class.forName(value[0]);
			Object instance = cls.newInstance();
			Object result = executeMethod(instance, value[1], command);

			return (String) result;
		} catch (ClassNotFoundException e) {
			log.error("ClassNotFoundException", e);
		} catch (InstantiationException e) {
			log.error("InstantiationException", e);
		} catch (IllegalAccessException e) {
			log.error("IllegalAccessException", e);
		}

		return "failed!";
	}

	private Object executeMethod(Object classInstance, String methodName, String args) {
		log.info("executeMethod: class=" + classInstance.getClass().getName() + ", method="
				+ methodName + ", paras value=" + args);
		try {
			Class<?> c = classInstance.getClass();
			if (c == null) {
				log.error("No this class: " + classInstance.getClass().getName());
				return null;
			}

			Method m = c.getDeclaredMethod(methodName, String.class);

			return m.invoke(classInstance, args);
		} catch (IllegalAccessException e) {
			log.error("executeMethod catch IllegalAccessException: ", e);
			return e;
		} catch (IllegalArgumentException e) {
			log.error("executeMethod catch IllegalArgumentException: ", e);
			return e;
		} catch (InvocationTargetException e) {
			log.error("executeMethod catch InvocationTargetException: ", e);
			return e;
		} catch (Exception e) {
			log.error("executeMethod catch Exception: ", e);
			return e;
		}
	}

}