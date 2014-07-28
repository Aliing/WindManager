package com.ah.bo.mgmt;

import java.util.Collection;

import com.ah.bo.HmBo;

public interface QueryBo {
	public Collection<HmBo> load(HmBo bo);
}
