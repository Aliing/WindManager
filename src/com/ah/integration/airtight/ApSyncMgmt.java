package com.ah.integration.airtight;

import java.util.Collection;

import com.ah.bo.admin.HmDomain;

import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;

public interface ApSyncMgmt {

	void syncAuthorizedHiveAps(APISession session, Collection<HmDomain> hmDomains, ReportingEntity reporter) throws APIException;

	void syncUncategorizedAps(APISession session, Collection<HmDomain> hmDomains, ReportingEntity reporter) throws APIException;

	Collection<AP> fetchAps(APISession session, ReportingEntity reporter) throws APIException;

}