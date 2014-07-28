package com.ah.apiengine.agent.subagent;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.element.HhmInfo;
import com.ah.apiengine.element.HhmList;
import com.ah.apiengine.element.StringList;

public interface HhmAgent {

	void updateHhmList(HhmList hhmList) throws HmApiEngineException;

	HhmInfo queryHhmInfo() throws HmApiEngineException;

	void updateDenyEmailInfo(StringList maillist) throws HmApiEngineException;
}