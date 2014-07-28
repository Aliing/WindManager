package com.ah.be.rest.ahmdm.server.service;

import com.ah.be.rest.ahmdm.server.models.MdmStatusUpdateRequest;
import com.ah.be.rest.ahmdm.server.models.MdmStatusUpdateResponse;

public interface MdmStatusUpdateService {
	 MdmStatusUpdateResponse updateStatus(MdmStatusUpdateRequest req);
}
