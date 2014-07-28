package com.ah.apiengine.agent.subagent;

import java.util.Collection;
import java.util.List;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.element.MvInfo;
import com.ah.apiengine.element.MvResponseInfo;

public interface CommonAgent {
	void doMoveVhm(MvInfo request) throws HmApiEngineException;

	Collection<MvResponseInfo> doQueryVhmMovingStatus() throws HmApiEngineException;

	void doClearVhmMovingStatus() throws HmApiEngineException;

	List<String> doQueryVhmLoginInfos() throws HmApiEngineException;
}
