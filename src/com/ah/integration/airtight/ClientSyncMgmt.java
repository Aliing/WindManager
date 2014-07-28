package com.ah.integration.airtight;

import java.util.Collection;

import com.ah.bo.admin.HmDomain;

import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;

public interface ClientSyncMgmt {

	void syncClients(APISession session, Collection<HmDomain> hmDomains, ReportingEntity reporter) throws APIException;

	Collection<Client> fetchClients(APISession session, ReportingEntity reporter) throws APIException;

}