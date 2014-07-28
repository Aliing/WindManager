package com.ah.integration.airtight;

import java.util.Collection;

import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;

public interface AssociationSyncMgmt {

	void syncInfrastructureAssociations(APISession session, Collection<AP> authorizedAps, Collection<Client> authorizedClients, ReportingEntity reporter) throws APIException;

	void syncAdHocAssociations(APISession session, Collection<Client> clients, ReportingEntity reporter) throws APIException;

}