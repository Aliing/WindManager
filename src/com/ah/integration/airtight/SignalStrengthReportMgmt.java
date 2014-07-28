package com.ah.integration.airtight;

import java.util.Collection;

import com.airtight.spectraguard.api.dataobjects.devices.AP;
import com.airtight.spectraguard.api.dataobjects.devices.Client;
import com.airtight.spectraguard.api.dataobjects.devices.WiFiInterface;
import com.airtight.spectraguard.api.dataobjects.session.APISession;
import com.airtight.spectraguard.api.dataobjects.session.ReportingEntity;
import com.airtight.spectraguard.api.exceptions.APIException;

public interface SignalStrengthReportMgmt {

	void syncSignalStrengthMonitors(APISession session, Collection<AP> authorizedAps) throws APIException;

	void reportClientSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<Client> transmitterClients, ReportingEntity reporter) throws APIException;

	void reportClientSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<Client> transmitterClients, Collection<WiFiInterface> rssiMonitors, ReportingEntity reporter) throws APIException;

	void reportDetectedDeviceSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<WiFiInterface> transmitters, ReportingEntity reporter) throws APIException;

	void reportDetectedDeviceSignalStrengths(APISession session, Collection<AP> monitorHiveAps, Collection<WiFiInterface> transmitters, Collection<WiFiInterface> rssiMonitors, ReportingEntity reporter) throws APIException;

	Collection<WiFiInterface> getSignalStrengthMonitors(APISession session) throws APIException;

}