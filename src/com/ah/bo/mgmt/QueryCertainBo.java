package com.ah.bo.mgmt;

import java.util.Collection;

import com.ah.bo.HmBo;

public abstract class QueryCertainBo<T extends HmBo> implements QueryBo {
	@SuppressWarnings("unchecked")
	public Collection<HmBo> load(HmBo bo) {
		return loadBo((T)bo);
	}
	public abstract Collection<HmBo> loadBo(T bo);
}
