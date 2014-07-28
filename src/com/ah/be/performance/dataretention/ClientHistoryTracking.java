package com.ah.be.performance.dataretention;


import static com.ah.be.performance.dataretention.ClientHistory.CLIENT_REQUEST_MONITOR;


public class ClientHistoryTracking {
	
  /** Updates tables:
    <UL><LI>
      "ClientDeviceInfo"
      </LI><OL><LI>
        If "MAC"+"owner" exists, update "HostName", "IP4", "IP6", "OS_type", "UserName", "LastModification", unless null/empty
      </LI><LI>
        Otherwise, insert all those fields *plus* "vendor" and "seen1st"
    </LI></OL><LI>
      "UserNameInfo"
      </LI><OL><LI>
        If "UserName"+"owner" exists, update "UserProfile", "eMail", "LastModification"
      </LI><LI>
        Otherwise, insert all those fields plus "seen1st"
    </LI></OL><LI>
      "UserNameSeen"
      </LI><OL><LI>
        If "UserName" + HiveAP[networkDeviceMAC].owner + "SSId" exists, update "authentication", "NetworkDeviceMAC", "UserNameInfo"."LastModification"
      </LI><LI>
        Otherwise, insert all those fields
    </LI></OL></UL>
  */
	
	
	public static void associated(final long timeStampWithAerohiveDeviceTimeZone,
			final String clientMAC,final String hostName,final String OS_type, final String option55, final String networkDeviceMAC,final long owner,final String userName,final String userProfile,final String eMail,final String SSId, final byte authentication,
			final byte[] ip4, final byte[] ip6) {
			
		ClientHistory clientHistory = new ClientHistory();
		clientHistory.setTimeStampWithAerohiveDeviceTimeZone(timeStampWithAerohiveDeviceTimeZone);
		clientHistory.setClientMAC(clientMAC);
		clientHistory.setHostName(hostName);
		clientHistory.setOS_type(OS_type);
		clientHistory.setOption55(option55);
		clientHistory.setNetworkDeviceMAC(networkDeviceMAC);
		clientHistory.setOwner(owner);
		clientHistory.setUserName(userName);
		clientHistory.setUserProfile(userProfile);
		clientHistory.seteMail(eMail);
		clientHistory.setSSId(SSId);
		clientHistory.setAuthentication(authentication);
		clientHistory.setIp4(ip4);
		clientHistory.setIp6(ip6);
		clientHistory.setType(ClientHistory.CLIENT_REQUEST_ASSOCIATE);
//		DataRetentionProcessor.addEvent(clientHistory);
			
		}

  /** Updates tables:
    <UL><LI>
      "ClientDeviceSeen"
      </LI><OL><LI>
        If "MAC" + HiveAP[networkDeviceMAC].owner + "SSId" exists, update "LastSeen", "ClientDeviceInfo"."LastModification"
      </LI><LI>
        Otherwise, insert all those fields
    </LI></OL><LI>
      "UserNameDevices"
      </LI><OL><LI>
        If "UserName" + HiveAP[networkDeviceMAC].owner + "ClientDeviceMAC" exists, update "LastSeen", "UserNameInfo"."LastModification"
      </LI><LI>
        Otherwise, insert all those fields
    </LI></OL><LI>
      "UserNameSeen"
      </LI><OL><LI>
        If "UserName" + HiveAP[networkDeviceMAC].owner + "SSId" exists, update "LastSeen", "UserNameInfo"."LastModification"
      </LI><LI>
        Otherwise, insert all those fields
    </LI></OL></UL>
  */
	public static void deassociated(final long timeStampWithAerohiveDeviceTimeZone,final String clientMAC,
			final String SSId,final String networkDeviceMAC,final long owner,final String userName) {
		
		ClientHistory clientHistory = new ClientHistory();
		clientHistory.setTimeStampWithAerohiveDeviceTimeZone(timeStampWithAerohiveDeviceTimeZone);
		clientHistory.setClientMAC(clientMAC);
		clientHistory.setNetworkDeviceMAC(networkDeviceMAC);
		clientHistory.setOwner(owner);
		clientHistory.setUserName(userName);
		clientHistory.setSSId(SSId);
		clientHistory.setType(ClientHistory.CLIENT_REQUEST_DEASSOCIATE);
//		DataRetentionProcessor.addEvent(clientHistory);
		
	}
	
  /** Updates  "HostName", "IP4", "IP6", "UserName", "LastModification", unless null/empty, of "ClientDeviceInfo" where "MAC"+"owner"
  */
	public static void clientInfo(final String MAC, final Long owner, final String ip4, final String hostName, final String userName, final String SSID, final byte[] ip6){
		
		ClientHistory clientHistory = new ClientHistory();
		clientHistory.setClientMAC(MAC);
		clientHistory.setOwner(owner);
		clientHistory.setIp(ip4);
		clientHistory.setHostName(hostName);
		clientHistory.setUserName(userName);
		clientHistory.setSSId(SSID);
		clientHistory.setIp6(ip6);
		clientHistory.setType(ClientHistory.CLIENT_REQUEST_CLIENTINFO);
//		DataRetentionProcessor.addEvent(clientHistory);
		
	}
	
  /** Updates  "OS_type", "LastModification", unless null/empty, of "ClientDeviceInfo" where "MAC"+"owner"
  */
	public static void clientOsInfo(final String MAC, final long owner, final String OS){
		
		ClientHistory clientHistory = new ClientHistory();
		clientHistory.setClientMAC(MAC);
		clientHistory.setOwner(owner);
		clientHistory.setOS(OS);
		clientHistory.setType(ClientHistory.CLIENT_REQUEST_CLIENTOSINFO);
//		DataRetentionProcessor.addEvent(clientHistory);
		
	}

  /** Updates table "ClientDeviceInfo":
    <UL><LI>
      Update "located", "LastModification" where "MAC"+"owner"
    </LI><LI>
      For 0 clientMACor0ifAll, update "located", "LastModification" where "owner"
    </LI></UL>
  */
	public static void watched(final long clientMACor0ifAll,final long owner,final boolean located) {
		
		ClientHistory clientHistory = new ClientHistory();
		clientHistory.setClientMACor0ifAll(clientMACor0ifAll);
		clientHistory.setOwner(owner);
		clientHistory.setLocated(located);
		clientHistory.setType(ClientHistory.CLIENT_REQUEST_WATCH);
//		DataRetentionProcessor.addEvent(clientHistory);
		
	}

  /** Updates table "ClientDeviceInfo":
    <UL><LI>
      Update "monitored", "LastModification" where "MAC"+"owner"
    </LI><LI>
      For 0 clientMACor0ifAll, update "monitored", "LastModification" where "owner"
    </LI></UL>
  */
	public static void monitored(final long clientMACor0ifAll,final long owner,final boolean monitored) {
		
		ClientHistory clientHistory = new ClientHistory();
		clientHistory.setClientMACor0ifAll(clientMACor0ifAll);
		clientHistory.setOwner(owner);
		clientHistory.setMonitored(monitored);
		clientHistory.setType(CLIENT_REQUEST_MONITOR);
//		DataRetentionProcessor.addEvent(clientHistory);
		
	}
}
