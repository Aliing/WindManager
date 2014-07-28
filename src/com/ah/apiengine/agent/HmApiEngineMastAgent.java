package com.ah.apiengine.agent;

import java.util.Collection;

import com.ah.apiengine.agent.subagent.CommandLineAgent;
import com.ah.apiengine.agent.subagent.CommonAgent;
import com.ah.apiengine.agent.subagent.HhmAgent;
import com.ah.apiengine.agent.subagent.UserAgent;
import com.ah.apiengine.agent.subagent.VhmAgent;
import com.ah.apiengine.element.Login;
import com.ah.bo.hhm.HMUpdateSoftwareInfo;

public interface HmApiEngineMastAgent {

	HhmAgent getHhmAgent();

	UserAgent getUserAgent();

	VhmAgent getVhmAgent();
	
	CommandLineAgent getCommandAgent();

	CommonAgent getCommonAgent();
	
	boolean login(Login login) throws HmApiEngineException;

	Collection<HMUpdateSoftwareInfo> queryDnsUpdateInfos() throws HmApiEngineException;

}