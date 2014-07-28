package com.ah.apiengine.agent.subagent.impl;

import java.util.Collection;
import java.util.List;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.apiengine.agent.subagent.CommonAgent;
import com.ah.apiengine.element.MvInfo;
import com.ah.apiengine.element.MvResponseInfo;
import com.ah.be.admin.hhmoperate.HHMmove;
import com.ah.be.admin.hhmoperate.VhmLoginInfoCache;

//import com.ah.util.Tracer;

public class CommonAgentImpl implements CommonAgent {

	// private static Tracer log = new Tracer(CommonAgentImpl.class);

	@Override
	public void doMoveVhm(MvInfo request) throws HmApiEngineException {
		HHMmove.initList(request);
	}

	@Override
	public Collection<MvResponseInfo> doQueryVhmMovingStatus() throws HmApiEngineException {
		return HHMmove.lStatus;
	}

	@Override
	public void doClearVhmMovingStatus() throws HmApiEngineException {
		HHMmove.cleanStatusInfo();
	}

	@Override
	public List<String> doQueryVhmLoginInfos() throws HmApiEngineException {
		return VhmLoginInfoCache.getInstance().getInfo();
	}

}
