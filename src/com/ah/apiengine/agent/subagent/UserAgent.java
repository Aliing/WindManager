package com.ah.apiengine.agent.subagent;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.element.UserOperation;

public interface UserAgent {

	void execute(UserOperation operation) throws HmApiEngineException;

}