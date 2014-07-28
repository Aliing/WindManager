package com.ah.apiengine.agent.subagent;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.element.VhmOperation;

public interface VhmAgent {

	void execute(VhmOperation operation) throws HmApiEngineException;

}