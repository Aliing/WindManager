
    alter table ACCESS_CONSOLE 
        drop constraint FK20CF1D5C9587E4C2;

    alter table ACCESS_CONSOLE_MAC_FILTER 
        drop constraint FK28B4374B2B6384A;

    alter table ACCESS_CONSOLE_MAC_FILTER 
        drop constraint FK28B4374B76773CB2;

    alter table ACTIVECLIENT_FILTER 
        drop constraint FKE715B1269587E4C2;

    alter table ACTIVE_DIRECTORY_OR_LDAP 
        drop constraint FKDE7417984DED2FCF;

    alter table ACTIVE_DIRECTORY_OR_LDAP 
        drop constraint FKDE7417988F6F6CB;

    alter table ACTIVE_DIRECTORY_OR_LDAP 
        drop constraint FKDE7417989587E4C2;

    alter table AH_ADMINLOGIN_SESSION 
        drop constraint FKC85F13C99587E4C2;

    alter table AH_ALARM 
        drop constraint FKE6C0F4199587E4C2;

    alter table AH_ALARMS_FILTER 
        drop constraint FKA64913FD9587E4C2;

    alter table AH_CLIENTEDITVALUES 
        drop constraint FKD6917ACF9587E4C2;

    alter table AH_CLIENTSESSION 
        drop constraint FK42EFE6339587E4C2;

    alter table AH_CLIENTSESSION_HISTORY 
        drop constraint FKD7E71A689587E4C2;

    alter table AH_DEVICE_PSE_POWER 
        drop constraint FK2F938F779587E4C2;

    alter table AH_EVENT 
        drop constraint FKE6FDEC629587E4C2;

    alter table AH_EVENTS_FILTER 
        drop constraint FK3D91B3469587E4C2;

    alter table AH_PORT_AVAILABILITY 
        drop constraint FKD41B46419587E4C2;

    alter table AH_PSE_STATUS 
        drop constraint FK96BEAF479587E4C2;

    alter table AH_STATS_AVAILABILITY_HIGH 
        drop constraint FK16EBAE2E9587E4C2;

    alter table AH_STATS_AVAILABILITY_LOW 
        drop constraint FK5B940D289587E4C2;

    alter table AH_STATS_LATENCY_HIGH 
        drop constraint FKAEA09B3B9587E4C2;

    alter table AH_STATS_LATENCY_LOW 
        drop constraint FK5836CA7B9587E4C2;

    alter table AH_STATS_THROUGHPUT_HIGH 
        drop constraint FKB839DCBF9587E4C2;

    alter table AH_STATS_THROUGHPUT_LOW 
        drop constraint FKF56D48779587E4C2;

    alter table AH_STATS_VPNSTATUS_HIGH 
        drop constraint FKD85D4D539587E4C2;

    alter table AH_STATS_VPNSTATUS_LOW 
        drop constraint FK598F75639587E4C2;

    alter table AH_USERLOGIN_SESSION 
        drop constraint FK93A2077D9587E4C2;

    alter table AIR_SCREEN_ACTION 
        drop constraint FKF995A7149587E4C2;

    alter table AIR_SCREEN_BEHAVIOR 
        drop constraint FKA78E17B09587E4C2;

    alter table AIR_SCREEN_GROUP_RULE 
        drop constraint FK396F02BAC5845E0E;

    alter table AIR_SCREEN_GROUP_RULE 
        drop constraint FK396F02BA3E57C36F;

    alter table AIR_SCREEN_RULE 
        drop constraint FKF95D635A3A617625;

    alter table AIR_SCREEN_RULE 
        drop constraint FKF95D635A9587E4C2;

    alter table AIR_SCREEN_RULE_ACTION 
        drop constraint FKE1B972BBBEDF4FC2;

    alter table AIR_SCREEN_RULE_ACTION 
        drop constraint FKE1B972BBF44395E9;

    alter table AIR_SCREEN_RULE_BEHAVIOR 
        drop constraint FK15ED9597F44395E9;

    alter table AIR_SCREEN_RULE_BEHAVIOR 
        drop constraint FK15ED959725DC8A3A;

    alter table AIR_SCREEN_RULE_GROUP 
        drop constraint FK7A3593A9587E4C2;

    alter table AIR_SCREEN_SOURCE 
        drop constraint FK18F690F98A77400F;

    alter table AIR_SCREEN_SOURCE 
        drop constraint FK18F690F99587E4C2;

    alter table AIR_TIGHT_SETTINGS 
        drop constraint FK570159F99587E4C2;

    alter table ALG_CONFIGURATION 
        drop constraint FK5C8913539587E4C2;

    alter table ALG_CONFIG_INFO 
        drop constraint FK62EDD9481BC08128;

    alter table APPLICATION 
        drop constraint FKDCF799309587E4C2;

    alter table APPLICATION_PROFILE 
        drop constraint FK4E0D257A9587E4C2;

    alter table APPPROFILE_APP 
        drop constraint FKD4C43DAADF9D6DC2;

    alter table APPPROFILE_APP 
        drop constraint FKD4C43DAA80DC2311;

    alter table AP_CONNECT_HISTORY_INFO 
        drop constraint FK235620FE9587E4C2;

    alter table ATTRIBUTE_ITEM 
        drop constraint FKADA9EF6D675AD87;

    alter table ATTRIBUTE_ITEM 
        drop constraint FKADA9EF694F7914;

    alter table A_RATE_SETTING_INFO 
        drop constraint FKD0E925DE6B81105E;

    alter table BONJOUR_ACTIVE_SERVICE 
        drop constraint FK98A77658472EACEB;

    alter table BONJOUR_ACTIVE_SERVICE 
        drop constraint FK98A7765865833EFA;

    alter table BONJOUR_FILTER_RULE 
        drop constraint FKBE2664E72F8C5108;

    alter table BONJOUR_FILTER_RULE 
        drop constraint FKBE2664E751C11BF7;

    alter table BONJOUR_FILTER_RULE 
        drop constraint FKBE2664E7FCAC7FF1;

    alter table BONJOUR_FILTER_RULE 
        drop constraint FKBE2664E765833EFA;

    alter table BONJOUR_GATEWAY_MONITORING 
        drop constraint FKBC09273F9587E4C2;

    alter table BONJOUR_GATEWAY_SETTINGS 
        drop constraint FKD1BA94DA9587E4C2;

    alter table BONJOUR_REALM 
        drop constraint FKAFE446F39587E4C2;

    alter table BONJOUR_SERVICE 
        drop constraint FK7DC8A6198E53367B;

    alter table BONJOUR_SERVICE 
        drop constraint FK7DC8A6199587E4C2;

    alter table BONJOUR_SERVICE_CATEGORY 
        drop constraint FK34CC04649587E4C2;

    alter table BONJOUR_SERVICE_DETAIL 
        drop constraint FK32BB05375AAA3E8B;

    alter table CAPWAPSETTINGS 
        drop constraint FK8DBDEDF79587E4C2;

    alter table CLOUD_AUTH_CUSTOMER 
        drop constraint FKAD7697EB9587E4C2;

    alter table COMPLIANCE_POLICY 
        drop constraint FK8DD135D69587E4C2;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D78FCC8276;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D71B39B1C4;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7CEF72BD0;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D72A8805D2;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D740BA6225;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7A12CF1F3;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D78F3AAB44;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7FD99D266;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7919407E;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D721BD815E;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7D78BE81D;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7C6763859;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7F3C29698;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D726D66D8E;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7AEBEB8DD;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D72134929D;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7CED7CD2E;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7BDDBE8E;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D79587E4C2;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D75D76E437;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7A8F782CB;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D775273323;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7106F4712;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D735DCA0FD;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D72B6384A;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7EA4F4A0E;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D71BC08128;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D725CE4881;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D74245837D;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7C0AD3EE0;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D789A44C6F;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D74E9345A9;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7F2653E02;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7FB312ECE;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7F77DA6A4;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D7575330CE;

    alter table CONFIG_TEMPLATE 
        drop constraint FK193880D71DB123B5;

    alter table CONFIG_TEMPLATE_IP_TRACK 
        drop constraint FK7B755E7B74CE17C7;

    alter table CONFIG_TEMPLATE_IP_TRACK 
        drop constraint FK7B755E7B64132EB3;

    alter table CONFIG_TEMPLATE_LAN 
        drop constraint FKD4FA921174CE17C7;

    alter table CONFIG_TEMPLATE_LAN 
        drop constraint FKD4FA9211514BBC3;

    alter table CONFIG_TEMPLATE_SSID 
        drop constraint FKCA5B21E36B81105E;

    alter table CONFIG_TEMPLATE_SSID 
        drop constraint FKCA5B21E374CE17C7;

    alter table CONFIG_TEMPLATE_STORM_CONTROL 
        drop constraint FK2A734E5F74CE17C7;

    alter table CONFIG_TEMPLATE_STORM_CONTROL 
        drop constraint FK2A734E5F9587E4C2;

    alter table CONFIG_TEMPLATE_TV_SERVICE 
        drop constraint FK223D1A401791D6DF;

    alter table CONFIG_TEMPLATE_TV_SERVICE 
        drop constraint FK223D1A4074CE17C7;

    alter table CWP 
        drop constraint FK1065C36487063;

    alter table CWP 
        drop constraint FK1065C25CE4881;

    alter table CWP 
        drop constraint FK1065C9587E4C2;

    alter table CWP_CERTIFICATE 
        drop constraint FK9F5D90149587E4C2;

    alter table CWP_PAGE_FIELD 
        drop constraint FK87E2FCED8403D0AB;

    alter table DASHBOARD_COMPONENT_DATA 
        drop constraint FKF2BCB2F71D80B1E2;

    alter table DASHBOARD_COMPONENT_METRIC 
        drop constraint FK45F015BD9587E4C2;

    alter table DEVICE_AUTO_PROVISION_INTERFACE 
        drop constraint FK5F93E548E98072AB;

    alter table DEVICE_AUTO_PROVISION_IPSUBNETWORKS 
        drop constraint FKA8A63F4DE98072AB;

    alter table DEVICE_DA_INFO 
        drop constraint FK46C8D6479587E4C2;

    alter table DEVICE_INTERFACE_IPSUBNETWORK 
        drop constraint FK156C4DC49587E4C2;

    alter table DEVICE_POLICY_RULE 
        drop constraint FK9F8E3D8045792A2B;

    alter table DEVICE_POLICY_RULE 
        drop constraint FK9F8E3D8085894088;

    alter table DEVICE_POLICY_RULE 
        drop constraint FK9F8E3D80882386CB;

    alter table DEVICE_POLICY_RULE 
        drop constraint FK9F8E3D80FD9DB108;

    alter table DHCP_SERVER_CUSTOM 
        drop constraint FK5408501F3CE53463;

    alter table DHCP_SERVER_IPPOOL 
        drop constraint FK5DFD7E713CE53463;

    alter table DIRECTORY_OPENLDAP_INFO 
        drop constraint FK3A8A7CCAC71C89FF;

    alter table DIRECTORY_OPENLDAP_INFO 
        drop constraint FK3A8A7CCAB0DFE94C;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9BEBC5C2A;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9F4F8DC03;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9F8AE2CF5;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9F8AEA154;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A99587E4C2;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9F8ADB896;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9F4F95062;

    alter table DNS_SERVICE_PROFILE 
        drop constraint FK6044D6A9F4F867A4;

    alter table DNS_SPECIFIC_SETTINGS 
        drop constraint FK92165EDA8C5CFC31;

    alter table DNS_SPECIFIC_SETTINGS 
        drop constraint FK92165EDABD128E3D;

    alter table DOMAIN_NAME_ITEM 
        drop constraint FK4C68368CBEBC5C2A;

    alter table DOMAIN_OBJECT 
        drop constraint FKA432271A9587E4C2;

    alter table DOS_PREVENTION 
        drop constraint FK6CE842A79587E4C2;

    alter table DOS_PREVENTION_DOS_PARAMS 
        drop constraint FK84CB7915A828BAA4;

    alter table ETHERNET_ACCESS 
        drop constraint FKBCC636CC45792A2B;

    alter table ETHERNET_ACCESS 
        drop constraint FKBCC636CC9587E4C2;

    alter table ETHERNET_ACCESS_MAC 
        drop constraint FKEE22493C517029AE;

    alter table ETHERNET_ACCESS_MAC 
        drop constraint FKEE22493C6BA4E2FB;

    alter table FIREWALL_POLICY 
        drop constraint FK1D1089519587E4C2;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92AAE8289E;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92A4BB0E195;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92A6824EA71;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92AF2653E02;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92ABCFACB24;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92A9B4985E1;

    alter table FIREWALL_POLICY_RULE 
        drop constraint FKE476E92A9B60BB48;

    alter table G_RATE_SETTING_INFO 
        drop constraint FKA2E770646B81105E;

    alter table HIVEAP_DEVICE_INTERFACE 
        drop constraint FK102B8710CBE059E2;

    alter table HIVEAP_FILTER 
        drop constraint FK5BEB78189587E4C2;

    alter table HIVEAP_IMAGE_INFO 
        drop constraint FK132B59D29587E4C2;

    alter table HIVE_AP 
        drop constraint FK622F1F5EC2E1F89D;

    alter table HIVE_AP 
        drop constraint FK622F1F5E89C5DBA3;

    alter table HIVE_AP 
        drop constraint FK622F1F5E230C7064;

    alter table HIVE_AP 
        drop constraint FK622F1F5E66BBE26B;

    alter table HIVE_AP 
        drop constraint FK622F1F5EB505A93B;

    alter table HIVE_AP 
        drop constraint FK622F1F5E86287687;

    alter table HIVE_AP 
        drop constraint FK622F1F5E2A8805D2;

    alter table HIVE_AP 
        drop constraint FK622F1F5E421DEC8E;

    alter table HIVE_AP 
        drop constraint FK622F1F5EB988BF74;

    alter table HIVE_AP 
        drop constraint FK622F1F5E73F960B8;

    alter table HIVE_AP 
        drop constraint FK622F1F5E680648DC;

    alter table HIVE_AP 
        drop constraint FK622F1F5EC868473;

    alter table HIVE_AP 
        drop constraint FK622F1F5EE6C87EA9;

    alter table HIVE_AP 
        drop constraint FK622F1F5E2DBA7BAB;

    alter table HIVE_AP 
        drop constraint FK622F1F5EDA6FB9A4;

    alter table HIVE_AP 
        drop constraint FK622F1F5EA017973E;

    alter table HIVE_AP 
        drop constraint FK622F1F5E9587E4C2;

    alter table HIVE_AP 
        drop constraint FK622F1F5EBD52F695;

    alter table HIVE_AP 
        drop constraint FK622F1F5E35DCA0FD;

    alter table HIVE_AP 
        drop constraint FK622F1F5E47070944;

    alter table HIVE_AP 
        drop constraint FK622F1F5E979FCA39;

    alter table HIVE_AP 
        drop constraint FK622F1F5ED6944ACC;

    alter table HIVE_AP 
        drop constraint FK622F1F5EC0AD3EE0;

    alter table HIVE_AP 
        drop constraint FK622F1F5E1D08A5A4;

    alter table HIVE_AP 
        drop constraint FK622F1F5E9B262CEB;

    alter table HIVE_AP 
        drop constraint FK622F1F5E60E45778;

    alter table HIVE_AP_AUTO_PROVISION 
        drop constraint FK203651269587E4C2;

    alter table HIVE_AP_AUTO_PROVISION_MACES 
        drop constraint FKF91A61C410A63DB3;

    alter table HIVE_AP_DHCP_SERVER 
        drop constraint FK9934DFF0A5161A19;

    alter table HIVE_AP_DHCP_SERVER 
        drop constraint FK9934DFF056E301C3;

    alter table HIVE_AP_DYNAMIC_ROUTE 
        drop constraint FKE861EAE856E301C3;

    alter table HIVE_AP_INTERNAL_NETWORK 
        drop constraint FK46FF614D56E301C3;

    alter table HIVE_AP_IP_ROUTE 
        drop constraint FKDADE8D256E301C3;

    alter table HIVE_AP_L3CFG_NEIGHBOR 
        drop constraint FK6ACD727556E301C3;

    alter table HIVE_AP_LEARNING_MAC 
        drop constraint FK640EAFCF56E301C3;

    alter table HIVE_AP_LEARNING_MAC 
        drop constraint FK640EAFCFA6986AC4;

    alter table HIVE_AP_MULTIPLE_VLAN 
        drop constraint FK5A6B5F1156E301C3;

    alter table HIVE_AP_SERIAL_NUMBER 
        drop constraint FK2ED034D39587E4C2;

    alter table HIVE_AP_SSID_ALLOCATION 
        drop constraint FK60F5254356E301C3;

    alter table HIVE_AP_STATIC_ROUTE 
        drop constraint FK54A4885956E301C3;

    alter table HIVE_AP_UPDATE_RESULT 
        drop constraint FKC98BFD329587E4C2;

    alter table HIVE_AP_UPDATE_RESULT_ITEM 
        drop constraint FK8A199E007CD9C5A9;

    alter table HIVE_AP_UPDATE_SETTINGS 
        drop constraint FK7FBF89587E4C2;

    alter table HIVE_AP_USB_MODEM 
        drop constraint FKEAE310CE56E301C3;

    alter table HIVE_AP_USER_PROFILE 
        drop constraint FKFE6D585645792A2B;

    alter table HIVE_AP_USER_PROFILE 
        drop constraint FKFE6D585656E301C3;

    alter table HIVE_AP_VIRTUAL_CONNECTION 
        drop constraint FK7E6A233356E301C3;

    alter table HIVE_PROFILE 
        drop constraint FK4B3523BA8B6D17F2;

    alter table HIVE_PROFILE 
        drop constraint FK4B3523BA938131AE;

    alter table HIVE_PROFILE 
        drop constraint FK4B3523BA9587E4C2;

    alter table HIVE_PROFILE_MAC_FILTER 
        drop constraint FK9FA92A2D2134929D;

    alter table HIVE_PROFILE_MAC_FILTER 
        drop constraint FK9FA92A2D76773CB2;

    alter table HM_ACCESS_CONTROL 
        drop constraint FK1E237DC9587E4C2;

    alter table HM_ACCESS_CONTROL_IP 
        drop constraint FK3443D86A7983EC;

    alter table HM_ACSPNEIGHBOR 
        drop constraint FK9678BEEB9587E4C2;

    alter table HM_ASSOCIATION 
        drop constraint FK99D278279587E4C2;

    alter table HM_AUDITLOG 
        drop constraint FKC8A223839587E4C2;

    alter table HM_AUTOREFRESH_SETTINGS 
        drop constraint FKB79A0FD078A7ACAB;

    alter table HM_BANDWIDTHSENTINEL_HISTORY 
        drop constraint FKFE63CF309587E4C2;

    alter table HM_CUSTOM_REPORT 
        drop constraint FKE300E408D675AD87;

    alter table HM_CUSTOM_REPORT 
        drop constraint FKE300E4089587E4C2;

    alter table HM_CUSTOM_REPORT_FIELD_TABLE 
        drop constraint FK44973A52728F1EE6;

    alter table HM_CUSTOM_REPORT_FIELD_TABLE 
        drop constraint FK44973A52791619A9;

    alter table HM_DASHBOARD 
        drop constraint FK1A43BBAD675AD87;

    alter table HM_DASHBOARD 
        drop constraint FK1A43BBA9587E4C2;

    alter table HM_DASHBOARD_APPAP 
        drop constraint FK5B68F52B9587E4C2;

    alter table HM_DASHBOARD_COMPONENT 
        drop constraint FK21E88298EB5F744B;

    alter table HM_DASHBOARD_COMPONENT 
        drop constraint FK21E88298C6DEF2A4;

    alter table HM_DASHBOARD_COMPONENT 
        drop constraint FK21E882989587E4C2;

    alter table HM_DASHBOARD_LAYOUT 
        drop constraint FK23ABEB4F1C2D6E72;

    alter table HM_DASHBOARD_LAYOUT 
        drop constraint FK23ABEB4F9587E4C2;

    alter table HM_DASHBOARD_WIDGET 
        drop constraint FK36D84A69D675AD87;

    alter table HM_DASHBOARD_WIDGET 
        drop constraint FK36D84A69BD1D7147;

    alter table HM_DASHBOARD_WIDGET 
        drop constraint FK36D84A694AC8BB24;

    alter table HM_DASHBOARD_WIDGET 
        drop constraint FK36D84A699587E4C2;

    alter table HM_FEATURE_PERMISSION 
        drop constraint FK7F0D4C5272289F5E;

    alter table HM_INSTANCE_PERMISSION 
        drop constraint FKAA09257F72289F5E;

    alter table HM_INTERFERENCESTATS 
        drop constraint FKDD0AB8839587E4C2;

    alter table HM_L3FIREWALLLOG 
        drop constraint FKEB2FA8A39587E4C2;

    alter table HM_LATESTACSPNEIGHBOR 
        drop constraint FKF7B20C729587E4C2;

    alter table HM_LATESTINTERFERENCESTATS 
        drop constraint FKA2F7C75C9587E4C2;

    alter table HM_LATESTNEIGHBOR 
        drop constraint FK69A751F39587E4C2;

    alter table HM_LATESTRADIOATTRIBUTE 
        drop constraint FK4B6E67429587E4C2;

    alter table HM_LATESTXIF 
        drop constraint FKD4818FD49587E4C2;

    alter table HM_LLDP_INFORMATION 
        drop constraint FKE3B94B539587E4C2;

    alter table HM_LOGIN_AUTHENTICATION 
        drop constraint FK6BFFF9288ED6DA8B;

    alter table HM_LOGIN_AUTHENTICATION 
        drop constraint FK6BFFF9289587E4C2;

    alter table HM_NEIGHBOR 
        drop constraint FKC91428EC9587E4C2;

    alter table HM_NEW_REPORT 
        drop constraint FK89CED04DD675AD87;

    alter table HM_NEW_REPORT 
        drop constraint FK89CED04D9587E4C2;

    alter table HM_NTP_SERVER_INTERVAL 
        drop constraint FK5E7171129587E4C2;

    alter table HM_PCIDATA 
        drop constraint FKD838AC269587E4C2;

    alter table HM_RADIOATTRIBUTE 
        drop constraint FK53525F7B9587E4C2;

    alter table HM_RADIOSTATS 
        drop constraint FKC40BC8FE9587E4C2;

    alter table HM_REPORT 
        drop constraint FK2348FDAED675AD87;

    alter table HM_REPORT 
        drop constraint FK2348FDAE9587E4C2;

    alter table HM_SPECTRAL_ANALYSIS 
        drop constraint FKF27DBA9D9587E4C2;

    alter table HM_START_CONFIG 
        drop constraint FKC464AAD99587E4C2;

    alter table HM_SUMMARY_PAGE 
        drop constraint FKA86A14A29587E4C2;

    alter table HM_SYSTEMLOG 
        drop constraint FK83669FDB9587E4C2;

    alter table HM_TABLE_COLUMN 
        drop constraint FK2399920178A7ACAB;

    alter table HM_TABLE_SIZE 
        drop constraint FK6B27516C78A7ACAB;

    alter table HM_UPGRADE_LOG 
        drop constraint FK312B26279587E4C2;

    alter table HM_USER 
        drop constraint FK69886EC5B8291324;

    alter table HM_USER 
        drop constraint FK69886EC59587E4C2;

    alter table HM_USER_GROUP 
        drop constraint FK6C54E8659587E4C2;

    alter table HM_USER_REPORT 
        drop constraint FK30569FEE9587E4C2;

    alter table HM_VIFSTATS 
        drop constraint FKD7467E669587E4C2;

    alter table HM_VPNSTATUS 
        drop constraint FK8FE3630C9587E4C2;

    alter table HM_XIF 
        drop constraint FK7F46813B9587E4C2;

    alter table IDP 
        drop constraint FK11A959587E4C2;

    alter table IDP_AP 
        drop constraint FK8075CE1918D38E32;

    alter table IDP_ENCLOSED_FRIENDLY_AP 
        drop constraint FK6DC531C3BD099BC4;

    alter table IDP_ENCLOSED_ROGUE_AP 
        drop constraint FK9B31E354BD099BC4;

    alter table IDP_SETTINGS 
        drop constraint FK9EE8DA8D9587E4C2;

    alter table IDS_POLICY 
        drop constraint FKE790E0B99587E4C2;

    alter table IDS_POLICY_MAC_OR_OUI 
        drop constraint FK7BB6AB1D6BA4E2FB;

    alter table IDS_POLICY_MAC_OR_OUI 
        drop constraint FK7BB6AB1D8F3AAB44;

    alter table IDS_POLICY_SSID_PROFILE 
        drop constraint FK23884A8B6B81105E;

    alter table IDS_POLICY_SSID_PROFILE 
        drop constraint FK23884A8B8F3AAB44;

    alter table IDS_POLICY_VLAN 
        drop constraint FKD10EA12925CE4881;

    alter table IDS_POLICY_VLAN 
        drop constraint FKD10EA1298F3AAB44;

    alter table INTER_ROAMING 
        drop constraint FK95147F769587E4C2;

    alter table IP_ADDRESS 
        drop constraint FK7146C0BC9587E4C2;

    alter table IP_ADDRESS_ITEM 
        drop constraint FK531D80B6D675AD87;

    alter table IP_ADDRESS_ITEM 
        drop constraint FK531D80B6AAF112E4;

    alter table IP_FILTER 
        drop constraint FKDAED69909587E4C2;

    alter table IP_FILTER_IP_ADDRESS 
        drop constraint FKE1D912CBCEF72BD0;

    alter table IP_FILTER_IP_ADDRESS 
        drop constraint FKE1D912CBAAF112E4;

    alter table IP_POLICY 
        drop constraint FKEC52434A9587E4C2;

    alter table IP_POLICY_RULE 
        drop constraint FKCF4782D14BB0E195;

    alter table IP_POLICY_RULE 
        drop constraint FKCF4782D17711990;

    alter table IP_POLICY_RULE 
        drop constraint FKCF4782D1BCFACB24;

    alter table IP_POLICY_RULE 
        drop constraint FKCF4782D19B60BB48;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C3C6763859;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C353819019;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C3E7EB6CF5;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C32BE64EFE;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C38ED6DA8B;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C3CB985E5B;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C390F4D100;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C39587E4C2;

    alter table LAN_PROFILE 
        drop constraint FKC4E58C390A27B35;

    alter table LAN_PROFILE_REGULAR_NETWORKS 
        drop constraint FK50DFD564F994E743;

    alter table LAN_PROFILE_REGULAR_NETWORKS 
        drop constraint FK50DFD564D9CF1696;

    alter table LAN_PROFILE_REGULAR_VLAN 
        drop constraint FK1891BE42F994E743;

    alter table LAN_PROFILE_REGULAR_VLAN 
        drop constraint FK1891BE4225CE4881;

    alter table LAN_PROFILE_SCHEDULER 
        drop constraint FKCE394C1FBD52F695;

    alter table LAN_PROFILE_SCHEDULER 
        drop constraint FKCE394C1FF994E743;

    alter table LAN_PROFILE_USER_PROFILE 
        drop constraint FK7D134B1145792A2B;

    alter table LAN_PROFILE_USER_PROFILE 
        drop constraint FK7D134B11F994E743;

    alter table LAN_RADIUS_USER_GROUP 
        drop constraint FK43AC4972F994E743;

    alter table LAN_RADIUS_USER_GROUP 
        drop constraint FK43AC49723EB265DC;

    alter table LICENSE_SERVER_SETTING 
        drop constraint FK494169129587E4C2;

    alter table LLDPCDPPROFILE 
        drop constraint FK4F83C5A69587E4C2;

    alter table LOCAL_USER 
        drop constraint FK6C628C3FD8B831BC;

    alter table LOCAL_USER 
        drop constraint FK6C628C3F9587E4C2;

    alter table LOCAL_USER_GROUP 
        drop constraint FKAE1D825F827E8739;

    alter table LOCAL_USER_GROUP 
        drop constraint FKAE1D825F9587E4C2;

    alter table LOCATIONCLIENTWATCH 
        drop constraint FK682808EF9587E4C2;

    alter table LOCATIONCLIENT_ITEM 
        drop constraint FK689C683258E017BF;

    alter table LOCATIONCLIENT_ITEM 
        drop constraint FK689C6832D675AD87;

    alter table LOCATION_RSSI_REPORT 
        drop constraint FKCE6D04929587E4C2;

    alter table LOCATION_SERVER 
        drop constraint FKDFB56B0D8487C133;

    alter table LOCATION_SERVER 
        drop constraint FKDFB56B0D9587E4C2;

    alter table LOGSETTINGS 
        drop constraint FK39B26F479587E4C2;

    alter table MAC_AUTH 
        drop constraint FK24DDCAB89587E4C2;

    alter table MAC_FILTER 
        drop constraint FK6C71B0889587E4C2;

    alter table MAC_FILTER_MAC_OR_OUI 
        drop constraint FK83333EE6BA4E2FB;

    alter table MAC_FILTER_MAC_OR_OUI 
        drop constraint FK83333EE76773CB2;

    alter table MAC_OR_OUI 
        drop constraint FK7C54B3D79587E4C2;

    alter table MAC_OR_OUI_ITEM 
        drop constraint FKFFE2467BD675AD87;

    alter table MAC_OR_OUI_ITEM 
        drop constraint FKFFE2467B6BA4E2FB;

    alter table MAC_POLICY 
        drop constraint FK7DD68A429587E4C2;

    alter table MAC_POLICY_RULE 
        drop constraint FKAAB0D6D97016ABD4;

    alter table MAC_POLICY_RULE 
        drop constraint FKAAB0D6D9C9CB4F27;

    alter table MAC_POLICY_RULE 
        drop constraint FKAAB0D6D9AEF12A72;

    alter table MAIL_NOTIFICATION 
        drop constraint FK66C31E739587E4C2;

    alter table MAIL_NOTIFICATION_VHM 
        drop constraint FKFA85280F9587E4C2;

    alter table MAP_LINK 
        drop constraint FK3B119D1DD06933B5;

    alter table MAP_LINK 
        drop constraint FK3B119D1DC8BAE284;

    alter table MAP_LINK 
        drop constraint FK3B119D1D58137855;

    alter table MAP_NODE 
        drop constraint FK3B129B25D06933B5;

    alter table MAP_NODE 
        drop constraint FK3B129B2556E301C3;

    alter table MAP_NODE 
        drop constraint FK3B129B259587E4C2;

    alter table MAP_PERIMETER 
        drop constraint FKA7FF04BA75157280;

    alter table MAP_SETTINGS 
        drop constraint FKFF0157C69587E4C2;

    alter table MAP_WALL 
        drop constraint FK3B167EED75157280;

    alter table MGMT_SERVICE_DNS 
        drop constraint FKDFD091E19587E4C2;

    alter table MGMT_SERVICE_DNS_INFO 
        drop constraint FK7631884C4D4D31DC;

    alter table MGMT_SERVICE_DNS_INFO 
        drop constraint FK7631884CFD99D266;

    alter table MGMT_SERVICE_IP_TRACK 
        drop constraint FKFB46EA3B9587E4C2;

    alter table MGMT_SERVICE_OPTION 
        drop constraint FKA363249D8ED6DA8B;

    alter table MGMT_SERVICE_OPTION 
        drop constraint FKA363249D9587E4C2;

    alter table MGMT_SERVICE_SNMP 
        drop constraint FK1A487B669587E4C2;

    alter table MGMT_SERVICE_SNMP_INFO 
        drop constraint FKAA37C9E7CED7CD2E;

    alter table MGMT_SERVICE_SNMP_INFO 
        drop constraint FKAA37C9E74D4D31DC;

    alter table MGMT_SERVICE_SYSLOG 
        drop constraint FKAAB4F17F9587E4C2;

    alter table MGMT_SERVICE_SYSLOG_INFO 
        drop constraint FK3CE3B6E26D66D8E;

    alter table MGMT_SERVICE_SYSLOG_INFO 
        drop constraint FK3CE3B6E4D4D31DC;

    alter table MGMT_SERVICE_TIME 
        drop constraint FK1A48DCF59587E4C2;

    alter table MGMT_SERVICE_TIME_INFO 
        drop constraint FK245109B8FB312ECE;

    alter table MGMT_SERVICE_TIME_INFO 
        drop constraint FK245109B84D4D31DC;

    alter table MULTICAST_FORWARDING 
        drop constraint FK556521C446E355D4;

    alter table NEIGHBORS_NAME_ITEM 
        drop constraint FKA622F129F53AFA07;

    alter table NETWORK_SERVICE 
        drop constraint FKF1C3F1649587E4C2;

    alter table NOTIFICATION_MESSAGE_STATUS 
        drop constraint FKDE92FD1E9587E4C2;

    alter table N_RATE_SETTING_INFO 
        drop constraint FK42901CAB6B81105E;

    alter table ONETIME_PASSWORD 
        drop constraint FK6F759747E25061C6;

    alter table ONETIME_PASSWORD 
        drop constraint FK6F7597479587E4C2;

    alter table OS_OBJECT 
        drop constraint FK9B2361FA9587E4C2;

    alter table OS_OBJECT_VERSION 
        drop constraint FK4D571773B47FF9EA;

    alter table OS_OBJECT_VERSION_DHCP 
        drop constraint FK7A17513DB47FF9EA;

    alter table OS_VERSION 
        drop constraint FK412A355D9587E4C2;

    alter table PLANNED_AP 
        drop constraint FK629D6CAAD06933B5;

    alter table PLANNED_AP 
        drop constraint FK629D6CAA9587E4C2;

    alter table PLAN_TOOL 
        drop constraint FK252D28EE9587E4C2;

    alter table PPPOE 
        drop constraint FK48CEC269587E4C2;

    alter table PRINT_TEMPLATE 
        drop constraint FK97FCC1EC9587E4C2;

    alter table QOS_CLASSFIER_AND_MARKER 
        drop constraint FKBB7626639587E4C2;

    alter table QOS_CLASSIFICATION 
        drop constraint FK261A83709587E4C2;

    alter table QOS_CLASSIFICATION_MAC 
        drop constraint FKC577A3E06BA4E2FB;

    alter table QOS_CLASSIFICATION_MAC 
        drop constraint FKC577A3E04E9345A9;

    alter table QOS_CLASSIFICATION_SERVICE 
        drop constraint FK7C770CC64E9345A9;

    alter table QOS_CLASSIFICATION_SERVICE 
        drop constraint FK7C770CC6BCFACB24;

    alter table QOS_CLASSIFICATION_SSID 
        drop constraint FKE97FD6EA4E9345A9;

    alter table QOS_CLASSIFICATION_SSID 
        drop constraint FKE97FD6EA50A78608;

    alter table QOS_MARKING 
        drop constraint FK7A97A98B9587E4C2;

    alter table QOS_RATE_CONTROL 
        drop constraint FK2AFAC2E89587E4C2;

    alter table QOS_RATE_CONTROL_RATE_LIMIT 
        drop constraint FKD139053382960198;

    alter table RADIO_PROFILE 
        drop constraint FKF5CBCD259587E4C2;

    alter table RADIO_PROFILE_WMM_INFO 
        drop constraint FK41B5AD90B3EC8EC8;

    alter table RADIUS_AD_DOMAIN 
        drop constraint FKE63F2CF3E437B33D;

    alter table RADIUS_ATTRIBUTE_ITEM 
        drop constraint FKD5EA3643D675AD87;

    alter table RADIUS_ATTRIBUTE_ITEM 
        drop constraint FKD5EA3643165DAFD4;

    alter table RADIUS_HIVEAP_AUTH 
        drop constraint FK7E7BAA1B741D28BA;

    alter table RADIUS_HIVEAP_AUTH 
        drop constraint FK7E7BAA1BAAF112E4;

    alter table RADIUS_HIVEAP_LDAP_USER_PROFILE 
        drop constraint FK6D51211A320FE175;

    alter table RADIUS_HIVEAP_LDAP_USER_PROFILE 
        drop constraint FK6D51211A3EB265DC;

    alter table RADIUS_LIBRARY_SIP 
        drop constraint FK8647CEE92764AE32;

    alter table RADIUS_LIBRARY_SIP 
        drop constraint FK8647CEE99587E4C2;

    alter table RADIUS_ON_HIVEAP 
        drop constraint FK174BFA92D85EB174;

    alter table RADIUS_ON_HIVEAP 
        drop constraint FK174BFA92FC199088;

    alter table RADIUS_ON_HIVEAP 
        drop constraint FK174BFA929587E4C2;

    alter table RADIUS_ON_HIVEAP_LOCAL_USER_GROUP 
        drop constraint FK3741206CA03CD790;

    alter table RADIUS_ON_HIVEAP_LOCAL_USER_GROUP 
        drop constraint FK3741206C3EB265DC;

    alter table RADIUS_OPERATOR_ATTRIBUTE 
        drop constraint FK6DDE796E9587E4C2;

    alter table RADIUS_PROXY 
        drop constraint FKBB1160619587E4C2;

    alter table RADIUS_PROXY_NAS 
        drop constraint FK18E4A42235DCA0FD;

    alter table RADIUS_PROXY_NAS 
        drop constraint FK18E4A422AAF112E4;

    alter table RADIUS_PROXY_REALM 
        drop constraint FK7286191135DCA0FD;

    alter table RADIUS_PROXY_REALM 
        drop constraint FK72861911FD593BC1;

    alter table RADIUS_RULE_USER_PROFILE 
        drop constraint FK67CC30B45792A2B;

    alter table RADIUS_RULE_USER_PROFILE 
        drop constraint FK67CC30B5C4B0AB1;

    alter table RADIUS_SERVICE 
        drop constraint FKC4E7A7E8D9B4E764;

    alter table RADIUS_SERVICE 
        drop constraint FKC4E7A7E8AAF112E4;

    alter table RADIUS_SERVICE_ASSIGN 
        drop constraint FK56A295269587E4C2;

    alter table RADIUS_USER_PROFILE_RULE 
        drop constraint FK3EAB91799587E4C2;

    alter table ROUTING_POLICY 
        drop constraint FK302F42EB89A44C6F;

    alter table ROUTING_POLICY 
        drop constraint FK302F42EB3E8CBCF7;

    alter table ROUTING_POLICY 
        drop constraint FK302F42EB9587E4C2;

    alter table ROUTING_POLICY_RULE 
        drop constraint FKCB6E1FD0F94E5ADF;

    alter table ROUTING_POLICY_RULE 
        drop constraint FKCB6E1FD0CA4D879B;

    alter table ROUTING_POLICY_RULE 
        drop constraint FKCB6E1FD0FC66A11D;

    alter table ROUTING_POLICY_RULE 
        drop constraint FKCB6E1FD0F8020307;

    alter table ROUTING_PROFILE 
        drop constraint FKDB00A2F09587E4C2;

    alter table RPC_SETTINGS 
        drop constraint FK77254D5D9587E4C2;

    alter table SCHEDULER 
        drop constraint FK9C83D09B9587E4C2;

    alter table SCHEDULE_BACKUP 
        drop constraint FK29D21C6A9587E4C2;

    alter table SERVICE_FILTER 
        drop constraint FK938B48629587E4C2;

    alter table SIP_POLICY_RULE 
        drop constraint FKF16AF5E4680D20DA;

    alter table SIP_POLICY_RULE 
        drop constraint FKF16AF5E4DBBB8BB0;

    alter table SLA_MAPPING_CUSTOMIZE 
        drop constraint FK8B5CB25B9587E4C2;

    alter table SSID_LOCAL_USER_GROUP 
        drop constraint FKA88CEEA36B81105E;

    alter table SSID_LOCAL_USER_GROUP 
        drop constraint FKA88CEEA33EB265DC;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC53C1DC8FC;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC5D6DF1805;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC58ED6DA8B;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC5FEE748D1;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC590A27B35;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC553819019;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC52BE64EFE;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC5938131AE;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC5CB985E5B;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC5F46CA5A;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC58CC46187;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC590F4D100;

    alter table SSID_PROFILE 
        drop constraint FK74C70CC59587E4C2;

    alter table SSID_PROFILE_MAC_FILTER 
        drop constraint FK859DBC426B81105E;

    alter table SSID_PROFILE_MAC_FILTER 
        drop constraint FK859DBC4276773CB2;

    alter table SSID_PROFILE_SCHEDULER 
        drop constraint FKF82A65A1BD52F695;

    alter table SSID_PROFILE_SCHEDULER 
        drop constraint FKF82A65A16B81105E;

    alter table SSID_PROFILE_USER_PROFILE 
        drop constraint FK4D1AAA4F45792A2B;

    alter table SSID_PROFILE_USER_PROFILE 
        drop constraint FK4D1AAA4F6B81105E;

    alter table SSID_RADIUS_USER_GROUP 
        drop constraint FK6D9D62F46B81105E;

    alter table SSID_RADIUS_USER_GROUP 
        drop constraint FK6D9D62F43EB265DC;

    alter table SUB_NETWORK_RESOURCE 
        drop constraint FKDFE1935EC8605ECA;

    alter table SUB_NETWORK_RESOURCE 
        drop constraint FKDFE1935E9587E4C2;

    alter table TCA_ALARM 
        drop constraint FKEAAC1B649587E4C2;

    alter table TEMPLATE_FIELD 
        drop constraint FK673504D54ED9818;

    alter table TREX 
        drop constraint FK276BB1D06933B5;

    alter table TREX 
        drop constraint FK276BB19587E4C2;

    alter table TUNNEL_SETTING 
        drop constraint FKDECDD59AAF112E4;

    alter table TUNNEL_SETTING 
        drop constraint FKDECDD599587E4C2;

    alter table TUNNEL_SETTING_IP_ADDRESS 
        drop constraint FK13E97625AC9204D;

    alter table TUNNEL_SETTING_IP_ADDRESS 
        drop constraint FK13E9762AAF112E4;

    alter table TV_CLASS 
        drop constraint FK812AB55B30CFCFE5;

    alter table TV_CLASS 
        drop constraint FK812AB55B9587E4C2;

    alter table TV_CLASS_SCHEDULE 
        drop constraint FKFB9DD4FBE7B90D67;

    alter table TV_COMPUTER_CART 
        drop constraint FK14AB46C79587E4C2;

    alter table TV_COMPUTER_CART_MAC 
        drop constraint FKACB8E3B7C631E328;

    alter table TV_RESOURCE_MAP 
        drop constraint FK526958289587E4C2;

    alter table TV_SCHEDULE_MAP 
        drop constraint FKAE97BA319587E4C2;

    alter table TV_SCHEDULE_PERIODTIME 
        drop constraint FK24484D79E58C2E9E;

    alter table TV_SCHEDULE_WEEKDAY 
        drop constraint FKE237097DE58C2E9E;

    alter table TV_STUDENT_ROSTER 
        drop constraint FK7C315C8CD0D8B84A;

    alter table TV_STUDENT_ROSTER 
        drop constraint FK7C315C8C9587E4C2;

    alter table USB_MODEM_PARAMETER 
        drop constraint FK388C53B99587E4C2;

    alter table USB_MODEM_SIGNAL_STRENGTH_CHECK 
        drop constraint FK5F5ACA11B3FA74D8;

    alter table USER_LOCALUSERGROUP 
        drop constraint FK1635F97D8D1ECDE5;

    alter table USER_LOCALUSERGROUP 
        drop constraint FK1635F97D712B3972;

    alter table USER_PROFILE 
        drop constraint FKCEC2A515B7604B76;

    alter table USER_PROFILE 
        drop constraint FKCEC2A5152CC2CD27;

    alter table USER_PROFILE 
        drop constraint FKCEC2A5159E69E7F1;

    alter table USER_PROFILE 
        drop constraint FKCEC2A515CFEAAA76;

    alter table USER_PROFILE 
        drop constraint FKCEC2A515616216CD;

    alter table USER_PROFILE 
        drop constraint FKCEC2A5159CC4BF1C;

    alter table USER_PROFILE 
        drop constraint FKCEC2A515BFA9A9A6;

    alter table USER_PROFILE 
        drop constraint FKCEC2A515D6DF1805;

    alter table USER_PROFILE 
        drop constraint FKCEC2A51535ACB7F8;

    alter table USER_PROFILE 
        drop constraint FKCEC2A51525CE4881;

    alter table USER_PROFILE 
        drop constraint FKCEC2A5159587E4C2;

    alter table USER_PROFILE_ATTRIBUTE 
        drop constraint FKB05BC3D29587E4C2;

    alter table USER_PROFILE_SCHEDULER 
        drop constraint FKA5FFD9F145792A2B;

    alter table USER_PROFILE_SCHEDULER 
        drop constraint FKA5FFD9F1BD52F695;

    alter table USER_REG_INFO_FOR_LS 
        drop constraint FK6256A1CF9587E4C2;

    alter table USER_SSIDPROFILE 
        drop constraint FK9AE1333A6DFE4ED5;

    alter table USER_SSIDPROFILE 
        drop constraint FK9AE1333A8D1ECDE5;

    alter table VIEWING_CLASS 
        drop constraint FKB82FCD969587E4C2;

    alter table VLAN 
        drop constraint FK283D639587E4C2;

    alter table VLAN_DHCP_SERVER 
        drop constraint FKD001E759587E4C2;

    alter table VLAN_GROUP 
        drop constraint FK397AC8839587E4C2;

    alter table VLAN_ITEM 
        drop constraint FK9EC2D46FD675AD87;

    alter table VLAN_ITEM 
        drop constraint FK9EC2D46F25CE4881;

    alter table VPN_GATEWAY_SETTING 
        drop constraint FKC2A429AA463346F8;

    alter table VPN_NETWORK 
        drop constraint FKADDDC5A3F3740612;

    alter table VPN_NETWORK 
        drop constraint FKADDDC5A325CE4881;

    alter table VPN_NETWORK 
        drop constraint FKADDDC5A39587E4C2;

    alter table VPN_NETWORK_CUSTOM 
        drop constraint FKF403A98D3BF5004E;

    alter table VPN_NETWORK_IP_RESERVE_ITEM 
        drop constraint FK1DDDC252D675AD87;

    alter table VPN_NETWORK_IP_RESERVE_ITEM 
        drop constraint FK1DDDC2522EE7D6A3;

    alter table VPN_NETWORK_SUBITEM 
        drop constraint FKD9E0BE971B9D6857;

    alter table VPN_NETWORK_SUBNETCLASS 
        drop constraint FK6EB9537F87B0C7C;

    alter table VPN_NETWORK_SUBNETCLASS 
        drop constraint FK6EB9537FD675AD87;

    alter table VPN_NETWORK_SUBNET_CUSTOMS 
        drop constraint FKB346EE3C96928C24;

    alter table VPN_SERVICE 
        drop constraint FKB640322AE17CF9A1;

    alter table VPN_SERVICE 
        drop constraint FKB640322A99964783;

    alter table VPN_SERVICE 
        drop constraint FKB640322A9587E4C2;

    alter table VPN_SERVICE_CREDENTIAL 
        drop constraint FKC3DD98CF3C29698;

    alter table VPN_USERPROFILE_TRAFFICL2 
        drop constraint FK3BFE6F37FD7C9A6B;

    alter table VPN_USERPROFILE_TRAFFICL2 
        drop constraint FK3BFE6F37F94E5ADF;

    alter table VPN_USERPROFILE_TRAFFICL3 
        drop constraint FK3BFE6F38F94E5ADF;

    alter table VPN_USERPROFILE_TRAFFICL3 
        drop constraint FK3BFE6F38FD7D0ECA;

    alter table WALLED_GARDEN_ITEM 
        drop constraint FK37FBFB877F5D3065;

    alter table WALLED_GARDEN_ITEM 
        drop constraint FK37FBFB87CD301006;

    alter table ah_new_sla_stats 
        drop constraint FK4F4990D19587E4C2;

    alter table ah_new_sla_stats_day 
        drop constraint FK7BAF732E9587E4C2;

    alter table ah_new_sla_stats_hour 
        drop constraint FKFA40F8929587E4C2;

    alter table ah_new_sla_stats_week 
        drop constraint FKFA47A2A29587E4C2;

    alter table ah_report_compliance 
        drop constraint FK40AB01EE9587E4C2;

    alter table ah_sla_stats 
        drop constraint FK3019A8B09587E4C2;

    alter table client_device_info 
        drop constraint FKA4157E839587E4C2;

    alter table client_device_seen 
        drop constraint FKA419E8509587E4C2;

    alter table clients_osinfo_count 
        drop constraint FK83454FF99587E4C2;

    alter table clients_osinfo_count_day 
        drop constraint FKBFE75E569587E4C2;

    alter table clients_osinfo_count_hour 
        drop constraint FK3D06726A9587E4C2;

    alter table clients_osinfo_count_week 
        drop constraint FK3D0D1C7A9587E4C2;

    alter table hm_client_stats 
        drop constraint FKEE3A44259587E4C2;

    alter table hm_client_stats_day 
        drop constraint FK1AE5C829587E4C2;

    alter table hm_client_stats_hour 
        drop constraint FK341F39BE9587E4C2;

    alter table hm_client_stats_week 
        drop constraint FK3425E3CE9587E4C2;

    alter table hm_cpu_memory_usage 
        drop constraint FKB46C52149587E4C2;

    alter table hm_device_stats 
        drop constraint FKC4D1A709587E4C2;

    alter table hm_interface_stats 
        drop constraint FK7212BEDF9587E4C2;

    alter table hm_interface_stats_day 
        drop constraint FK9AF6EA3C9587E4C2;

    alter table hm_interface_stats_hour 
        drop constraint FKC3E863449587E4C2;

    alter table hm_interface_stats_week 
        drop constraint FKC3EF0D549587E4C2;

    alter table hmservicessettings 
        drop constraint FK8E5FF186A0006DB7;

    alter table hmservicessettings 
        drop constraint FK8E5FF186554ABB4E;

    alter table hmservicessettings 
        drop constraint FK8E5FF18652C70A9A;

    alter table hmservicessettings 
        drop constraint FK8E5FF1869587E4C2;

    alter table max_clients_count 
        drop constraint FKE1C4CC7D9587E4C2;

    alter table network_device_history 
        drop constraint FKC9DBA5DC9587E4C2;

    alter table ssid_clients_count 
        drop constraint FK1AB336549587E4C2;

    alter table ssid_clients_count_day 
        drop constraint FK86F49F319587E4C2;

    alter table ssid_clients_count_hour 
        drop constraint FK57A14CEF9587E4C2;

    alter table ssid_clients_count_week 
        drop constraint FK57A7F6FF9587E4C2;

    alter table user_name_devices 
        drop constraint FKE1E7531D9587E4C2;

    alter table user_name_info 
        drop constraint FKA6EE666E9587E4C2;

    alter table user_name_seen 
        drop constraint FKA6F2D03B9587E4C2;

    alter table user_profiles_history 
        drop constraint FKCE98D3339587E4C2;

    drop table if exists ACCESS_CONSOLE cascade;

    drop table if exists ACCESS_CONSOLE_MAC_FILTER cascade;

    drop table if exists ACTIVATION_KEY_INFO cascade;

    drop table if exists ACTIVECLIENT_FILTER cascade;

    drop table if exists ACTIVE_DIRECTORY_OR_LDAP cascade;

    drop table if exists AH_ADMINLOGIN_SESSION cascade;

    drop table if exists AH_ALARM cascade;

    drop table if exists AH_ALARMS_FILTER cascade;

    drop table if exists AH_CLIENTEDITVALUES cascade;

    drop table if exists AH_CLIENTSESSION cascade;

    drop table if exists AH_CLIENTSESSION_HISTORY cascade;

    drop table if exists AH_DEVICE_PSE_POWER cascade;

    drop table if exists AH_EVENT cascade;

    drop table if exists AH_EVENTS_FILTER cascade;

    drop table if exists AH_PORT_AVAILABILITY cascade;

    drop table if exists AH_PSE_STATUS cascade;

    drop table if exists AH_STATS_AVAILABILITY_HIGH cascade;

    drop table if exists AH_STATS_AVAILABILITY_LOW cascade;

    drop table if exists AH_STATS_LATENCY_HIGH cascade;

    drop table if exists AH_STATS_LATENCY_LOW cascade;

    drop table if exists AH_STATS_THROUGHPUT_HIGH cascade;

    drop table if exists AH_STATS_THROUGHPUT_LOW cascade;

    drop table if exists AH_STATS_VPNSTATUS_HIGH cascade;

    drop table if exists AH_STATS_VPNSTATUS_LOW cascade;

    drop table if exists AH_USERLOGIN_SESSION cascade;

    drop table if exists AIR_SCREEN_ACTION cascade;

    drop table if exists AIR_SCREEN_BEHAVIOR cascade;

    drop table if exists AIR_SCREEN_GROUP_RULE cascade;

    drop table if exists AIR_SCREEN_RULE cascade;

    drop table if exists AIR_SCREEN_RULE_ACTION cascade;

    drop table if exists AIR_SCREEN_RULE_BEHAVIOR cascade;

    drop table if exists AIR_SCREEN_RULE_GROUP cascade;

    drop table if exists AIR_SCREEN_SOURCE cascade;

    drop table if exists AIR_TIGHT_SETTINGS cascade;

    drop table if exists ALG_CONFIGURATION cascade;

    drop table if exists ALG_CONFIG_INFO cascade;

    drop table if exists APPLICATION cascade;

    drop table if exists APPLICATION_PROFILE cascade;

    drop table if exists APPPROFILE_APP cascade;

    drop table if exists AP_CONNECT_HISTORY_INFO cascade;

    drop table if exists ATTRIBUTE_ITEM cascade;

    drop table if exists A_RATE_SETTING_INFO cascade;

    drop table if exists BONJOUR_ACTIVE_SERVICE cascade;

    drop table if exists BONJOUR_FILTER_RULE cascade;

    drop table if exists BONJOUR_GATEWAY_MONITORING cascade;

    drop table if exists BONJOUR_GATEWAY_SETTINGS cascade;

    drop table if exists BONJOUR_REALM cascade;

    drop table if exists BONJOUR_SERVICE cascade;

    drop table if exists BONJOUR_SERVICE_CATEGORY cascade;

    drop table if exists BONJOUR_SERVICE_DETAIL cascade;

    drop table if exists CAPWAPSETTINGS cascade;

    drop table if exists CLOUD_AUTH_CUSTOMER cascade;

    drop table if exists COMPLIANCE_POLICY cascade;

    drop table if exists CONFIG_TEMPLATE cascade;

    drop table if exists CONFIG_TEMPLATE_IP_TRACK cascade;

    drop table if exists CONFIG_TEMPLATE_LAN cascade;

    drop table if exists CONFIG_TEMPLATE_SSID cascade;

    drop table if exists CONFIG_TEMPLATE_STORM_CONTROL cascade;

    drop table if exists CONFIG_TEMPLATE_TV_SERVICE cascade;

    drop table if exists CWP cascade;

    drop table if exists CWP_CERTIFICATE cascade;

    drop table if exists CWP_PAGE_FIELD cascade;

    drop table if exists DASHBOARD_COMPONENT_DATA cascade;

    drop table if exists DASHBOARD_COMPONENT_METRIC cascade;

    drop table if exists DENY_UPGRADE_EMAIL_SUFFIX cascade;

    drop table if exists DEVICE_AUTO_PROVISION_INTERFACE cascade;

    drop table if exists DEVICE_AUTO_PROVISION_IPSUBNETWORKS cascade;

    drop table if exists DEVICE_DA_INFO cascade;

    drop table if exists DEVICE_INTERFACE_IPSUBNETWORK cascade;

    drop table if exists DEVICE_POLICY_RULE cascade;

    drop table if exists DHCP_SERVER_CUSTOM cascade;

    drop table if exists DHCP_SERVER_IPPOOL cascade;

    drop table if exists DIRECTORY_OPENLDAP_INFO cascade;

    drop table if exists DNS_SERVICE_PROFILE cascade;

    drop table if exists DNS_SPECIFIC_SETTINGS cascade;

    drop table if exists DOMAIN_NAME_ITEM cascade;

    drop table if exists DOMAIN_OBJECT cascade;

    drop table if exists DOMAIN_ORDER_KEY_INFO cascade;

    drop table if exists DOS_PREVENTION cascade;

    drop table if exists DOS_PREVENTION_DOS_PARAMS cascade;

    drop table if exists ETHERNET_ACCESS cascade;

    drop table if exists ETHERNET_ACCESS_MAC cascade;

    drop table if exists FIREWALL_POLICY cascade;

    drop table if exists FIREWALL_POLICY_RULE cascade;

    drop table if exists G_RATE_SETTING_INFO cascade;

    drop table if exists HA_SETTINGS cascade;

    drop table if exists HHM_UPGRADE_VERSION_INFO cascade;

    drop table if exists HIVEAP_DEVICE_INTERFACE cascade;

    drop table if exists HIVEAP_FILTER cascade;

    drop table if exists HIVEAP_IMAGE_INFO cascade;

    drop table if exists HIVE_AP cascade;

    drop table if exists HIVE_AP_AUTO_PROVISION cascade;

    drop table if exists HIVE_AP_AUTO_PROVISION_MACES cascade;

    drop table if exists HIVE_AP_DHCP_SERVER cascade;

    drop table if exists HIVE_AP_DYNAMIC_ROUTE cascade;

    drop table if exists HIVE_AP_INTERNAL_NETWORK cascade;

    drop table if exists HIVE_AP_IP_ROUTE cascade;

    drop table if exists HIVE_AP_L3CFG_NEIGHBOR cascade;

    drop table if exists HIVE_AP_LEARNING_MAC cascade;

    drop table if exists HIVE_AP_MULTIPLE_VLAN cascade;

    drop table if exists HIVE_AP_SERIAL_NUMBER cascade;

    drop table if exists HIVE_AP_SSID_ALLOCATION cascade;

    drop table if exists HIVE_AP_STATIC_ROUTE cascade;

    drop table if exists HIVE_AP_UPDATE_RESULT cascade;

    drop table if exists HIVE_AP_UPDATE_RESULT_ITEM cascade;

    drop table if exists HIVE_AP_UPDATE_SETTINGS cascade;

    drop table if exists HIVE_AP_USB_MODEM cascade;

    drop table if exists HIVE_AP_USER_PROFILE cascade;

    drop table if exists HIVE_AP_VIRTUAL_CONNECTION cascade;

    drop table if exists HIVE_PROFILE cascade;

    drop table if exists HIVE_PROFILE_MAC_FILTER cascade;

    drop table if exists HMOL_UPGRADE_SERVER_INFO cascade;

    drop table if exists HM_ACCESS_CONTROL cascade;

    drop table if exists HM_ACCESS_CONTROL_IP cascade;

    drop table if exists HM_ACSPNEIGHBOR cascade;

    drop table if exists HM_ASSOCIATION cascade;

    drop table if exists HM_AUDITLOG cascade;

    drop table if exists HM_AUTOREFRESH_SETTINGS cascade;

    drop table if exists HM_BANDWIDTHSENTINEL_HISTORY cascade;

    drop table if exists HM_CUSTOM_REPORT cascade;

    drop table if exists HM_CUSTOM_REPORT_FIELD cascade;

    drop table if exists HM_CUSTOM_REPORT_FIELD_TABLE cascade;

    drop table if exists HM_DASHBOARD cascade;

    drop table if exists HM_DASHBOARD_APPAP cascade;

    drop table if exists HM_DASHBOARD_COMPONENT cascade;

    drop table if exists HM_DASHBOARD_LAYOUT cascade;

    drop table if exists HM_DASHBOARD_WIDGET cascade;

    drop table if exists HM_DOMAIN cascade;

    drop table if exists HM_EXPRESSMODE_ENABLE cascade;

    drop table if exists HM_FEATURE_PERMISSION cascade;

    drop table if exists HM_INSTANCE_PERMISSION cascade;

    drop table if exists HM_INTERFERENCESTATS cascade;

    drop table if exists HM_L3FIREWALLLOG cascade;

    drop table if exists HM_LATESTACSPNEIGHBOR cascade;

    drop table if exists HM_LATESTINTERFERENCESTATS cascade;

    drop table if exists HM_LATESTNEIGHBOR cascade;

    drop table if exists HM_LATESTRADIOATTRIBUTE cascade;

    drop table if exists HM_LATESTXIF cascade;

    drop table if exists HM_LLDP_INFORMATION cascade;

    drop table if exists HM_LOGIN_AUTHENTICATION cascade;

    drop table if exists HM_NEIGHBOR cascade;

    drop table if exists HM_NEW_REPORT cascade;

    drop table if exists HM_NTP_SERVER_INTERVAL cascade;

    drop table if exists HM_PCIDATA cascade;

    drop table if exists HM_RADIOATTRIBUTE cascade;

    drop table if exists HM_RADIOSTATS cascade;

    drop table if exists HM_REPORT cascade;

    drop table if exists HM_REPO_APCPUMEM_DATE cascade;

    drop table if exists HM_REPO_APCPUMEM_DATE_AP cascade;

    drop table if exists HM_REPO_APCPUMEM_HOUR cascade;

    drop table if exists HM_REPO_APCPUMEM_MONTH cascade;

    drop table if exists HM_REPO_APCPUMEM_MONTH_AP cascade;

    drop table if exists HM_REPO_APCPUMEM_WEEK cascade;

    drop table if exists HM_REPO_APCPUMEM_WEEK_AP cascade;

    drop table if exists HM_REPO_APP_DATA_DATE cascade;

    drop table if exists HM_REPO_APP_DATA_DATE_AP cascade;

    drop table if exists HM_REPO_APP_DATA_HOUR cascade;

    drop table if exists HM_REPO_APP_DATA_MONTH cascade;

    drop table if exists HM_REPO_APP_DATA_MONTH_AP cascade;

    drop table if exists HM_REPO_APP_DATA_WEEK cascade;

    drop table if exists HM_REPO_APP_DATA_WEEK_AP cascade;

    drop table if exists HM_REPO_CLIENT_DATA_DATE cascade;

    drop table if exists HM_REPO_CLIENT_DATA_DATE_AP cascade;

    drop table if exists HM_REPO_CLIENT_DATA_HOUR cascade;

    drop table if exists HM_REPO_CLIENT_DATA_MONTH cascade;

    drop table if exists HM_REPO_CLIENT_DATA_MONTH_AP cascade;

    drop table if exists HM_REPO_CLIENT_DATA_WEEK cascade;

    drop table if exists HM_REPO_CLIENT_DATA_WEEK_AP cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_DATE cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_DATE_AP cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_HOUR cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_MONTH cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_MONTH_AP cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_WEEK cascade;

    drop table if exists HM_REPO_NETWORK_INTERFACE_WEEK_AP cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_DATE cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_DATE_AP cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_HOUR cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_MONTH cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_MONTH_AP cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_WEEK cascade;

    drop table if exists HM_REPO_NEWSLA_STATS_WEEK_AP cascade;

    drop table if exists HM_REPO_ROLLUP_RECORD cascade;

    drop table if exists HM_SPECTRAL_ANALYSIS cascade;

    drop table if exists HM_START_CONFIG cascade;

    drop table if exists HM_SUMMARY_PAGE cascade;

    drop table if exists HM_SYSTEMLOG cascade;

    drop table if exists HM_TABLE_COLUMN cascade;

    drop table if exists HM_TABLE_SIZE cascade;

    drop table if exists HM_UPGRADE_LOG cascade;

    drop table if exists HM_USER cascade;

    drop table if exists HM_USER_GROUP cascade;

    drop table if exists HM_USER_REPORT cascade;

    drop table if exists HM_VIFSTATS cascade;

    drop table if exists HM_VPNSTATUS cascade;

    drop table if exists HM_XIF cascade;

    drop table if exists IDP cascade;

    drop table if exists IDP_AP cascade;

    drop table if exists IDP_ENCLOSED_FRIENDLY_AP cascade;

    drop table if exists IDP_ENCLOSED_ROGUE_AP cascade;

    drop table if exists IDP_SETTINGS cascade;

    drop table if exists IDS_POLICY cascade;

    drop table if exists IDS_POLICY_MAC_OR_OUI cascade;

    drop table if exists IDS_POLICY_SSID_PROFILE cascade;

    drop table if exists IDS_POLICY_VLAN cascade;

    drop table if exists INTER_ROAMING cascade;

    drop table if exists IP_ADDRESS cascade;

    drop table if exists IP_ADDRESS_ITEM cascade;

    drop table if exists IP_FILTER cascade;

    drop table if exists IP_FILTER_IP_ADDRESS cascade;

    drop table if exists IP_POLICY cascade;

    drop table if exists IP_POLICY_RULE cascade;

    drop table if exists LAN_PROFILE cascade;

    drop table if exists LAN_PROFILE_REGULAR_NETWORKS cascade;

    drop table if exists LAN_PROFILE_REGULAR_VLAN cascade;

    drop table if exists LAN_PROFILE_SCHEDULER cascade;

    drop table if exists LAN_PROFILE_USER_PROFILE cascade;

    drop table if exists LAN_RADIUS_USER_GROUP cascade;

    drop table if exists LICENSE_HISTORY_INFO cascade;

    drop table if exists LICENSE_SERVER_SETTING cascade;

    drop table if exists LLDPCDPPROFILE cascade;

    drop table if exists LOCAL_USER cascade;

    drop table if exists LOCAL_USER_GROUP cascade;

    drop table if exists LOCATIONCLIENTWATCH cascade;

    drop table if exists LOCATIONCLIENT_ITEM cascade;

    drop table if exists LOCATION_RSSI_REPORT cascade;

    drop table if exists LOCATION_SERVER cascade;

    drop table if exists LOGSETTINGS cascade;

    drop table if exists MAC_AUTH cascade;

    drop table if exists MAC_FILTER cascade;

    drop table if exists MAC_FILTER_MAC_OR_OUI cascade;

    drop table if exists MAC_OR_OUI cascade;

    drop table if exists MAC_OR_OUI_ITEM cascade;

    drop table if exists MAC_POLICY cascade;

    drop table if exists MAC_POLICY_RULE cascade;

    drop table if exists MAIL_NOTIFICATION cascade;

    drop table if exists MAIL_NOTIFICATION_VHM cascade;

    drop table if exists MAP_LINK cascade;

    drop table if exists MAP_NODE cascade;

    drop table if exists MAP_PERIMETER cascade;

    drop table if exists MAP_SETTINGS cascade;

    drop table if exists MAP_WALL cascade;

    drop table if exists MGMT_SERVICE_DNS cascade;

    drop table if exists MGMT_SERVICE_DNS_INFO cascade;

    drop table if exists MGMT_SERVICE_IP_TRACK cascade;

    drop table if exists MGMT_SERVICE_OPTION cascade;

    drop table if exists MGMT_SERVICE_SNMP cascade;

    drop table if exists MGMT_SERVICE_SNMP_INFO cascade;

    drop table if exists MGMT_SERVICE_SYSLOG cascade;

    drop table if exists MGMT_SERVICE_SYSLOG_INFO cascade;

    drop table if exists MGMT_SERVICE_TIME cascade;

    drop table if exists MGMT_SERVICE_TIME_INFO cascade;

    drop table if exists MULTICAST_FORWARDING cascade;

    drop table if exists NEIGHBORS_NAME_ITEM cascade;

    drop table if exists NETWORK_SERVICE cascade;

    drop table if exists NOTIFICATION_MESSAGE_STATUS cascade;

    drop table if exists N_RATE_SETTING_INFO cascade;

    drop table if exists ONETIME_PASSWORD cascade;

    drop table if exists OS_OBJECT cascade;

    drop table if exists OS_OBJECT_VERSION cascade;

    drop table if exists OS_OBJECT_VERSION_DHCP cascade;

    drop table if exists OS_VERSION cascade;

    drop table if exists PLANNED_AP cascade;

    drop table if exists PLAN_TOOL cascade;

    drop table if exists PPPOE cascade;

    drop table if exists PRINT_TEMPLATE cascade;

    drop table if exists QOS_CLASSFIER_AND_MARKER cascade;

    drop table if exists QOS_CLASSIFICATION cascade;

    drop table if exists QOS_CLASSIFICATION_MAC cascade;

    drop table if exists QOS_CLASSIFICATION_SERVICE cascade;

    drop table if exists QOS_CLASSIFICATION_SSID cascade;

    drop table if exists QOS_MARKING cascade;

    drop table if exists QOS_RATE_CONTROL cascade;

    drop table if exists QOS_RATE_CONTROL_RATE_LIMIT cascade;

    drop table if exists RADIO_PROFILE cascade;

    drop table if exists RADIO_PROFILE_WMM_INFO cascade;

    drop table if exists RADIUS_AD_DOMAIN cascade;

    drop table if exists RADIUS_ATTRIBUTE_ITEM cascade;

    drop table if exists RADIUS_HIVEAP_AUTH cascade;

    drop table if exists RADIUS_HIVEAP_LDAP_USER_PROFILE cascade;

    drop table if exists RADIUS_LIBRARY_SIP cascade;

    drop table if exists RADIUS_ON_HIVEAP cascade;

    drop table if exists RADIUS_ON_HIVEAP_LOCAL_USER_GROUP cascade;

    drop table if exists RADIUS_OPERATOR_ATTRIBUTE cascade;

    drop table if exists RADIUS_PROXY cascade;

    drop table if exists RADIUS_PROXY_NAS cascade;

    drop table if exists RADIUS_PROXY_REALM cascade;

    drop table if exists RADIUS_RULE_USER_PROFILE cascade;

    drop table if exists RADIUS_SERVICE cascade;

    drop table if exists RADIUS_SERVICE_ASSIGN cascade;

    drop table if exists RADIUS_USER_PROFILE_RULE cascade;

    drop table if exists ROUTING_POLICY cascade;

    drop table if exists ROUTING_POLICY_RULE cascade;

    drop table if exists ROUTING_PROFILE cascade;

    drop table if exists RPC_SETTINGS cascade;

    drop table if exists SCHEDULER cascade;

    drop table if exists SCHEDULE_BACKUP cascade;

    drop table if exists SERVICE_FILTER cascade;

    drop table if exists SIP_POLICY_RULE cascade;

    drop table if exists SLA_MAPPING_CUSTOMIZE cascade;

    drop table if exists SSID_LOCAL_USER_GROUP cascade;

    drop table if exists SSID_PROFILE cascade;

    drop table if exists SSID_PROFILE_MAC_FILTER cascade;

    drop table if exists SSID_PROFILE_SCHEDULER cascade;

    drop table if exists SSID_PROFILE_USER_PROFILE cascade;

    drop table if exists SSID_RADIUS_USER_GROUP cascade;

    drop table if exists SUB_NETWORK_RESOURCE cascade;

    drop table if exists TARGET cascade;

    drop table if exists TCA_ALARM cascade;

    drop table if exists TEMPLATE_FIELD cascade;

    drop table if exists TREX cascade;

    drop table if exists TUNNEL_SETTING cascade;

    drop table if exists TUNNEL_SETTING_IP_ADDRESS cascade;

    drop table if exists TV_CLASS cascade;

    drop table if exists TV_CLASS_SCHEDULE cascade;

    drop table if exists TV_COMPUTER_CART cascade;

    drop table if exists TV_COMPUTER_CART_MAC cascade;

    drop table if exists TV_RESOURCE_MAP cascade;

    drop table if exists TV_SCHEDULE_MAP cascade;

    drop table if exists TV_SCHEDULE_PERIODTIME cascade;

    drop table if exists TV_SCHEDULE_WEEKDAY cascade;

    drop table if exists TV_STUDENT_ROSTER cascade;

    drop table if exists USB_MODEM_PARAMETER cascade;

    drop table if exists USB_MODEM_SIGNAL_STRENGTH_CHECK cascade;

    drop table if exists USER_LOCALUSERGROUP cascade;

    drop table if exists USER_PROFILE cascade;

    drop table if exists USER_PROFILE_ATTRIBUTE cascade;

    drop table if exists USER_PROFILE_SCHEDULER cascade;

    drop table if exists USER_REG_INFO_FOR_LS cascade;

    drop table if exists USER_SSIDPROFILE cascade;

    drop table if exists VIEWING_CLASS cascade;

    drop table if exists VLAN cascade;

    drop table if exists VLAN_DHCP_SERVER cascade;

    drop table if exists VLAN_GROUP cascade;

    drop table if exists VLAN_ITEM cascade;

    drop table if exists VPN_GATEWAY_SETTING cascade;

    drop table if exists VPN_NETWORK cascade;

    drop table if exists VPN_NETWORK_CUSTOM cascade;

    drop table if exists VPN_NETWORK_IP_RESERVE_ITEM cascade;

    drop table if exists VPN_NETWORK_SUBITEM cascade;

    drop table if exists VPN_NETWORK_SUBNETCLASS cascade;

    drop table if exists VPN_NETWORK_SUBNET_CUSTOMS cascade;

    drop table if exists VPN_SERVICE cascade;

    drop table if exists VPN_SERVICE_CREDENTIAL cascade;

    drop table if exists VPN_USERPROFILE_TRAFFICL2 cascade;

    drop table if exists VPN_USERPROFILE_TRAFFICL3 cascade;

    drop table if exists WALLED_GARDEN_ITEM cascade;

    drop table if exists ah_new_sla_stats cascade;

    drop table if exists ah_new_sla_stats_day cascade;

    drop table if exists ah_new_sla_stats_hour cascade;

    drop table if exists ah_new_sla_stats_week cascade;

    drop table if exists ah_report_compliance cascade;

    drop table if exists ah_sla_stats cascade;

    drop table if exists client_device_info cascade;

    drop table if exists client_device_seen cascade;

    drop table if exists clients_osinfo_count cascade;

    drop table if exists clients_osinfo_count_day cascade;

    drop table if exists clients_osinfo_count_hour cascade;

    drop table if exists clients_osinfo_count_week cascade;

    drop table if exists hm_appdata_hour cascade;

    drop table if exists hm_appdata_seconds cascade;

    drop table if exists hm_capwapclient cascade;

    drop table if exists hm_client_stats cascade;

    drop table if exists hm_client_stats_day cascade;

    drop table if exists hm_client_stats_hour cascade;

    drop table if exists hm_client_stats_week cascade;

    drop table if exists hm_cpu_memory_usage cascade;

    drop table if exists hm_device_stats cascade;

    drop table if exists hm_interface_stats cascade;

    drop table if exists hm_interface_stats_day cascade;

    drop table if exists hm_interface_stats_hour cascade;

    drop table if exists hm_interface_stats_week cascade;

    drop table if exists hm_repo_historyonlineuser_date cascade;

    drop table if exists hm_repo_historyonlineuser_date_ap cascade;

    drop table if exists hm_repo_historyonlineuser_hour cascade;

    drop table if exists hm_repo_historyonlineuser_month cascade;

    drop table if exists hm_repo_historyonlineuser_month_ap cascade;

    drop table if exists hm_repo_historyonlineuser_week cascade;

    drop table if exists hm_repo_historyonlineuser_week_ap cascade;

    drop table if exists hm_updatesoftwareinfo cascade;

    drop table if exists hmservicessettings cascade;

    drop table if exists max_clients_count cascade;

    drop table if exists network_device_history cascade;

    drop table if exists order_history_info cascade;

    drop table if exists ssid_clients_count cascade;

    drop table if exists ssid_clients_count_day cascade;

    drop table if exists ssid_clients_count_hour cascade;

    drop table if exists ssid_clients_count_week cascade;

    drop table if exists sync_task_on_hmol cascade;

    drop table if exists user_name_devices cascade;

    drop table if exists user_name_info cascade;

    drop table if exists user_name_seen cascade;

    drop table if exists user_profiles_history cascade;

    drop sequence hibernate_sequence;

    create table ACCESS_CONSOLE (
        id int8 not null,
        asciiKey varchar(63),
        consoleMode int2 not null,
        consoleName varchar(32) not null,
        defaultAction int2 not null,
        description varchar(64),
        enableTelnet boolean not null,
        encryption int4 not null,
        hideSsid boolean not null,
        maxClient int2 not null,
        mgmtKey int4 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ACCESS_CONSOLE_MAC_FILTER (
        ACCESS_CONSOLE_ID int8 not null,
        MAC_FILTER_ID int8 not null,
        primary key (ACCESS_CONSOLE_ID, MAC_FILTER_ID)
    );

    create table ACTIVATION_KEY_INFO (
        id int8 not null,
        activateSuccess boolean not null,
        activationKey varchar(35),
        hasRetryTime int2 not null,
        hoursUsed varchar(255),
        queryInterval int4 not null,
        queryPeriod int4 not null,
        queryRetryTime int2 not null,
        startRetryTimer boolean not null,
        systemId varchar(39),
        version timestamp,
        primary key (id)
    );

    create table ACTIVECLIENT_FILTER (
        id int8 not null,
        filterApName varchar(255),
        filterClientChannel int4 not null,
        filterClientHostName varchar(255),
        filterClientIP varchar(255),
        filterClientMac varchar(255),
        filterClientOsInfo varchar(255),
        filterClientUserName varchar(255),
        filterClientUserProfId int4 not null,
        filterClientVLAN int4 not null,
        filterName varchar(32) not null,
        filterOverallClientHealth int2 not null,
        filterTopologyMap int8,
        userName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ACTIVE_DIRECTORY_OR_LDAP (
        id int8 not null,
        apMac varchar(255),
        authTlsEnable boolean not null,
        basedN varchar(256),
        bindDnName varchar(256),
        caCertFileO varchar(255),
        clientFile varchar(255),
        computerOU varchar(256),
        description varchar(64),
        destinationPort int4 not null,
        filterAttr varchar(32),
        keyFileO varchar(255),
        keyPasswordO varchar(64),
        ldapProtocol int2 not null,
        name varchar(64) not null,
        passwordA varchar(64),
        passwordO varchar(64),
        saveCredentials boolean not null,
        stripFilter boolean not null,
        typeFlag int2 not null,
        userNameA varchar(64),
        verifyServer int2 not null,
        version timestamp,
        AD_IPADDRESS_ID int8,
        LDAP_IPADDRESS_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_ADMINLOGIN_SESSION (
        id  bigserial not null,
        apCount int8 not null,
        emailAddress varchar(128),
        loginTime int8 not null,
        logoutTime int8 not null,
        timeZone varchar(255),
        totalLoginTime int8 not null,
        userFullName varchar(128),
        userName varchar(128),
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_ALARM (
        id  bigserial not null,
        alarmSubType int2 not null,
        alarmType int2 not null,
        apId varchar(20),
        apName varchar(32),
        CLEAR_TIME int8 not null,
        CLEAR_TIME_ZONE varchar(255),
        code int4 not null,
        MODIFY_TIME int8 not null,
        MODIFY_TIME_ZONE varchar(255),
        objectName varchar(64),
        severity int2 not null,
        tag1 int4,
        tag2 int4,
        tag3 varchar(255),
        trapDesc varchar(255),
        TRAP_TIME int8 not null,
        TRAP_TIME_ZONE varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_ALARMS_FILTER (
        id int8 not null,
        apId varchar(255),
        component varchar(255),
        endTime int8,
        filterName varchar(32) not null,
        severity int2 not null,
        startTime int8,
        userName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_CLIENTEDITVALUES (
        id int8 not null,
        clientHostname varchar(32),
        clientIP varchar(255),
        clientMac varchar(48) not null,
        clientUsername varchar(128),
        comment1 varchar(32),
        comment2 varchar(32),
        companyName varchar(64),
        email varchar(64),
        expirationTime int8 not null,
        ssidName varchar(255),
        type int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_CLIENTSESSION (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        apSerialNumber varchar(14),
        applicationHealthScore int2 not null,
        bandWidthSentinelStatus int4 not null,
        clientAuthMethod int2 not null,
        clientBSSID varchar(255),
        clientCWPUsed int2 not null,
        clientChannel int4 not null,
        clientEncryptionMethod int2 not null,
        clientHostname varchar(32),
        clientIP varchar(255),
        clientMACProtocol int2 not null,
        clientMac varchar(20) not null,
        clientOsInfo varchar(255),
        clientRssi int4 not null,
        clientSSID varchar(32),
        clientUserProfId int4 not null,
        clientUsername varchar(128),
        clientVLAN int4 not null,
        comment1 varchar(32),
        comment2 varchar(32),
        companyName varchar(255),
        connectstate int2 not null,
        email varchar(255),
        endTimeStamp int8 not null,
        endTimeZone varchar(255),
        ifIndex int4 not null,
        ifName varchar(255),
        ipNetworkConnectivityScore int2 not null,
        mapId int8,
        memo varchar(255),
        os_option55 varchar(256),
        overallClientHealthScore int2 not null,
        simulated boolean not null,
        slaConnectScore int2 not null,
        startTimeStamp int8 not null,
        startTimeZone varchar(255),
        userProfileName varchar(255),
        wirelessClient boolean not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_CLIENTSESSION_HISTORY (
        id  bigserial not null,
        apMac varchar(255),
        apName varchar(64),
        apSerialNumber varchar(14),
        bandWidthSentinelStatus int4 not null,
        clientAuthMethod int2 not null,
        clientBSSID varchar(255),
        clientCWPUsed int2 not null,
        clientChannel int4 not null,
        clientEncryptionMethod int2 not null,
        clientHostname varchar(32),
        clientIP varchar(255),
        clientMACProtocol int2 not null,
        clientMac varchar(255) not null,
        clientOsInfo varchar(255),
        clientSSID varchar(32),
        clientUserProfId int4 not null,
        clientUsername varchar(128),
        clientVLAN int4 not null,
        comment1 varchar(32),
        comment2 varchar(32),
        companyName varchar(255),
        email varchar(255),
        endTimeStamp int8 not null,
        endTimeZone varchar(255),
        ifIndex int4 not null,
        ifName varchar(255),
        mapId int8,
        memo varchar(255),
        simulated boolean not null,
        startTimeStamp int8 not null,
        startTimeZone varchar(255),
        userProfileName varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_DEVICE_PSE_POWER (
        id  bigserial not null,
        mac varchar(12) not null,
        powerUsed float4 not null,
        totalPower float4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_EVENT (
        id  bigserial not null,
        alertType int2 not null,
        apId varchar(20),
        apName varchar(32),
        asInstanceID int4 not null,
        asName varchar(255),
        asNameType int2 not null,
        asReportType int2 not null,
        asRuleName varchar(255),
        asSourceID varchar(255),
        asSourceType int2 not null,
        AS_TIME int8 not null,
        AS_TIME_ZONE varchar(255),
        associationTime int8 not null,
        clientAuthMethod int2 not null,
        clientBSSID varchar(255),
        clientCWPUsed int2 not null,
        clientChannel int4 not null,
        clientEncryptionMethod int2 not null,
        clientHostName varchar(255),
        clientIp varchar(255),
        clientMacProtocol int2 not null,
        clientUserName varchar(255),
        clientUserProfId int4 not null,
        clientVLAN int4 not null,
        code int4 not null,
        curValue int4 not null,
        currentState int2 not null,
        eventType int2 not null,
        ifIndex int4 not null,
        objectName varchar(64),
        objectType int2 not null,
        poEEth0MaxSpeed int2 not null,
        poEEth0On int2 not null,
        poEEth0Pwr int4 not null,
        poEEth1MaxSpeed int2 not null,
        poEEth1On int2 not null,
        poEEth1Pwr int4 not null,
        poEWifi0Setting int2 not null,
        poEWifi1Setting int2 not null,
        poEWifi2Setting int2 not null,
        powerSource int4 not null,
        previousState int2 not null,
        radioChannel int4 not null,
        radioTxPower int4 not null,
        remoteId varchar(20),
        shorttermValue int4 not null,
        snapshotValue int4 not null,
        ssid varchar(255),
        tag1 varchar(255),
        thresholdHigh int4 not null,
        thresholdLow int4 not null,
        thresholdValue int4 not null,
        trapDesc varchar(255),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_EVENTS_FILTER (
        id int8 not null,
        apId varchar(255),
        component varchar(255),
        endTime int8,
        filterName varchar(32) not null,
        startTime int8,
        userName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_PORT_AVAILABILITY (
        id  bigserial not null,
        interfMode int2 not null,
        interfName varchar(255),
        interfStatus int2 not null,
        interfType int2 not null,
        mac varchar(12) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_PSE_STATUS (
        id  bigserial not null,
        interfName varchar(32),
        mac varchar(12) not null,
        pdClass int2 not null,
        pdType int2 not null,
        power float4 not null,
        status int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_AVAILABILITY_HIGH (
        id  bigserial not null,
        hostName varchar(255),
        interfActive int2 not null,
        interfName varchar(255),
        interfStatus int2 not null,
        mac varchar(12) not null,
        sid varchar(255),
        time int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_AVAILABILITY_LOW (
        id  bigserial not null,
        hostName varchar(255),
        interfActive int2 not null,
        interfName varchar(255),
        interfStatus int2 not null,
        mac varchar(12) not null,
        sid varchar(255),
        time int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_LATENCY_HIGH (
        id  bigserial not null,
        hostName varchar(255),
        interfName varchar(255),
        interfServer varchar(255),
        interfType int2 not null,
        mac varchar(12) not null,
        name varchar(255),
        rtt float8 not null,
        sid varchar(255),
        targetStatus int2 not null,
        time int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_LATENCY_LOW (
        id  bigserial not null,
        hostName varchar(255),
        interfName varchar(255),
        interfServer varchar(255),
        interfType int2 not null,
        mac varchar(12) not null,
        name varchar(255),
        rtt float8 not null,
        sid varchar(255),
        targetStatus int2 not null,
        time int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_THROUGHPUT_HIGH (
        id  bigserial not null,
        hostName varchar(255),
        interfName varchar(255),
        interfServer varchar(255),
        interfType int2 not null,
        mac varchar(12) not null,
        rxBytes int8 not null,
        rxPkts int8 not null,
        sid varchar(255),
        time int8 not null,
        txBytes int8 not null,
        txPkts int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_THROUGHPUT_LOW (
        id  bigserial not null,
        hostName varchar(255),
        interfName varchar(255),
        interfServer varchar(255),
        interfType int2 not null,
        mac varchar(12) not null,
        rxBytes int8 not null,
        rxPkts int8 not null,
        sid varchar(255),
        time int8 not null,
        txBytes int8 not null,
        txPkts int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_VPNSTATUS_HIGH (
        id  bigserial not null,
        hostName varchar(255),
        mac varchar(12) not null,
        sid varchar(255),
        time int8 not null,
        tunnelCount int4 not null,
        vpnStatus int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_STATS_VPNSTATUS_LOW (
        id  bigserial not null,
        hostName varchar(255),
        mac varchar(12) not null,
        sid varchar(255),
        time int8 not null,
        tunnelCount int4 not null,
        vpnStatus int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table AH_USERLOGIN_SESSION (
        id  bigserial not null,
        emailAddress varchar(128),
        loginTime int8 not null,
        logoutTime int8 not null,
        timeZone varchar(255),
        totalLoginTime int8 not null,
        userFullName varchar(128),
        userName varchar(128),
        OWNER int8 not null,
        primary key (id)
    );

    create table AIR_SCREEN_ACTION (
        id int8 not null,
        comment varchar(64),
        interval int4 not null,
        profileName varchar(32) not null,
        type int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table AIR_SCREEN_BEHAVIOR (
        id int8 not null,
        comment varchar(64),
        connectionCase int2 not null,
        interval int4 not null,
        profileName varchar(32) not null,
        threshold int4 not null,
        type int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table AIR_SCREEN_GROUP_RULE (
        GROUP_ID int8 not null,
        rules_id int8 not null,
        primary key (GROUP_ID, rules_id)
    );

    create table AIR_SCREEN_RULE (
        id int8 not null,
        comment varchar(64),
        profileName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        SOURCE int8,
        primary key (id)
    );

    create table AIR_SCREEN_RULE_ACTION (
        RULE_ID int8 not null,
        actions_id int8 not null,
        primary key (RULE_ID, actions_id)
    );

    create table AIR_SCREEN_RULE_BEHAVIOR (
        RULE_ID int8 not null,
        behaviors_id int8 not null,
        primary key (RULE_ID, behaviors_id)
    );

    create table AIR_SCREEN_RULE_GROUP (
        id int8 not null,
        description varchar(64),
        profileName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table AIR_SCREEN_SOURCE (
        id int8 not null,
        authMode int2 not null,
        comment varchar(64),
        encryptionMode int2 not null,
        maxRssi int4 not null,
        minRssi int4 not null,
        profileName varchar(32) not null,
        type int2 not null,
        version timestamp,
        OUI_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table AIR_TIGHT_SETTINGS (
        id int8 not null,
        enabled boolean not null,
        password varchar(32),
        serverURL varchar(128),
        syncInterval int4 not null,
        userName varchar(32),
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table ALG_CONFIGURATION (
        id int8 not null,
        configName varchar(32) not null,
        defaultFlag boolean not null,
        description varchar(64),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ALG_CONFIG_INFO (
        ALG_CONFIGURATION_ID int8 not null,
        duration int4 not null,
        ifEnable boolean not null,
        qosClass int2 not null,
        timeout int4 not null,
        mapkey varchar(255) not null,
        primary key (ALG_CONFIGURATION_ID, mapkey)
    );

    create table APPLICATION (
        id int8 not null,
        appCode int4,
        appGroupCode int4,
        appGroupName varchar(32),
        appName varchar(32) not null,
        defaultFlag boolean,
        description varchar(1024),
        shortName varchar(32),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table APPLICATION_PROFILE (
        id int8 not null,
        defaultFlag boolean,
        profileName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table APPPROFILE_APP (
        PROFILE_ID int8 not null,
        APP_ID int8 not null,
        primary key (PROFILE_ID, APP_ID)
    );

    create table AP_CONNECT_HISTORY_INFO (
        id  bigserial not null,
        apId varchar(255),
        apName varchar(255),
        mapId int8,
        trapMessage varchar(255),
        trapTime int8 not null,
        trapType int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ATTRIBUTE_ITEM (
        ATTRIBUTE_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (ATTRIBUTE_ID, POSITION)
    );

    create table A_RATE_SETTING_INFO (
        SSID_PROFILE_ID int8 not null,
        rateSet int2 not null,
        mapkey varchar(255) not null,
        primary key (SSID_PROFILE_ID, mapkey)
    );

    create table BONJOUR_ACTIVE_SERVICE (
        BONJOUR_GATEWAY_SETTINGS_ID int8 not null,
        BONJOUR_SERVICE_ID int8,
        POSITION int4 not null,
        primary key (BONJOUR_GATEWAY_SETTINGS_ID, POSITION)
    );

    create table BONJOUR_FILTER_RULE (
        BONJOUR_FILTER_ID int8 not null,
        BONJOUR_SERVICE_ID int8,
        filterAction int2 not null,
        FROM_VLAN_GROUP_ID int8,
        metric varchar(255),
        ruleId int2 not null,
        TO_VLAN_GROUP_ID int8,
        POSITION int4 not null,
        primary key (BONJOUR_FILTER_ID, POSITION)
    );

    create table BONJOUR_GATEWAY_MONITORING (
        id int8 not null,
        defaultFlag boolean not null,
        hostName varchar(32) not null,
        macAddress varchar(12) not null unique,
        realmId varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table BONJOUR_GATEWAY_SETTINGS (
        id int8 not null,
        bonjourGwName varchar(32) not null,
        defaultFlag boolean not null,
        description varchar(255),
        version timestamp,
        vlans varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table BONJOUR_REALM (
        id int8 not null,
        realmId varchar(255),
        realmName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table BONJOUR_SERVICE (
        id int8 not null,
        serviceName varchar(255),
        type varchar(255),
        typeId int4 not null,
        version timestamp,
        BONJOUR_SERVICE_CATEGRORY_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table BONJOUR_SERVICE_CATEGORY (
        id int8 not null,
        serviceCategoryName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table BONJOUR_SERVICE_DETAIL (
        BONJOUR_GATEWAY_MONITORING_ID int8 not null,
        action varchar(255),
        host varchar(255),
        ip4 varchar(255),
        ip6 varchar(255),
        macAddress varchar(12) not null,
        name varchar(255),
        port int2 not null,
        shareRomoteBdd varchar(255),
        shared boolean not null,
        text varchar(2048),
        type varchar(255),
        vlan int2 not null,
        vlanGroupName varchar(255),
        POSITION int4 not null,
        primary key (BONJOUR_GATEWAY_MONITORING_ID, POSITION)
    );

    create table CAPWAPSETTINGS (
        id int8 not null,
        backupCapwapIP varchar(255),
        bootStrap varchar(32),
        dtlsCapability int2 not null,
        enableRollback boolean not null,
        neighborDeadInterval int2 not null,
        primaryCapwapIP varchar(255),
        timeOut int2 not null,
        trapFilterInterval int2 not null,
        udpPort int4 not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table CLOUD_AUTH_CUSTOMER (
        id int8 not null,
        customerId varchar(255),
        password varchar(255),
        userName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table COMPLIANCE_POLICY (
        id int8 not null,
        client8021x int4 not null,
        clientOpen int4 not null,
        clientOpenAuth int4 not null,
        clientPrivatePsk int4 not null,
        clientPsk int4 not null,
        clientWep int4 not null,
        hiveApPing int4 not null,
        hiveApSnmp int4 not null,
        hiveApSsh int4 not null,
        hiveApTelnet int4 not null,
        passwordCapwap boolean not null,
        passwordHive boolean not null,
        passwordHiveap boolean not null,
        passwordSSID boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table CONFIG_TEMPLATE (
        id int8 not null,
        blnBonjourOnly boolean not null,
        blnWirelessRouter boolean not null,
        clientExpireTime8021X int4 not null,
        clientSuppressInterval8021X int4 not null,
        collectionClientAirtime int4 not null,
        collectionClientRxDrop int4 not null,
        collectionClientTxDrop int4 not null,
        collectionClientTxRetry int4 not null,
        collectionIfAirtime int4 not null,
        collectionIfCrc int4 not null,
        collectionIfRxDrop int4 not null,
        collectionIfTxDrop int4 not null,
        collectionIfTxRetry int4 not null,
        collectionInterval int4 not null,
        configName varchar(32) not null,
        defaultFlag boolean not null,
        description varchar(64),
        enableAirTime boolean not null,
        enableEth0LimitDownloadBandwidth boolean not null,
        enableEth0LimitUploadBandwidth boolean not null,
        enableHttpServer boolean not null,
        enableOSDURL boolean not null,
        enableProbe boolean not null,
        enableReportCollection boolean not null,
        enableTVService boolean not null,
        enableUSBLimitDownloadBandwidth boolean not null,
        enableUSBLimitUploadBandwidth boolean not null,
        enabledMapOverride boolean not null,
        eth0LimitDownloadRate int2 not null,
        eth0LimitUploadRate int2 not null,
        probeInterval int4 not null,
        probePassword varchar(64),
        probeRetryCount int4 not null,
        probeRetryInterval int4 not null,
        probeUsername varchar(32),
        slaInterval int4 not null,
        usbLimitDownloadRate int2 not null,
        usbLimitUploadRate int2 not null,
        version timestamp,
        ACCESS_CONSOLE_ID int8,
        AGG0BACK_SERVICE_FILTER_ID int8,
        AGG0_SERVICE_FILTER_ID int8,
        ALG_CONFIGURATION_ID int8,
        appProfileId int8,
        BONJOUR_GATEWAY_ID int8,
        QOS_CLASSIFICATION_ID int8,
        CLIENT_WATCH_ID int8,
        ETH0BACK_SERVICE_FILTER_ID int8,
        ETH0_SERVICE_FILTER_ID int8,
        ETH1BACK_SERVICE_FILTER_ID int8,
        ETH1_SERVICE_FILTER_ID int8,
        FIREWALL_POLICY_ID int8,
        HIVE_PROFILE_ID int8,
        IDS_POLICY_ID int8,
        IP_FILTER_ID int8,
        LLDPCDP_ID int8,
        LOCATION_SERVER_ID int8,
        QOS_MARKING_ID int8,
        MGMT_SERVICE_DNS_ID int8,
        MGMT_SERVICE_OPTION_ID int8,
        MGMT_SERVICE_SNMP_ID int8,
        MGMT_SERVICE_SYSLOG_ID int8,
        MGMT_SERVICE_TIME_ID int8,
        mgt_network_id int8,
        OWNER int8 not null,
        RADIUS_ATTRS_ID int8,
        RADIUS_PROXY_ID int8,
        RADIUS_SERVER_ID int8,
        RED0BACK_SERVICE_FILTER_ID int8,
        RED0_SERVICE_FILTER_ID int8,
        IP_TRACK_ID int8,
        ROUTING_POLICY_ID int8,
        VLAN_ID int8,
        NATIVE_VLAN_ID int8,
        VPN_SERVICE_ID int8,
        WIRE_SERVICE_FILTER_ID int8,
        primary key (id),
        unique (OWNER, configName)
    );

    create table CONFIG_TEMPLATE_IP_TRACK (
        CONFIG_TEMPLATE_ID int8 not null,
        ipTracks_id int8 not null,
        primary key (CONFIG_TEMPLATE_ID, ipTracks_id)
    );

    create table CONFIG_TEMPLATE_LAN (
        CONFIG_TEMPLATE_ID int8 not null,
        lanProfiles_id int8 not null,
        primary key (CONFIG_TEMPLATE_ID, lanProfiles_id)
    );

    create table CONFIG_TEMPLATE_SSID (
        CONFIG_TEMPLATE_ID int8 not null,
        checkD boolean not null,
        checkDT boolean not null,
        checkE boolean not null,
        checkET boolean not null,
        checkP boolean not null,
        checkPT boolean not null,
        interfaceName varchar(255),
        macOuisEnabled boolean not null,
        networkServicesEnabled boolean not null,
        ssidEnabled boolean not null,
        ssidOnlyEnabled boolean not null,
        SSID_PROFILE_ID int8,
        mapkey int8 not null,
        primary key (CONFIG_TEMPLATE_ID, mapkey)
    );

    create table CONFIG_TEMPLATE_STORM_CONTROL (
        id int8 not null,
        allTrafficType boolean not null,
        broadcast boolean not null,
        defaultFlag boolean not null,
        interfaceType varchar(255),
        rateLimitType int8 not null,
        rateLimitValue int8 not null,
        registeredMulticast boolean not null,
        tcpsyn boolean not null,
        unknownUnicast boolean not null,
        unregisteredMulticast boolean not null,
        version timestamp,
        CONFIG_TEMPLATE_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table CONFIG_TEMPLATE_TV_SERVICE (
        CONFIG_TEMPLATE_ID int8 not null,
        tvNetworkService_id int8 not null,
        primary key (CONFIG_TEMPLATE_ID, tvNetworkService_id)
    );

    create table CWP (
        id int8 not null,
        authMethod int4 not null,
        blockRedirectURL varchar(256),
        certificateDN boolean not null,
        comment varchar(64),
        cwpName varchar(32) not null,
        defaultFlag boolean not null,
        dhcpMode int4 not null,
        directoryName varchar(32),
        disableRoamingLogin boolean not null,
        enableUsePolicy boolean not null,
        enabledHTTP302 boolean not null,
        enabledHttps boolean not null,
        enabledNewWin boolean not null,
        enabledPopup boolean not null,
        failureDelay int2 not null,
        failureExternalURL varchar(256),
        failurePageName varchar(32),
        failurePageSource int2 not null,
        failureRedirection int2 not null,
        ipForAMode varchar(32),
        ipForBGMode varchar(32),
        ipForEth0 varchar(32),
        ipForEth1 varchar(32),
        leaseTime int4 not null,
        loginURL varchar(256),
        maskForAMode varchar(32),
        maskForBGMode varchar(32),
        maskForEth0 varchar(32),
        maskForEth1 varchar(32),
        needReassociate boolean not null,
        numberField int4 not null,
        overrideVlan boolean not null,
        backgroundImage varchar(32),
        failureBackgroundImage varchar(32),
        failureFootImage varchar(32),
        failureForegroundColor varchar(32),
        failureHeadImage varchar(32),
        failureLibrarySIP boolean not null,
        failureLibrarySIPFines varchar(256),
        footImage varchar(32),
        foregroundColor varchar(32),
        headImage varchar(32),
        successBackgroundImage varchar(32),
        successFootImage varchar(32),
        successForegroundColor varchar(32),
        successHeadImage varchar(32),
        successLibrarySIP boolean not null,
        successLibrarySIPFines varchar(256),
        successLibrarySIPStatus varchar(256),
        successNotice varchar(256),
        tileBackgroundImage boolean not null,
        tileFailureBackgroundImage boolean not null,
        tileSuccessBackgroundImage boolean not null,
        userPolicy varchar(64),
        passwordEncryption int2 not null,
        ppskServerType int2 not null,
        registrationPeriod int4 not null,
        registrationType int2 not null,
        requestField int4 not null,
        resultPageName varchar(32),
        serverDomainName varchar(32),
        serverType int4 not null,
        sessionAlert int2 not null,
        sharedSecret varchar(128),
        showFailurePage boolean not null,
        showSuccessPage boolean not null,
        successDelay int2 not null,
        successExternalURL varchar(256),
        successPageSource int2 not null,
        successRedirection int2 not null,
        useDefaultNetwork boolean not null,
        useLoginAsFailure boolean not null,
        version timestamp,
        webPageName varchar(32),
        webPageSource int2 not null,
        CERTIFICATE_ID int8,
        OWNER int8 not null,
        VLAN_ID int8,
        primary key (id)
    );

    create table CWP_CERTIFICATE (
        id int8 not null,
        certName varchar(255),
        defaultFlag boolean not null,
        description varchar(255),
        encrypted boolean not null,
        index int4 not null,
        srcCertName varchar(255),
        srcKeyName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table CWP_PAGE_FIELD (
        CWP_PAGE_CUSTOMIZATION_ID int8 not null,
        enabled boolean not null,
        label varchar(255),
        place int2 not null,
        required boolean not null,
        mapkey varchar(255) not null,
        primary key (CWP_PAGE_CUSTOMIZATION_ID, mapkey)
    );

    create table DASHBOARD_COMPONENT_DATA (
        COMPONENT_METRIC_ID int8 not null,
        displayName varchar(255),
        displayValue varchar(255),
        displayValueKey varchar(255),
        enableBreakdown boolean not null,
        enableDisplayTotal boolean not null,
        groupIndex int4 not null,
        levelBreakDown int2 not null,
        positionIndex int4 not null,
        sourceData varchar(255),
        validBreakdown boolean not null,
        POSITION int4 not null,
        primary key (COMPONENT_METRIC_ID, POSITION)
    );

    create table DASHBOARD_COMPONENT_METRIC (
        id int8 not null,
        createTime int8 not null,
        defaultFlg boolean not null,
        homeonly boolean not null,
        metricName varchar(32),
        sourceType varchar(255),
        specifyType int4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table DENY_UPGRADE_EMAIL_SUFFIX (
        id int8 not null,
        emailSuffix varchar(255),
        version timestamp,
        primary key (id)
    );

    create table DEVICE_AUTO_PROVISION_INTERFACE (
        DEVICE_AUTO_PROVISION_ID int8 not null,
        adminState int2 not null,
        enableNat boolean not null,
        interfaceDownstreamBandwidth varchar(255),
        interfacePort int2 not null,
        interfaceRole int2 not null,
        interfaceSpeed int2 not null,
        interfaceTransmissionType int2 not null,
        pseEnabled boolean not null,
        psePriority varchar(255),
        pseState int2 not null,
        POSITION int4 not null,
        primary key (DEVICE_AUTO_PROVISION_ID, POSITION)
    );

    create table DEVICE_AUTO_PROVISION_IPSUBNETWORKS (
        DEVICE_AUTO_PROVISION_ID int8 not null,
        ipSubNetwork varchar(18),
        POSITION int4 not null,
        primary key (DEVICE_AUTO_PROVISION_ID, POSITION)
    );

    create table DEVICE_DA_INFO (
        id int8 not null,
        BDAMac varchar(255),
        DAMac varchar(255),
        PortalMac varchar(255),
        cpuUsage1 int4 not null,
        cpuUsage2 int4 not null,
        freeMem int4 not null,
        macAddress varchar(255),
        totalMem int4 not null,
        usedMem int4 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table DEVICE_INTERFACE_IPSUBNETWORK (
        id int8 not null,
        ipSubNetwork varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table DEVICE_POLICY_RULE (
        USER_PROFILE_ID int8 not null,
        DOMAIN_OBJ_ID int8,
        MAC_OBJ_ID int8,
        OS_OBJ_ID int8,
        ruleId int2 not null,
        userProfileId int8,
        userProfileName varchar(255),
        POSITION int4 not null,
        primary key (USER_PROFILE_ID, POSITION)
    );

    create table DHCP_SERVER_CUSTOM (
        VLAN_DHCP_SERVER_ID int8 not null,
        number int2 not null,
        type int2 not null,
        value varchar(255),
        POSITION int4 not null,
        primary key (VLAN_DHCP_SERVER_ID, POSITION)
    );

    create table DHCP_SERVER_IPPOOL (
        VLAN_DHCP_SERVER_ID int8 not null,
        endIp varchar(15),
        startIp varchar(15),
        POSITION int4 not null,
        primary key (VLAN_DHCP_SERVER_ID, POSITION)
    );

    create table DIRECTORY_OPENLDAP_INFO (
        DIRECTORY_OPENLDAP_ID int8 not null,
        DIRECTORY_OR_LDAP_ID int8 not null,
        serverPriority int2 not null,
        POSITION int4 not null,
        primary key (DIRECTORY_OPENLDAP_ID, POSITION)
    );

    create table DNS_SERVICE_PROFILE (
        id int8 not null,
        description varchar(64),
        externalServerType int4 not null,
        serviceName varchar(32),
        splitDNS boolean not null,
        version timestamp,
        DOMAIN_OBJECT_ID int8,
        EXTERNAL_DNS1_ID int8,
        EXTERNAL_DNS2_ID int8,
        EXTERNAL_DNS3_ID int8,
        INTERNAL_DNS1_ID int8,
        INTERNAL_DNS2_ID int8,
        INTERNAL_DNS3_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table DNS_SPECIFIC_SETTINGS (
        DNS_SERVICE_ID int8 not null,
        specificDNS int8,
        domainName varchar(255),
        POSITION int4 not null,
        primary key (DNS_SERVICE_ID, POSITION)
    );

    create table DOMAIN_NAME_ITEM (
        DOMAIN_OBJECT_ID int8 not null,
        description varchar(64),
        domainName varchar(32),
        POSITION int4 not null,
        primary key (DOMAIN_OBJECT_ID, POSITION)
    );

    create table DOMAIN_OBJECT (
        id int8 not null,
        autoGenerateFlag boolean not null,
        objName varchar(32) not null,
        objType int2,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table DOMAIN_ORDER_KEY_INFO (
        id int8 not null,
        createTime int8 not null,
        domainName varchar(255),
        evalKeyChange int2 not null,
        hoursUsed varchar(255),
        licenseStr varchar(255),
        orderKey varchar(255),
        systemId varchar(255),
        version timestamp,
        primary key (id)
    );

    create table DOS_PREVENTION (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        dosPreventionName varchar(32) not null,
        dosType int4,
        enabledSynCheck boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table DOS_PREVENTION_DOS_PARAMS (
        DOS_PREVENTION_ID int8 not null,
        alarmInterval int4 not null,
        alarmThreshold int4 not null,
        dosAction int4,
        dosActionTime int4 not null,
        enabled boolean not null,
        mapkey varchar(255) not null,
        primary key (DOS_PREVENTION_ID, mapkey)
    );

    create table ETHERNET_ACCESS (
        id int8 not null,
        description varchar(64),
        enableIdle boolean not null,
        ethernetName varchar(32) not null,
        idleTimeout int2 not null,
        macLearning boolean not null,
        version timestamp,
        OWNER int8 not null,
        USER_PROFILE_ID int8,
        primary key (id)
    );

    create table ETHERNET_ACCESS_MAC (
        ETHERNET_ACCESS_ID int8 not null,
        MAC_OR_OUI_ID int8 not null,
        primary key (ETHERNET_ACCESS_ID, MAC_OR_OUI_ID)
    );

    create table FIREWALL_POLICY (
        id int8 not null,
        defRuleAction int2 not null,
        defRuleLog int2 not null,
        description varchar(64),
        policyName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table FIREWALL_POLICY_RULE (
        FIREWALL_POLICY_ID int8 not null,
        actionLog int2 not null,
        destType int2 not null,
        DESTINATION_IP_ID int8,
        DESTINATION_NETWORK_ID int8,
        disableRule boolean not null,
        filterAction int2 not null,
        NETWORK_SERVICE_ID int8,
        ruleId int2 not null,
        SOURCE_IP_ID int8,
        SOURCE_NETWORK_ID int8,
        sourceType int2 not null,
        SOURCE_UP_ID int8,
        POSITION int4 not null,
        primary key (FIREWALL_POLICY_ID, POSITION)
    );

    create table G_RATE_SETTING_INFO (
        SSID_PROFILE_ID int8 not null,
        rateSet int2 not null,
        mapkey varchar(255) not null,
        primary key (SSID_PROFILE_ID, mapkey)
    );

    create table HA_SETTINGS (
        id int8 not null,
        domainName varchar(255),
        enableExternalDb int2 not null,
        enableFailBack boolean not null,
        haNotifyEmail varchar(255),
        haPort int2 not null,
        haSecret varchar(255),
        haStatus int2 not null,
        heartbeatTimeOutValue int4 not null,
        lastSwitchOverTime int8 not null,
        masterHostNameSticky varchar(255),
        primaryDbPwd varchar(255),
        primaryDbUrl varchar(255),
        primaryDefaultGateway varchar(255),
        primaryExternalIPHostname varchar(255),
        primaryHostName varchar(255),
        primaryLANIP varchar(255),
        primaryLANNetmask varchar(255),
        primaryMGTIP varchar(255),
        primaryMGTNetmask varchar(255),
        primarySystemId varchar(255),
        primaryUpTime int8 not null,
        secondaryDbPwd varchar(255),
        secondaryDbUrl varchar(255),
        secondaryDefaultGateway varchar(255),
        secondaryExternalIPHostname varchar(255),
        secondaryHostName varchar(255),
        secondaryLANIP varchar(255),
        secondaryLANNetmask varchar(255),
        secondaryMGTIP varchar(255),
        secondaryMGTNetmask varchar(255),
        secondarySystemId varchar(255),
        secondaryUpTime int8 not null,
        useExternalIPHostname boolean not null,
        version timestamp,
        primary key (id)
    );

    create table HHM_UPGRADE_VERSION_INFO (
        id int8 not null,
        hmVersion varchar(255),
        ipAddress varchar(255),
        leftApCount int4 not null,
        leftVhmCount int4 not null,
        password varchar(255),
        userName varchar(255),
        version timestamp,
        primary key (id)
    );

    create table HIVEAP_DEVICE_INTERFACE (
        HIVEAP_ID int8 not null,
        adminState int2 not null,
        deviceIfType int2 not null,
        duplex int2 not null,
        enableDhcp boolean not null,
        enableMaxDownload boolean not null,
        enableMaxUpload boolean not null,
        enableNat boolean not null,
        gateway varchar(255),
        ifActive boolean not null,
        interfaceName varchar(255),
        ipAddress varchar(255),
        maxDownload int2 not null,
        maxUpload int2 not null,
        netMask varchar(255),
        pseEnabled boolean not null,
        psePriority varchar(255),
        pseState int2 not null,
        role int2 not null,
        speed int2 not null,
        mapkey int8 not null,
        primary key (HIVEAP_ID, mapkey)
    );

    create table HIVEAP_FILTER (
        id int8 not null,
        agg0Bridge boolean not null,
        classificationTag1 varchar(64),
        classificationTag2 varchar(64),
        classificationTag3 varchar(64),
        displayVer varchar(255),
        eth0Bridge boolean not null,
        eth1Bridge boolean not null,
        filterConfiguration int4 not null,
        filterDeviceType int2 not null,
        filterDhcpServer boolean not null,
        filterHive int8,
        filterIp varchar(255),
        filterName varchar(32) not null,
        filterProvision int4 not null,
        filterProvisionFlag boolean not null,
        filterRadiusProxy boolean not null,
        filterRadiusServer boolean not null,
        filterTemplate int8,
        filterTopology int8,
        filterVpnClient boolean not null,
        filterVpnServer boolean not null,
        hiveApModel int2 not null,
        hiveApType int2 not null,
        hostname varchar(255),
        red0Bridge boolean not null,
        userName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table HIVEAP_IMAGE_INFO (
        id int8 not null,
        imageName varchar(64),
        imageUid int4 not null,
        majorVersion varchar(5),
        minorVersion varchar(5),
        patchVersion varchar(5),
        productName varchar(32),
        relVersion varchar(5),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table HIVE_AP (
        id int8 not null,
        IDMProxy boolean not null,
        addressOnly boolean not null,
        adminPassword varchar(32),
        adminUser varchar(20),
        AGG0_ADMIN_STATE int2,
        AGG0_ALLOWED_VLAN varchar(4),
        AGG0_BIND_INTERFACE int2,
        AGG0_BIND_ROLE int2,
        AGG0_DUPLEX int2,
        AGG0_IDEL_TIMEOUT int4,
        AGG0_LEARNING_ENABLED boolean,
        AGG0_MULTINATIVE_VLAN int4,
        AGG0_OPERATION_MODE int2,
        AGG0_SPEED int2,
        capwapClientIp varchar(15),
        capwapLinkIp varchar(15),
        cfgAdminUser varchar(20),
        cfgGateway varchar(15),
        cfgIpAddress varchar(15),
        cfgNetmask varchar(15),
        cfgPassword varchar(32),
        cfgReadOnlyPassword varchar(32),
        cfgReadOnlyUser varchar(20),
        classificationTag1 varchar(64),
        classificationTag2 varchar(64),
        classificationTag3 varchar(64),
        configVer int4 not null,
        connChangedTime int8 not null,
        connectStatus int2 not null,
        connected boolean not null,
        countryCode int4 not null,
        currentDtlsEnable boolean not null,
        currentKeyId int4 not null,
        currentPassPhrase varchar(32),
        customizedNasIdentifier varchar(255),
        delayTime int4 not null,
        deviceType int2 not null,
        dhcp boolean not null,
        dhcpFallback boolean not null,
        dhcpServerCount int4 not null,
        dhcpTimeout int4 not null,
        disconnChangedTime int8 not null,
        discoveryReported boolean not null,
        discoveryTime int8 not null,
        displayVer varchar(64),
        distributedPriority int2 not null,
        enableBrPMTUD boolean not null,
        enableCvgPMTUD boolean not null,
        enableDas boolean not null,
        enableEthBridge boolean not null,
        enableOverrideBrPMTUD boolean not null,
        enablePppoe boolean not null,
        enablePreempt boolean not null,
        enableVRRP boolean not null,
        enabledBrAsPpskServer boolean not null,
        enabledBrAsRadiusServer boolean not null,
        enabledOverrideVoipSetting boolean not null,
        ETH0_ADMIN_STATE int2,
        ETH0_ALLOWED_VLAN varchar(255),
        ETH0_BIND_INTERFACE int2,
        ETH0_BIND_ROLE int2,
        ETH0_DUPLEX int2,
        ETH0_IDEL_TIMEOUT int4,
        ETH0_LEARNING_ENABLED boolean,
        ETH0_MULTINATIVE_VLAN int4,
        ETH0_OPERATION_MODE int2,
        ETH0_SPEED int2,
        eth0DeviceId varchar(255),
        eth0PortId varchar(255),
        eth0SystemId varchar(255),
        ETH1_ADMIN_STATE int2,
        ETH1_ALLOWED_VLAN varchar(255),
        ETH1_BIND_INTERFACE int2,
        ETH1_BIND_ROLE int2,
        ETH1_DUPLEX int2,
        ETH1_IDEL_TIMEOUT int4,
        ETH1_LEARNING_ENABLED boolean,
        ETH1_MULTINATIVE_VLAN int4,
        ETH1_OPERATION_MODE int2,
        ETH1_SPEED int2,
        eth1DeviceId varchar(255),
        eth1PortId varchar(255),
        eth1SystemId varchar(255),
        ethConfigType int2 not null,
        ethCwpActiveTime int4 not null,
        ethCwpAuthMethod int2 not null,
        ethCwpDenyAction int2 not null,
        ethCwpEnableEthCwp boolean not null,
        ethCwpEnableMacAuth boolean not null,
        ethCwpEnableStriction boolean not null,
        ethCwpLimitUserProfiles boolean not null,
        ethLanStatus varchar(255),
        gateway varchar(15),
        hiveApModel int2 not null,
        hiveApType int2 not null,
        hostName varchar(32) not null,
        ipAddress varchar(15),
        isOutdoor boolean,
        keyId int4 not null,
        lastAuditTime int8 not null,
        lastCfgTime int8 not null,
        lastImageTime int8 not null,
        location varchar(255),
        macAddress varchar(12) not null unique,
        manageStatus int2 not null,
        manageUponContact boolean not null,
        maxPowerSource int4 not null,
        metric int2 not null,
        metricInteval int4 not null,
        mgtVlan int4 not null,
        monitorBrMSS boolean not null,
        monitorCvgMSS boolean not null,
        nasIdentifierType int2 not null,
        nativeVlan int4 not null,
        netmask varchar(15),
        origin int2 not null,
        passPhrase varchar(32),
        pending boolean not null,
        pendingIndex int4 not null,
        pendingIndex_user int4 not null,
        pendingMsg varchar(255),
        pendingMsg_user varchar(255),
        pending_user boolean not null,
        pppoeEnableCurrent boolean not null,
        priority varchar(255),
        productName varchar(12),
        provision int4 not null,
        proxyName varchar(64),
        proxyPassword varchar(64),
        proxyPort int4 not null,
        proxyUsername varchar(64),
        radioConfigType int2 not null,
        readOnlyPassword varchar(32),
        readOnlyUser varchar(20),
        RED0_ADMIN_STATE int2,
        RED0_ALLOWED_VLAN varchar(4),
        RED0_BIND_INTERFACE int2,
        RED0_BIND_ROLE int2,
        RED0_DUPLEX int2,
        RED0_IDEL_TIMEOUT int4,
        RED0_LEARNING_ENABLED boolean,
        RED0_MULTINATIVE_VLAN int4,
        RED0_OPERATION_MODE int2,
        RED0_SPEED int2,
        regionCode int4 not null,
        routeInterval int4 not null,
        runningHive varchar(64),
        serialNumber varchar(14),
        severity int2 not null,
        simulateClientInfo varchar(255),
        simulateCode int4 not null,
        simulated boolean not null,
        softVer varchar(16),
        thresholdBrForAllTCP int4 not null,
        thresholdBrThroughVPNTunnel int4 not null,
        thresholdCvgForAllTCP int4 not null,
        thresholdCvgThroughVPNTunnel int4 not null,
        timeZoneOffset int2 not null,
        totalConnectTime int8 not null,
        totalConnectTimes int8 not null,
        transferPort int4 not null,
        transferProtocol int2 not null,
        tunnelThreshold int2 not null,
        upTime int8 not null,
        usbConnectionModel int2 not null,
        version timestamp,
        virtualLanIp varchar(15),
        virtualWanIp varchar(15),
        vpnMark int2 not null,
        vrrpDelay int4 not null,
        vrrpId int4 not null,
        vrrpPriority int4 not null,
        WIFI0_ADMIN_STATE int2,
        WIFI0_RADIO_CHANNEL int4,
        WIFI0_OPERATION_MODE int2,
        WIFI0_RADIO_POWER int4,
        WIFI0_RADIO_MODE int2,
        WIFI1_ADMIN_STATE int2,
        WIFI1_RADIO_CHANNEL int4,
        WIFI1_OPERATION_MODE int2,
        WIFI1_RADIO_POWER int4,
        WIFI1_RADIO_MODE int2,
        AGG0_USER_PROFILE_ID int8,
        CAPWAP_BACKUP_IP_ID int8,
        CAPWAP_IP_ID int8,
        TEMPLATE_ID int8,
        CVG_DNS_ID int8,
        CVG_MGT0_NETWORK_ID int8,
        CVG_NTP_ID int8,
        ETH0_USER_PROFILE_ID int8,
        ETH1_USER_PROFILE_ID int8,
        ETHERNET_CWP_ID int8,
        DEFAULT_ETH_AUTH_USER_PROFILE_ID int8,
        DEFAULT_ETH_REG_USER_PROFILE_ID int8,
        RADIUS_CLIENT_ID int8,
        MAP_CONTAINER_ID int8,
        OWNER int8 not null,
        PPPOE_AUTH_ID int8,
        RADIUS_PROXY_ID int8,
        RADIUS_SERVER_ID int8,
        RED0_USER_PROFILE_ID int8,
        ROUTING_POLICY_ID int8,
        ROUTING_PROFILE_ID int8,
        SCHEDULER_ID int8,
        SECOND_VPN_GATEWAY_ID int8,
        VPN_IP_TRACK_ID int8,
        WIFI0_RADIO_PROFILE_ID int8,
        WIFI1_RADIO_PROFILE_ID int8,
        primary key (id)
    );

    create table HIVE_AP_AUTO_PROVISION (
        id int8 not null,
        accessControled boolean not null,
        aclType int2 not null,
        AGG0_ADMIN_STATE int2,
        AGG0_ALLOWED_VLAN varchar(4),
        AGG0_BIND_INTERFACE int2,
        AGG0_BIND_ROLE int2,
        AGG0_DUPLEX int2,
        AGG0_IDEL_TIMEOUT int4,
        AGG0_LEARNING_ENABLED boolean,
        AGG0_MULTINATIVE_VLAN int4,
        AGG0_OPERATION_MODE int2,
        AGG0_SPEED int2,
        autoProvision boolean not null,
        capwapBackupIpId int8,
        cfgAdminUser varchar(20),
        cfgCapwapIpId int8,
        cfgPassword varchar(32),
        cfgReadOnlyPassword varchar(32),
        cfgReadOnlyUser varchar(20),
        classificationTag1 varchar(64),
        classificationTag2 varchar(64),
        classificationTag3 varchar(64),
        configTemplateId int8,
        countryCode int4 not null,
        description varchar(255),
        deviceType int2 not null,
        enableOneTimePassword boolean not null,
        ETH0_ADMIN_STATE int2,
        ETH0_ALLOWED_VLAN varchar(4),
        ETH0_BIND_INTERFACE int2,
        ETH0_BIND_ROLE int2,
        ETH0_DUPLEX int2,
        ETH0_IDEL_TIMEOUT int4,
        ETH0_LEARNING_ENABLED boolean,
        ETH0_MULTINATIVE_VLAN int4,
        ETH0_OPERATION_MODE int2,
        ETH0_SPEED int2,
        ETH1_ADMIN_STATE int2,
        ETH1_ALLOWED_VLAN varchar(4),
        ETH1_BIND_INTERFACE int2,
        ETH1_BIND_ROLE int2,
        ETH1_DUPLEX int2,
        ETH1_IDEL_TIMEOUT int4,
        ETH1_LEARNING_ENABLED boolean,
        ETH1_MULTINATIVE_VLAN int4,
        ETH1_OPERATION_MODE int2,
        ETH1_SPEED int2,
        imageName varchar(255),
        imageVersion varchar(15),
        mapContainerId int8,
        modelType int2 not null,
        name varchar(32) not null,
        passPhrase varchar(32),
        provisioningName varchar(36),
        rebooting boolean not null,
        RED0_ADMIN_STATE int2,
        RED0_ALLOWED_VLAN varchar(4),
        RED0_BIND_INTERFACE int2,
        RED0_BIND_ROLE int2,
        RED0_DUPLEX int2,
        RED0_IDEL_TIMEOUT int4,
        RED0_LEARNING_ENABLED boolean,
        RED0_MULTINATIVE_VLAN int4,
        RED0_OPERATION_MODE int2,
        RED0_SPEED int2,
        rewriteMap boolean not null,
        uploadConfig boolean not null,
        uploadImage boolean not null,
        usbConnectionModel int2 not null,
        version timestamp,
        WIFI0_ADMIN_STATE int2,
        WIFI0_RADIO_CHANNEL int4,
        WIFI0_OPERATION_MODE int2,
        WIFI0_RADIO_POWER int4,
        WIFI0_RADIO_MODE int2,
        wifi0ProfileId int8,
        WIFI1_ADMIN_STATE int2,
        WIFI1_RADIO_CHANNEL int4,
        WIFI1_OPERATION_MODE int2,
        WIFI1_RADIO_POWER int4,
        WIFI1_RADIO_MODE int2,
        wifi1ProfileId int8,
        OWNER int8 not null,
        primary key (id),
        unique (OWNER, name)
    );

    create table HIVE_AP_AUTO_PROVISION_MACES (
        HIVE_AP_AUTO_PROVISION_ID int8 not null,
        macAddress varchar(14),
        POSITION int4 not null,
        primary key (HIVE_AP_AUTO_PROVISION_ID, POSITION)
    );

    create table HIVE_AP_DHCP_SERVER (
        HIVE_AP_ID int8 not null,
        dhcpServers_id int8 not null,
        primary key (HIVE_AP_ID, dhcpServers_id)
    );

    create table HIVE_AP_DYNAMIC_ROUTE (
        HIVE_AP_ID int8 not null,
        neighborMac varchar(255),
        routeMaximun int4 not null,
        routeMinimun int4 not null,
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_INTERNAL_NETWORK (
        HIVE_AP_ID int8 not null,
        internalNetwork varchar(255),
        netmask varchar(15),
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_IP_ROUTE (
        HIVE_AP_ID int8 not null,
        advertiseCvg boolean not null,
        distributeBR boolean not null,
        gateway varchar(255),
        netmask varchar(255),
        sourceIp varchar(255),
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_L3CFG_NEIGHBOR (
        HIVE_AP_ID int8 not null,
        neighborMac varchar(255),
        neighborType int2 not null,
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_LEARNING_MAC (
        HIVE_AP_ID int8 not null,
        learningMacType int2 not null,
        LEARNING_MAC_ID int8,
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_MULTIPLE_VLAN (
        HIVE_AP_ID int8 not null,
        vlanid varchar(255),
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_SERIAL_NUMBER (
        id int8 not null,
        serialNumber varchar(14) not null unique,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table HIVE_AP_SSID_ALLOCATION (
        HIVE_AP_ID int8 not null,
        interType int2 not null,
        ssid int8,
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_STATIC_ROUTE (
        HIVE_AP_ID int8 not null,
        destinationMac varchar(255),
        interfaceType int2 not null,
        nextHopMac varchar(255),
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_UPDATE_RESULT (
        id int8 not null,
        actionType int2 not null,
        description varchar(1024),
        downloadRate float4 not null,
        finishTime int8 not null,
        hostname varchar(255),
        ipAddress varchar(255),
        nodeId varchar(255),
        result int2 not null,
        stagedTime int4 not null,
        startTime int8 not null,
        state int2 not null,
        tag int4 not null,
        updateType int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table HIVE_AP_UPDATE_RESULT_ITEM (
        HIVE_AP_ID int8 not null,
        actived boolean not null,
        clis varchar(40960),
        continued boolean not null,
        description varchar(512),
        fileSize int4 not null,
        result int2 not null,
        scriptType int2 not null,
        updateType int2 not null,
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_UPDATE_SETTINGS (
        id int8 not null,
        configActivateOffset int8 not null,
        configActivateTime int8 not null,
        configActivateType varchar(255),
        configCertificate boolean not null,
        configConfiguration boolean not null,
        configCwp boolean not null,
        configSelectType varchar(255),
        configUserDatabase boolean not null,
        distributedUpgrades boolean not null,
        imageActivateOffset int8 not null,
        imageActivateTime int8 not null,
        imageActivateType varchar(255),
        imageConnType int2 not null,
        imageSelectType varchar(255),
        imageTimedout int8 not null,
        imageTransfer varchar(255),
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table HIVE_AP_USB_MODEM (
        HIVE_AP_ID int8 not null,
        apn varchar(255),
        dialupNum varchar(255),
        modemName varchar(255),
        osVersion varchar(255),
        password varchar(255),
        userId varchar(255),
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_AP_USER_PROFILE (
        HIVE_AP_ID int8 not null,
        USER_PROFILE_ID int8 not null,
        primary key (HIVE_AP_ID, USER_PROFILE_ID)
    );

    create table HIVE_AP_VIRTUAL_CONNECTION (
        HIVE_AP_ID int8 not null,
        destMac varchar(255),
        forwardAction int2 not null,
        forwardName varchar(255),
        interface_in int2 not null,
        interface_out int2 not null,
        rxMac varchar(255),
        sourceMac varchar(255),
        txMac varchar(255),
        POSITION int4 not null,
        primary key (HIVE_AP_ID, POSITION)
    );

    create table HIVE_PROFILE (
        id int8 not null,
        agg0Priority int4 not null,
        connectionThreshold int2 not null,
        defaultAction int4 not null,
        defaultFlag boolean not null,
        description varchar(64),
        enabledL3Setting boolean not null,
        enabledPassword boolean not null,
        enabledThreshold boolean not null,
        eth0Priority int4 not null,
        eth1Priority int4 not null,
        fragThreshold int4 not null,
        generatePasswordType int4 not null,
        hiveName varchar(32) not null,
        hivePassword varchar(63),
        keepAliveAgeout int4 not null,
        keepAliveInterval int4 not null,
        l3TrafficPort int4 not null,
        neighborTypeAccess boolean not null,
        neighborTypeBack boolean not null,
        pollingInterval int4 not null,
        red0Priority int4 not null,
        rtsThreshold int4 not null,
        updateAgeout int4 not null,
        updateInterval int4 not null,
        version timestamp,
        HIVE_DOS_ID int8,
        OWNER int8 not null,
        STATION_DOS_ID int8,
        primary key (id),
        unique (OWNER, hiveName)
    );

    create table HIVE_PROFILE_MAC_FILTER (
        HIVE_PROFILE_ID int8 not null,
        MAC_FILTER_ID int8 not null,
        primary key (HIVE_PROFILE_ID, MAC_FILTER_ID)
    );

    create table HMOL_UPGRADE_SERVER_INFO (
        versionName varchar(255) not null,
        vhmId varchar(255) not null,
        serverAddress varchar(255),
        serverDomainName varchar(255),
        version timestamp,
        primary key (versionName, vhmId)
    );

    create table HM_ACCESS_CONTROL (
        id int8 not null,
        controlType int2 not null,
        denyBehavior int2 not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table HM_ACCESS_CONTROL_IP (
        HM_ACCESS_CONTROL_ID int8 not null,
        ipAddress varchar(255),
        POSITION int4 not null,
        primary key (HM_ACCESS_CONTROL_ID, POSITION)
    );

    create table HM_ACSPNEIGHBOR (
        id  bigserial not null,
        apMac varchar(255),
        bssid varchar(255),
        channelNumber int4 not null,
        ifIndex int4 not null,
        lastSeen int8 not null,
        neighborMac varchar(255),
        neighborRadioMac varchar(255),
        rssi int2 not null,
        ssid varchar(255),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        txPower int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_ASSOCIATION (
        id  bigserial not null,
        apMac varchar(255),
        apName varchar(64),
        apSerialNumber varchar(14),
        clientAssociateTime int8 not null,
        clientAuthMethod int2 not null,
        clientBSSID varchar(255),
        clientCWPUsed int2 not null,
        clientChannel int4 not null,
        clientEncryptionMethod int2 not null,
        clientHostname varchar(32),
        clientIP varchar(255),
        clientLastRxRate int4 not null,
        clientLastTxRate int4 not null,
        clientLinkUptime int8 not null,
        clientMACProtocol int2 not null,
        clientMac varchar(255),
        clientOsInfo varchar(255),
        clientRSSI int4 not null,
        clientRxAirtime float8 not null,
        clientRxBroadcastFrames int8 not null,
        clientRxDataFrames int8 not null,
        clientRxDataOctets int8 not null,
        clientRxMICFailures int8 not null,
        clientRxMgtFrames int8 not null,
        clientRxMulticastFrames int8 not null,
        clientRxUnicastFrames int8 not null,
        clientSSID varchar(32),
        clientTxAirtime float8 not null,
        clientTxBeDataFrames int8 not null,
        clientTxBgDataFrames int8 not null,
        clientTxBroadcastFrames int8 not null,
        clientTxDataFrames int8 not null,
        clientTxDataOctets int8 not null,
        clientTxMgtFrames int8 not null,
        clientTxMulticastFrames int8 not null,
        clientTxUnicastFrames int8 not null,
        clientTxViDataFrames int8 not null,
        clientTxVoDataFrames int8 not null,
        clientUserProfId int4 not null,
        clientUsername varchar(32),
        clientVLAN int4 not null,
        ifIndex int4 not null,
        ifName varchar(255),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        userProfileName varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_AUDITLOG (
        id int8 not null,
        hostIP varchar(15),
        logTimeStamp int8 not null,
        logTimeZone varchar(255),
        opeationComment varchar(256),
        status int2 not null,
        userOwner varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_AUTOREFRESH_SETTINGS (
        HM_USER_ID int8 not null,
        autoRefresh boolean not null,
        refInterval varchar(255),
        tableId int4 not null,
        POSITION int4 not null,
        primary key (HM_USER_ID, POSITION)
    );

    create table HM_BANDWIDTHSENTINEL_HISTORY (
        id  bigserial not null,
        action int4 not null,
        actualBandWidth int4 not null,
        apMac varchar(255),
        apName varchar(255),
        bandWidthSentinelStatus int4 not null,
        channelUltil int2 not null,
        clientMac varchar(255),
        guaranteedBandWidth int4 not null,
        ifIndex int4 not null,
        interferenceUltil int2 not null,
        rxUltil int2 not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        txUltil int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_CUSTOM_REPORT (
        id int8 not null,
        apName varchar(255),
        authHostName varchar(255),
        authIp varchar(255),
        authMac varchar(255),
        authUserName varchar(255),
        defaultFlag boolean not null,
        description varchar(255),
        interfaceRole int4 not null,
        longSortBy int8,
        name varchar(32) not null,
        reportDetailType int4 not null,
        reportPeriod int4 not null,
        reportType int4 not null,
        sortByType int4 not null,
        ssidName varchar(255),
        LOCATION_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_CUSTOM_REPORT_FIELD (
        id int8 not null,
        description varchar(255),
        detailType int4 not null,
        fieldString varchar(255),
        strUnit varchar(255),
        tableField varchar(255),
        tableName varchar(255),
        type int4 not null,
        primary key (id)
    );

    create table HM_CUSTOM_REPORT_FIELD_TABLE (
        CUSTOM_REPORT_ID int8 not null,
        CUSTOM_REPORT_FIELD_ID int8 not null,
        POSITION int4 not null,
        primary key (CUSTOM_REPORT_ID, POSITION)
    );

    create table HM_DASHBOARD (
        id int8 not null,
        active boolean not null,
        bgRollup boolean not null,
        customEndTime int8 not null,
        customStartTime int8 not null,
        daType int4 not null,
        dashName varchar(255),
        defaultFlag boolean not null,
        enableTimeLocal boolean not null,
        filterObjectId varchar(255),
        filterObjectType varchar(255),
        objectId varchar(255),
        objectType varchar(255),
        reCmTimePeriod int4 not null,
        reCmTimeStartDayType int4 not null,
        reCmTimeStartDayValue int4 not null,
        reCmTimeStartMontyYear int4 not null,
        reCmTimeStartSepcYear int4 not null,
        reCmTimeType int4 not null,
        reCustomDay boolean not null,
        reCustomDayValue varchar(255),
        reCustomTime boolean not null,
        reCustomTimeEnd int4 not null,
        reCustomTimeStart int4 not null,
        reEmailAddress varchar(255),
        reEnabledScheduleCheckbox boolean not null,
        reWeekStart int4 not null,
        refrequency int4 not null,
        selectTimeType int4 not null,
        userName varchar(255),
        LOCATION_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_DASHBOARD_APPAP (
        id int8 not null,
        apMac varchar(32),
        dashId int8,
        timestamp int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_DASHBOARD_COMPONENT (
        id int8 not null,
        chartInverted boolean not null,
        chartType varchar(255),
        componentName varchar(32),
        componentType varchar(255),
        createTime int8 not null,
        customHtml varchar(5120),
        defaultFlg boolean not null,
        displayName varchar(64),
        displayValue varchar(64),
        displayValueKey varchar(255),
        enableExampleData boolean not null,
        enabledHtml boolean not null,
        homeonly boolean not null,
        sourceType varchar(64),
        specifyName varchar(255),
        specifyType int4 not null,
        METRIC_ID int8,
        DCOMPONENT_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_DASHBOARD_LAYOUT (
        id int8 not null,
        itemOrder int2 not null,
        sizeType int2 not null,
        tabId varchar(255),
        width float8 not null,
        dashboard_id int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_DASHBOARD_WIDGET (
        id int8 not null,
        blnChecked boolean not null,
        blnDdSpecialType boolean not null,
        blnOverTime boolean not null,
        chartHeight float8 not null,
        customEndTime int8 not null,
        customStartTime int8 not null,
        enableTimeLocal boolean not null,
        filterObjectId varchar(255),
        filterObjectType varchar(255),
        itemOrder int4 not null,
        mainTitle varchar(64),
        objectId varchar(255),
        objectType varchar(255),
        reportId int8,
        selectTimeType int4 not null,
        sizeType int2 not null,
        width float8 not null,
        da_layout_id int8,
        LOCATION_ID int8,
        OWNER int8 not null,
        widget_config_id int8,
        primary key (id)
    );

    create table HM_DOMAIN (
        id int8 not null,
        accessChanged boolean not null,
        accessMode int2 not null,
        authorizationEndDate int8,
        bccEmail varchar(255),
        comment varchar(64),
        domainName varchar(32) not null unique,
        maxApNum int4 not null,
        maxSimuAp int4 not null,
        maxSimuClient int4 not null,
        partnerId varchar(255),
        runStatus int4 not null,
        supportFullMode boolean not null,
        supportGM boolean not null,
        timeZone varchar(255),
        version timestamp,
        vhmID varchar(10),
        primary key (id)
    );

    create table HM_EXPRESSMODE_ENABLE (
        id int8 not null,
        expressModeEnable boolean not null,
        version timestamp,
        primary key (id)
    );

    create table HM_FEATURE_PERMISSION (
        HM_USER_GROUP_ID int8 not null,
        label varchar(255),
        operations int2 not null,
        mapkey varchar(255) not null,
        primary key (HM_USER_GROUP_ID, mapkey)
    );

    create table HM_INSTANCE_PERMISSION (
        HM_USER_GROUP_ID int8 not null,
        label varchar(255),
        operations int2 not null,
        mapkey int8 not null,
        primary key (HM_USER_GROUP_ID, mapkey)
    );

    create table HM_INTERFERENCESTATS (
        id  bigserial not null,
        apMac varchar(255),
        apName varchar(255),
        averageInterferenceCU int2 not null,
        averageNoiseFloor int2 not null,
        averageRXCU int2 not null,
        averageTXCU int2 not null,
        channelNumber int2 not null,
        crcError int2 not null,
        crcErrorRateThreshold int2 not null,
        ifIndex int4 not null,
        ifName varchar(255),
        interferenceCUThreshold int2 not null,
        severity int2 not null,
        shortTermInterferenceCU int2 not null,
        shortTermNoiseFloor int2 not null,
        shortTermRXCU int2 not null,
        shortTermTXCU int2 not null,
        snapShotInterferenceCU int2 not null,
        snapShotNoiseFloor int2 not null,
        snapShotRXCU int2 not null,
        snapShotTXCU int2 not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_L3FIREWALLLOG (
        id int8 not null,
        action varchar(255),
        clientIp varchar(15),
        destination varchar(255),
        device varchar(255),
        operationTimeStamp int8 not null,
        operationTimeZone varchar(255),
        service varchar(255),
        source varchar(255),
        username varchar(255),
        version timestamp,
        OWNER int8,
        primary key (id)
    );

    create table HM_LATESTACSPNEIGHBOR (
        id  bigserial not null,
        apMac varchar(255),
        bssid varchar(255),
        channelNumber int4 not null,
        ifIndex int4 not null,
        lastSeen int8 not null,
        neighborMac varchar(255),
        neighborRadioMac varchar(255),
        rssi int2 not null,
        ssid varchar(255),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        txPower int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_LATESTINTERFERENCESTATS (
        id  bigserial not null,
        apMac varchar(255),
        averageInterferenceCU int2 not null,
        averageNoiseFloor int2 not null,
        averageRXCU int2 not null,
        averageTXCU int2 not null,
        channelNumber int2 not null,
        crcError int2 not null,
        crcErrorRateThreshold int2 not null,
        ifIndex int4 not null,
        interferenceCUThreshold int2 not null,
        severity int2 not null,
        shortTermInterferenceCU int2 not null,
        shortTermNoiseFloor int2 not null,
        shortTermRXCU int2 not null,
        shortTermTXCU int2 not null,
        snapShotInterferenceCU int2 not null,
        snapShotNoiseFloor int2 not null,
        snapShotRXCU int2 not null,
        snapShotTXCU int2 not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_LATESTNEIGHBOR (
        id  bigserial not null,
        apMac varchar(255) not null,
        apName varchar(64) not null,
        apSerialNumber varchar(14),
        ifIndex int4 not null,
        linkCost int8 not null,
        linkType int2 not null,
        linkUpTime int8 not null,
        neighborAPID varchar(48) not null,
        rssi int4 not null,
        rxBroadcastFrames int8 not null,
        rxDataFrames int8 not null,
        rxDataOctets int8 not null,
        rxMgtFrames int8 not null,
        rxMulticastFrames int8 not null,
        rxUnicastFrames int8 not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        txBeDataFrames int8 not null,
        txBgDataFrames int8 not null,
        txBroadcastFrames int8 not null,
        txDataFrames int8 not null,
        txDataOctets int8 not null,
        txMgtFrames int8 not null,
        txMulticastFrames int8 not null,
        txUnicastFrames int8 not null,
        txViDataFrames int8 not null,
        txVoDataFrames int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_LATESTRADIOATTRIBUTE (
        id  bigserial not null,
        apMac varchar(255) not null,
        apName varchar(64) not null,
        apSerialNumber varchar(14),
        beaconInterval int4 not null,
        eirp float4 not null,
        ifIndex int4 not null,
        radioChannel int8 not null,
        radioNoiseFloor int8 not null,
        radioTxPower int8 not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_LATESTXIF (
        id  bigserial not null,
        apMac varchar(255) not null,
        apName varchar(64) not null,
        apSerialNumber varchar(14),
        bssid varchar(255),
        ifAdminStatus int2 not null,
        ifConfMode int2 not null,
        ifIndex int4 not null,
        ifMode int2 not null,
        ifName varchar(32),
        ifOperStatus int2 not null,
        ifPromiscuous int2 not null,
        ifType int2 not null,
        ssidName varchar(32),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_LLDP_INFORMATION (
        id int8 not null,
        deviceID varchar(255),
        ifIndex int4 not null,
        ifName varchar(255),
        poePower int4 not null,
        portID varchar(255),
        reporter varchar(255) not null,
        systemName varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id),
        unique (ifIndex, reporter)
    );

    create table HM_LOGIN_AUTHENTICATION (
        id int8 not null,
        authType int4 not null,
        hmAdminAuth int2 not null,
        version timestamp,
        OWNER int8 not null unique,
        RADIUS_SERVICE_ASSIGN_ID int8,
        primary key (id)
    );

    create table HM_NEIGHBOR (
        id  bigserial not null,
        apMac varchar(255),
        apName varchar(64),
        apSerialNumber varchar(14),
        ifIndex int4 not null,
        linkCost int8 not null,
        linkType int2 not null,
        linkUpTime int8 not null,
        neighborAPID varchar(48),
        rssi int4 not null,
        rxBroadcastFrames int8 not null,
        rxDataFrames int8 not null,
        rxDataOctets int8 not null,
        rxMgtFrames int8 not null,
        rxMulticastFrames int8 not null,
        rxUnicastFrames int8 not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        txBeDataFrames int8 not null,
        txBgDataFrames int8 not null,
        txBroadcastFrames int8 not null,
        txDataFrames int8 not null,
        txDataOctets int8 not null,
        txMgtFrames int8 not null,
        txMulticastFrames int8 not null,
        txUnicastFrames int8 not null,
        txViDataFrames int8 not null,
        txVoDataFrames int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_NEW_REPORT (
        id int8 not null,
        customDay boolean not null,
        customDayValue varchar(255),
        customTime boolean not null,
        customTimeEnd int4 not null,
        customTimeStart int4 not null,
        defaultFlag boolean not null,
        description varchar(64),
        emailAddress varchar(255),
        endTime int8 not null,
        excuteType varchar(255),
        frequency int4 not null,
        name varchar(32) not null,
        reportPeriod int4 not null,
        reportType int4 not null,
        ssidName varchar(255),
        startTime int8 not null,
        LOCATION_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_NTP_SERVER_INTERVAL (
        id int8 not null,
        ntpInterval int4 not null,
        ntpServer varchar(32),
        timeType int2 not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table HM_PCIDATA (
        id  bigserial not null,
        alertCode int2 not null,
        destObject varchar(255),
        mapID int8,
        nodeID varchar(255),
        reportSystem varchar(255),
        reportTime int8 not null,
        srcObject varchar(255),
        violationCounter int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_RADIOATTRIBUTE (
        id  bigserial not null,
        apMac varchar(12),
        apSerialNumber varchar(14),
        beaconInterval int4 not null,
        eirp real default 0,
        radioChannel int8 not null,
        radioNoiseFloor int8 not null,
        radioTxPower int8 not null,
        apName varchar(64) not null,
        ifIndex int4 not null,
        statTimeStamp int8 not null,
        OWNER int8 not null,
        primary key (id),
        unique (statTimeStamp, apName, ifIndex)
    );

    create table HM_RADIOSTATS (
        id  bigserial not null,
        apMac varchar(255),
        apSerialNumber varchar(14),
        bandWidth int4 not null,
        radioRxAirtime float8 not null,
        radioRxBroadcastDataFrames int8 not null,
        radioRxMgtFrames int8 not null,
        radioRxMulticastDataFrames int8 not null,
        radioRxTotalDataFrames int8 not null,
        radioRxTotalFrameDropped int8 not null,
        radioRxUnicastDataFrames int8 not null,
        radioTXRTSFailures int8 not null,
        radioTxAirtime float8 not null,
        radioTxBeDataFrames int8 not null,
        radioTxBeaconFrames int8 not null,
        radioTxBgDataFrames int8 not null,
        radioTxBroadcastDataFrames int8 not null,
        radioTxDataFrames int8 not null,
        radioTxFEForExcessiveHWRetries int8 not null,
        radioTxMulticastDataFrames int8 not null,
        radioTxNonBeaconMgtFrames int8 not null,
        radioTxTotalFrameErrors int8 not null,
        radioTxTotalFramesDropped int8 not null,
        radioTxTotalRetries int8 not null,
        radioTxUnicastDataFrames int8 not null,
        radioTxViDataFrames int8 not null,
        radioTxVoDataFrames int8 not null,
        apName varchar(64) not null,
        ifIndex int4 not null,
        statTimeStamp int8 not null,
        OWNER int8 not null,
        primary key (id),
        unique (statTimeStamp, apName, ifIndex)
    );

    create table HM_REPORT (
        id int8 not null,
        apName varchar(255),
        authHostName varchar(255),
        authIp varchar(255),
        authMac varchar(255),
        authType int4 not null,
        authUserName varchar(255),
        complianceType int4 not null,
        defaultFlag boolean not null,
        detailDomainName varchar(255),
        emailAddress varchar(255),
        enabledEmail boolean not null,
        enabledRecurrence boolean not null,
        excuteType varchar(255),
        name varchar(32) not null,
        newOldFlg int4 not null,
        recurrenceType varchar(255),
        reportPeriod int4 not null,
        reportType varchar(255),
        role int4 not null,
        ssidName varchar(255),
        startTime int8 not null,
        timeAggregation int4 not null,
        weekDay int4 not null,
        LOCATION_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_DATE (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_DATE_AP (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_HOUR (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timeLocal int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_MONTH (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_MONTH_AP (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_WEEK (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APCPUMEM_WEEK_AP (
        id  bigserial not null,
        apmac varchar(255),
        averageCpu int8 not null,
        averageMem int8 not null,
        collectPeriod int8 not null,
        maxCpu int8 not null,
        maxMem int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_DATE (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_DATE_AP (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_HOUR (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timeLocal int8 not null,
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_MONTH (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_MONTH_AP (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_WEEK (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_APP_DATA_WEEK_AP (
        id  bigserial not null,
        apmac varchar(255),
        application int2 not null,
        clientmac varchar(20),
        hostname varchar(128),
        osname varchar(64),
        owner int8 not null,
        rx24bytecount int8 not null,
        rx24framecount int8 not null,
        rx5bytecount int8 not null,
        rx5framecount int8 not null,
        rxwiredbytecount int8 not null,
        rxwiredframecount int8 not null,
        seconds int8 not null,
        ssid varchar(32),
        timestamp int8 not null,
        tx24bytecount int8 not null,
        tx24framecount int8 not null,
        tx5bytecount int8 not null,
        tx5framecount int8 not null,
        txwiredbytecount int8 not null,
        txwiredframecount int8 not null,
        userProfileName varchar(32),
        username varchar(255),
        vlan int8 not null,
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_DATE (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_DATE_AP (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_HOUR (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timeLocal int8 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_MONTH (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_MONTH_AP (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_WEEK (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_CLIENT_DATA_WEEK_AP (
        id  bigserial not null,
        apmac varchar(255),
        averageSNR int4 not null,
        clientmac varchar(255),
        collectperiod int8,
        osname varchar(255),
        owner int8,
        rssi int4 not null,
        rx24framebytecount int8,
        rx5framebytecount int8,
        rxAirTime int2 not null,
        timestamp int8,
        tx24framebytecount int8,
        tx5framebytecount int8,
        txAirTime int2 not null,
        userProfileName varchar(32),
        username varchar(255),
        vendor varchar(255),
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_DATE (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_DATE_AP (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_HOUR (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_MONTH (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_MONTH_AP (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_WEEK (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NETWORK_INTERFACE_WEEK_AP (
        id  bigserial not null,
        Interference0Utilization int8 not null,
        apmac varchar(255),
        bcastRx0FrameCount int8 not null,
        bcastRx1FrameCount int8 not null,
        bcastTx0FrameCount int8 not null,
        bcastTx1FrameCount int8 not null,
        collectperiod int8 not null,
        crc0errorframe int8 not null,
        crc1errorframe int8 not null,
        interference1Utilization int8 not null,
        owner int8,
        rx0ByteCount int8 not null,
        rx0Utilization int8 not null,
        rx0airtime int8 not null,
        rx0dropframebysw int8 not null,
        rx0framecount int8 not null,
        rx0retryframe int8 not null,
        rx1ByteCount int8 not null,
        rx1Utilization int8 not null,
        rx1airtime int8 not null,
        rx1dropframebysw int8 not null,
        rx1framecount int8 not null,
        rx1retryframe int8 not null,
        rx24framebytecount int8 not null,
        rx5framebytecount int8 not null,
        timeLocal int8,
        timestamp int8,
        totalChannelUtilization int8 not null,
        totalCount int8,
        tx0ByteCount int8 not null,
        tx0Utilization int8 not null,
        tx0airtime int8 not null,
        tx0dropframebyhw int8 not null,
        tx0dropframebysw int8 not null,
        tx0framecount int8 not null,
        tx0retryframe int8 not null,
        tx1ByteCount int8 not null,
        tx1Utilization int8 not null,
        tx1airtime int8 not null,
        tx1dropframebyhw int8 not null,
        tx1dropframebysw int8 not null,
        tx1framecount int8 not null,
        tx1retryframe int8 not null,
        tx24framebytecount int8 not null,
        tx5framebytecount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_DATE (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_DATE_AP (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_HOUR (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeLocal int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_MONTH (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_MONTH_AP (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_WEEK (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_NEWSLA_STATS_WEEK_AP (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCount int8 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        downcount int8 not null,
        owner int8 not null,
        timeStamp int8 not null,
        upcount int8 not null,
        primary key (id)
    );

    create table HM_REPO_ROLLUP_RECORD (
        id  bigserial not null,
        checkTimes int4,
        domainID int4,
        granularity int4,
        lastChangedTime int8,
        metrixGroup varchar(255),
        owner int8,
        result int4,
        rollupMode int4 not null,
        timeZone int4,
        timestamp int8,
        primary key (id)
    );

    create table HM_SPECTRAL_ANALYSIS (
        id  bigserial not null,
        apMac varchar(12) not null,
        apName varchar(255),
        channel0 varchar(255),
        channel1 varchar(255),
        dataFile varchar(255),
        interf int2 not null,
        interval int2 not null,
        runTime int8 not null,
        timeStamp int8 not null,
        timeZone varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_START_CONFIG (
        id int8 not null,
        adminUserLogin boolean not null,
        hiveApPassword varchar(255),
        idmPolicyCreatePanelPopUpFlag boolean not null,
        ledBrightness int2 not null,
        modeType int2 not null,
        networkName varchar(255),
        useAccessConsole boolean not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table HM_SUMMARY_PAGE (
        id int8 not null,
        attribute varchar(2048),
        ckwidgetAPalarm boolean not null,
        ckwidgetAPbandwidth boolean not null,
        ckwidgetAPcompliance boolean not null,
        ckwidgetAPhealth boolean not null,
        ckwidgetAPmostBandwidth boolean not null,
        ckwidgetAPmostClientCount boolean not null,
        ckwidgetAPmostCrcError boolean not null,
        ckwidgetAPmostInterference boolean not null,
        ckwidgetAPmostRxRetry boolean not null,
        ckwidgetAPmostTxRetry boolean not null,
        ckwidgetAPsecurity boolean not null,
        ckwidgetAPsla boolean not null,
        ckwidgetAPuptime boolean not null,
        ckwidgetAPversion boolean not null,
        ckwidgetActiveUser boolean not null,
        ckwidgetAuditLog boolean not null,
        ckwidgetCinfo boolean not null,
        ckwidgetCmostFailure boolean not null,
        ckwidgetCmostRxAirtime boolean not null,
        ckwidgetCmostTxAirtime boolean not null,
        ckwidgetCradio boolean not null,
        ckwidgetCsla boolean not null,
        ckwidgetCuserprofile boolean not null,
        ckwidgetCvendor boolean not null,
        ckwidgetScpu boolean not null,
        ckwidgetSinfo boolean not null,
        ckwidgetSperformanceInfo boolean not null,
        ckwidgetSuser boolean not null,
        userName varchar(128) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_SYSTEMLOG (
        id int8 not null,
        level int2 not null,
        logTimeStamp int8 not null,
        logTimeZone varchar(255),
        source varchar(64),
        systemComment varchar(512),
        version timestamp,
        OWNER int8,
        primary key (id)
    );

    create table HM_TABLE_COLUMN (
        HM_USER_ID int8 not null,
        columnId int4 not null,
        tableId int4 not null,
        POSITION int4 not null,
        primary key (HM_USER_ID, POSITION)
    );

    create table HM_TABLE_SIZE (
        HM_USER_ID int8 not null,
        tableId int4 not null,
        tableSize int4 not null,
        POSITION int4 not null,
        primary key (HM_USER_ID, POSITION)
    );

    create table HM_UPGRADE_LOG (
        id int8 not null,
        annotation varchar(512),
        formerContent varchar(1024),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        postContent varchar(512),
        recommendAction varchar(512),
        type int4 not null,
        version timestamp,
        OWNER int8,
        primary key (id)
    );

    create table HM_USER (
        id int8 not null,
        accessMyhive boolean not null,
        defapplication int2 not null,
        defaultFlag boolean not null,
        emailAddress varchar(128) not null unique,
        endUserLicAgree boolean not null,
        maxAPNum int4 not null,
        navCustomization INT default 16 not null,
        orderFolders boolean not null,
        password varchar(255),
        promptChanges boolean not null,
        syncResult int2 not null,
        timeZone varchar(255),
        treeWidth int2 not null,
        userFullName varchar(128),
        userName varchar(128) not null,
        version timestamp,
        OWNER int8 not null,
        GROUP_ID int8 not null,
        primary key (id)
    );

    create table HM_USER_GROUP (
        id int8 not null,
        defaultFlag boolean not null,
        groupAttribute int4 not null,
        groupName varchar(32),
        helpURL varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_USER_REPORT (
        id int8 not null,
        apName varchar(255),
        authHostName varchar(255),
        authIp varchar(255),
        authMac varchar(255),
        authUserName varchar(255),
        defaultFlag boolean not null,
        description varchar(255),
        name varchar(32) not null,
        reportPeriod int4 not null,
        reportType varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_VIFSTATS (
        id  bigserial not null,
        apMac varchar(255),
        apSerialNumber varchar(14),
        rxVIfBroadcastDataFrames int8 not null,
        rxVIfDataFrames int8 not null,
        rxVIfDroppedFrames int8 not null,
        rxVIfErrorFrames int8 not null,
        rxVIfMulticastDataFrames int8 not null,
        rxVIfUnicastDataFrames int8 not null,
        rxVifAirtime float8 not null,
        txVIfBeDataFrames int8 not null,
        txVIfBgDataFrames int8 not null,
        txVIfBroadcastDataFrames int8 not null,
        txVIfDataFrames int8 not null,
        txVIfDroppedFrames int8 not null,
        txVIfErrorFrames int8 not null,
        txVIfMulticastDataFrames int8 not null,
        txVIfUnicastDataFrames int8 not null,
        txVIfViDataFrames int8 not null,
        txVIfVoDataFrames int8 not null,
        txVifAirtime float8 not null,
        apName varchar(64) not null,
        ifIndex int4 not null,
        statTimeStamp int8 not null,
        OWNER int8 not null,
        primary key (id),
        unique (statTimeStamp, apName, ifIndex)
    );

    create table HM_VPNSTATUS (
        id int8 not null,
        clientID varchar(255),
        connectTimeStamp int8 not null,
        serverID varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table HM_XIF (
        id  bigserial not null,
        apMac varchar(255),
        apSerialNumber varchar(14),
        bssid varchar(255),
        ifAdminStatus int2 not null,
        ifConfMode int2 not null,
        ifMode int2 not null,
        ifName varchar(32),
        ifOperStatus int2 not null,
        ifPromiscuous int2 not null,
        ifType int2 not null,
        ssidName varchar(32),
        apName varchar(64) not null,
        ifIndex int4 not null,
        statTimeStamp int8 not null,
        OWNER int8 not null,
        primary key (id),
        unique (statTimeStamp, apName, ifIndex)
    );

    create table IDP (
        id  bigserial not null,
        channel int2 not null,
        compliance int2 not null,
        idpType int2 not null,
        ifIndex int2 not null,
        ifMacAddress varchar(12) not null,
        inNetworkFlag int2 not null,
        isManaged boolean not null,
        mapId int8,
        mitigated boolean not null,
        parentBssid varchar(255),
        reportNodeId varchar(12) not null,
        TIME int8 not null,
        TIME_ZONE varchar(255),
        rssi int2 not null,
        simulated boolean not null,
        ssid varchar(255),
        stationData int2 not null,
        stationType int2 not null,
        x float8 not null,
        y float8 not null,
        OWNER int8 not null,
        primary key (id),
        unique (reportNodeId, ifMacAddress)
    );

    create table IDP_AP (
        IDP_ID int8 not null,
        ifName varchar(32),
        mitiMac varchar(12) not null,
        POSITION int4 not null,
        primary key (IDP_ID, POSITION)
    );

    create table IDP_ENCLOSED_FRIENDLY_AP (
        IDP_SETTING_ID int8 not null,
        bssid varchar(12),
        POSITION int4 not null,
        primary key (IDP_SETTING_ID, POSITION)
    );

    create table IDP_ENCLOSED_ROGUE_AP (
        IDP_SETTING_ID int8 not null,
        bssid varchar(12),
        POSITION int4 not null,
        primary key (IDP_SETTING_ID, POSITION)
    );

    create table IDP_SETTINGS (
        id int8 not null,
        filterManagedHiveAPBssid boolean not null,
        interval int4 not null,
        threshold int4 not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table IDS_POLICY (
        id int8 not null,
        deAuthTime int4 not null,
        defaultFlag boolean not null,
        description varchar(64),
        detectorAps int4 not null,
        inNetworkEnable boolean not null,
        inSameNetwork boolean not null,
        mitigateDuration int4 not null,
        mitigatePeriod int4 not null,
        mitigateQuiet int4 not null,
        mitigationMode int2 not null,
        networkDetectionEnable boolean not null,
        ouiEnable boolean not null,
        policyName varchar(32) not null,
        rogueDetectionEnable boolean not null,
        shortBeanchIntervalEnable boolean not null,
        shortPreambleEnable boolean not null,
        ssidEnable boolean not null,
        staReportAgeout int4 not null,
        staReportDuration int4 not null,
        staReportEnabled boolean not null,
        staReportInterval int4 not null,
        staReportPeriod int4 not null,
        version timestamp,
        wmmEnable boolean not null,
        OWNER int8 not null,
        primary key (id),
        unique (OWNER, policyName)
    );

    create table IDS_POLICY_MAC_OR_OUI (
        IDS_POLICY_ID int8 not null,
        MAC_OR_OUI_ID int8 not null,
        primary key (IDS_POLICY_ID, MAC_OR_OUI_ID)
    );

    create table IDS_POLICY_SSID_PROFILE (
        IDS_POLICY_ID int8 not null,
        encryptionEnable boolean not null,
        encryptionType int4 not null,
        SSID_PROFILE_ID int8,
        POSITION int4 not null,
        primary key (IDS_POLICY_ID, POSITION)
    );

    create table IDS_POLICY_VLAN (
        IDS_POLICY_ID int8 not null,
        VLAN_ID int8 not null,
        primary key (IDS_POLICY_ID, VLAN_ID)
    );

    create table INTER_ROAMING (
        id int8 not null,
        description varchar(64),
        enabledL3Setting boolean not null,
        keepAliveAgeout int4 not null,
        keepAliveInterval int4 not null,
        roamingName varchar(32) not null,
        updateAgeout int4 not null,
        updateInterval int4 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table IP_ADDRESS (
        id int8 not null,
        addressName varchar(32) not null,
        defaultFlag boolean not null,
        typeFlag int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table IP_ADDRESS_ITEM (
        IP_ADDRESS_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (IP_ADDRESS_ID, POSITION)
    );

    create table IP_FILTER (
        id int8 not null,
        description varchar(64),
        filterName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table IP_FILTER_IP_ADDRESS (
        IP_FILTER_ID int8 not null,
        IP_ADDRESS_ID int8 not null,
        primary key (IP_FILTER_ID, IP_ADDRESS_ID)
    );

    create table IP_POLICY (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        policyName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table IP_POLICY_RULE (
        IP_POLICY_ID int8 not null,
        actionLog int2 not null,
        DESTINATION_IP_ID int8,
        filterAction int2 not null,
        NETWORK_SERVICE_ID int8,
        ruleId int2 not null,
        SOURCE_IP_ID int8,
        POSITION int4 not null,
        primary key (IP_POLICY_ID, POSITION)
    );

    create table LAN_PROFILE (
        id int8 not null,
        actionTime int8 not null,
        assignUserProfileAttributeId int4 not null,
        assignUserProfileVenderId int4 not null,
        authProtocol int4 not null,
        authSequence int4 not null,
        chkDeauthenticate boolean not null,
        chkUserOnly boolean not null,
        cwpSelectEnabled boolean not null,
        denyAction int2 not null,
        description varchar(255),
        enableAssignUserProfile boolean not null,
        enableOsDection boolean not null,
        enabled8021Q boolean not null,
        enabled8021X boolean not null,
        ETH0_ON boolean,
        ETH1_ON boolean,
        ETH2_ON boolean,
        ETH3_ON boolean,
        ETH4_ON boolean,
        macAuthEnabled boolean not null,
        name varchar(255) not null,
        userProfileAttributeType int2 not null,
        version timestamp,
        CWP_ID int8,
        IP_DOS_ID int8,
        NATIVE_NETWORK_ID int8,
        NATIVE_VLAN_ID int8,
        OWNER int8 not null,
        RADIUS_SERVICE_ASSIGN_ID int8,
        SERVICE_FILTER_ID int8,
        USERPROFILE_DEFAULT_ID int8,
        USERPROFILE_SELFREG_ID int8,
        primary key (id)
    );

    create table LAN_PROFILE_REGULAR_NETWORKS (
        LAN_PROFILE_ID int8 not null,
        NETWORKS_ID int8 not null,
        primary key (LAN_PROFILE_ID, NETWORKS_ID)
    );

    create table LAN_PROFILE_REGULAR_VLAN (
        LAN_PROFILE_ID int8 not null,
        VLAN_ID int8 not null,
        primary key (LAN_PROFILE_ID, VLAN_ID)
    );

    create table LAN_PROFILE_SCHEDULER (
        LAN_PROFILE_ID int8 not null,
        SCHEDULER_ID int8 not null,
        primary key (LAN_PROFILE_ID, SCHEDULER_ID)
    );

    create table LAN_PROFILE_USER_PROFILE (
        LAN_PROFILE_ID int8 not null,
        USER_PROFILE_ID int8 not null,
        primary key (LAN_PROFILE_ID, USER_PROFILE_ID)
    );

    create table LAN_RADIUS_USER_GROUP (
        LAN_PROFILE_ID int8 not null,
        LOCAL_USER_GROUP_ID int8 not null,
        primary key (LAN_PROFILE_ID, LOCAL_USER_GROUP_ID)
    );

    create table LICENSE_HISTORY_INFO (
        id int8 not null,
        active boolean not null,
        hoursUsed varchar(255),
        licenseString varchar(255),
        systemId varchar(255),
        type int2 not null,
        version timestamp,
        primary key (id)
    );

    create table LICENSE_SERVER_SETTING (
        id int8 not null,
        apTimerInterval int4 not null,
        availableSoftToUpdate boolean not null,
        hoursUsed int4 not null,
        lserverUrl varchar(255),
        sendStatistic boolean not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table LLDPCDPPROFILE (
        id int8 not null,
        cdpMaxEntries int4 not null,
        description varchar(64),
        enableCDP boolean not null,
        enableLLDP boolean not null,
        lldpHoldTime int4 not null,
        lldpMaxEntries int4 not null,
        lldpMaxPower int4 not null,
        lldpReceiveOnly boolean not null,
        lldpTimer int4 not null,
        profileName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id),
        unique (OWNER, profileName)
    );

    create table LOCAL_USER (
        id int8 not null,
        activated boolean not null,
        defaultFlag boolean not null,
        description varchar(64),
        localUserPassword varchar(64),
        mailAddress varchar(128),
        oldPPSK varchar(128),
        revoked boolean not null,
        sponsor varchar(32),
        ssidName varchar(32),
        status int4 not null,
        userName varchar(32) not null,
        userType int4 not null,
        version timestamp,
        visitorCompany varchar(32),
        visitorName varchar(32),
        GROUP_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table LOCAL_USER_GROUP (
        id int8 not null,
        blnBulkType boolean not null,
        blnCharDigits boolean not null,
        blnCharLetters boolean not null,
        blnCharSpecial boolean not null,
        bulkNumber int4 not null,
        concatenateString varchar(255),
        credentialType int4 not null,
        description varchar(64),
        expiredTime timestamp,
        groupName varchar(32) not null,
        indexRange int4 not null,
        intervalDay int4 not null,
        intervalHour int4 not null,
        intervalMin int4 not null,
        personPskCombo int4 not null,
        pskGenerateMethod int4 not null,
        pskLength int4 not null,
        pskLocation varchar(255),
        pskSecret varchar(255),
        reauthTime int4 not null,
        startTime timestamp,
        timeZoneStr varchar(255),
        userNamePrefix varchar(255),
        userProfileId int4 not null,
        userType int4 not null,
        validTimeType int4 not null,
        version timestamp,
        vlanId int4 not null,
        OWNER int8 not null,
        SCHEDULE_ID int8,
        primary key (id)
    );

    create table LOCATIONCLIENTWATCH (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(255),
        name varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table LOCATIONCLIENT_ITEM (
        LOCATIONCLIENTWATCH_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (LOCATIONCLIENTWATCH_ID, POSITION)
    );

    create table LOCATION_RSSI_REPORT (
        id int8 not null,
        channel int4 not null,
        clientMac varchar(20) not null,
        reportTime timestamp,
        reporterMac varchar(20) not null,
        rssi int2 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table LOCATION_SERVER (
        id int8 not null,
        description varchar(64),
        ekahauMac varchar(255),
        ekahauPort int4 not null,
        ekahauTagThreshold int4 not null,
        enableRogue boolean not null,
        enableServer boolean not null,
        enableStation boolean not null,
        enableTag boolean not null,
        locationReportInterval int4 not null,
        name varchar(32) not null,
        reportSuppressCount int4 not null,
        rogueThreshold int4 not null,
        rssiChangeThreshold int4 not null,
        rssiHoldCount int4 not null,
        rssiValidPeriod int4 not null,
        serviceType int2 not null,
        stationThreshold int4 not null,
        tagThreshold int4 not null,
        version timestamp,
        OWNER int8 not null,
        IPADDRESS_ID int8,
        primary key (id)
    );

    create table LOGSETTINGS (
        id int8 not null,
        alarmInterval int4 not null,
        auditlogExpirationDays int4 not null,
        clientPeriod int4 not null,
        eventInterval int4 not null,
        interfaceStatsInterval int4 not null,
        intervalTablePartCli int4 not null,
        intervalTablePartPer int4 not null,
        l3FirewallLogExpirationDays int4 not null,
        maxDayValue int4 not null,
        maxHistoryClientRecord int4 not null,
        maxHourValue int4 not null,
        maxOriginalCount int4 not null,
        maxPerfRecord int4 not null,
        maxSupportAp int4 not null,
        maxTimeTableCliSave int4 not null,
        maxTimeTablePerSave int4 not null,
        maxWeekValue int4 not null,
        reportIntervalMinute int4 not null,
        reportMaxApCount int4 not null,
        reportMaxApPercent int4 not null,
        slaPeriod int4 not null,
        statsStartMinute int4 not null,
        syslogExpirationDays int4 not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table MAC_AUTH (
        id int8 not null,
        macAddress varchar(64),
        schoolName varchar(128),
        studentId varchar(32) not null,
        studentName varchar(128),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MAC_FILTER (
        id int8 not null,
        description varchar(64),
        filterName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MAC_FILTER_MAC_OR_OUI (
        MAC_FILTER_ID int8 not null,
        filterAction int2 not null,
        MAC_OR_OUI_ID int8,
        POSITION int4 not null,
        primary key (MAC_FILTER_ID, POSITION)
    );

    create table MAC_OR_OUI (
        id int8 not null,
        defaultFlag boolean not null,
        macOrOuiName varchar(32) not null,
        typeFlag int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MAC_OR_OUI_ITEM (
        MAC_OR_OUI_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (MAC_OR_OUI_ID, POSITION)
    );

    create table MAC_POLICY (
        id int8 not null,
        description varchar(64),
        policyName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MAC_POLICY_RULE (
        MAC_POLICY_ID int8 not null,
        actionLog int2 not null,
        DESTINATION_MAC_ID int8,
        filterAction int2 not null,
        ruleId int2 not null,
        SOURCE_MAC_ID int8,
        POSITION int4 not null,
        primary key (MAC_POLICY_ID, POSITION)
    );

    create table MAIL_NOTIFICATION (
        id int8 not null,
        ad int2 not null,
        airScreen boolean not null,
        auth boolean not null,
        capWap int2 not null,
        clientMonitor boolean not null,
        config int2 not null,
        emailPassword varchar(255),
        emailUserName varchar(255),
        hdCpu boolean not null,
        hdMemory boolean not null,
        hdRadio int2 not null,
        inNetIdp boolean not null,
        interfaceValue boolean not null,
        l2Dos boolean not null,
        mailFrom varchar(64),
        mailTo varchar(512),
        port int4 not null,
        screen boolean not null,
        security int2 not null,
        sendMailFlag boolean not null,
        serverName varchar(32),
        supportPwdAuth boolean not null,
        supportSSL boolean not null,
        supportTLS boolean not null,
        tca int2 not null,
        timeBomb int2 not null,
        version timestamp,
        vpn boolean not null,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table MAIL_NOTIFICATION_VHM (
        id int8 not null,
        mailTo varchar(512),
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table MAP_LINK (
        PARENT_MAP_ID int8 not null,
        FROM_NODE_ID int8 not null,
        fromRssi int4 not null,
        TO_NODE_ID int8 not null,
        toRssi int4 not null,
        mapkey varchar(255) not null,
        primary key (PARENT_MAP_ID, mapkey)
    );

    create table MAP_NODE (
        NODE_TYPE varchar(31) not null,
        id int8 not null,
        address varchar(255),
        centerLatitude float4,
        centerLongitude float4,
        centerZoom int2 not null,
        iconName varchar(255),
        latitude float4,
        longitude float4,
        severity int2 not null,
        version timestamp,
        x float8 not null,
        y float8 not null,
        actualHeight float8,
        actualWidth float8,
        apElevation float8,
        background varchar(255),
        environment int4,
        floorLoss float8,
        height float8,
        lengthUnit int2,
        mapName varchar(255),
        mapOrder int4,
        mapType int2,
        originX float8,
        originY float8,
        useHeatmap boolean,
        viewType varchar(255),
        width float8,
        apId varchar(20),
        apName varchar(32),
        ethId varchar(255),
        fetchLinksTimeout boolean,
        OWNER int8 not null,
        PARENT_MAP_ID int8,
        HIVE_AP_ID int8,
        primary key (id)
    );

    create table MAP_PERIMETER (
        MAP_ID int8 not null,
        id int4 not null,
        type int2 not null,
        x float8 not null,
        y float8 not null,
        POSITION int4 not null,
        primary key (MAP_ID, POSITION)
    );

    create table MAP_SETTINGS (
        id int8 not null,
        bgMapOpacity int4 not null,
        calibrateHeatmap boolean not null,
        clientRssiThreshold int4 not null,
        heatMapOpacity int4 not null,
        heatmapResolution int4 not null,
        locationWindow int4 not null,
        minRssiCount int4 not null,
        neighborRssiFlag boolean not null,
        onHoverFlag boolean not null,
        periVal boolean not null,
        pollingInterval int4 not null,
        realTime boolean not null,
        rssiFrom int4 not null,
        rssiUntil int4 not null,
        summaryFlag boolean not null,
        surveyErp float8 not null,
        useHeatmap boolean not null,
        useStreetMaps boolean not null,
        useSurveyErp boolean not null,
        version timestamp,
        wallsOpacity int4 not null,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table MAP_WALL (
        MAP_ID int8 not null,
        type int2 not null,
        x1 float8 not null,
        x2 float8 not null,
        y1 float8 not null,
        y2 float8 not null,
        POSITION int4 not null,
        primary key (MAP_ID, POSITION)
    );

    create table MGMT_SERVICE_DNS (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        domainName varchar(32),
        mgmtName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MGMT_SERVICE_DNS_INFO (
        MGMT_SERVICE_DNS_ID int8 not null,
        dnsDescription varchar(64),
        MGMT_SERVICE_IP_ADDRESS_ID int8,
        serverName varchar(32),
        severity int2 not null,
        POSITION int4 not null,
        primary key (MGMT_SERVICE_DNS_ID, POSITION)
    );

    create table MGMT_SERVICE_IP_TRACK (
        id int8 not null,
        description varchar(64),
        disableRadio boolean not null,
        enableAccess boolean not null,
        enableTrack boolean not null,
        interval int2 not null,
        ipAddresses varchar(64),
        retryTime int2 not null,
        startFailover boolean not null,
        timeout int2 not null,
        trackLogic int2 not null,
        trackName varchar(32) not null,
        useGateway boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MGMT_SERVICE_OPTION (
        id int8 not null,
        airtimePerSecond int2 not null,
        description varchar(64),
        disableCallAdmissionControl boolean not null,
        disableConsolePort boolean not null,
        disableProxyArp boolean not null,
        disableResetButton boolean not null,
        disableSsid boolean not null,
        dropFragmentedIpPackets boolean not null,
        dropNonMgtTraffic boolean not null,
        enableForwardMaxIp boolean not null,
        enableForwardMaxMac boolean not null,
        enableIcmpRedirect boolean not null,
        enableOsdetection boolean not null,
        enablePCIData boolean not null,
        enablePMTUD boolean not null,
        enableSmartPoe boolean not null,
        enableSyncVlanId boolean not null,
        enableTcpMss boolean not null,
        forwardMaxIp int2 not null,
        forwardMaxMac int2 not null,
        logDroppedPackets boolean not null,
        logFirstPackets boolean not null,
        macAuthCase int2 not null,
        macAuthDelimiter int2 not null,
        macAuthStyle int2 not null,
        mgmtName varchar(32) not null,
        monitorMSS boolean not null,
        multicastselect int2 not null,
        osDetectionMethod int2 not null,
        ppskAutoSaveInt int4 not null,
        radiusAuthType int4 not null,
        roamingGuaranteedAirtime int2 not null,
        systemLedBrightness int2 not null,
        tcpMssThreshold int4 not null,
        tempAlarmThreshold int2 not null,
        thresholdForAllTCP int4 not null,
        thresholdThroughVPNTunnel int4 not null,
        userAuth int2 not null,
        version timestamp,
        OWNER int8 not null,
        RADIUS_SERVICE_ASSIGN_ID int8,
        primary key (id)
    );

    create table MGMT_SERVICE_SNMP (
        id int8 not null,
        contact varchar(255),
        defaultFlag boolean not null,
        description varchar(64),
        enableCapwap boolean not null,
        enableSnmp boolean not null,
        mgmtName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MGMT_SERVICE_SNMP_INFO (
        MGMT_SERVICE_SNMP_ID int8 not null,
        authPass varchar(64),
        authPassMethod int2 not null,
        community varchar(32),
        encryPass varchar(64),
        encryPassMethod int2 not null,
        MGMT_SERVICE_IP_ADDRESS_ID int8,
        snmpOperation int2 not null,
        snmpVersion int2 not null,
        userName varchar(32),
        POSITION int4 not null,
        primary key (MGMT_SERVICE_SNMP_ID, POSITION)
    );

    create table MGMT_SERVICE_SYSLOG (
        id int8 not null,
        description varchar(64),
        facility int2 not null,
        internalServer boolean not null,
        mgmtName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MGMT_SERVICE_SYSLOG_INFO (
        MGMT_SERVICE_SYSLOG_ID int8 not null,
        MGMT_SERVICE_IP_ADDRESS_ID int8,
        severity int2 not null,
        syslogDescription varchar(64),
        POSITION int4 not null,
        primary key (MGMT_SERVICE_SYSLOG_ID, POSITION)
    );

    create table MGMT_SERVICE_TIME (
        id int8 not null,
        description varchar(64),
        enableClock boolean not null,
        enableNtp boolean not null,
        interval int4 not null,
        mgmtName varchar(32) not null,
        timeZoneStr varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table MGMT_SERVICE_TIME_INFO (
        MGMT_SERVICE_TIME_ID int8 not null,
        MGMT_SERVICE_IP_ADDRESS_ID int8,
        timeDescription varchar(64),
        POSITION int4 not null,
        primary key (MGMT_SERVICE_TIME_ID, POSITION)
    );

    create table MULTICAST_FORWARDING (
        MGMT_SERVICE_ID int8 not null,
        ip varchar(255),
        netmask varchar(255),
        POSITION int4 not null,
        primary key (MGMT_SERVICE_ID, POSITION)
    );

    create table NEIGHBORS_NAME_ITEM (
        NEIGHBORS_OBJECT_ID int8 not null,
        description varchar(64),
        neighborsName varchar(15),
        POSITION int4 not null,
        primary key (NEIGHBORS_OBJECT_ID, POSITION)
    );

    create table NETWORK_SERVICE (
        id int8 not null,
        algType int2 not null,
        cliDefaultFlag boolean not null,
        defaultFlag boolean not null,
        description varchar(64),
        idleTimeout int4 not null,
        portNumber int4 not null,
        protocolId int2 not null,
        protocolNumber int4 not null,
        serviceName varchar(32),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table NOTIFICATION_MESSAGE_STATUS (
        id int8 not null,
        displayStatusSection1 int8 not null,
        displayStatusSection2 int8 not null,
        previousDefinedMsg1 int8 not null,
        previousDefinedMsg2 int8 not null,
        userId int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table N_RATE_SETTING_INFO (
        SSID_PROFILE_ID int8 not null,
        rateSet int2 not null,
        mapkey varchar(255) not null,
        primary key (SSID_PROFILE_ID, mapkey)
    );

    create table ONETIME_PASSWORD (
        id int8 not null,
        dateActivateStamp int8 not null,
        dateSentStamp int8 not null,
        dateTimeZone varchar(255),
        description varchar(255),
        deviceModel int2 not null,
        emailAddress varchar(255),
        macAddress varchar(255),
        oneTimePassword varchar(255),
        userName varchar(255),
        version timestamp,
        HIVEAPAUTOPROVISION int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table OS_OBJECT (
        id int8 not null,
        defaultFlag boolean not null,
        osName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table OS_OBJECT_VERSION (
        OS_OBJECT_ID int8 not null,
        description varchar(64),
        option55 varchar(255),
        osVersion varchar(32),
        POSITION int4 not null,
        primary key (OS_OBJECT_ID, POSITION)
    );

    create table OS_OBJECT_VERSION_DHCP (
        OS_OBJECT_ID int8 not null,
        description varchar(64),
        option55 varchar(255),
        osVersion varchar(32),
        POSITION int4 not null,
        primary key (OS_OBJECT_ID, POSITION)
    );

    create table OS_VERSION (
        id int8 not null,
        option55 varchar(255),
        osVersion varchar(32),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table PLANNED_AP (
        id int8 not null,
        apModel int2 not null,
        countryCode int4 not null,
        hostName varchar(255),
        version timestamp,
        wifi0Channel int2 not null,
        wifi0ChannelWidth int2 not null,
        wifi0Enabled boolean not null,
        wifi0Power int2 not null,
        wifi1Channel int2 not null,
        wifi1ChannelWidth int2 not null,
        wifi1Enabled boolean not null,
        wifi1Power int2 not null,
        x float8 not null,
        y float8 not null,
        OWNER int8 not null,
        PARENT_MAP_ID int8,
        primary key (id)
    );

    create table PLAN_TOOL (
        id int8 not null,
        actualHeight float8 not null,
        actualWidth float8 not null,
        backgroundImg varchar(255),
        backgroundType int2 not null,
        bgMapOpacity int4 not null,
        channelWidth int2 not null,
        countryCode int4 not null,
        defaultApType int2 not null,
        fadeMargin int4 not null,
        heatMapOpacity int4 not null,
        installHeight float8 not null,
        lengthUnit int2 not null,
        mapEnv int2 not null,
        planToolMapId int8,
        version timestamp,
        wallColorBookshelf varchar(16),
        wallColorBrickWall varchar(16),
        wallColorConcrete varchar(16),
        wallColorCubicle varchar(16),
        wallColorDryWall varchar(16),
        wallColorElevatorShaft varchar(16),
        wallColorThickDoor varchar(16),
        wallColorThickWindow varchar(16),
        wallColorThinDoor varchar(16),
        wallColorThinWindow varchar(16),
        wallTypeBookshelf int2 not null,
        wallTypeBrickWall int2 not null,
        wallTypeConcrete int2 not null,
        wallTypeCubicle int2 not null,
        wallTypeDryWall int2 not null,
        wallTypeElevatorShaft int2 not null,
        wallTypeThickDoor int2 not null,
        wallTypeThickWindow int2 not null,
        wallTypeThinDoor int2 not null,
        wallTypeThinWindow int2 not null,
        wallsOpacity int4 not null,
        wifi0Channel int4 not null,
        wifi0Enabled boolean not null,
        wifi0Power int4 not null,
        wifi1Channel int4 not null,
        wifi1Enabled boolean not null,
        wifi1Power int4 not null,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table PPPOE (
        id int8 not null,
        description varchar(64),
        domain varchar(255),
        encryptionMethod int2 not null,
        password varchar(255),
        pppoeName varchar(32) not null,
        username varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table PRINT_TEMPLATE (
        id int8 not null,
        asDefault boolean not null,
        defaultFlag boolean not null,
        enabled boolean not null,
        footerHTML varchar(2048),
        headerHTML varchar(2048),
        name varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table QOS_CLASSFIER_AND_MARKER (
        id int8 not null,
        checkD boolean not null,
        checkDT boolean not null,
        checkE boolean not null,
        checkET boolean not null,
        checkP boolean not null,
        checkPT boolean not null,
        description varchar(64),
        macOuisEnabled boolean not null,
        marksEnabled boolean not null,
        networkServicesEnabled boolean not null,
        qosName varchar(32) not null,
        ssidEnabled boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table QOS_CLASSIFICATION (
        id int8 not null,
        classificationName varchar(32) not null,
        description varchar(64),
        generalEnabled boolean not null,
        macOuisEnabled boolean not null,
        marksEnabled boolean not null,
        networkServicesEnabled boolean not null,
        prtclD varchar(20),
        prtclE varchar(20),
        prtclP varchar(20),
        ssidEnabled boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table QOS_CLASSIFICATION_MAC (
        QOS_CLASSIFICATION_ID int8 not null,
        comment varchar(32),
        filterActionMacOuis int2 not null,
        loggingMacOuis int2 not null,
        macEntry varchar(255),
        MAC_OR_OUI_ID int8 not null,
        qosClassMacOuis int2 not null,
        mapkey int8 not null,
        primary key (QOS_CLASSIFICATION_ID, mapkey)
    );

    create table QOS_CLASSIFICATION_SERVICE (
        QOS_CLASSIFICATION_ID int8 not null,
        filterAction int2 not null,
        logging int2 not null,
        NETWORK_SERVICE_ID int8,
        qosClass int2 not null,
        mapkey int8 not null,
        primary key (QOS_CLASSIFICATION_ID, mapkey)
    );

    create table QOS_CLASSIFICATION_SSID (
        QOS_CLASSIFICATION_ID int8 not null,
        qosClassSsids int2 not null,
        SSID_ID int8 not null,
        mapkey int8 not null,
        primary key (QOS_CLASSIFICATION_ID, mapkey)
    );

    create table QOS_MARKING (
        id int8 not null,
        description varchar(64),
        prtclD varchar(32),
        prtclE varchar(32),
        prtclP varchar(32),
        qosName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table QOS_RATE_CONTROL (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        qosName varchar(32),
        rateLimit int4 not null,
        rateLimit11n int4 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table QOS_RATE_CONTROL_RATE_LIMIT (
        QOS_RATE_LIMIT_ID int8 not null,
        policing11nRateLimit int4 not null,
        policingRateLimit int4 not null,
        qosClass int2 not null,
        schedulingType int2 not null,
        schedulingWeight int4 not null,
        POSITION int4 not null,
        primary key (QOS_RATE_LIMIT_ID, POSITION)
    );

    create table RADIO_PROFILE (
        id int8 not null,
        aggregateMPDU boolean not null,
        antennaType20 int2 not null,
        antennaType28 int2 not null,
        averageInterval int2 not null,
        backgroundScan boolean not null,
        backhaulFailover boolean not null,
        bandSteeringMode int2 not null,
        beaconPeriod int2 not null,
        channelClient int2 not null,
        channelModel int2 not null,
        channelRegion int2 not null,
        channelSwitch boolean not null,
        channelThreshold int2 not null,
        channelValue varchar(11),
        channelWidth int2 not null,
        cliDefaultFlag boolean not null,
        clientConnect boolean not null,
        clientHoldTime int4 not null,
        crcChannelThr int2 not null,
        crcErrorLimit int4 not null,
        crcThreshold int2 not null,
        cuLimit int4 not null,
        defaultCcaValue int2 not null,
        defaultFlag boolean not null,
        deny11abg boolean not null,
        deny11b boolean not null,
        description varchar(64),
        enableBandSteering boolean not null,
        enableBroadcastProbe boolean not null,
        enableCca boolean not null,
        enableChannel boolean not null,
        enableClientLoadBalance boolean not null,
        enableContinuousProbe boolean not null,
        enableDfs boolean not null,
        enableHighDensity boolean not null,
        enableInterfernce boolean not null,
        enablePower boolean not null,
        enableRadarDetect boolean not null,
        enableSafetyNet boolean not null,
        enableSuppress boolean not null,
        enabledBssidSpoof boolean not null,
        fromHour int2 not null,
        fromMinute int2 not null,
        guardInterval boolean not null,
        highDensityTransmitRate int2 not null,
        holdTime int2 not null,
        interval int4 not null,
        iuThreshold int2 not null,
        limitNumber int4 not null,
        loadBalance boolean not null,
        loadBalancingMode int2 not null,
        maxCcaValue int2 not null,
        maxClients int2 not null,
        maxInterference int4 not null,
        minCount int2 not null,
        minimumRatio int4 not null,
        powerSave boolean not null,
        queryInterval int4 not null,
        radioMode int2 not null,
        radioName varchar(32) not null,
        radioRange int4 not null,
        receiveChain int2 not null,
        roamingThreshold int2 not null,
        safetyNetTimeout int4 not null,
        shortPreamble int2 not null,
        slaThoughput int2 not null,
        stationConnect boolean not null,
        suppressThreshold int4 not null,
        threshold int2 not null,
        toHour int2 not null,
        toMinute int2 not null,
        trafficVoice boolean not null,
        transmitChain int2 not null,
        transmitPower int2 not null,
        triggerTime int2 not null,
        turboMode boolean not null,
        useDefaultChain boolean not null,
        useDefaultChannelModel boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id),
        unique (OWNER, radioName)
    );

    create table RADIO_PROFILE_WMM_INFO (
        RADIO_PROFILE_ID int8 not null,
        aifs int2 not null,
        maximum int2 not null,
        minimum int2 not null,
        noAck boolean not null,
        txoplimit int4 not null,
        mapkey varchar(255) not null,
        primary key (RADIO_PROFILE_ID, mapkey)
    );

    create table RADIUS_AD_DOMAIN (
        AD_DOMAIN_ID int8 not null,
        bindDnName varchar(256),
        bindDnPass varchar(64),
        defaultFlag boolean not null,
        domain varchar(64),
        fullName varchar(64),
        server varchar(64),
        POSITION int4 not null,
        primary key (AD_DOMAIN_ID, POSITION)
    );

    create table RADIUS_ATTRIBUTE_ITEM (
        RADIUS_ATTRIBUTE_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (RADIUS_ATTRIBUTE_ID, POSITION)
    );

    create table RADIUS_HIVEAP_AUTH (
        AUTH_ID int8 not null,
        description varchar(64),
        IP_ADDRESS_ID int8,
        sharedKey varchar(31),
        POSITION int4 not null,
        primary key (AUTH_ID, POSITION)
    );

    create table RADIUS_HIVEAP_LDAP_USER_PROFILE (
        LDAP_USER_PROFILE_ID int8 not null,
        groupAttributeValue varchar(255),
        LOCAL_USER_GROUP_ID int8 not null,
        rowId int4 not null,
        serverId int8,
        typeFlag int2 not null,
        userProfileId int8,
        userProfileName varchar(255),
        POSITION int4 not null,
        primary key (LDAP_USER_PROFILE_ID, POSITION)
    );

    create table RADIUS_LIBRARY_SIP (
        id int8 not null,
        defAction int2 not null,
        defMessage varchar(255),
        description varchar(64),
        policyName varchar(32) not null,
        version timestamp,
        DEFAULT_USER_GROUP_ID int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table RADIUS_ON_HIVEAP (
        id int8 not null,
        accPolicy boolean not null,
        authType int2 not null,
        caCertFile varchar(255),
        cacheEnable boolean not null,
        cacheTime int4 not null,
        cnEnable boolean not null,
        databaseType int2 not null,
        dbEnable boolean not null,
        description varchar(64),
        globalCatalog boolean not null,
        groupAttribute varchar(32),
        institutionId varchar(64),
        keyFile varchar(255),
        keyPassword varchar(64),
        librarySipCheck boolean not null,
        localInterval int4 not null,
        loginEnable boolean not null,
        loginPwd varchar(32),
        loginUser varchar(32),
        mapByGroupOrUser int2 not null,
        mapEnable boolean not null,
        peapCheckInDb boolean not null,
        radiusName varchar(32) not null,
        reauthTime varchar(32),
        remoteInterval int4 not null,
        retryInterval int4 not null,
        separator varchar(1),
        serverEnable boolean not null,
        serverFile varchar(255),
        serverPort int4 not null,
        sipPort int4 not null,
        ttlsCheckInDb boolean not null,
        useEdirect boolean not null,
        userProfileId varchar(32),
        version timestamp,
        vlanId varchar(32),
        OWNER int8 not null,
        LIBRARY_SIP_POLICY_ID int8,
        LIBRARY_SIP_SERVER_ID int8,
        primary key (id)
    );

    create table RADIUS_ON_HIVEAP_LOCAL_USER_GROUP (
        RADIUS_ON_HIVEAP_ID int8 not null,
        LOCAL_USER_GROUP_ID int8 not null,
        primary key (RADIUS_ON_HIVEAP_ID, LOCAL_USER_GROUP_ID)
    );

    create table RADIUS_OPERATOR_ATTRIBUTE (
        id int8 not null,
        defaultFlag boolean not null,
        objectName varchar(64) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table RADIUS_PROXY (
        id int8 not null,
        deadTime int4 not null,
        description varchar(64),
        injectOperatorNmAttri boolean not null,
        proxyFormat int2 not null,
        proxyName varchar(32) not null,
        retryCount int2 not null,
        retryDelay int2 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table RADIUS_PROXY_NAS (
        RADIUS_PROXY_ID int8 not null,
        description varchar(64),
        IP_ADDRESS_ID int8,
        sharedKey varchar(31),
        POSITION int4 not null,
        primary key (RADIUS_PROXY_ID, POSITION)
    );

    create table RADIUS_PROXY_REALM (
        RADIUS_PROXY_ID int8 not null,
        RADIUS_SERVER_ID int8,
        serverName varchar(32) not null,
        strip boolean not null,
        tlsPort int4 not null,
        useIDM boolean not null,
        POSITION int4 not null,
        primary key (RADIUS_PROXY_ID, POSITION)
    );

    create table RADIUS_RULE_USER_PROFILE (
        RADIUS_USER_PROFILE_RULE_ID int8 not null,
        USER_PROFILE_ID int8 not null,
        primary key (RADIUS_USER_PROFILE_RULE_ID, USER_PROFILE_ID)
    );

    create table RADIUS_SERVICE (
        ASSIGNMENT_ID int8 not null,
        acctPort int4 not null,
        authPort int4 not null,
        IP_ADDRESS_ID int8,
        serverPriority int2 not null,
        serverType int2 not null,
        sharedSecret varchar(64),
        POSITION int4 not null,
        primary key (ASSIGNMENT_ID, POSITION)
    );

    create table RADIUS_SERVICE_ASSIGN (
        id int8 not null,
        description varchar(64),
        enableDHCP4RadiusServer boolean not null,
        enableExtensionRadius boolean not null,
        injectOperatorNmAttri boolean not null,
        radiusName varchar(32) not null,
        retryInterval int4 not null,
        updateInterval int4 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table RADIUS_USER_PROFILE_RULE (
        id int8 not null,
        actionTime int8 not null,
        allUserProfilesPermitted boolean not null,
        defaultFlag boolean not null,
        denyAction int2 not null,
        description varchar(64),
        radiusUserProfileRuleName varchar(32) not null,
        strict boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ROUTING_POLICY (
        id int8 not null,
        description varchar(64),
        enableDomainObjectForDesList boolean not null,
        enableIpTrackForCheck boolean not null,
        policyName varchar(32) not null,
        policyRuleType int2 not null,
        version timestamp,
        EXCEPTIONLIST_ID int8,
        IP_TRACK_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table ROUTING_POLICY_RULE (
        ROUTING_POLICY_RULE_ID int8 not null,
        forwardActionTypePri int2 not null,
        forwardActionTypeSec int2 not null,
        interfaceTypePri int2 not null,
        interfaceTypeSec int2 not null,
        IP_TRACK_PRI_ID int8,
        IP_TRACK_SEC_ID int8,
        ruleType int2 not null,
        USERPROFILEID int8,
        POSITION int4 not null,
        primary key (ROUTING_POLICY_RULE_ID, POSITION)
    );

    create table ROUTING_PROFILE (
        id int8 not null,
        area varchar(32),
        autonmousSysNm int4,
        bgpRouterId varchar(32),
        enableDynamicRouting boolean not null,
        enableRouteLan boolean not null,
        enableRouteWan boolean not null,
        keepalive int4,
        password varchar(255),
        routerId varchar(32),
        typeFlag int2 not null,
        useMD5 boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table RPC_SETTINGS (
        id int8 not null,
        enabled boolean not null,
        password varchar(32),
        timeout int4 not null,
        userName varchar(32),
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table SCHEDULER (
        id int8 not null,
        beginDate varchar(20),
        beginTime varchar(20),
        beginTimeS varchar(20),
        defaultFlag boolean not null,
        description varchar(64),
        endDate varchar(20),
        endTime varchar(20),
        endTimeS varchar(20),
        schedulerName varchar(32) not null,
        type int4 not null,
        version timestamp,
        weeks varchar(30),
        OWNER int8 not null,
        primary key (id)
    );

    create table SCHEDULE_BACKUP (
        id int8 not null,
        backupContent int2 not null,
        backupType varchar(255),
        endDate varchar(64),
        endDateFlag boolean not null,
        endHour int2 not null,
        endMinute int2 not null,
        interval int4 not null,
        liveFlag boolean not null,
        protocol int2 not null,
        rescurFlag boolean not null,
        scpFilePath varchar(512),
        scpIpAdd varchar(64),
        scpPort int4 not null,
        scpPsd varchar(512),
        scpUsr varchar(512),
        startDate varchar(64),
        startHour int2 not null,
        startMinute int2 not null,
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table SERVICE_FILTER (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        enablePing boolean not null,
        enableSNMP boolean not null,
        enableSSH boolean not null,
        enableTelnet boolean not null,
        filterName varchar(64) not null,
        interTraffic boolean not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table SIP_POLICY_RULE (
        RADIUS_LIBRARY_SIP_ID int8 not null,
        action int2 not null,
        field varchar(2),
        message varchar(255),
        operator int2 not null,
        ruleId int2 not null,
        USER_GROUP_ID int8 not null,
        valueStr varchar(32),
        POSITION int4 not null,
        primary key (RADIUS_LIBRARY_SIP_ID, POSITION)
    );

    create table SLA_MAPPING_CUSTOMIZE (
        id int8 not null,
        itemOrder int2 not null,
        level int2 not null,
        phymode varchar(255),
        rate varchar(255),
        success int4 not null,
        usage int4 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table SSID_LOCAL_USER_GROUP (
        SSID_PROFILE_ID int8 not null,
        LOCAL_USER_GROUP_ID int8 not null,
        primary key (SSID_PROFILE_ID, LOCAL_USER_GROUP_ID)
    );

    create table SSID_PROFILE (
        id int8 not null,
        accessMode int4 not null,
        actionTime int8 not null,
        ageOut int4 not null,
        assignUserProfileAttributeId int4 not null,
        assignUserProfileVenderId int4 not null,
        authSequence int4 not null,
        authentication int4 not null,
        blnBrAsPpskServer boolean not null,
        broadcase boolean not null,
        chkDeauthenticate boolean not null,
        chkUserOnly boolean not null,
        clientAgeOut int4 not null,
        comment varchar(256),
        convtounicast int2 not null,
        cuthreshold int4 not null,
        cwpSelectEnabled boolean not null,
        defaultAction int4 not null,
        defaultFlag boolean not null,
        denyAction int2 not null,
        dtimSetting int4 not null,
        eapRetries int4 not null,
        eapTimeOut int4 not null,
        enableADIntegration boolean not null,
        enableARateSet boolean not null,
        enableAppleOs boolean not null,
        enableAssignUserProfile boolean not null,
        enableGRateSet boolean not null,
        enableMDM boolean not null,
        enableMacOs boolean not null,
        enableNRateSet boolean not null,
        enableOsDection boolean not null,
        enablePpskSelfReg boolean not null,
        enabled80211k boolean not null,
        enabled80211v boolean not null,
        enabledAcBackground boolean not null,
        enabledAcBesteffort boolean not null,
        enabledAcVideo boolean not null,
        enabledAcVoice boolean not null,
        enabledDefaultSetting boolean not null,
        enabledIDM boolean not null,
        enabledLegacy boolean not null,
        enabledUnscheduled boolean not null,
        enabledUseGuestManager boolean not null,
        enabledVoiceEnterprise boolean not null,
        enabledwmm boolean not null,
        encryption int4 not null,
        fallBackToEcwp boolean not null,
        fragThreshold int4 not null,
        hide boolean not null,
        localCacheTimeout int4 not null,
        macAuthEnabled boolean not null,
        maxClient int4 not null,
        mdmPassword varchar(32),
        mdmType int4 not null,
        mdmUserName varchar(32),
        memberthreshold int4 not null,
        mgmtKey int4 not null,
        newRadiusType int4 not null,
        personPskRadiusAuth int4 not null,
        ppskOpenSsid varchar(255),
        PPSK_SERVER_ID int8,
        ppskServerIp varchar(255),
        preauthenticationEnabled boolean not null,
        radioMode int4 not null,
        rootURLPath varchar(256),
        rtsThreshold int4 not null,
        showExpressUserAccess boolean not null,
        ssid varchar(32) not null,
        ssidName varchar(32) not null,
        blnMacBindingEnable boolean not null,
        defaultKeyIndex int4 not null,
        enable80211w boolean not null,
        firstKeyValue varchar(64),
        fourthValue varchar(64),
        gtkRetries int4 not null,
        gtkTimeOut int4 not null,
        keyType int4 not null,
        localTkip boolean not null,
        proactiveEnabled boolean not null,
        pskUserLimit int4 not null,
        ptkRetries int4 not null,
        ptkTimeOut int4 not null,
        reauthInterval int4 not null,
        rekeyPeriod int4 not null,
        rekeyPeriodGMK int4 not null,
        rekeyPeriodPTK int4 not null,
        remoteTkip boolean not null,
        replayWindow int4 not null,
        secondKeyValue varchar(64),
        strict boolean not null,
        thirdKeyValue varchar(64),
        wpa2mfpType int4 not null,
        updateInterval int4 not null,
        userCategory int4 not null,
        userInternetAccess boolean not null,
        userProfileAttributeType int2 not null,
        userPskMethod int4 not null,
        userRatelimit int4 not null,
        version timestamp,
        AS_RULE_GROUP_ID int8,
        CWP_ID int8,
        IP_DOS_ID int8,
        OWNER int8 not null,
        PPSK_CWP_ID int8,
        RADIUS_SERVICE_ASSIGN_ID int8,
        RADIUS_SERVICE_ASSIGN_ID_PPSK int8,
        SERVICE_FILTER_ID int8,
        SSID_DOS_ID int8,
        STATION_DOS_ID int8,
        CWP_USERPOLICY_ID int8,
        USERPROFILE_DEFAULT_ID int8,
        USERPROFILE_SELFREG_ID int8,
        primary key (id),
        unique (OWNER, ssidName)
    );

    create table SSID_PROFILE_MAC_FILTER (
        SSID_PROFILE_ID int8 not null,
        MAC_FILTER_ID int8 not null,
        primary key (SSID_PROFILE_ID, MAC_FILTER_ID)
    );

    create table SSID_PROFILE_SCHEDULER (
        SSID_PROFILE_ID int8 not null,
        SCHEDULER_ID int8 not null,
        primary key (SSID_PROFILE_ID, SCHEDULER_ID)
    );

    create table SSID_PROFILE_USER_PROFILE (
        SSID_PROFILE_ID int8 not null,
        USER_PROFILE_ID int8 not null,
        primary key (SSID_PROFILE_ID, USER_PROFILE_ID)
    );

    create table SSID_RADIUS_USER_GROUP (
        SSID_PROFILE_ID int8 not null,
        LOCAL_USER_GROUP_ID int8 not null,
        primary key (SSID_PROFILE_ID, LOCAL_USER_GROUP_ID)
    );

    create table SUB_NETWORK_RESOURCE (
        id int8 not null,
        firstIp varchar(18),
        hiveApMac varchar(12),
        hiveApMgtx int2 not null,
        ipEndLong int8 not null,
        ipPoolEnd varchar(18),
        ipPoolStart varchar(18),
        ipStartLong int8 not null,
        network varchar(18),
        parentNetwork varchar(18),
        status int2 not null,
        version timestamp,
        vipIpAddress boolean not null,
        OWNER int8 not null,
        networkId int8,
        primary key (id)
    );

    create table TARGET (
        TARGET_TYPE varchar(31) not null,
        id int8 not null,
        action varchar(255),
        boDomainId int8,
        feature varchar(255),
        type int4 not null,
        urlParams varchar(255),
        userDomainId int8,
        userName varchar(255),
        version timestamp,
        field varchar(2048),
        column_name varchar(255),
        boId int8,
        fieldName varchar(255),
        fieldValue varchar(255),
        reference varchar(255),
        primary key (id)
    );

    create table TCA_ALARM (
        id  bigserial not null,
        description varchar(255),
        highthreshold int8 not null,
        interval int8 not null,
        lowthreshold int8 not null,
        meatureitem varchar(255) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table TEMPLATE_FIELD (
        TEMPLATE_ID int8 not null,
        label varchar(255),
        place int2 not null,
        required boolean not null,
        mapkey varchar(255) not null,
        primary key (TEMPLATE_ID, mapkey)
    );

    create table TREX (
        id int8 not null,
        elevation float8 not null,
        erp float8 not null,
        frequency int4 not null,
        note varchar(255),
        rid varchar(20),
        rssi int4 not null,
        rx float8 not null,
        ry float8 not null,
        tid varchar(20),
        TIME int8 not null,
        TIME_ZONE varchar(255),
        tx float8 not null,
        ty float8 not null,
        version timestamp,
        OWNER int8 not null,
        PARENT_MAP_ID int8,
        primary key (id)
    );

    create table TUNNEL_SETTING (
        id int8 not null,
        description varchar(64),
        enableType int4 not null,
        ipRangeEnd varchar(255),
        ipRangeStart varchar(255),
        password varchar(255),
        tunnelName varchar(32) not null,
        tunnelToType int4 not null,
        unroamingAgeout int4 not null,
        unroamingInterval int4 not null,
        version timestamp,
        IP_ADDRESS_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table TUNNEL_SETTING_IP_ADDRESS (
        TUNNEL_SETTING_ID int8 not null,
        IP_ADDRESS_ID int8 not null,
        primary key (TUNNEL_SETTING_ID, IP_ADDRESS_ID)
    );

    create table TV_CLASS (
        id int8 not null,
        className varchar(32) not null,
        description varchar(256),
        rosterType int4 not null,
        subject varchar(128),
        teacherId varchar(255),
        version timestamp,
        CART_ID int8,
        OWNER int8 not null,
        primary key (id)
    );

    create table TV_CLASS_SCHEDULE (
        TV_CLASS_ID int8 not null,
        endTime varchar(255),
        room varchar(256),
        startTime varchar(255),
        weekday varchar(32),
        weekdaySec varchar(7),
        POSITION int4 not null,
        primary key (TV_CLASS_ID, POSITION)
    );

    create table TV_COMPUTER_CART (
        id int8 not null,
        cartName varchar(128),
        description varchar(256),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table TV_COMPUTER_CART_MAC (
        TV_CART_ID int8 not null,
        stuMac varchar(255),
        stuName varchar(255),
        POSITION int4 not null,
        primary key (TV_CART_ID, POSITION)
    );

    create table TV_RESOURCE_MAP (
        id int8 not null,
        alias varchar(15) not null,
        description varchar(256),
        port int4 not null,
        resource varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table TV_SCHEDULE_MAP (
        id int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table TV_SCHEDULE_PERIODTIME (
        TV_SCHEDULE_MAP_ID int8 not null,
        endTime varchar(255),
        section int4 not null,
        startTime varchar(255),
        POSITION int4 not null,
        primary key (TV_SCHEDULE_MAP_ID, POSITION)
    );

    create table TV_SCHEDULE_WEEKDAY (
        TV_SCHEDULE_MAP_ID int8 not null,
        symbol varchar(255),
        weekday varchar(7),
        POSITION int4 not null,
        primary key (TV_SCHEDULE_MAP_ID, POSITION)
    );

    create table TV_STUDENT_ROSTER (
        id int8 not null,
        description varchar(256),
        studentId varchar(32) not null,
        studentName varchar(128),
        version timestamp,
        OWNER int8 not null,
        CLASS_ID int8,
        primary key (id)
    );

    create table USB_MODEM_PARAMETER (
        id int8 not null,
        apn varchar(255),
        authType varchar(255),
        connectType varchar(255),
        dailupNumber varchar(255),
        displayName varchar(255),
        displayType varchar(255),
        hiveOSVersionMin varchar(255),
        modemName varchar(255),
        password varchar(255),
        selected boolean not null,
        serialPort varchar(255),
        usbModule varchar(255),
        usbProductId varchar(255),
        usbVendorId varchar(255),
        usePeerDns boolean not null,
        userId varchar(255),
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table USB_MODEM_SIGNAL_STRENGTH_CHECK (
        USB_MODEM_ID int8 not null,
        checkCmd varchar(255),
        serialPort varchar(255),
        type varchar(255),
        POSITION int4 not null,
        primary key (USB_MODEM_ID, POSITION)
    );

    create table USER_LOCALUSERGROUP (
        USER_ID int8 not null,
        LOCALUSERGROUP_ID int8 not null,
        primary key (USER_ID, LOCALUSERGROUP_ID)
    );

    create table USER_PROFILE (
        id int8 not null,
        actionIp int2 not null,
        actionMac int2 not null,
        attributeValue int2 not null,
        blnUserManager boolean not null,
        defaultFlag boolean not null,
        description varchar(64),
        enableAssign boolean not null,
        enableCallAdmissionControl boolean not null,
        enableShareTime boolean not null,
        guarantedAirTime int2 not null,
        policingRate int4 not null,
        policingRate11n int4 not null,
        schedulingWeight int4 not null,
        slaAction int2 not null,
        slaBandwidth int4 not null,
        slaEnable boolean not null,
        tunnelTraffic int2 not null,
        userProfileName varchar(32) not null,
        version timestamp,
        AS_RULE_GROUP_ID int8,
        IP_POLICE_FROM_ID int8,
        IP_POLICE_TO_ID int8,
        MAC_POLICY_FROM_ID int8,
        MAC_POLICY_TO_ID int8,
        VPN_NETWORK_ID int8,
        OWNER int8 not null,
        QOS_RATE_CONTROL_ID int8,
        IDENTITY_BASED_TUNNEL_ID int8,
        ATTRITUTE_GROUP_ID int8,
        VLAN_ID int8,
        primary key (id),
        unique (OWNER, userProfileName)
    );

    create table USER_PROFILE_ATTRIBUTE (
        id int8 not null,
        attributeName varchar(32) not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table USER_PROFILE_SCHEDULER (
        USER_PROFILE_ID int8 not null,
        SCHEDULER_ID int8 not null,
        primary key (USER_PROFILE_ID, SCHEDULER_ID)
    );

    create table USER_REG_INFO_FOR_LS (
        id int8 not null,
        activeBySelf boolean not null,
        addressLine1 varchar(256),
        addressLine2 varchar(256),
        company varchar(64),
        country varchar(32),
        email varchar(64),
        name varchar(64),
        postalCode varchar(32),
        telephone varchar(32),
        version timestamp,
        OWNER int8 not null unique,
        primary key (id)
    );

    create table USER_SSIDPROFILE (
        USER_ID int8 not null,
        SSIDPROFILE_ID int8 not null,
        primary key (USER_ID, SSIDPROFILE_ID)
    );

    create table VIEWING_CLASS (
        id int8 not null,
        apAddress varchar(32) not null,
        className varchar(32) not null,
        endTime int8 not null,
        selectedTime int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table VLAN (
        id int8 not null,
        defaultFlag boolean not null,
        version timestamp,
        vlanName varchar(32) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table VLAN_DHCP_SERVER (
        id int8 not null,
        authoritative boolean not null,
        defaultGateway varchar(15),
        description varchar(64),
        dhcpMgt int2 not null,
        dhcpNetmask varchar(15),
        dnsServer1 varchar(15),
        dnsServer2 varchar(15),
        dnsServer3 varchar(15),
        domainName varchar(32),
        enableArp boolean not null,
        enablePing boolean not null,
        interVlan int4 not null,
        interfaceIp varchar(15),
        interfaceNet varchar(15),
        ipHelper1 varchar(15),
        ipHelper2 varchar(15),
        leaseTime varchar(255),
        logsrv varchar(15),
        mtu varchar(255),
        natSupport boolean not null,
        ntpServer1 varchar(15),
        ntpServer2 varchar(15),
        pop3 varchar(15),
        profileName varchar(32) not null,
        smtp varchar(15),
        typeFlag int2 not null,
        version timestamp,
        wins1 varchar(15),
        wins2 varchar(15),
        OWNER int8 not null,
        primary key (id),
        unique (OWNER, profileName)
    );

    create table VLAN_GROUP (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        version timestamp,
        vlanGroupName varchar(32) not null,
        vlans varchar(255),
        OWNER int8 not null,
        primary key (id)
    );

    create table VLAN_ITEM (
        VLAN_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (VLAN_ID, POSITION)
    );

    create table VPN_GATEWAY_SETTING (
        VPN_GATEWAY_SETTING_ID int8 not null,
        externalIpAddress varchar(15),
        hiveApId int8 not null,
        POSITION int4 not null,
        primary key (VPN_GATEWAY_SETTING_ID, POSITION)
    );

    create table VPN_NETWORK (
        id int8 not null,
        defaultFlag boolean not null,
        description varchar(64),
        domainName varchar(32),
        enableDhcp boolean not null,
        failConnectionOption int4 not null,
        guestLeftReserved int4 not null,
        guestRightReserved int4 not null,
        ipAddressSpace varchar(255),
        leaseTime int4 not null,
        networkName varchar(32) not null,
        networkType int4 not null,
        ntpServerIp varchar(255),
        version timestamp,
        webSecurity int4 not null,
        OWNER int8 not null,
        VLAN_ID int8,
        VPN_DNS_ID int8,
        primary key (id)
    );

    create table VPN_NETWORK_CUSTOM (
        VPN_NETWORK_CUSTOM_ID int8 not null,
        number int2 not null,
        type int2 not null,
        value varchar(255),
        POSITION int4 not null,
        primary key (VPN_NETWORK_CUSTOM_ID, POSITION)
    );

    create table VPN_NETWORK_IP_RESERVE_ITEM (
        VPN_NETWORK_RESERVECLASS_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (VPN_NETWORK_RESERVECLASS_ID, POSITION)
    );

    create table VPN_NETWORK_SUBITEM (
        VPN_NETWORK_SUB_ID int8 not null,
        domainName varchar(32),
        enableDhcp boolean not null,
        ipBranches int4 not null,
        ipNetwork varchar(255),
        key int4 not null,
        leaseTime int4 not null,
        leftEnd int4 not null,
        ntpServerIp varchar(255),
        reserveClassification boolean not null,
        rightEnd int4 not null,
        subnetClassification boolean not null,
        POSITION int4 not null,
        primary key (VPN_NETWORK_SUB_ID, POSITION)
    );

    create table VPN_NETWORK_SUBNETCLASS (
        VPN_NETWORK_SUBNETCLASS_ID int8 not null,
        attributeValue varchar(128),
        description varchar(64),
        ipAddress varchar(64),
        LOCATION_ID int8,
        macEntry varchar(12),
        macRangeFrom varchar(12),
        macRangeTo varchar(12),
        nameSpaceId int2 not null,
        netmask varchar(15),
        operatorName varchar(64),
        tag1 varchar(255),
        tag1Checked boolean not null,
        tag2 varchar(255),
        tag2Checked boolean not null,
        tag3 varchar(255),
        tag3Checked boolean not null,
        type int2 not null,
        typeName varchar(255),
        vlanId int4 not null,
        POSITION int4 not null,
        primary key (VPN_NETWORK_SUBNETCLASS_ID, POSITION)
    );

    create table VPN_NETWORK_SUBNET_CUSTOMS (
        VPN_NETWORK_SUBNET_CUSTOM_ID int8 not null,
        key int4 not null,
        number int2 not null,
        type int2 not null,
        value varchar(255),
        POSITION int4 not null,
        primary key (VPN_NETWORK_SUBNET_CUSTOM_ID, POSITION)
    );

    create table VPN_SERVICE (
        id int8 not null,
        amrpInterval int4 not null,
        amrpRetry int4 not null,
        capwapThroughTunnel boolean not null,
        certificate varchar(255),
        clientIpPoolEnd1 varchar(15),
        clientIpPoolEnd2 varchar(15),
        clientIpPoolNetmask1 varchar(15),
        clientIpPoolNetmask2 varchar(15),
        clientIpPoolStart1 varchar(15),
        clientIpPoolStart2 varchar(15),
        dbTypeAdThroughTunnel boolean not null,
        dbTypeLdapThroughTunnel boolean not null,
        description varchar(64),
        dpdIdelInterval int4 not null,
        dpdRetry int4 not null,
        dpdRetryInterval int4 not null,
        hiveApVpnServer1 int8,
        hiveApVpnServer2 int8,
        ikeValidation boolean not null,
        ipsecVpnType int2 not null,
        keepAlive boolean not null,
        loadBalance boolean not null,
        logThroughTunnel boolean not null,
        natTraversal boolean not null,
        ntpThroughTunnel boolean not null,
        phase1AuthMethod int2 not null,
        phase1DhGroup int2 not null,
        phase1EncrypAlg int2 not null,
        phase1Hash int2 not null,
        phase1LifeTime int4 not null,
        phase2EncrypAlg int2 not null,
        phase2Hash int2 not null,
        phase2LifeTime int4 not null,
        phase2PfsGroup int2 not null,
        privateKey varchar(255),
        profileName varchar(32) not null,
        radiusThroughTunnel boolean not null,
        rootCa varchar(255),
        routeTrafficType int2 not null,
        serverDeaultGateway1 varchar(15),
        serverDeaultGateway2 varchar(15),
        serverIkeId int4 not null,
        serverPrivateIp1 varchar(15),
        serverPrivateIp2 varchar(15),
        serverPublicIp1 varchar(15),
        serverPublicIp2 varchar(15),
        snmpThroughTunnel boolean not null,
        upgradeFlag boolean not null,
        version timestamp,
        DNS_IP int8,
        DOMAINOBJECT_ID int8,
        OWNER int8 not null,
        primary key (id),
        unique (OWNER, profileName)
    );

    create table VPN_SERVICE_CREDENTIAL (
        VPN_SERVICE_ID int8 not null,
        allocated boolean not null,
        assignedClient varchar(255),
        backupRole int2 not null,
        clientName varchar(255) not null,
        credential varchar(255) not null,
        primaryRole int2 not null,
        POSITION int4 not null,
        primary key (VPN_SERVICE_ID, POSITION)
    );

    create table VPN_USERPROFILE_TRAFFICL2 (
        VPN_USERPROFILE_TRAFFICL2_ID int8 not null,
        tunnelSelected varchar(255),
        USERPROFILEID int8,
        vpnTunnelModeL2 int2 not null,
        POSITION int4 not null,
        primary key (VPN_USERPROFILE_TRAFFICL2_ID, POSITION)
    );

    create table VPN_USERPROFILE_TRAFFICL3 (
        VPN_USERPROFILE_TRAFFICL3_ID int8 not null,
        USERPROFILEID int8,
        vpnTunnelBehavior int2 not null,
        POSITION int4 not null,
        primary key (VPN_USERPROFILE_TRAFFICL3_ID, POSITION)
    );

    create table WALLED_GARDEN_ITEM (
        WALLED_GARDEN_ID int8 not null,
        itemId int4 not null,
        port int4 not null,
        protocol int4 not null,
        WALLED_GARDEN_ITEM_SERVER_ID int8,
        service int2 not null,
        POSITION int4 not null,
        primary key (WALLED_GARDEN_ID, POSITION)
    );

    create table ah_new_sla_stats (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apStatus int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ah_new_sla_stats_day (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ah_new_sla_stats_hour (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ah_new_sla_stats_week (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCrcError_Red int4 not null,
        apMac varchar(255),
        apName varchar(255),
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientCount int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ah_report_compliance (
        id  bigserial not null,
        apmac varchar(255) not null,
        status int4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table ah_sla_stats (
        id  bigserial not null,
        apAirTime_Red int4 not null,
        apCrcError_Red int4 not null,
        apRetry_Red int4 not null,
        apRxDrop_Red int4 not null,
        apSla_Red int4 not null,
        apSla_Yellow int4 not null,
        apTotal_Red int4 not null,
        apTotal_Yellow int4 not null,
        apTxDrop_Red int4 not null,
        clientAirTime_Red int4 not null,
        clientScore_Red int4 not null,
        clientScore_Yellow int4 not null,
        clientSla_Red int4 not null,
        clientSla_Yellow int4 not null,
        clientTotal_Red int4 not null,
        clientTotal_Yellow int4 not null,
        globalFlag boolean not null,
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table client_device_info (
        MAC int8 not null,
        IP4 bytea,
        IP6 bytea,
        OS_type varchar(32),
        comment varchar(32),
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        hostName varchar(128),
        lastModification timestamp with time zone not null,
        located boolean,
        monitored boolean,
        seen1st timestamp with time zone not null,
        userName varchar(128),
        vendor varchar(128),
        OWNER int8 not null,
        primary key (MAC, OWNER)
    );

    create table client_device_seen (
        id  bigserial not null,
        MAC int8 not null,
        SSId varchar(32) not null,
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        lastSeen timestamp with time zone not null,
        networkDeviceMac varchar(12) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table clients_osinfo_count (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        osInfo varchar(255),
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table clients_osinfo_count_day (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        osInfo varchar(255),
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table clients_osinfo_count_hour (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        osInfo varchar(255),
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table clients_osinfo_count_week (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        osInfo varchar(255),
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_appdata_hour (
        id  bigserial not null,
        apMac varchar(20),
        appSeconds int2 not null,
        application int2 not null,
        bytesDownLoad int8 not null,
        bytesUpLoad int8 not null,
        clientMac varchar(20),
        clientOsType varchar(32),
        extensionBigInt bytea,
        extensionByteArray bytea,
        extensionText bytea,
        extensionTimeStamp bytea,
        hostName varchar(128),
        interface4Client int2 not null,
        osName varchar(64),
        owner int8,
        packetsDownLoad int4 not null,
        packetsUpLoad int4 not null,
        passThrough boolean not null,
        peerInterface int2 not null,
        radioType int2 not null,
        ssid varchar(32),
        timeStamp int8 not null,
        userName varchar(128),
        userProfile int4 not null,
        userProfileName varchar(32),
        vLan int4 not null,
        primary key (id)
    );

    create table hm_appdata_seconds (
        id  bigserial not null,
        apMac varchar(20),
        appSeconds int2 not null,
        application int2 not null,
        bytesDownLoad int8 not null,
        bytesUpLoad int8 not null,
        clientMac varchar(20),
        clientOsType varchar(32),
        extensionBigInt bytea,
        extensionByteArray bytea,
        extensionText bytea,
        extensionTimeStamp bytea,
        hostName varchar(128),
        interface4Client int2 not null,
        osName varchar(64),
        owner int8,
        packetsDownLoad int4 not null,
        packetsUpLoad int4 not null,
        passThrough boolean not null,
        peerInterface int2 not null,
        radioType int2 not null,
        seconds int2 not null,
        ssid varchar(32),
        timeStamp int8 not null,
        userName varchar(128),
        userProfile int4 not null,
        userProfileName varchar(32),
        vLan int4 not null,
        primary key (id)
    );

    create table hm_capwapclient (
        id int8 not null,
        backupCapwapIP varchar(255),
        capwapEnable boolean not null,
        dtlsEnable boolean not null,
        neighborDeadInterval int2 not null,
        passphrase varchar(32),
        primaryCapwapIP varchar(255),
        serverType int2 not null,
        timeOut int2 not null,
        transportMode int2 not null,
        udpPort int4 not null,
        version timestamp,
        primary key (id)
    );

    create table hm_client_stats (
        id  bigserial not null,
        alarmFlag int4 not null,
        apMac varchar(20),
        apName varchar(32),
        applicationHealthScore int2 not null,
        averageSNR int4 not null,
        bandWidthUsage int4 not null,
        clientMac varchar(20),
        clientosinfo varchar(32),
        collectPeriod int2 not null,
        hostName varchar(255),
        ifIndex int4 not null,
        ipNetworkConnectivityScore int2 not null,
        osname varchar(32),
        overallClientHealthScore int2 not null,
        powerSaveModeTimes int4 not null,
        radioType int4 not null,
        rssi int4 not null,
        rxAirTime int2 not null,
        rxFrameByteCount int8 not null,
        rxFrameCount int4 not null,
        rxFrameDropped int4 not null,
        rxRateInfo varchar(1000),
        slaConnectScore int2 not null,
        slaViolationTraps int4 not null,
        ssidName varchar(32),
        timeStamp int8 not null,
        totalRxBitSuccessRate int2 not null,
        totalTxBitSuccessRate int2 not null,
        txAirTime int2 not null,
        txFrameByteCount int8 not null,
        txFrameCount int4 not null,
        txFrameDropped int4 not null,
        txRateInfo varchar(1000),
        userName varchar(255),
        userProfileName varchar(255),
        vendor varchar(32),
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_client_stats_day (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        bandWidthUsage int4 not null,
        clientMac varchar(20),
        rxAirTime int2 not null,
        rxFrameByteCount int8 not null,
        rxFrameDropped int4 not null,
        ssidName varchar(32),
        timeStamp int8 not null,
        txAirTime int2 not null,
        txFrameByteCount int8 not null,
        txFrameDropped int4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_client_stats_hour (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        bandWidthUsage int4 not null,
        clientMac varchar(20),
        rxAirTime int2 not null,
        rxFrameByteCount int8 not null,
        rxFrameDropped int4 not null,
        ssidName varchar(32),
        timeStamp int8 not null,
        txAirTime int2 not null,
        txFrameByteCount int8 not null,
        txFrameDropped int4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_client_stats_week (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        bandWidthUsage int4 not null,
        clientMac varchar(20),
        rxAirTime int2 not null,
        rxFrameByteCount int8 not null,
        rxFrameDropped int4 not null,
        ssidName varchar(32),
        timeStamp int8 not null,
        txAirTime int2 not null,
        txFrameByteCount int8 not null,
        txFrameDropped int4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_cpu_memory_usage (
        id int8 not null,
        cpuUsage Numeric(4,2) not null,
        memUsage Numeric(4,2) not null,
        timeStamp int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_device_stats (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        averageCpu int2 not null,
        averageMem int2 not null,
        collectPeriod int2 not null,
        maxCpu int2 not null,
        maxMem int2 not null,
        timeStamp int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_interface_stats (
        id  bigserial not null,
        alarmFlag int4 not null,
        apMac varchar(20),
        apName varchar(32),
        authRequestSuppressCount int4 not null,
        bandSteerSuppressCount int4 not null,
        bcastRxFrameCount int8 not null,
        bcastTxFrameCount int8 not null,
        collectPeriod int2 not null,
        crcErrorRate int2 not null,
        crcerrorframe int8 not null,
        ifIndex int4 not null,
        ifName varchar(255),
        interferenceUtilization int2 not null,
        loadBalanceSuppressCount int4 not null,
        noiseFloor int2 not null,
        probeRequestSuppressCount int4 not null,
        radioType int4 not null,
        rxAirTime int2 not null,
        rxByteCount int8 not null,
        rxDrops int8 not null,
        rxRateInfo varchar(1000),
        rxRetryRate int2 not null,
        rxUtilization int2 not null,
        rxbcastbytecount int8 not null,
        rxdropframebysw int8 not null,
        rxretryframe int8 not null,
        safetyNetAnswerCount int4 not null,
        timeStamp int8 not null,
        totalChannelUtilization int2 not null,
        totalRxBitSuccessRate int2 not null,
        totalTxBitSuccessRate int2 not null,
        txAirTime int2 not null,
        txByteCount int8 not null,
        txDrops int8 not null,
        txRateInfo varchar(1000),
        txRetryRate int2 not null,
        txUtilization int2 not null,
        txbcastbytecount int8 not null,
        txdropframebyhw int8 not null,
        txdropframebysw int8 not null,
        txretryframe int8 not null,
        uniRxFrameCount int8 not null,
        uniTxFrameCount int8 not null,
        weakSnrSuppressCount int4 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_interface_stats_day (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        crcErrorRate int2 not null,
        ifName varchar(255),
        interferenceUtilization int2 not null,
        noiseFloor int2 not null,
        radioType int4 not null,
        rxAirTime int2 not null,
        rxByteCount int8 not null,
        rxDrops int8 not null,
        rxRetryRate int2 not null,
        rxUtilization int2 not null,
        timeStamp int8 not null,
        totalChannelUtilization int2 not null,
        txAirTime int2 not null,
        txByteCount int8 not null,
        txDrops int8 not null,
        txRetryRate int2 not null,
        txUtilization int2 not null,
        uniRxFrameCount int8 not null,
        uniTxFrameCount int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_interface_stats_hour (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        crcErrorRate int2 not null,
        ifName varchar(255),
        interferenceUtilization int2 not null,
        noiseFloor int2 not null,
        radioType int4 not null,
        rxAirTime int2 not null,
        rxByteCount int8 not null,
        rxDrops int8 not null,
        rxRetryRate int2 not null,
        rxUtilization int2 not null,
        timeStamp int8 not null,
        totalChannelUtilization int2 not null,
        txAirTime int2 not null,
        txByteCount int8 not null,
        txDrops int8 not null,
        txRetryRate int2 not null,
        txUtilization int2 not null,
        uniRxFrameCount int8 not null,
        uniTxFrameCount int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_interface_stats_week (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        crcErrorRate int2 not null,
        ifName varchar(255),
        interferenceUtilization int2 not null,
        noiseFloor int2 not null,
        radioType int4 not null,
        rxAirTime int2 not null,
        rxByteCount int8 not null,
        rxDrops int8 not null,
        rxRetryRate int2 not null,
        rxUtilization int2 not null,
        timeStamp int8 not null,
        totalChannelUtilization int2 not null,
        txAirTime int2 not null,
        txByteCount int8 not null,
        txDrops int8 not null,
        txRetryRate int2 not null,
        txUtilization int2 not null,
        uniRxFrameCount int8 not null,
        uniTxFrameCount int8 not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_date (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_date_ap (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_hour (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timeLocal int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_month (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_month_ap (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_week (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_repo_historyonlineuser_week_ap (
        id  bigserial not null,
        apmac varchar(255),
        clientCount int8 not null,
        owner int8 not null,
        timestamp int8 not null,
        primary key (id)
    );

    create table hm_updatesoftwareinfo (
        id int8 not null,
        apSwithStatus boolean not null,
        domainName varchar(255),
        hmVersion varchar(255),
        ipAddress varchar(255),
        status int4 not null,
        version timestamp,
        primary key (id)
    );

    create table hmservicessettings (
        id int8 not null,
        accountID varchar(64),
        apSlaType int4 not null,
        authorizationKey varchar(40),
        barracudaDefaultUserName varchar(255),
        classifierTag varchar(255),
        clientSlaType int4 not null,
        concurrentConfigGenNum int2 not null,
        concurrentSearchUserNum int2 not null,
        dateFormat varchar(255),
        dateSeparator varchar(255),
        defaultDomain varchar(255),
        enableBarracuda boolean not null,
        enableClientRefresh boolean not null,
        enableProxy boolean not null,
        enableTVProxy boolean not null,
        enableTeacher boolean not null,
        enableWebsense boolean not null,
        enabledBetaIDM boolean not null,
        hmStatus int2 not null,
        infiniteSession boolean not null,
        maxUpdateNum int4 not null,
        notifyInformation varchar(1024),
        notifyInformationTitle varchar(255),
        port int4 not null,
        proxyPassword varchar(255),
        proxyPort int4 not null,
        proxyServer varchar(255),
        proxyUserName varchar(255),
        refreshFilterName varchar(255),
        refreshInterval int4 not null,
        securityKey varchar(255),
        serviceHost varchar(255),
        servicePort int4 not null,
        sessionExpiration int4 not null,
        showNotifyInfo boolean not null,
        snmpCommunity varchar(255),
        snpMaximum int4 not null,
        timeFormat int4 not null,
        tvAutoProxyFile varchar(128),
        tvProxyIP varchar(128),
        tvProxyPort int4 not null,
        version timestamp,
        virtualHostName varchar(255),
        webSenseServiceHost varchar(255),
        wensenseMode int2 not null,
        windowsDomain varchar(255),
        BARRACUDAWHITELIST_ID int8,
        OWNER int8 not null unique,
        SNMPRECEIVERIP int8,
        WEBSENSEWHITELIST_ID int8,
        primary key (id)
    );

    create table max_clients_count (
        id  bigserial not null,
        currentClientCount int4 not null,
        globalFlg boolean not null,
        maxClientCount int4 not null,
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table network_device_history (
        id  bigserial not null,
        MAC varchar(12) not null,
        beginTimeStamp timestamp with time zone,
        endTimeStamp timestamp with time zone,
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        milliSeconds2GMT int4 not null,
        networkPolicy int8 not null,
        tags character varying(64)[3],
        topologyGroup bigint[],
        vLAN smallint[17],
        OWNER int8 not null,
        primary key (id)
    );

    create table order_history_info (
        id int8 not null,
        activeTime int8 not null,
        cvgStatusFlag int2 not null,
        cvgSubEndDate int8 not null,
        domainName varchar(255),
        licenseType varchar(255),
        numberOfAps int4 not null,
        numberOfCvgs int4 not null,
        numberOfEvalValidDays int4 not null,
        numberOfVhms int4 not null,
        orderKey varchar(64) not null,
        sendEmail boolean not null,
        statusFlag int2 not null,
        subEndDate int8 not null,
        supportEndDate int8 not null,
        version timestamp,
        primary key (id)
    );

    create table ssid_clients_count (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        radioMode int4 not null,
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ssid_clients_count_day (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        radioMode int4 not null,
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ssid_clients_count_hour (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        radioMode int4 not null,
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table ssid_clients_count_week (
        id  bigserial not null,
        apMac varchar(20),
        apName varchar(32),
        clientCount int4 not null,
        radioMode int4 not null,
        ssid varchar(32),
        timeStamp int8 not null,
        version timestamp,
        OWNER int8 not null,
        primary key (id)
    );

    create table sync_task_on_hmol (
        id int8 not null,
        createTime int8 not null,
        syncTime int8 not null,
        syncTimes int4 not null,
        syncType int2 not null,
        version timestamp,
        vhmName varchar(255),
        vhmUsername varchar(255),
        primary key (id)
    );

    create table user_name_devices (
        id  bigserial not null,
        clientDeviceMAC int8 not null,
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        lastSeen timestamp with time zone not null,
        networkDeviceMAC varchar(12) not null,
        userName varchar(128) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table user_name_info (
        id  bigserial not null,
        comment varchar(32),
        eMail varchar(64),
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        lastModification timestamp with time zone not null,
        seen1st timestamp with time zone not null,
        userName varchar(128) not null,
        userProfile varchar(255) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table user_name_seen (
        id  bigserial not null,
        SSId varchar(32) not null,
        authentication char(1),
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        lastSeen timestamp with time zone not null,
        networkDeviceMAC varchar(12) not null,
        userName varchar(128) not null,
        OWNER int8 not null,
        primary key (id)
    );

    create table user_profiles_history (
        attribute int2 not null,
        networkDeviceHistory int8 not null,
        userProfile int8 not null,
        extensionBigInt bigint[],
        extensionByteArray byteA[],
        extensionText text[],
        extensionTimeStamp TimeStamp with Time Zone[],
        OWNER int8 not null,
        primary key (attribute, networkDeviceHistory, userProfile)
    );

    create index ACCESS_CONSOLE_OWNER on ACCESS_CONSOLE (OWNER);

    alter table ACCESS_CONSOLE 
        add constraint FK20CF1D5C9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table ACCESS_CONSOLE_MAC_FILTER 
        add constraint FK28B4374B2B6384A 
        foreign key (ACCESS_CONSOLE_ID) 
        references ACCESS_CONSOLE;

    alter table ACCESS_CONSOLE_MAC_FILTER 
        add constraint FK28B4374B76773CB2 
        foreign key (MAC_FILTER_ID) 
        references MAC_FILTER;

    create index ACTIVE_CLIENT_FILTER_OWNER on ACTIVECLIENT_FILTER (OWNER);

    alter table ACTIVECLIENT_FILTER 
        add constraint FKE715B1269587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index ACTIVE_DIRECTORY_OR_LDAP_OWNER on ACTIVE_DIRECTORY_OR_LDAP (OWNER);

    alter table ACTIVE_DIRECTORY_OR_LDAP 
        add constraint FKDE7417984DED2FCF 
        foreign key (AD_IPADDRESS_ID) 
        references IP_ADDRESS;

    alter table ACTIVE_DIRECTORY_OR_LDAP 
        add constraint FKDE7417988F6F6CB 
        foreign key (LDAP_IPADDRESS_ID) 
        references IP_ADDRESS;

    alter table ACTIVE_DIRECTORY_OR_LDAP 
        add constraint FKDE7417989587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index ADMIN_LOGIN_SESSION_TIME on AH_ADMINLOGIN_SESSION (loginTime);

    create index ADMIN_LOGIN_SESSION_OWNER on AH_ADMINLOGIN_SESSION (OWNER);

    alter table AH_ADMINLOGIN_SESSION 
        add constraint FKC85F13C99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_ALARM_OWNER on AH_ALARM (OWNER);

    create index IDX_ALARM_TYPE_SUBTYPE on AH_ALARM (alarmType, alarmSubType);

    alter table AH_ALARM 
        add constraint FKE6C0F4199587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index ALARMS_FILTER_OWNER on AH_ALARMS_FILTER (OWNER);

    alter table AH_ALARMS_FILTER 
        add constraint FKA64913FD9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CLIENT_EDIT_VALUES_CLIENT_MAC_TYPE on AH_CLIENTEDITVALUES (clientMac, type);

    create index CLIENT_EDIT_VALUES_OWNER on AH_CLIENTEDITVALUES (OWNER);

    create index CLIENT_EDIT_VALUES_TYPE_TIME on AH_CLIENTEDITVALUES (type, expirationTime);

    alter table AH_CLIENTEDITVALUES 
        add constraint FKD6917ACF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CLIENT_SESSION_CLIENT_MAC on AH_CLIENTSESSION (clientMac);

    create index CLIENT_SESSION_AP_MAC on AH_CLIENTSESSION (apMac);

    create index CLIENT_SESSION_OWNER on AH_CLIENTSESSION (OWNER);

    alter table AH_CLIENTSESSION 
        add constraint FK42EFE6339587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CLIENT_SESSION_HISTORY_CLIENT_START_END on AH_CLIENTSESSION_HISTORY (clientMac, startTimeStamp, endTimeStamp);

    create index CLIENT_SESSION_HISTORY_OWNER on AH_CLIENTSESSION_HISTORY (OWNER);

    alter table AH_CLIENTSESSION_HISTORY 
        add constraint FKD7E71A689587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DEVICE_PSE_POWER_OWNER on AH_DEVICE_PSE_POWER (OWNER);

    create index DEVICE_PSE_POWER_OWNER_MAC on AH_DEVICE_PSE_POWER (OWNER, mac);

    alter table AH_DEVICE_PSE_POWER 
        add constraint FK2F938F779587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index EVENT_AP_ID on AH_EVENT (apId);

    create index EVENT_OWNER on AH_EVENT (OWNER);

    create index EVENT_OBJECT_NAME on AH_EVENT (objectName);

    create index EVENT_AP_NAME on AH_EVENT (apName);

    alter table AH_EVENT 
        add constraint FKE6FDEC629587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index EVENTS_FILTER_OWNER on AH_EVENTS_FILTER (OWNER);

    alter table AH_EVENTS_FILTER 
        add constraint FK3D91B3469587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index PORT_AVAILABILITY_MAC_TYPE_NAME_MODE on AH_PORT_AVAILABILITY (mac, interfType, interfName, interfMode);

    create index PORT_AVAILABILITY_OWNER on AH_PORT_AVAILABILITY (OWNER);

    alter table AH_PORT_AVAILABILITY 
        add constraint FKD41B46419587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index PSE_STATUS_OWNER_MAC_NAME on AH_PSE_STATUS (OWNER, mac, interfName);

    create index PSE_STATUS_OWNER on AH_PSE_STATUS (OWNER);

    alter table AH_PSE_STATUS 
        add constraint FK96BEAF479587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index AVAILABILITY_HIGH_OWNER on AH_STATS_AVAILABILITY_HIGH (OWNER);

    create index AVAILABILITY_HIGH_MAC on AH_STATS_AVAILABILITY_HIGH (mac);

    alter table AH_STATS_AVAILABILITY_HIGH 
        add constraint FK16EBAE2E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index AVAILABILITY_LOW_OWNER on AH_STATS_AVAILABILITY_LOW (OWNER);

    create index AVAILABILITY_LOW_MAC on AH_STATS_AVAILABILITY_LOW (mac);

    alter table AH_STATS_AVAILABILITY_LOW 
        add constraint FK5B940D289587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATENCY_HIGH_OWNER on AH_STATS_LATENCY_HIGH (OWNER);

    create index LATENCY_HIGH_MAC on AH_STATS_LATENCY_HIGH (mac);

    alter table AH_STATS_LATENCY_HIGH 
        add constraint FKAEA09B3B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATENCY_LOW_OWNER on AH_STATS_LATENCY_LOW (OWNER);

    create index LATENCY_LOW_MAC on AH_STATS_LATENCY_LOW (mac);

    alter table AH_STATS_LATENCY_LOW 
        add constraint FK5836CA7B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index THROUGHPUT_HIGH_OWNER on AH_STATS_THROUGHPUT_HIGH (OWNER);

    create index THROUGHPUT_HIGH_MAC on AH_STATS_THROUGHPUT_HIGH (mac);

    alter table AH_STATS_THROUGHPUT_HIGH 
        add constraint FKB839DCBF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index THROUGHPUT_LOW_OWNER on AH_STATS_THROUGHPUT_LOW (OWNER);

    create index THROUGHPUT_LOW_MAC on AH_STATS_THROUGHPUT_LOW (mac);

    alter table AH_STATS_THROUGHPUT_LOW 
        add constraint FKF56D48779587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VPN_STATUS_HIGH_MAC on AH_STATS_VPNSTATUS_HIGH (mac);

    create index VPN_STATUS_HIGH_OWNER on AH_STATS_VPNSTATUS_HIGH (OWNER);

    alter table AH_STATS_VPNSTATUS_HIGH 
        add constraint FKD85D4D539587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VPN_STATUS_LOW_MAC on AH_STATS_VPNSTATUS_LOW (mac);

    create index VPN_STATUS_LOW_OWNER on AH_STATS_VPNSTATUS_LOW (OWNER);

    alter table AH_STATS_VPNSTATUS_LOW 
        add constraint FK598F75639587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index USER_LOGIN_SESSION_OWNER on AH_USERLOGIN_SESSION (OWNER);

    create index USER_LOGIN_SESSION_TIME on AH_USERLOGIN_SESSION (loginTime);

    alter table AH_USERLOGIN_SESSION 
        add constraint FK93A2077D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index AIR_SCREEN_ACTION_OWNER on AIR_SCREEN_ACTION (OWNER);

    alter table AIR_SCREEN_ACTION 
        add constraint FKF995A7149587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index AIR_SCREEN_BEHAVIOR_OWNER on AIR_SCREEN_BEHAVIOR (OWNER);

    alter table AIR_SCREEN_BEHAVIOR 
        add constraint FKA78E17B09587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table AIR_SCREEN_GROUP_RULE 
        add constraint FK396F02BAC5845E0E 
        foreign key (rules_id) 
        references AIR_SCREEN_RULE;

    alter table AIR_SCREEN_GROUP_RULE 
        add constraint FK396F02BA3E57C36F 
        foreign key (GROUP_ID) 
        references AIR_SCREEN_RULE_GROUP;

    create index AIR_SCREEN_RULE_OWNER on AIR_SCREEN_RULE (OWNER);

    alter table AIR_SCREEN_RULE 
        add constraint FKF95D635A3A617625 
        foreign key (SOURCE) 
        references AIR_SCREEN_SOURCE;

    alter table AIR_SCREEN_RULE 
        add constraint FKF95D635A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table AIR_SCREEN_RULE_ACTION 
        add constraint FKE1B972BBBEDF4FC2 
        foreign key (actions_id) 
        references AIR_SCREEN_ACTION;

    alter table AIR_SCREEN_RULE_ACTION 
        add constraint FKE1B972BBF44395E9 
        foreign key (RULE_ID) 
        references AIR_SCREEN_RULE;

    alter table AIR_SCREEN_RULE_BEHAVIOR 
        add constraint FK15ED9597F44395E9 
        foreign key (RULE_ID) 
        references AIR_SCREEN_RULE;

    alter table AIR_SCREEN_RULE_BEHAVIOR 
        add constraint FK15ED959725DC8A3A 
        foreign key (behaviors_id) 
        references AIR_SCREEN_BEHAVIOR;

    create index AIR_SCREEN_RULE_GROUP_OWNER on AIR_SCREEN_RULE_GROUP (OWNER);

    alter table AIR_SCREEN_RULE_GROUP 
        add constraint FK7A3593A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index AIR_SCREEN_SOURCE_OWNER on AIR_SCREEN_SOURCE (OWNER);

    alter table AIR_SCREEN_SOURCE 
        add constraint FK18F690F98A77400F 
        foreign key (OUI_ID) 
        references MAC_OR_OUI;

    alter table AIR_SCREEN_SOURCE 
        add constraint FK18F690F99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table AIR_TIGHT_SETTINGS 
        add constraint FK570159F99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index ALG_CONFIGURATION_OWNER on ALG_CONFIGURATION (OWNER);

    alter table ALG_CONFIGURATION 
        add constraint FK5C8913539587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table ALG_CONFIG_INFO 
        add constraint FK62EDD9481BC08128 
        foreign key (ALG_CONFIGURATION_ID) 
        references ALG_CONFIGURATION;

    create index IDX_APPLICATION_OWNER on APPLICATION (OWNER);

    alter table APPLICATION 
        add constraint FKDCF799309587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_APPLICATION_PROFILE_OWNER on APPLICATION_PROFILE (OWNER);

    alter table APPLICATION_PROFILE 
        add constraint FK4E0D257A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table APPPROFILE_APP 
        add constraint FKD4C43DAADF9D6DC2 
        foreign key (APP_ID) 
        references APPLICATION;

    alter table APPPROFILE_APP 
        add constraint FKD4C43DAA80DC2311 
        foreign key (PROFILE_ID) 
        references APPLICATION_PROFILE;

    create index AP_CONNECT_HISTORY_INFO_OWNER on AP_CONNECT_HISTORY_INFO (OWNER);

    create index AP_CONNECT_HISTORY_INFO_TIME on AP_CONNECT_HISTORY_INFO (trapTime);

    alter table AP_CONNECT_HISTORY_INFO 
        add constraint FK235620FE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table ATTRIBUTE_ITEM 
        add constraint FKADA9EF6D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table ATTRIBUTE_ITEM 
        add constraint FKADA9EF694F7914 
        foreign key (ATTRIBUTE_ID) 
        references USER_PROFILE_ATTRIBUTE;

    alter table A_RATE_SETTING_INFO 
        add constraint FKD0E925DE6B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table BONJOUR_ACTIVE_SERVICE 
        add constraint FK98A77658472EACEB 
        foreign key (BONJOUR_GATEWAY_SETTINGS_ID) 
        references BONJOUR_GATEWAY_SETTINGS;

    alter table BONJOUR_ACTIVE_SERVICE 
        add constraint FK98A7765865833EFA 
        foreign key (BONJOUR_SERVICE_ID) 
        references BONJOUR_SERVICE;

    alter table BONJOUR_FILTER_RULE 
        add constraint FKBE2664E72F8C5108 
        foreign key (TO_VLAN_GROUP_ID) 
        references VLAN_GROUP;

    alter table BONJOUR_FILTER_RULE 
        add constraint FKBE2664E751C11BF7 
        foreign key (FROM_VLAN_GROUP_ID) 
        references VLAN_GROUP;

    alter table BONJOUR_FILTER_RULE 
        add constraint FKBE2664E7FCAC7FF1 
        foreign key (BONJOUR_FILTER_ID) 
        references BONJOUR_GATEWAY_SETTINGS;

    alter table BONJOUR_FILTER_RULE 
        add constraint FKBE2664E765833EFA 
        foreign key (BONJOUR_SERVICE_ID) 
        references BONJOUR_SERVICE;

    create index BONJOUR_GATEWAY_MONITORING_OWNER on BONJOUR_GATEWAY_MONITORING (OWNER);

    alter table BONJOUR_GATEWAY_MONITORING 
        add constraint FKBC09273F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index BONJOUR_GATEWAY_SETTINGS_OWNER on BONJOUR_GATEWAY_SETTINGS (OWNER);

    alter table BONJOUR_GATEWAY_SETTINGS 
        add constraint FKD1BA94DA9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index BONJOUR_REALM_OWNER on BONJOUR_REALM (OWNER);

    alter table BONJOUR_REALM 
        add constraint FKAFE446F39587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index BONJOUR_SERVICE_OWNER on BONJOUR_SERVICE (OWNER);

    alter table BONJOUR_SERVICE 
        add constraint FK7DC8A6198E53367B 
        foreign key (BONJOUR_SERVICE_CATEGRORY_ID) 
        references BONJOUR_SERVICE_CATEGORY;

    alter table BONJOUR_SERVICE 
        add constraint FK7DC8A6199587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index BONJOUR_SERVICE_CATEGORY_OWNER on BONJOUR_SERVICE_CATEGORY (OWNER);

    alter table BONJOUR_SERVICE_CATEGORY 
        add constraint FK34CC04649587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table BONJOUR_SERVICE_DETAIL 
        add constraint FK32BB05375AAA3E8B 
        foreign key (BONJOUR_GATEWAY_MONITORING_ID) 
        references BONJOUR_GATEWAY_MONITORING;

    alter table CAPWAPSETTINGS 
        add constraint FK8DBDEDF79587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CLOUD_AUTH_CUSTOMER_OWNER on CLOUD_AUTH_CUSTOMER (OWNER);

    alter table CLOUD_AUTH_CUSTOMER 
        add constraint FKAD7697EB9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index COMPLIANCE_POLICY_OWNER on COMPLIANCE_POLICY (OWNER);

    alter table COMPLIANCE_POLICY 
        add constraint FK8DD135D69587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CONFIG_TEMPLATE_OWNER on CONFIG_TEMPLATE (OWNER);

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D78FCC8276 
        foreign key (ETH0_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D71B39B1C4 
        foreign key (WIRE_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7CEF72BD0 
        foreign key (IP_FILTER_ID) 
        references IP_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D72A8805D2 
        foreign key (RADIUS_SERVER_ID) 
        references RADIUS_ON_HIVEAP;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D740BA6225 
        foreign key (RED0BACK_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7A12CF1F3 
        foreign key (CLIENT_WATCH_ID) 
        references LOCATIONCLIENTWATCH;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D78F3AAB44 
        foreign key (IDS_POLICY_ID) 
        references IDS_POLICY;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7FD99D266 
        foreign key (MGMT_SERVICE_DNS_ID) 
        references MGMT_SERVICE_DNS;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7919407E 
        foreign key (RED0_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D721BD815E 
        foreign key (ETH1BACK_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7D78BE81D 
        foreign key (ETH0BACK_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7C6763859 
        foreign key (NATIVE_VLAN_ID) 
        references VLAN;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7F3C29698 
        foreign key (VPN_SERVICE_ID) 
        references VPN_SERVICE;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D726D66D8E 
        foreign key (MGMT_SERVICE_SYSLOG_ID) 
        references MGMT_SERVICE_SYSLOG;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7AEBEB8DD 
        foreign key (BONJOUR_GATEWAY_ID) 
        references BONJOUR_GATEWAY_SETTINGS;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D72134929D 
        foreign key (HIVE_PROFILE_ID) 
        references HIVE_PROFILE;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7CED7CD2E 
        foreign key (MGMT_SERVICE_SNMP_ID) 
        references MGMT_SERVICE_SNMP;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7BDDBE8E 
        foreign key (RADIUS_ATTRS_ID) 
        references RADIUS_OPERATOR_ATTRIBUTE;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D79587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D75D76E437 
        foreign key (ETH1_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7A8F782CB 
        foreign key (QOS_MARKING_ID) 
        references QOS_MARKING;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D775273323 
        foreign key (appProfileId) 
        references APPLICATION_PROFILE;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7106F4712 
        foreign key (mgt_network_id) 
        references VPN_NETWORK;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D735DCA0FD 
        foreign key (RADIUS_PROXY_ID) 
        references RADIUS_PROXY;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D72B6384A 
        foreign key (ACCESS_CONSOLE_ID) 
        references ACCESS_CONSOLE;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7EA4F4A0E 
        foreign key (AGG0_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D71BC08128 
        foreign key (ALG_CONFIGURATION_ID) 
        references ALG_CONFIGURATION;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D725CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D74245837D 
        foreign key (LOCATION_SERVER_ID) 
        references LOCATION_SERVER;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7C0AD3EE0 
        foreign key (ROUTING_POLICY_ID) 
        references ROUTING_POLICY;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D789A44C6F 
        foreign key (IP_TRACK_ID) 
        references MGMT_SERVICE_IP_TRACK;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D74E9345A9 
        foreign key (QOS_CLASSIFICATION_ID) 
        references QOS_CLASSIFICATION;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7F2653E02 
        foreign key (FIREWALL_POLICY_ID) 
        references FIREWALL_POLICY;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7FB312ECE 
        foreign key (MGMT_SERVICE_TIME_ID) 
        references MGMT_SERVICE_TIME;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7F77DA6A4 
        foreign key (LLDPCDP_ID) 
        references LLDPCDPPROFILE;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D7575330CE 
        foreign key (MGMT_SERVICE_OPTION_ID) 
        references MGMT_SERVICE_OPTION;

    alter table CONFIG_TEMPLATE 
        add constraint FK193880D71DB123B5 
        foreign key (AGG0BACK_SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table CONFIG_TEMPLATE_IP_TRACK 
        add constraint FK7B755E7B74CE17C7 
        foreign key (CONFIG_TEMPLATE_ID) 
        references CONFIG_TEMPLATE;

    alter table CONFIG_TEMPLATE_IP_TRACK 
        add constraint FK7B755E7B64132EB3 
        foreign key (ipTracks_id) 
        references MGMT_SERVICE_IP_TRACK;

    alter table CONFIG_TEMPLATE_LAN 
        add constraint FKD4FA921174CE17C7 
        foreign key (CONFIG_TEMPLATE_ID) 
        references CONFIG_TEMPLATE;

    alter table CONFIG_TEMPLATE_LAN 
        add constraint FKD4FA9211514BBC3 
        foreign key (lanProfiles_id) 
        references LAN_PROFILE;

    alter table CONFIG_TEMPLATE_SSID 
        add constraint FKCA5B21E36B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table CONFIG_TEMPLATE_SSID 
        add constraint FKCA5B21E374CE17C7 
        foreign key (CONFIG_TEMPLATE_ID) 
        references CONFIG_TEMPLATE;

    create index CONFIG_TEMPLATE_STORM_CONTROL_OWNER on CONFIG_TEMPLATE_STORM_CONTROL (OWNER);

    alter table CONFIG_TEMPLATE_STORM_CONTROL 
        add constraint FK2A734E5F74CE17C7 
        foreign key (CONFIG_TEMPLATE_ID) 
        references CONFIG_TEMPLATE;

    alter table CONFIG_TEMPLATE_STORM_CONTROL 
        add constraint FK2A734E5F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table CONFIG_TEMPLATE_TV_SERVICE 
        add constraint FK223D1A401791D6DF 
        foreign key (tvNetworkService_id) 
        references NETWORK_SERVICE;

    alter table CONFIG_TEMPLATE_TV_SERVICE 
        add constraint FK223D1A4074CE17C7 
        foreign key (CONFIG_TEMPLATE_ID) 
        references CONFIG_TEMPLATE;

    create index CWP_OWNER on CWP (OWNER);

    alter table CWP 
        add constraint FK1065C36487063 
        foreign key (CERTIFICATE_ID) 
        references CWP_CERTIFICATE;

    alter table CWP 
        add constraint FK1065C25CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table CWP 
        add constraint FK1065C9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CWP_CERTIFICATE_OWNER on CWP_CERTIFICATE (OWNER);

    alter table CWP_CERTIFICATE 
        add constraint FK9F5D90149587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table CWP_PAGE_FIELD 
        add constraint FK87E2FCED8403D0AB 
        foreign key (CWP_PAGE_CUSTOMIZATION_ID) 
        references CWP;

    alter table DASHBOARD_COMPONENT_DATA 
        add constraint FKF2BCB2F71D80B1E2 
        foreign key (COMPONENT_METRIC_ID) 
        references DASHBOARD_COMPONENT_METRIC;

    create index DASHBOARD_COMPONENT_METRIC_OWNER on DASHBOARD_COMPONENT_METRIC (OWNER);

    alter table DASHBOARD_COMPONENT_METRIC 
        add constraint FK45F015BD9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table DEVICE_AUTO_PROVISION_INTERFACE 
        add constraint FK5F93E548E98072AB 
        foreign key (DEVICE_AUTO_PROVISION_ID) 
        references HIVE_AP_AUTO_PROVISION;

    alter table DEVICE_AUTO_PROVISION_IPSUBNETWORKS 
        add constraint FKA8A63F4DE98072AB 
        foreign key (DEVICE_AUTO_PROVISION_ID) 
        references HIVE_AP_AUTO_PROVISION;

    create index DA_OWNER on DEVICE_DA_INFO (OWNER);

    create index DA_MAC on DEVICE_DA_INFO (macAddress);

    alter table DEVICE_DA_INFO 
        add constraint FK46C8D6479587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DEVICE_INTERFACE_IP_SUB_NETWORK_OWNER on DEVICE_INTERFACE_IPSUBNETWORK (OWNER);

    alter table DEVICE_INTERFACE_IPSUBNETWORK 
        add constraint FK156C4DC49587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table DEVICE_POLICY_RULE 
        add constraint FK9F8E3D8045792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table DEVICE_POLICY_RULE 
        add constraint FK9F8E3D8085894088 
        foreign key (OS_OBJ_ID) 
        references OS_OBJECT;

    alter table DEVICE_POLICY_RULE 
        add constraint FK9F8E3D80882386CB 
        foreign key (MAC_OBJ_ID) 
        references MAC_OR_OUI;

    alter table DEVICE_POLICY_RULE 
        add constraint FK9F8E3D80FD9DB108 
        foreign key (DOMAIN_OBJ_ID) 
        references DOMAIN_OBJECT;

    alter table DHCP_SERVER_CUSTOM 
        add constraint FK5408501F3CE53463 
        foreign key (VLAN_DHCP_SERVER_ID) 
        references VLAN_DHCP_SERVER;

    alter table DHCP_SERVER_IPPOOL 
        add constraint FK5DFD7E713CE53463 
        foreign key (VLAN_DHCP_SERVER_ID) 
        references VLAN_DHCP_SERVER;

    alter table DIRECTORY_OPENLDAP_INFO 
        add constraint FK3A8A7CCAC71C89FF 
        foreign key (DIRECTORY_OPENLDAP_ID) 
        references RADIUS_ON_HIVEAP;

    alter table DIRECTORY_OPENLDAP_INFO 
        add constraint FK3A8A7CCAB0DFE94C 
        foreign key (DIRECTORY_OR_LDAP_ID) 
        references ACTIVE_DIRECTORY_OR_LDAP;

    create index DNS_SERVICE_PROFILE_OWNER on DNS_SERVICE_PROFILE (OWNER);

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9BEBC5C2A 
        foreign key (DOMAIN_OBJECT_ID) 
        references DOMAIN_OBJECT;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9F4F8DC03 
        foreign key (EXTERNAL_DNS2_ID) 
        references IP_ADDRESS;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9F8AE2CF5 
        foreign key (INTERNAL_DNS2_ID) 
        references IP_ADDRESS;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9F8AEA154 
        foreign key (INTERNAL_DNS3_ID) 
        references IP_ADDRESS;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9F8ADB896 
        foreign key (INTERNAL_DNS1_ID) 
        references IP_ADDRESS;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9F4F95062 
        foreign key (EXTERNAL_DNS3_ID) 
        references IP_ADDRESS;

    alter table DNS_SERVICE_PROFILE 
        add constraint FK6044D6A9F4F867A4 
        foreign key (EXTERNAL_DNS1_ID) 
        references IP_ADDRESS;

    alter table DNS_SPECIFIC_SETTINGS 
        add constraint FK92165EDA8C5CFC31 
        foreign key (DNS_SERVICE_ID) 
        references DNS_SERVICE_PROFILE;

    alter table DNS_SPECIFIC_SETTINGS 
        add constraint FK92165EDABD128E3D 
        foreign key (specificDNS) 
        references IP_ADDRESS;

    alter table DOMAIN_NAME_ITEM 
        add constraint FK4C68368CBEBC5C2A 
        foreign key (DOMAIN_OBJECT_ID) 
        references DOMAIN_OBJECT;

    create index DOMAIN_OBJECT_OWNER on DOMAIN_OBJECT (OWNER);

    alter table DOMAIN_OBJECT 
        add constraint FKA432271A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DOS_PREVENTION_OWNER on DOS_PREVENTION (OWNER);

    alter table DOS_PREVENTION 
        add constraint FK6CE842A79587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table DOS_PREVENTION_DOS_PARAMS 
        add constraint FK84CB7915A828BAA4 
        foreign key (DOS_PREVENTION_ID) 
        references DOS_PREVENTION;

    create index ETHERNET_ACCESS_OWNER on ETHERNET_ACCESS (OWNER);

    alter table ETHERNET_ACCESS 
        add constraint FKBCC636CC45792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table ETHERNET_ACCESS 
        add constraint FKBCC636CC9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table ETHERNET_ACCESS_MAC 
        add constraint FKEE22493C517029AE 
        foreign key (ETHERNET_ACCESS_ID) 
        references ETHERNET_ACCESS;

    alter table ETHERNET_ACCESS_MAC 
        add constraint FKEE22493C6BA4E2FB 
        foreign key (MAC_OR_OUI_ID) 
        references MAC_OR_OUI;

    create index FIREWALL_POLICY_OWNER on FIREWALL_POLICY (OWNER);

    alter table FIREWALL_POLICY 
        add constraint FK1D1089519587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92AAE8289E 
        foreign key (DESTINATION_NETWORK_ID) 
        references VPN_NETWORK;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92A4BB0E195 
        foreign key (SOURCE_IP_ID) 
        references IP_ADDRESS;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92A6824EA71 
        foreign key (SOURCE_NETWORK_ID) 
        references VPN_NETWORK;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92AF2653E02 
        foreign key (FIREWALL_POLICY_ID) 
        references FIREWALL_POLICY;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92ABCFACB24 
        foreign key (NETWORK_SERVICE_ID) 
        references NETWORK_SERVICE;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92A9B4985E1 
        foreign key (SOURCE_UP_ID) 
        references USER_PROFILE;

    alter table FIREWALL_POLICY_RULE 
        add constraint FKE476E92A9B60BB48 
        foreign key (DESTINATION_IP_ID) 
        references IP_ADDRESS;

    alter table G_RATE_SETTING_INFO 
        add constraint FKA2E770646B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table HIVEAP_DEVICE_INTERFACE 
        add constraint FK102B8710CBE059E2 
        foreign key (HIVEAP_ID) 
        references HIVE_AP;

    create index HIVE_AP_FILTER_OWNER on HIVEAP_FILTER (OWNER);

    alter table HIVEAP_FILTER 
        add constraint FK5BEB78189587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HIVE_AP_IMAGE_INFO_OWNER on HIVEAP_IMAGE_INFO (OWNER);

    alter table HIVEAP_IMAGE_INFO 
        add constraint FK132B59D29587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HIVE_AP_OWNER on HIVE_AP (OWNER);

    create index HIVE_AP_OWNER_MANAGE_STATUS on HIVE_AP (OWNER, manageStatus);

    alter table HIVE_AP 
        add constraint FK622F1F5EC2E1F89D 
        foreign key (ETHERNET_CWP_ID) 
        references CWP;

    alter table HIVE_AP 
        add constraint FK622F1F5E89C5DBA3 
        foreign key (ETH0_USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5E230C7064 
        foreign key (CVG_NTP_ID) 
        references MGMT_SERVICE_TIME;

    alter table HIVE_AP 
        add constraint FK622F1F5E66BBE26B 
        foreign key (CVG_MGT0_NETWORK_ID) 
        references VPN_NETWORK;

    alter table HIVE_AP 
        add constraint FK622F1F5EB505A93B 
        foreign key (AGG0_USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5E86287687 
        foreign key (CAPWAP_BACKUP_IP_ID) 
        references IP_ADDRESS;

    alter table HIVE_AP 
        add constraint FK622F1F5E2A8805D2 
        foreign key (RADIUS_SERVER_ID) 
        references RADIUS_ON_HIVEAP;

    alter table HIVE_AP 
        add constraint FK622F1F5E421DEC8E 
        foreign key (CAPWAP_IP_ID) 
        references IP_ADDRESS;

    alter table HIVE_AP 
        add constraint FK622F1F5EB988BF74 
        foreign key (ROUTING_PROFILE_ID) 
        references ROUTING_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5E73F960B8 
        foreign key (DEFAULT_ETH_AUTH_USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5E680648DC 
        foreign key (DEFAULT_ETH_REG_USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5EC868473 
        foreign key (SECOND_VPN_GATEWAY_ID) 
        references HIVE_AP;

    alter table HIVE_AP 
        add constraint FK622F1F5EE6C87EA9 
        foreign key (CVG_DNS_ID) 
        references MGMT_SERVICE_DNS;

    alter table HIVE_AP 
        add constraint FK622F1F5E2DBA7BAB 
        foreign key (RED0_USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5EDA6FB9A4 
        foreign key (ETH1_USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5EA017973E 
        foreign key (MAP_CONTAINER_ID) 
        references MAP_NODE;

    alter table HIVE_AP 
        add constraint FK622F1F5E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HIVE_AP 
        add constraint FK622F1F5EBD52F695 
        foreign key (SCHEDULER_ID) 
        references SCHEDULER;

    alter table HIVE_AP 
        add constraint FK622F1F5E35DCA0FD 
        foreign key (RADIUS_PROXY_ID) 
        references RADIUS_PROXY;

    alter table HIVE_AP 
        add constraint FK622F1F5E47070944 
        foreign key (TEMPLATE_ID) 
        references CONFIG_TEMPLATE;

    alter table HIVE_AP 
        add constraint FK622F1F5E979FCA39 
        foreign key (RADIUS_CLIENT_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table HIVE_AP 
        add constraint FK622F1F5ED6944ACC 
        foreign key (WIFI0_RADIO_PROFILE_ID) 
        references RADIO_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5EC0AD3EE0 
        foreign key (ROUTING_POLICY_ID) 
        references ROUTING_POLICY;

    alter table HIVE_AP 
        add constraint FK622F1F5E1D08A5A4 
        foreign key (VPN_IP_TRACK_ID) 
        references MGMT_SERVICE_IP_TRACK;

    alter table HIVE_AP 
        add constraint FK622F1F5E9B262CEB 
        foreign key (WIFI1_RADIO_PROFILE_ID) 
        references RADIO_PROFILE;

    alter table HIVE_AP 
        add constraint FK622F1F5E60E45778 
        foreign key (PPPOE_AUTH_ID) 
        references PPPOE;

    create index HIVE_AP_AUTO_PROVISION_OWNER on HIVE_AP_AUTO_PROVISION (OWNER);

    alter table HIVE_AP_AUTO_PROVISION 
        add constraint FK203651269587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HIVE_AP_AUTO_PROVISION_MACES 
        add constraint FKF91A61C410A63DB3 
        foreign key (HIVE_AP_AUTO_PROVISION_ID) 
        references HIVE_AP_AUTO_PROVISION;

    alter table HIVE_AP_DHCP_SERVER 
        add constraint FK9934DFF0A5161A19 
        foreign key (dhcpServers_id) 
        references VLAN_DHCP_SERVER;

    alter table HIVE_AP_DHCP_SERVER 
        add constraint FK9934DFF056E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_DYNAMIC_ROUTE 
        add constraint FKE861EAE856E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_INTERNAL_NETWORK 
        add constraint FK46FF614D56E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_IP_ROUTE 
        add constraint FKDADE8D256E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_L3CFG_NEIGHBOR 
        add constraint FK6ACD727556E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_LEARNING_MAC 
        add constraint FK640EAFCF56E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_LEARNING_MAC 
        add constraint FK640EAFCFA6986AC4 
        foreign key (LEARNING_MAC_ID) 
        references MAC_OR_OUI;

    alter table HIVE_AP_MULTIPLE_VLAN 
        add constraint FK5A6B5F1156E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    create index HIVE_AP_SERIAL_NUMBER_OWNER on HIVE_AP_SERIAL_NUMBER (OWNER);

    alter table HIVE_AP_SERIAL_NUMBER 
        add constraint FK2ED034D39587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HIVE_AP_SSID_ALLOCATION 
        add constraint FK60F5254356E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_STATIC_ROUTE 
        add constraint FK54A4885956E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    create index HIVE_AP_UPDATE_RESULT_OWNER on HIVE_AP_UPDATE_RESULT (OWNER);

    alter table HIVE_AP_UPDATE_RESULT 
        add constraint FKC98BFD329587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HIVE_AP_UPDATE_RESULT_ITEM 
        add constraint FK8A199E007CD9C5A9 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP_UPDATE_RESULT;

    alter table HIVE_AP_UPDATE_SETTINGS 
        add constraint FK7FBF89587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HIVE_AP_USB_MODEM 
        add constraint FKEAE310CE56E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_USER_PROFILE 
        add constraint FKFE6D585645792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table HIVE_AP_USER_PROFILE 
        add constraint FKFE6D585656E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table HIVE_AP_VIRTUAL_CONNECTION 
        add constraint FK7E6A233356E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    create index HIVE_PROFILE_OWNER on HIVE_PROFILE (OWNER);

    alter table HIVE_PROFILE 
        add constraint FK4B3523BA8B6D17F2 
        foreign key (HIVE_DOS_ID) 
        references DOS_PREVENTION;

    alter table HIVE_PROFILE 
        add constraint FK4B3523BA938131AE 
        foreign key (STATION_DOS_ID) 
        references DOS_PREVENTION;

    alter table HIVE_PROFILE 
        add constraint FK4B3523BA9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HIVE_PROFILE_MAC_FILTER 
        add constraint FK9FA92A2D2134929D 
        foreign key (HIVE_PROFILE_ID) 
        references HIVE_PROFILE;

    alter table HIVE_PROFILE_MAC_FILTER 
        add constraint FK9FA92A2D76773CB2 
        foreign key (MAC_FILTER_ID) 
        references MAC_FILTER;

    alter table HM_ACCESS_CONTROL 
        add constraint FK1E237DC9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_ACCESS_CONTROL_IP 
        add constraint FK3443D86A7983EC 
        foreign key (HM_ACCESS_CONTROL_ID) 
        references HM_ACCESS_CONTROL;

    create index ACSP_NEIGHBOR_OWNER on HM_ACSPNEIGHBOR (OWNER);

    alter table HM_ACSPNEIGHBOR 
        add constraint FK9678BEEB9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index ASSOCIATION_OWNER on HM_ASSOCIATION (OWNER);

    create index ASSOCIATION_CLIENT_MAC_TIME on HM_ASSOCIATION (clientMac, TIME);

    alter table HM_ASSOCIATION 
        add constraint FK99D278279587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_AUDIT_LOG_OWNER on HM_AUDITLOG (OWNER);

    create index HM_AUDIT_LOG_TIME on HM_AUDITLOG (logTimeStamp);

    alter table HM_AUDITLOG 
        add constraint FKC8A223839587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_AUTOREFRESH_SETTINGS 
        add constraint FKB79A0FD078A7ACAB 
        foreign key (HM_USER_ID) 
        references HM_USER;

    create index BANDWIDTH_SENTINEL_HISTORY_TIME on HM_BANDWIDTHSENTINEL_HISTORY (TIME);

    create index BANDWIDTH_SENTINEL_HISTORY_OWNER on HM_BANDWIDTHSENTINEL_HISTORY (OWNER);

    alter table HM_BANDWIDTHSENTINEL_HISTORY 
        add constraint FKFE63CF309587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index CUSTOM_REPORT_OWNER on HM_CUSTOM_REPORT (OWNER);

    alter table HM_CUSTOM_REPORT 
        add constraint FKE300E408D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table HM_CUSTOM_REPORT 
        add constraint FKE300E4089587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_CUSTOM_REPORT_FIELD_TABLE 
        add constraint FK44973A52728F1EE6 
        foreign key (CUSTOM_REPORT_FIELD_ID) 
        references HM_CUSTOM_REPORT_FIELD;

    alter table HM_CUSTOM_REPORT_FIELD_TABLE 
        add constraint FK44973A52791619A9 
        foreign key (CUSTOM_REPORT_ID) 
        references HM_CUSTOM_REPORT;

    create index DASHBOARD_OWNER on HM_DASHBOARD (OWNER);

    alter table HM_DASHBOARD 
        add constraint FK1A43BBAD675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table HM_DASHBOARD 
        add constraint FK1A43BBA9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DASHBOARD_APPAP_OWNER on HM_DASHBOARD_APPAP (OWNER);

    alter table HM_DASHBOARD_APPAP 
        add constraint FK5B68F52B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DASHBOARD_COMPONENT_OWNER on HM_DASHBOARD_COMPONENT (OWNER);

    alter table HM_DASHBOARD_COMPONENT 
        add constraint FK21E88298EB5F744B 
        foreign key (DCOMPONENT_ID) 
        references HM_DASHBOARD_COMPONENT;

    alter table HM_DASHBOARD_COMPONENT 
        add constraint FK21E88298C6DEF2A4 
        foreign key (METRIC_ID) 
        references DASHBOARD_COMPONENT_METRIC;

    alter table HM_DASHBOARD_COMPONENT 
        add constraint FK21E882989587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DASHBOARD_LAYOUT_OWNER on HM_DASHBOARD_LAYOUT (OWNER);

    alter table HM_DASHBOARD_LAYOUT 
        add constraint FK23ABEB4F1C2D6E72 
        foreign key (dashboard_id) 
        references HM_DASHBOARD;

    alter table HM_DASHBOARD_LAYOUT 
        add constraint FK23ABEB4F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index DASHBOARD_WIDGET_OWNER on HM_DASHBOARD_WIDGET (OWNER);

    alter table HM_DASHBOARD_WIDGET 
        add constraint FK36D84A69D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table HM_DASHBOARD_WIDGET 
        add constraint FK36D84A69BD1D7147 
        foreign key (widget_config_id) 
        references HM_DASHBOARD_COMPONENT;

    alter table HM_DASHBOARD_WIDGET 
        add constraint FK36D84A694AC8BB24 
        foreign key (da_layout_id) 
        references HM_DASHBOARD_LAYOUT;

    alter table HM_DASHBOARD_WIDGET 
        add constraint FK36D84A699587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_DOMAIN_VHM_ID on HM_DOMAIN (vhmID);

    alter table HM_FEATURE_PERMISSION 
        add constraint FK7F0D4C5272289F5E 
        foreign key (HM_USER_GROUP_ID) 
        references HM_USER_GROUP;

    alter table HM_INSTANCE_PERMISSION 
        add constraint FKAA09257F72289F5E 
        foreign key (HM_USER_GROUP_ID) 
        references HM_USER_GROUP;

    create index INTERFERENCE_STATS_OWNER on HM_INTERFERENCESTATS (OWNER);

    alter table HM_INTERFERENCESTATS 
        add constraint FKDD0AB8839587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_L3_FIREWALL_LOG_TIMESTAMP on HM_L3FIREWALLLOG (operationTimeStamp);

    create index HM_L3_FIREWALL_LOG_OWNER on HM_L3FIREWALLLOG (OWNER);

    alter table HM_L3FIREWALLLOG 
        add constraint FKEB2FA8A39587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATEST_ACSP_NEIGHBOR_OWNER on HM_LATESTACSPNEIGHBOR (OWNER);

    create index LATEST_ACSP_NEIGHBOR_AP_MAC on HM_LATESTACSPNEIGHBOR (apMac);

    alter table HM_LATESTACSPNEIGHBOR 
        add constraint FKF7B20C729587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATEST_INTERFERENCE_STATS_OWNER on HM_LATESTINTERFERENCESTATS (OWNER);

    create index LATEST_INTERFERENCE_STATS_AP_MAC on HM_LATESTINTERFERENCESTATS (apMac);

    alter table HM_LATESTINTERFERENCESTATS 
        add constraint FKA2F7C75C9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATEST_NEIGHBOR_AP_MAC on HM_LATESTNEIGHBOR (apMac);

    create index LATEST_NEIGHBOR_OWNER on HM_LATESTNEIGHBOR (OWNER);

    alter table HM_LATESTNEIGHBOR 
        add constraint FK69A751F39587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATEST_RADIO_ATTRIBUTE_OWNER on HM_LATESTRADIOATTRIBUTE (OWNER);

    create index LATEST_RADIO_ATTRIBUTE_AP_MAC on HM_LATESTRADIOATTRIBUTE (apMac);

    alter table HM_LATESTRADIOATTRIBUTE 
        add constraint FK4B6E67429587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LATEST_XIF_OWNER on HM_LATESTXIF (OWNER);

    create index LATEST_XIF_AP_MAC on HM_LATESTXIF (apMac);

    alter table HM_LATESTXIF 
        add constraint FKD4818FD49587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_LLDP_INFORMATION_REPORTER_OWNER on HM_LLDP_INFORMATION (reporter, OWNER);

    create index HM_LLDP_INFORMATION_REPORTER on HM_LLDP_INFORMATION (reporter);

    alter table HM_LLDP_INFORMATION 
        add constraint FKE3B94B539587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_LOGIN_AUTHENTICATION 
        add constraint FK6BFFF9288ED6DA8B 
        foreign key (RADIUS_SERVICE_ASSIGN_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table HM_LOGIN_AUTHENTICATION 
        add constraint FK6BFFF9289587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index NEIGHBOR_OWNER on HM_NEIGHBOR (OWNER);

    alter table HM_NEIGHBOR 
        add constraint FKC91428EC9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index NEW_REPORT_OWNER on HM_NEW_REPORT (OWNER);

    alter table HM_NEW_REPORT 
        add constraint FK89CED04DD675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table HM_NEW_REPORT 
        add constraint FK89CED04D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_NTP_SERVER_INTERVAL 
        add constraint FK5E7171129587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index PCI_DATA_OWNER on HM_PCIDATA (OWNER);

    create index PCI_DATA_REPORTTIME on HM_PCIDATA (reportTime);

    alter table HM_PCIDATA 
        add constraint FKD838AC269587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index RADIO_ATTRIBUTE_OWNER on HM_RADIOATTRIBUTE (OWNER);

    alter table HM_RADIOATTRIBUTE 
        add constraint FK53525F7B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index RADIO_STATS_OWNER on HM_RADIOSTATS (OWNER);

    alter table HM_RADIOSTATS 
        add constraint FKC40BC8FE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index REPORT_OWNER on HM_REPORT (OWNER);

    alter table HM_REPORT 
        add constraint FK2348FDAED675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table HM_REPORT 
        add constraint FK2348FDAE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index SPECTRAL_ANALYSIS_AP_MAC_TIMESTAMP on HM_SPECTRAL_ANALYSIS (apMac, timeStamp);

    create index SPECTRAL_ANALYSIS_OWNER on HM_SPECTRAL_ANALYSIS (OWNER);

    alter table HM_SPECTRAL_ANALYSIS 
        add constraint FKF27DBA9D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_START_CONFIG 
        add constraint FKC464AAD99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index SUMMARY_PAGE_OWNER on HM_SUMMARY_PAGE (OWNER);

    alter table HM_SUMMARY_PAGE 
        add constraint FKA86A14A29587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_SYSTEM_LOG_OWNER on HM_SYSTEMLOG (OWNER);

    create index HM_SYSTEM_LOG_TIME on HM_SYSTEMLOG (logTimeStamp);

    alter table HM_SYSTEMLOG 
        add constraint FK83669FDB9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table HM_TABLE_COLUMN 
        add constraint FK2399920178A7ACAB 
        foreign key (HM_USER_ID) 
        references HM_USER;

    alter table HM_TABLE_SIZE 
        add constraint FK6B27516C78A7ACAB 
        foreign key (HM_USER_ID) 
        references HM_USER;

    create index HM_UPGRADE_LOG_OWNER on HM_UPGRADE_LOG (OWNER);

    alter table HM_UPGRADE_LOG 
        add constraint FK312B26279587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_USER_OWNER on HM_USER (OWNER);

    create index HM_USER_NAME on HM_USER (userName);

    alter table HM_USER 
        add constraint FK69886EC5B8291324 
        foreign key (GROUP_ID) 
        references HM_USER_GROUP;

    alter table HM_USER 
        add constraint FK69886EC59587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index HM_USER_GROUP_NAME on HM_USER_GROUP (groupName);

    create index HM_USER_GROUP_OWNER on HM_USER_GROUP (OWNER);

    create index HM_USER_GROUP_ATTRIBUTE on HM_USER_GROUP (groupAttribute);

    alter table HM_USER_GROUP 
        add constraint FK6C54E8659587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index USER_REPORT_OWNER on HM_USER_REPORT (OWNER);

    alter table HM_USER_REPORT 
        add constraint FK30569FEE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VIF_STATS_OWNER on HM_VIFSTATS (OWNER);

    alter table HM_VIFSTATS 
        add constraint FKD7467E669587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VPN_STATUS_OWNER on HM_VPNSTATUS (OWNER);

    alter table HM_VPNSTATUS 
        add constraint FK8FE3630C9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index XIF_OWNER on HM_XIF (OWNER);

    alter table HM_XIF 
        add constraint FK7F46813B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDP_OWNER_STATIONTYPE_SIMULATED on IDP (OWNER, stationType, simulated);

    create index IDP_BSSID_REPORTNODEID on IDP (ifMacAddress, reportNodeId);

    create index IDP_OWNER on IDP (OWNER);

    create index IDP_OWNER_STATIONTYPE_SIMULATED_BSSID on IDP (OWNER, stationType, simulated, ifMacAddress);

    alter table IDP 
        add constraint FK11A959587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table IDP_AP 
        add constraint FK8075CE1918D38E32 
        foreign key (IDP_ID) 
        references IDP;

    alter table IDP_ENCLOSED_FRIENDLY_AP 
        add constraint FK6DC531C3BD099BC4 
        foreign key (IDP_SETTING_ID) 
        references IDP_SETTINGS;

    alter table IDP_ENCLOSED_ROGUE_AP 
        add constraint FK9B31E354BD099BC4 
        foreign key (IDP_SETTING_ID) 
        references IDP_SETTINGS;

    alter table IDP_SETTINGS 
        add constraint FK9EE8DA8D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDS_POLICY_OWNER on IDS_POLICY (OWNER);

    alter table IDS_POLICY 
        add constraint FKE790E0B99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table IDS_POLICY_MAC_OR_OUI 
        add constraint FK7BB6AB1D6BA4E2FB 
        foreign key (MAC_OR_OUI_ID) 
        references MAC_OR_OUI;

    alter table IDS_POLICY_MAC_OR_OUI 
        add constraint FK7BB6AB1D8F3AAB44 
        foreign key (IDS_POLICY_ID) 
        references IDS_POLICY;

    alter table IDS_POLICY_SSID_PROFILE 
        add constraint FK23884A8B6B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table IDS_POLICY_SSID_PROFILE 
        add constraint FK23884A8B8F3AAB44 
        foreign key (IDS_POLICY_ID) 
        references IDS_POLICY;

    alter table IDS_POLICY_VLAN 
        add constraint FKD10EA12925CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table IDS_POLICY_VLAN 
        add constraint FKD10EA1298F3AAB44 
        foreign key (IDS_POLICY_ID) 
        references IDS_POLICY;

    create index INTER_ROAMING_OWNER on INTER_ROAMING (OWNER);

    alter table INTER_ROAMING 
        add constraint FK95147F769587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IP_ADDRESS_OWNER on IP_ADDRESS (OWNER);

    alter table IP_ADDRESS 
        add constraint FK7146C0BC9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table IP_ADDRESS_ITEM 
        add constraint FK531D80B6D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table IP_ADDRESS_ITEM 
        add constraint FK531D80B6AAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    create index IP_FILTER_OWNER on IP_FILTER (OWNER);

    alter table IP_FILTER 
        add constraint FKDAED69909587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table IP_FILTER_IP_ADDRESS 
        add constraint FKE1D912CBCEF72BD0 
        foreign key (IP_FILTER_ID) 
        references IP_FILTER;

    alter table IP_FILTER_IP_ADDRESS 
        add constraint FKE1D912CBAAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    create index IP_POLICY_OWNER on IP_POLICY (OWNER);

    alter table IP_POLICY 
        add constraint FKEC52434A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table IP_POLICY_RULE 
        add constraint FKCF4782D14BB0E195 
        foreign key (SOURCE_IP_ID) 
        references IP_ADDRESS;

    alter table IP_POLICY_RULE 
        add constraint FKCF4782D17711990 
        foreign key (IP_POLICY_ID) 
        references IP_POLICY;

    alter table IP_POLICY_RULE 
        add constraint FKCF4782D1BCFACB24 
        foreign key (NETWORK_SERVICE_ID) 
        references NETWORK_SERVICE;

    alter table IP_POLICY_RULE 
        add constraint FKCF4782D19B60BB48 
        foreign key (DESTINATION_IP_ID) 
        references IP_ADDRESS;

    create index LAN_PROFILE_OWNER on LAN_PROFILE (OWNER);

    alter table LAN_PROFILE 
        add constraint FKC4E58C3C6763859 
        foreign key (NATIVE_VLAN_ID) 
        references VLAN;

    alter table LAN_PROFILE 
        add constraint FKC4E58C353819019 
        foreign key (USERPROFILE_SELFREG_ID) 
        references USER_PROFILE;

    alter table LAN_PROFILE 
        add constraint FKC4E58C3E7EB6CF5 
        foreign key (NATIVE_NETWORK_ID) 
        references VPN_NETWORK;

    alter table LAN_PROFILE 
        add constraint FKC4E58C32BE64EFE 
        foreign key (SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table LAN_PROFILE 
        add constraint FKC4E58C38ED6DA8B 
        foreign key (RADIUS_SERVICE_ASSIGN_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table LAN_PROFILE 
        add constraint FKC4E58C3CB985E5B 
        foreign key (IP_DOS_ID) 
        references DOS_PREVENTION;

    alter table LAN_PROFILE 
        add constraint FKC4E58C390F4D100 
        foreign key (USERPROFILE_DEFAULT_ID) 
        references USER_PROFILE;

    alter table LAN_PROFILE 
        add constraint FKC4E58C39587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table LAN_PROFILE 
        add constraint FKC4E58C390A27B35 
        foreign key (CWP_ID) 
        references CWP;

    alter table LAN_PROFILE_REGULAR_NETWORKS 
        add constraint FK50DFD564F994E743 
        foreign key (LAN_PROFILE_ID) 
        references LAN_PROFILE;

    alter table LAN_PROFILE_REGULAR_NETWORKS 
        add constraint FK50DFD564D9CF1696 
        foreign key (NETWORKS_ID) 
        references VPN_NETWORK;

    alter table LAN_PROFILE_REGULAR_VLAN 
        add constraint FK1891BE42F994E743 
        foreign key (LAN_PROFILE_ID) 
        references LAN_PROFILE;

    alter table LAN_PROFILE_REGULAR_VLAN 
        add constraint FK1891BE4225CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table LAN_PROFILE_SCHEDULER 
        add constraint FKCE394C1FBD52F695 
        foreign key (SCHEDULER_ID) 
        references SCHEDULER;

    alter table LAN_PROFILE_SCHEDULER 
        add constraint FKCE394C1FF994E743 
        foreign key (LAN_PROFILE_ID) 
        references LAN_PROFILE;

    alter table LAN_PROFILE_USER_PROFILE 
        add constraint FK7D134B1145792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table LAN_PROFILE_USER_PROFILE 
        add constraint FK7D134B11F994E743 
        foreign key (LAN_PROFILE_ID) 
        references LAN_PROFILE;

    alter table LAN_RADIUS_USER_GROUP 
        add constraint FK43AC4972F994E743 
        foreign key (LAN_PROFILE_ID) 
        references LAN_PROFILE;

    alter table LAN_RADIUS_USER_GROUP 
        add constraint FK43AC49723EB265DC 
        foreign key (LOCAL_USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    alter table LICENSE_SERVER_SETTING 
        add constraint FK494169129587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LLDP_CDP_PROFILE_OWNER on LLDPCDPPROFILE (OWNER);

    alter table LLDPCDPPROFILE 
        add constraint FK4F83C5A69587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LOCAL_USER_OWNER on LOCAL_USER (OWNER);

    alter table LOCAL_USER 
        add constraint FK6C628C3FD8B831BC 
        foreign key (GROUP_ID) 
        references LOCAL_USER_GROUP;

    alter table LOCAL_USER 
        add constraint FK6C628C3F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LOCAL_USER_GROUP_OWNER on LOCAL_USER_GROUP (OWNER);

    alter table LOCAL_USER_GROUP 
        add constraint FKAE1D825F827E8739 
        foreign key (SCHEDULE_ID) 
        references SCHEDULER;

    alter table LOCAL_USER_GROUP 
        add constraint FKAE1D825F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LOCATION_CLIENT_WATCH_OWNER on LOCATIONCLIENTWATCH (OWNER);

    alter table LOCATIONCLIENTWATCH 
        add constraint FK682808EF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table LOCATIONCLIENT_ITEM 
        add constraint FK689C683258E017BF 
        foreign key (LOCATIONCLIENTWATCH_ID) 
        references LOCATIONCLIENTWATCH;

    alter table LOCATIONCLIENT_ITEM 
        add constraint FK689C6832D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    create index LOCATION_RSSI_REPORT_OWNER on LOCATION_RSSI_REPORT (OWNER);

    create index LOCATION_RSSI_REPORT_CLIENT_REPORTER on LOCATION_RSSI_REPORT (clientMac, reporterMac);

    alter table LOCATION_RSSI_REPORT 
        add constraint FKCE6D04929587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LOCATION_SERVER_OWNER on LOCATION_SERVER (OWNER);

    alter table LOCATION_SERVER 
        add constraint FKDFB56B0D8487C133 
        foreign key (IPADDRESS_ID) 
        references IP_ADDRESS;

    alter table LOCATION_SERVER 
        add constraint FKDFB56B0D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table LOGSETTINGS 
        add constraint FK39B26F479587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index LOCAL_USER_OWNER on MAC_AUTH (OWNER);

    alter table MAC_AUTH 
        add constraint FK24DDCAB89587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index MAC_FILTER_OWNER on MAC_FILTER (OWNER);

    alter table MAC_FILTER 
        add constraint FK6C71B0889587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAC_FILTER_MAC_OR_OUI 
        add constraint FK83333EE6BA4E2FB 
        foreign key (MAC_OR_OUI_ID) 
        references MAC_OR_OUI;

    alter table MAC_FILTER_MAC_OR_OUI 
        add constraint FK83333EE76773CB2 
        foreign key (MAC_FILTER_ID) 
        references MAC_FILTER;

    create index MAC_OR_OUI_OWNER on MAC_OR_OUI (OWNER);

    alter table MAC_OR_OUI 
        add constraint FK7C54B3D79587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAC_OR_OUI_ITEM 
        add constraint FKFFE2467BD675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table MAC_OR_OUI_ITEM 
        add constraint FKFFE2467B6BA4E2FB 
        foreign key (MAC_OR_OUI_ID) 
        references MAC_OR_OUI;

    create index MAC_POLICY_OWNER on MAC_POLICY (OWNER);

    alter table MAC_POLICY 
        add constraint FK7DD68A429587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAC_POLICY_RULE 
        add constraint FKAAB0D6D97016ABD4 
        foreign key (DESTINATION_MAC_ID) 
        references MAC_OR_OUI;

    alter table MAC_POLICY_RULE 
        add constraint FKAAB0D6D9C9CB4F27 
        foreign key (SOURCE_MAC_ID) 
        references MAC_OR_OUI;

    alter table MAC_POLICY_RULE 
        add constraint FKAAB0D6D9AEF12A72 
        foreign key (MAC_POLICY_ID) 
        references MAC_POLICY;

    alter table MAIL_NOTIFICATION 
        add constraint FK66C31E739587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAIL_NOTIFICATION_VHM 
        add constraint FKFA85280F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAP_LINK 
        add constraint FK3B119D1DD06933B5 
        foreign key (PARENT_MAP_ID) 
        references MAP_NODE;

    alter table MAP_LINK 
        add constraint FK3B119D1DC8BAE284 
        foreign key (FROM_NODE_ID) 
        references MAP_NODE;

    alter table MAP_LINK 
        add constraint FK3B119D1D58137855 
        foreign key (TO_NODE_ID) 
        references MAP_NODE;

    create index MAP_NODE_OWNER on MAP_NODE (OWNER);

    create index map_apId on MAP_NODE (apId);

    alter table MAP_NODE 
        add constraint FK3B129B25D06933B5 
        foreign key (PARENT_MAP_ID) 
        references MAP_NODE;

    alter table MAP_NODE 
        add constraint FK3B129B2556E301C3 
        foreign key (HIVE_AP_ID) 
        references HIVE_AP;

    alter table MAP_NODE 
        add constraint FK3B129B259587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAP_PERIMETER 
        add constraint FKA7FF04BA75157280 
        foreign key (MAP_ID) 
        references MAP_NODE;

    alter table MAP_SETTINGS 
        add constraint FKFF0157C69587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MAP_WALL 
        add constraint FK3B167EED75157280 
        foreign key (MAP_ID) 
        references MAP_NODE;

    create index MGMT_SERVICE_DNS_OWNER on MGMT_SERVICE_DNS (OWNER);

    alter table MGMT_SERVICE_DNS 
        add constraint FKDFD091E19587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MGMT_SERVICE_DNS_INFO 
        add constraint FK7631884C4D4D31DC 
        foreign key (MGMT_SERVICE_IP_ADDRESS_ID) 
        references IP_ADDRESS;

    alter table MGMT_SERVICE_DNS_INFO 
        add constraint FK7631884CFD99D266 
        foreign key (MGMT_SERVICE_DNS_ID) 
        references MGMT_SERVICE_DNS;

    create index MGMT_SERVICE_IP_TRACK_OWNER on MGMT_SERVICE_IP_TRACK (OWNER);

    alter table MGMT_SERVICE_IP_TRACK 
        add constraint FKFB46EA3B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index MGMT_SERVICE_OPTION_OWNER on MGMT_SERVICE_OPTION (OWNER);

    alter table MGMT_SERVICE_OPTION 
        add constraint FKA363249D8ED6DA8B 
        foreign key (RADIUS_SERVICE_ASSIGN_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table MGMT_SERVICE_OPTION 
        add constraint FKA363249D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index MGMT_SERVICE_SNMP_OWNER on MGMT_SERVICE_SNMP (OWNER);

    alter table MGMT_SERVICE_SNMP 
        add constraint FK1A487B669587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MGMT_SERVICE_SNMP_INFO 
        add constraint FKAA37C9E7CED7CD2E 
        foreign key (MGMT_SERVICE_SNMP_ID) 
        references MGMT_SERVICE_SNMP;

    alter table MGMT_SERVICE_SNMP_INFO 
        add constraint FKAA37C9E74D4D31DC 
        foreign key (MGMT_SERVICE_IP_ADDRESS_ID) 
        references IP_ADDRESS;

    create index MGMT_SERVICE_SYS_LOG_OWNER on MGMT_SERVICE_SYSLOG (OWNER);

    alter table MGMT_SERVICE_SYSLOG 
        add constraint FKAAB4F17F9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MGMT_SERVICE_SYSLOG_INFO 
        add constraint FK3CE3B6E26D66D8E 
        foreign key (MGMT_SERVICE_SYSLOG_ID) 
        references MGMT_SERVICE_SYSLOG;

    alter table MGMT_SERVICE_SYSLOG_INFO 
        add constraint FK3CE3B6E4D4D31DC 
        foreign key (MGMT_SERVICE_IP_ADDRESS_ID) 
        references IP_ADDRESS;

    create index MGMT_SERVICE_TIME_OWNER on MGMT_SERVICE_TIME (OWNER);

    alter table MGMT_SERVICE_TIME 
        add constraint FK1A48DCF59587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table MGMT_SERVICE_TIME_INFO 
        add constraint FK245109B8FB312ECE 
        foreign key (MGMT_SERVICE_TIME_ID) 
        references MGMT_SERVICE_TIME;

    alter table MGMT_SERVICE_TIME_INFO 
        add constraint FK245109B84D4D31DC 
        foreign key (MGMT_SERVICE_IP_ADDRESS_ID) 
        references IP_ADDRESS;

    alter table MULTICAST_FORWARDING 
        add constraint FK556521C446E355D4 
        foreign key (MGMT_SERVICE_ID) 
        references MGMT_SERVICE_OPTION;

    alter table NEIGHBORS_NAME_ITEM 
        add constraint FKA622F129F53AFA07 
        foreign key (NEIGHBORS_OBJECT_ID) 
        references ROUTING_PROFILE;

    create index NETWORK_SERVICE_OWNER on NETWORK_SERVICE (OWNER);

    alter table NETWORK_SERVICE 
        add constraint FKF1C3F1649587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index NOTIFICATION_MESSAGE_STATUS_OWNER on NOTIFICATION_MESSAGE_STATUS (OWNER);

    alter table NOTIFICATION_MESSAGE_STATUS 
        add constraint FKDE92FD1E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table N_RATE_SETTING_INFO 
        add constraint FK42901CAB6B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    create index ONE_TIME_PASSWORD_OWNER on ONETIME_PASSWORD (OWNER);

    create index ONE_TIME_PASSWORD_PASS on ONETIME_PASSWORD (oneTimePassword);

    create index ONE_TIME_PASSWORD_MAC on ONETIME_PASSWORD (macAddress);

    alter table ONETIME_PASSWORD 
        add constraint FK6F759747E25061C6 
        foreign key (HIVEAPAUTOPROVISION) 
        references HIVE_AP_AUTO_PROVISION;

    alter table ONETIME_PASSWORD 
        add constraint FK6F7597479587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index OS_OBJECT_OWNER on OS_OBJECT (OWNER);

    alter table OS_OBJECT 
        add constraint FK9B2361FA9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table OS_OBJECT_VERSION 
        add constraint FK4D571773B47FF9EA 
        foreign key (OS_OBJECT_ID) 
        references OS_OBJECT;

    alter table OS_OBJECT_VERSION_DHCP 
        add constraint FK7A17513DB47FF9EA 
        foreign key (OS_OBJECT_ID) 
        references OS_OBJECT;

    create index OS_VERSION_OWNER on OS_VERSION (OWNER);

    alter table OS_VERSION 
        add constraint FK412A355D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index PLANNED_AP_OWNER on PLANNED_AP (OWNER);

    alter table PLANNED_AP 
        add constraint FK629D6CAAD06933B5 
        foreign key (PARENT_MAP_ID) 
        references MAP_NODE;

    alter table PLANNED_AP 
        add constraint FK629D6CAA9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table PLAN_TOOL 
        add constraint FK252D28EE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index PPPOE_OWNER on PPPOE (OWNER);

    alter table PPPOE 
        add constraint FK48CEC269587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index PRINT_TEMPLATE_OWNER on PRINT_TEMPLATE (OWNER);

    alter table PRINT_TEMPLATE 
        add constraint FK97FCC1EC9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index QOS_CLASSFIER_AND_MARKER_OWNER on QOS_CLASSFIER_AND_MARKER (OWNER);

    alter table QOS_CLASSFIER_AND_MARKER 
        add constraint FKBB7626639587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index QOS_CLASSIFICATION_OWNER on QOS_CLASSIFICATION (OWNER);

    alter table QOS_CLASSIFICATION 
        add constraint FK261A83709587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table QOS_CLASSIFICATION_MAC 
        add constraint FKC577A3E06BA4E2FB 
        foreign key (MAC_OR_OUI_ID) 
        references MAC_OR_OUI;

    alter table QOS_CLASSIFICATION_MAC 
        add constraint FKC577A3E04E9345A9 
        foreign key (QOS_CLASSIFICATION_ID) 
        references QOS_CLASSIFICATION;

    alter table QOS_CLASSIFICATION_SERVICE 
        add constraint FK7C770CC64E9345A9 
        foreign key (QOS_CLASSIFICATION_ID) 
        references QOS_CLASSIFICATION;

    alter table QOS_CLASSIFICATION_SERVICE 
        add constraint FK7C770CC6BCFACB24 
        foreign key (NETWORK_SERVICE_ID) 
        references NETWORK_SERVICE;

    alter table QOS_CLASSIFICATION_SSID 
        add constraint FKE97FD6EA4E9345A9 
        foreign key (QOS_CLASSIFICATION_ID) 
        references QOS_CLASSIFICATION;

    alter table QOS_CLASSIFICATION_SSID 
        add constraint FKE97FD6EA50A78608 
        foreign key (SSID_ID) 
        references SSID_PROFILE;

    create index QOS_MARKING_OWNER on QOS_MARKING (OWNER);

    alter table QOS_MARKING 
        add constraint FK7A97A98B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index QOS_RATE_CONTROL_OWNER on QOS_RATE_CONTROL (OWNER);

    alter table QOS_RATE_CONTROL 
        add constraint FK2AFAC2E89587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table QOS_RATE_CONTROL_RATE_LIMIT 
        add constraint FKD139053382960198 
        foreign key (QOS_RATE_LIMIT_ID) 
        references QOS_RATE_CONTROL;

    create index RADIO_PROFILE_OWNER on RADIO_PROFILE (OWNER);

    alter table RADIO_PROFILE 
        add constraint FKF5CBCD259587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table RADIO_PROFILE_WMM_INFO 
        add constraint FK41B5AD90B3EC8EC8 
        foreign key (RADIO_PROFILE_ID) 
        references RADIO_PROFILE;

    alter table RADIUS_AD_DOMAIN 
        add constraint FKE63F2CF3E437B33D 
        foreign key (AD_DOMAIN_ID) 
        references ACTIVE_DIRECTORY_OR_LDAP;

    alter table RADIUS_ATTRIBUTE_ITEM 
        add constraint FKD5EA3643D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table RADIUS_ATTRIBUTE_ITEM 
        add constraint FKD5EA3643165DAFD4 
        foreign key (RADIUS_ATTRIBUTE_ID) 
        references RADIUS_OPERATOR_ATTRIBUTE;

    alter table RADIUS_HIVEAP_AUTH 
        add constraint FK7E7BAA1B741D28BA 
        foreign key (AUTH_ID) 
        references RADIUS_ON_HIVEAP;

    alter table RADIUS_HIVEAP_AUTH 
        add constraint FK7E7BAA1BAAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    alter table RADIUS_HIVEAP_LDAP_USER_PROFILE 
        add constraint FK6D51211A320FE175 
        foreign key (LDAP_USER_PROFILE_ID) 
        references RADIUS_ON_HIVEAP;

    alter table RADIUS_HIVEAP_LDAP_USER_PROFILE 
        add constraint FK6D51211A3EB265DC 
        foreign key (LOCAL_USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    create index RADIUS_LIBRARY_SIP_OWNER on RADIUS_LIBRARY_SIP (OWNER);

    alter table RADIUS_LIBRARY_SIP 
        add constraint FK8647CEE92764AE32 
        foreign key (DEFAULT_USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    alter table RADIUS_LIBRARY_SIP 
        add constraint FK8647CEE99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index RADIUS_ON_HIVE_AP_OWNER on RADIUS_ON_HIVEAP (OWNER);

    alter table RADIUS_ON_HIVEAP 
        add constraint FK174BFA92D85EB174 
        foreign key (LIBRARY_SIP_SERVER_ID) 
        references IP_ADDRESS;

    alter table RADIUS_ON_HIVEAP 
        add constraint FK174BFA92FC199088 
        foreign key (LIBRARY_SIP_POLICY_ID) 
        references RADIUS_LIBRARY_SIP;

    alter table RADIUS_ON_HIVEAP 
        add constraint FK174BFA929587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table RADIUS_ON_HIVEAP_LOCAL_USER_GROUP 
        add constraint FK3741206CA03CD790 
        foreign key (RADIUS_ON_HIVEAP_ID) 
        references RADIUS_ON_HIVEAP;

    alter table RADIUS_ON_HIVEAP_LOCAL_USER_GROUP 
        add constraint FK3741206C3EB265DC 
        foreign key (LOCAL_USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    create index RADIUS_OPERATOR_ATTRIBUTE_OWNER on RADIUS_OPERATOR_ATTRIBUTE (OWNER);

    alter table RADIUS_OPERATOR_ATTRIBUTE 
        add constraint FK6DDE796E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index RADIUS_PROXY_OWNER on RADIUS_PROXY (OWNER);

    alter table RADIUS_PROXY 
        add constraint FKBB1160619587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table RADIUS_PROXY_NAS 
        add constraint FK18E4A42235DCA0FD 
        foreign key (RADIUS_PROXY_ID) 
        references RADIUS_PROXY;

    alter table RADIUS_PROXY_NAS 
        add constraint FK18E4A422AAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    alter table RADIUS_PROXY_REALM 
        add constraint FK7286191135DCA0FD 
        foreign key (RADIUS_PROXY_ID) 
        references RADIUS_PROXY;

    alter table RADIUS_PROXY_REALM 
        add constraint FK72861911FD593BC1 
        foreign key (RADIUS_SERVER_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table RADIUS_RULE_USER_PROFILE 
        add constraint FK67CC30B45792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table RADIUS_RULE_USER_PROFILE 
        add constraint FK67CC30B5C4B0AB1 
        foreign key (RADIUS_USER_PROFILE_RULE_ID) 
        references RADIUS_USER_PROFILE_RULE;

    alter table RADIUS_SERVICE 
        add constraint FKC4E7A7E8D9B4E764 
        foreign key (ASSIGNMENT_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table RADIUS_SERVICE 
        add constraint FKC4E7A7E8AAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    create index RADIUS_SERVICE_ASSIGN_OWNER on RADIUS_SERVICE_ASSIGN (OWNER);

    alter table RADIUS_SERVICE_ASSIGN 
        add constraint FK56A295269587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index RADIUS_USER_PROFILE_RULE_OWNER on RADIUS_USER_PROFILE_RULE (OWNER);

    alter table RADIUS_USER_PROFILE_RULE 
        add constraint FK3EAB91799587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index ROUTING_POLICY_OWNER on ROUTING_POLICY (OWNER);

    alter table ROUTING_POLICY 
        add constraint FK302F42EB89A44C6F 
        foreign key (IP_TRACK_ID) 
        references MGMT_SERVICE_IP_TRACK;

    alter table ROUTING_POLICY 
        add constraint FK302F42EB3E8CBCF7 
        foreign key (EXCEPTIONLIST_ID) 
        references DOMAIN_OBJECT;

    alter table ROUTING_POLICY 
        add constraint FK302F42EB9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table ROUTING_POLICY_RULE 
        add constraint FKCB6E1FD0F94E5ADF 
        foreign key (USERPROFILEID) 
        references USER_PROFILE;

    alter table ROUTING_POLICY_RULE 
        add constraint FKCB6E1FD0CA4D879B 
        foreign key (ROUTING_POLICY_RULE_ID) 
        references ROUTING_POLICY;

    alter table ROUTING_POLICY_RULE 
        add constraint FKCB6E1FD0FC66A11D 
        foreign key (IP_TRACK_SEC_ID) 
        references MGMT_SERVICE_IP_TRACK;

    alter table ROUTING_POLICY_RULE 
        add constraint FKCB6E1FD0F8020307 
        foreign key (IP_TRACK_PRI_ID) 
        references MGMT_SERVICE_IP_TRACK;

    create index ROUTING_PROFILE_OWNER on ROUTING_PROFILE (OWNER);

    alter table ROUTING_PROFILE 
        add constraint FKDB00A2F09587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table RPC_SETTINGS 
        add constraint FK77254D5D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index SCHEDULER_OWNER on SCHEDULER (OWNER);

    alter table SCHEDULER 
        add constraint FK9C83D09B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table SCHEDULE_BACKUP 
        add constraint FK29D21C6A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index SERVICE_FILTER_OWNER on SERVICE_FILTER (OWNER);

    alter table SERVICE_FILTER 
        add constraint FK938B48629587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table SIP_POLICY_RULE 
        add constraint FKF16AF5E4680D20DA 
        foreign key (RADIUS_LIBRARY_SIP_ID) 
        references RADIUS_LIBRARY_SIP;

    alter table SIP_POLICY_RULE 
        add constraint FKF16AF5E4DBBB8BB0 
        foreign key (USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    create index SLA_MAPPING_CUSTOMIZE_OWNER on SLA_MAPPING_CUSTOMIZE (OWNER);

    alter table SLA_MAPPING_CUSTOMIZE 
        add constraint FK8B5CB25B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table SSID_LOCAL_USER_GROUP 
        add constraint FKA88CEEA36B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table SSID_LOCAL_USER_GROUP 
        add constraint FKA88CEEA33EB265DC 
        foreign key (LOCAL_USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    create index SSID_PROFILE_OWNER on SSID_PROFILE (OWNER);

    create index SSID_PROFILE_OWNER_SSID on SSID_PROFILE (OWNER, ssid);

    alter table SSID_PROFILE 
        add constraint FK74C70CC53C1DC8FC 
        foreign key (PPSK_CWP_ID) 
        references CWP;

    alter table SSID_PROFILE 
        add constraint FK74C70CC5D6DF1805 
        foreign key (AS_RULE_GROUP_ID) 
        references AIR_SCREEN_RULE_GROUP;

    alter table SSID_PROFILE 
        add constraint FK74C70CC58ED6DA8B 
        foreign key (RADIUS_SERVICE_ASSIGN_ID) 
        references RADIUS_SERVICE_ASSIGN;

    alter table SSID_PROFILE 
        add constraint FK74C70CC5FEE748D1 
        foreign key (CWP_USERPOLICY_ID) 
        references CWP;

    alter table SSID_PROFILE 
        add constraint FK74C70CC590A27B35 
        foreign key (CWP_ID) 
        references CWP;

    alter table SSID_PROFILE 
        add constraint FK74C70CC553819019 
        foreign key (USERPROFILE_SELFREG_ID) 
        references USER_PROFILE;

    alter table SSID_PROFILE 
        add constraint FK74C70CC52BE64EFE 
        foreign key (SERVICE_FILTER_ID) 
        references SERVICE_FILTER;

    alter table SSID_PROFILE 
        add constraint FK74C70CC5938131AE 
        foreign key (STATION_DOS_ID) 
        references DOS_PREVENTION;

    alter table SSID_PROFILE 
        add constraint FK74C70CC5CB985E5B 
        foreign key (IP_DOS_ID) 
        references DOS_PREVENTION;

    alter table SSID_PROFILE 
        add constraint FK74C70CC5F46CA5A 
        foreign key (RADIUS_SERVICE_ASSIGN_ID_PPSK) 
        references RADIUS_SERVICE_ASSIGN;

    alter table SSID_PROFILE 
        add constraint FK74C70CC58CC46187 
        foreign key (SSID_DOS_ID) 
        references DOS_PREVENTION;

    alter table SSID_PROFILE 
        add constraint FK74C70CC590F4D100 
        foreign key (USERPROFILE_DEFAULT_ID) 
        references USER_PROFILE;

    alter table SSID_PROFILE 
        add constraint FK74C70CC59587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table SSID_PROFILE_MAC_FILTER 
        add constraint FK859DBC426B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table SSID_PROFILE_MAC_FILTER 
        add constraint FK859DBC4276773CB2 
        foreign key (MAC_FILTER_ID) 
        references MAC_FILTER;

    alter table SSID_PROFILE_SCHEDULER 
        add constraint FKF82A65A1BD52F695 
        foreign key (SCHEDULER_ID) 
        references SCHEDULER;

    alter table SSID_PROFILE_SCHEDULER 
        add constraint FKF82A65A16B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table SSID_PROFILE_USER_PROFILE 
        add constraint FK4D1AAA4F45792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table SSID_PROFILE_USER_PROFILE 
        add constraint FK4D1AAA4F6B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table SSID_RADIUS_USER_GROUP 
        add constraint FK6D9D62F46B81105E 
        foreign key (SSID_PROFILE_ID) 
        references SSID_PROFILE;

    alter table SSID_RADIUS_USER_GROUP 
        add constraint FK6D9D62F43EB265DC 
        foreign key (LOCAL_USER_GROUP_ID) 
        references LOCAL_USER_GROUP;

    create index SUB_NETWORK_RESOURCE_OWNER on SUB_NETWORK_RESOURCE (OWNER);

    create index SUB_NETWORK_RESOURCE_STATUS on SUB_NETWORK_RESOURCE (status);

    create index SUB_NETWORK_RESOURCE_MAC on SUB_NETWORK_RESOURCE (hiveApMac);

    alter table SUB_NETWORK_RESOURCE 
        add constraint FKDFE1935EC8605ECA 
        foreign key (networkId) 
        references VPN_NETWORK;

    alter table SUB_NETWORK_RESOURCE 
        add constraint FKDFE1935E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table TCA_ALARM 
        add constraint FKEAAC1B649587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table TEMPLATE_FIELD 
        add constraint FK673504D54ED9818 
        foreign key (TEMPLATE_ID) 
        references PRINT_TEMPLATE;

    create index TREX_OWNER on TREX (OWNER);

    alter table TREX 
        add constraint FK276BB1D06933B5 
        foreign key (PARENT_MAP_ID) 
        references MAP_NODE;

    alter table TREX 
        add constraint FK276BB19587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index TUNNEL_SETTING_OWNER on TUNNEL_SETTING (OWNER);

    alter table TUNNEL_SETTING 
        add constraint FKDECDD59AAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    alter table TUNNEL_SETTING 
        add constraint FKDECDD599587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table TUNNEL_SETTING_IP_ADDRESS 
        add constraint FK13E97625AC9204D 
        foreign key (TUNNEL_SETTING_ID) 
        references TUNNEL_SETTING;

    alter table TUNNEL_SETTING_IP_ADDRESS 
        add constraint FK13E9762AAF112E4 
        foreign key (IP_ADDRESS_ID) 
        references IP_ADDRESS;

    create index TV_CLASS_OWNER on TV_CLASS (OWNER);

    alter table TV_CLASS 
        add constraint FK812AB55B30CFCFE5 
        foreign key (CART_ID) 
        references TV_COMPUTER_CART;

    alter table TV_CLASS 
        add constraint FK812AB55B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table TV_CLASS_SCHEDULE 
        add constraint FKFB9DD4FBE7B90D67 
        foreign key (TV_CLASS_ID) 
        references TV_CLASS;

    create index TV_COMPUTER_CART_OWNER on TV_COMPUTER_CART (OWNER);

    alter table TV_COMPUTER_CART 
        add constraint FK14AB46C79587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table TV_COMPUTER_CART_MAC 
        add constraint FKACB8E3B7C631E328 
        foreign key (TV_CART_ID) 
        references TV_COMPUTER_CART;

    create index TV_RESOURCE_MAP_OWNER on TV_RESOURCE_MAP (OWNER);

    alter table TV_RESOURCE_MAP 
        add constraint FK526958289587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index TV_SCHEDULE_MAP_OWNER on TV_SCHEDULE_MAP (OWNER);

    alter table TV_SCHEDULE_MAP 
        add constraint FKAE97BA319587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table TV_SCHEDULE_PERIODTIME 
        add constraint FK24484D79E58C2E9E 
        foreign key (TV_SCHEDULE_MAP_ID) 
        references TV_SCHEDULE_MAP;

    alter table TV_SCHEDULE_WEEKDAY 
        add constraint FKE237097DE58C2E9E 
        foreign key (TV_SCHEDULE_MAP_ID) 
        references TV_SCHEDULE_MAP;

    create index TV_STUDENT_ROSTER_OWNER on TV_STUDENT_ROSTER (OWNER);

    alter table TV_STUDENT_ROSTER 
        add constraint FK7C315C8CD0D8B84A 
        foreign key (CLASS_ID) 
        references TV_CLASS;

    alter table TV_STUDENT_ROSTER 
        add constraint FK7C315C8C9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index USB_MODEM_PARAMETER_OWNER on USB_MODEM_PARAMETER (OWNER);

    alter table USB_MODEM_PARAMETER 
        add constraint FK388C53B99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table USB_MODEM_SIGNAL_STRENGTH_CHECK 
        add constraint FK5F5ACA11B3FA74D8 
        foreign key (USB_MODEM_ID) 
        references USB_MODEM_PARAMETER;

    alter table USER_LOCALUSERGROUP 
        add constraint FK1635F97D8D1ECDE5 
        foreign key (USER_ID) 
        references HM_USER;

    alter table USER_LOCALUSERGROUP 
        add constraint FK1635F97D712B3972 
        foreign key (LOCALUSERGROUP_ID) 
        references LOCAL_USER_GROUP;

    create index USER_PROFILE_OWNER on USER_PROFILE (OWNER);

    alter table USER_PROFILE 
        add constraint FKCEC2A515B7604B76 
        foreign key (QOS_RATE_CONTROL_ID) 
        references QOS_RATE_CONTROL;

    alter table USER_PROFILE 
        add constraint FKCEC2A5152CC2CD27 
        foreign key (IP_POLICE_FROM_ID) 
        references IP_POLICY;

    alter table USER_PROFILE 
        add constraint FKCEC2A5159E69E7F1 
        foreign key (IDENTITY_BASED_TUNNEL_ID) 
        references TUNNEL_SETTING;

    alter table USER_PROFILE 
        add constraint FKCEC2A515CFEAAA76 
        foreign key (IP_POLICE_TO_ID) 
        references IP_POLICY;

    alter table USER_PROFILE 
        add constraint FKCEC2A515616216CD 
        foreign key (MAC_POLICY_FROM_ID) 
        references MAC_POLICY;

    alter table USER_PROFILE 
        add constraint FKCEC2A5159CC4BF1C 
        foreign key (MAC_POLICY_TO_ID) 
        references MAC_POLICY;

    alter table USER_PROFILE 
        add constraint FKCEC2A515BFA9A9A6 
        foreign key (ATTRITUTE_GROUP_ID) 
        references USER_PROFILE_ATTRIBUTE;

    alter table USER_PROFILE 
        add constraint FKCEC2A515D6DF1805 
        foreign key (AS_RULE_GROUP_ID) 
        references AIR_SCREEN_RULE_GROUP;

    alter table USER_PROFILE 
        add constraint FKCEC2A51535ACB7F8 
        foreign key (VPN_NETWORK_ID) 
        references VPN_NETWORK;

    alter table USER_PROFILE 
        add constraint FKCEC2A51525CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table USER_PROFILE 
        add constraint FKCEC2A5159587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index USER_PROFILE_ATTRIBUTE_OWNER on USER_PROFILE_ATTRIBUTE (OWNER);

    alter table USER_PROFILE_ATTRIBUTE 
        add constraint FKB05BC3D29587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table USER_PROFILE_SCHEDULER 
        add constraint FKA5FFD9F145792A2B 
        foreign key (USER_PROFILE_ID) 
        references USER_PROFILE;

    alter table USER_PROFILE_SCHEDULER 
        add constraint FKA5FFD9F1BD52F695 
        foreign key (SCHEDULER_ID) 
        references SCHEDULER;

    alter table USER_REG_INFO_FOR_LS 
        add constraint FK6256A1CF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table USER_SSIDPROFILE 
        add constraint FK9AE1333A6DFE4ED5 
        foreign key (SSIDPROFILE_ID) 
        references SSID_PROFILE;

    alter table USER_SSIDPROFILE 
        add constraint FK9AE1333A8D1ECDE5 
        foreign key (USER_ID) 
        references HM_USER;

    create index VIEWING_CLASS_OWNER on VIEWING_CLASS (OWNER);

    alter table VIEWING_CLASS 
        add constraint FKB82FCD969587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VLAN_OWNER on VLAN (OWNER);

    alter table VLAN 
        add constraint FK283D639587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VLAN_DHCP_SERVER_OWNER on VLAN_DHCP_SERVER (OWNER);

    alter table VLAN_DHCP_SERVER 
        add constraint FKD001E759587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index VLAN_GROUP_OWNER on VLAN_GROUP (OWNER);

    alter table VLAN_GROUP 
        add constraint FK397AC8839587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table VLAN_ITEM 
        add constraint FK9EC2D46FD675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table VLAN_ITEM 
        add constraint FK9EC2D46F25CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table VPN_GATEWAY_SETTING 
        add constraint FKC2A429AA463346F8 
        foreign key (VPN_GATEWAY_SETTING_ID) 
        references VPN_SERVICE;

    create index VPN_NETWORK_OWNER on VPN_NETWORK (OWNER);

    alter table VPN_NETWORK 
        add constraint FKADDDC5A3F3740612 
        foreign key (VPN_DNS_ID) 
        references DNS_SERVICE_PROFILE;

    alter table VPN_NETWORK 
        add constraint FKADDDC5A325CE4881 
        foreign key (VLAN_ID) 
        references VLAN;

    alter table VPN_NETWORK 
        add constraint FKADDDC5A39587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table VPN_NETWORK_CUSTOM 
        add constraint FKF403A98D3BF5004E 
        foreign key (VPN_NETWORK_CUSTOM_ID) 
        references VPN_NETWORK;

    alter table VPN_NETWORK_IP_RESERVE_ITEM 
        add constraint FK1DDDC252D675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table VPN_NETWORK_IP_RESERVE_ITEM 
        add constraint FK1DDDC2522EE7D6A3 
        foreign key (VPN_NETWORK_RESERVECLASS_ID) 
        references VPN_NETWORK;

    alter table VPN_NETWORK_SUBITEM 
        add constraint FKD9E0BE971B9D6857 
        foreign key (VPN_NETWORK_SUB_ID) 
        references VPN_NETWORK;

    alter table VPN_NETWORK_SUBNETCLASS 
        add constraint FK6EB9537F87B0C7C 
        foreign key (VPN_NETWORK_SUBNETCLASS_ID) 
        references VPN_NETWORK;

    alter table VPN_NETWORK_SUBNETCLASS 
        add constraint FK6EB9537FD675AD87 
        foreign key (LOCATION_ID) 
        references MAP_NODE;

    alter table VPN_NETWORK_SUBNET_CUSTOMS 
        add constraint FKB346EE3C96928C24 
        foreign key (VPN_NETWORK_SUBNET_CUSTOM_ID) 
        references VPN_NETWORK;

    create index VPN_SERVICE_OWNER on VPN_SERVICE (OWNER);

    alter table VPN_SERVICE 
        add constraint FKB640322AE17CF9A1 
        foreign key (DOMAINOBJECT_ID) 
        references DOMAIN_OBJECT;

    alter table VPN_SERVICE 
        add constraint FKB640322A99964783 
        foreign key (DNS_IP) 
        references IP_ADDRESS;

    alter table VPN_SERVICE 
        add constraint FKB640322A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table VPN_SERVICE_CREDENTIAL 
        add constraint FKC3DD98CF3C29698 
        foreign key (VPN_SERVICE_ID) 
        references VPN_SERVICE;

    alter table VPN_USERPROFILE_TRAFFICL2 
        add constraint FK3BFE6F37FD7C9A6B 
        foreign key (VPN_USERPROFILE_TRAFFICL2_ID) 
        references VPN_SERVICE;

    alter table VPN_USERPROFILE_TRAFFICL2 
        add constraint FK3BFE6F37F94E5ADF 
        foreign key (USERPROFILEID) 
        references USER_PROFILE;

    alter table VPN_USERPROFILE_TRAFFICL3 
        add constraint FK3BFE6F38F94E5ADF 
        foreign key (USERPROFILEID) 
        references USER_PROFILE;

    alter table VPN_USERPROFILE_TRAFFICL3 
        add constraint FK3BFE6F38FD7D0ECA 
        foreign key (VPN_USERPROFILE_TRAFFICL3_ID) 
        references VPN_SERVICE;

    alter table WALLED_GARDEN_ITEM 
        add constraint FK37FBFB877F5D3065 
        foreign key (WALLED_GARDEN_ITEM_SERVER_ID) 
        references IP_ADDRESS;

    alter table WALLED_GARDEN_ITEM 
        add constraint FK37FBFB87CD301006 
        foreign key (WALLED_GARDEN_ID) 
        references CWP;

    create index IDX_NEWSLA_OWNER on ah_new_sla_stats (OWNER);

    create index IDX_NEWSLA_TIMESTAMP on ah_new_sla_stats (timeStamp, apMac, OWNER);

    alter table ah_new_sla_stats 
        add constraint FK4F4990D19587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_NEWSLA_DAY_TIMESTAMP on ah_new_sla_stats_day (timeStamp, apMac, OWNER);

    create index IDX_NEWSLA_DAY_OWNER on ah_new_sla_stats_day (OWNER);

    alter table ah_new_sla_stats_day 
        add constraint FK7BAF732E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_NEWSLA_HOUR_TIMESTAMP on ah_new_sla_stats_hour (timeStamp, apMac, OWNER);

    create index IDX_NEWSLA_HOUR_OWNER on ah_new_sla_stats_hour (OWNER);

    alter table ah_new_sla_stats_hour 
        add constraint FKFA40F8929587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_NEWSLA_WEEK_TIMESTAMP on ah_new_sla_stats_week (timeStamp, apMac, OWNER);

    create index IDX_NEWSLA_WEEK_OWNER on ah_new_sla_stats_week (OWNER);

    alter table ah_new_sla_stats_week 
        add constraint FKFA47A2A29587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table ah_report_compliance 
        add constraint FK40AB01EE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_SLA_OWNER on ah_sla_stats (OWNER);

    create index IDX_SLA_TIMESTAMP on ah_sla_stats (timeStamp, OWNER);

    alter table ah_sla_stats 
        add constraint FK3019A8B09587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index idx_clientdeviceinfo_mac on client_device_info (MAC);

    create index idx_clientdeviceinfo_vendor on client_device_info (vendor);

    create index idx_clientdeviceinfo_owner on client_device_info (OWNER);

    alter table client_device_info 
        add constraint FKA4157E839587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index idx_clientdeviceseen_mac on client_device_seen (MAC);

    create index idx_clientdeviceseen_SSId on client_device_seen (SSId);

    create index idx_clientdeviceseen_networkDeviceMac on client_device_seen (networkDeviceMac);

    create index idx_clientdeviceseen_owner on client_device_seen (OWNER);

    alter table client_device_seen 
        add constraint FKA419E8509587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_OSINFO_CLIENTCOUNT_OWNER on clients_osinfo_count (OWNER);

    create index IDX_OSINFO_CLIENTCOUNT_TIMESTAMP on clients_osinfo_count (timeStamp, apMac, OWNER);

    alter table clients_osinfo_count 
        add constraint FK83454FF99587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_OSINFO_CLIENTCOUNT_DAY_OWNER on clients_osinfo_count_day (OWNER);

    create index IDX_OSINFO_CLIENTCOUNT_DAY_TIMESTAMP on clients_osinfo_count_day (timeStamp, apMac, OWNER);

    alter table clients_osinfo_count_day 
        add constraint FKBFE75E569587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_OSINFO_CLIENTCOUNT_HOUR_TIMESTAMP on clients_osinfo_count_hour (timeStamp, apMac, OWNER);

    create index IDX_OSINFO_CLIENTCOUNT_HOUR_OWNER on clients_osinfo_count_hour (OWNER);

    alter table clients_osinfo_count_hour 
        add constraint FK3D06726A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_OSINFO_CLIENTCOUNT_WEEK_TIMESTAMP on clients_osinfo_count_week (timeStamp, apMac, OWNER);

    create index IDX_OSINFO_CLIENTCOUNT_WEEK_OWNER on clients_osinfo_count_week (OWNER);

    alter table clients_osinfo_count_week 
        add constraint FK3D0D1C7A9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_APPDATAHOUR_OWNER on hm_appdata_hour (owner);

    create index IDX_APPDATAHOUR_TIMESTAMP on hm_appdata_hour (timeStamp, apMac, application);

    create index IDX_APPDATASECONDS_TIMESTAMP on hm_appdata_seconds (timeStamp, apMac, application);

    create index IDX_APPDATASECONDS_OWNER on hm_appdata_seconds (owner);

    create index IDX_CLIENT_STATS_OWNER on hm_client_stats (OWNER);

    create index IDX_CLIENT_APMAC_AND_TIMESTAMP on hm_client_stats (timeStamp, apMac, OWNER);

    create index IDX_CLIENT_MAC_AND_TIMESTAMP on hm_client_stats (timeStamp, clientMac, OWNER);

    alter table hm_client_stats 
        add constraint FKEE3A44259587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_CLIENT_STATS_DAY_OWNER on hm_client_stats_day (OWNER);

    create index IDX_CLIENT_STATS_DAY_TIMESTAMP on hm_client_stats_day (timeStamp, apMac, OWNER);

    alter table hm_client_stats_day 
        add constraint FK1AE5C829587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_CLIENT_STATS_HOUR_OWNER on hm_client_stats_hour (OWNER);

    create index IDX_CLIENT_STATS_HOUR_TIMESTAMP on hm_client_stats_hour (timeStamp, apMac, OWNER);

    alter table hm_client_stats_hour 
        add constraint FK341F39BE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_CLIENT_STATS_WEEK_OWNER on hm_client_stats_week (OWNER);

    create index IDX_CLIENT_STATS_WEEK_TIMESTAMP on hm_client_stats_week (timeStamp, apMac, OWNER);

    alter table hm_client_stats_week 
        add constraint FK3425E3CE9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table hm_cpu_memory_usage 
        add constraint FKB46C52149587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_INTERFACESTATS_OWNER on hm_device_stats (OWNER);

    create index IDX_INTERFACESTATS_TIMESTAMP on hm_device_stats (timeStamp, apMac, OWNER);

    alter table hm_device_stats 
        add constraint FKC4D1A709587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_INTERFACESTATS_OWNER on hm_interface_stats (OWNER);

    create index IDX_INTERFACESTATS_TIMESTAMP on hm_interface_stats (timeStamp, apMac, OWNER);

    alter table hm_interface_stats 
        add constraint FK7212BEDF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_INTERFACESTATS_DAY_OWNER on hm_interface_stats_day (OWNER);

    create index IDX_INTERFACESTATS_DAY_TIMESTAMP on hm_interface_stats_day (timeStamp, apMac, OWNER);

    alter table hm_interface_stats_day 
        add constraint FK9AF6EA3C9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_INTERFACESTATS_HOUR_TIMESTAMP on hm_interface_stats_hour (timeStamp, apMac, OWNER);

    create index IDX_INTERFACESTATS_HOUR_OWNER on hm_interface_stats_hour (OWNER);

    alter table hm_interface_stats_hour 
        add constraint FKC3E863449587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_INTERFACESTATS_WEEK_TIMESTAMP on hm_interface_stats_week (timeStamp, apMac, OWNER);

    create index IDX_INTERFACESTATS_WEEK_OWNER on hm_interface_stats_week (OWNER);

    alter table hm_interface_stats_week 
        add constraint FKC3EF0D549587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    alter table hmservicessettings 
        add constraint FK8E5FF186A0006DB7 
        foreign key (WEBSENSEWHITELIST_ID) 
        references DOMAIN_OBJECT;

    alter table hmservicessettings 
        add constraint FK8E5FF186554ABB4E 
        foreign key (BARRACUDAWHITELIST_ID) 
        references DOMAIN_OBJECT;

    alter table hmservicessettings 
        add constraint FK8E5FF18652C70A9A 
        foreign key (SNMPRECEIVERIP) 
        references IP_ADDRESS;

    alter table hmservicessettings 
        add constraint FK8E5FF1869587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index MAX_CLIENTS_COUNT_OWNER on max_clients_count (OWNER);

    create index MAX_CLIENTS_COUNT_TIME on max_clients_count (timeStamp);

    alter table max_clients_count 
        add constraint FKE1C4CC7D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index idx_networkdevicehistory_mac on network_device_history (MAC);

    create index idx_networkdevicehistory_owner on network_device_history (OWNER);

    alter table network_device_history 
        add constraint FKC9DBA5DC9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_SSID_CLIENTCOUNT_OWNER on ssid_clients_count (OWNER);

    create index IDX_SSID_CLIENTCOUNT_TIMESTAMP on ssid_clients_count (timeStamp, apMac, OWNER);

    alter table ssid_clients_count 
        add constraint FK1AB336549587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_SSID_CLIENTCOUNT_DAY_TIMESTAMP on ssid_clients_count_day (timeStamp, apMac, OWNER);

    create index IDX_SSID_CLIENTCOUNT_DAY_OWNER on ssid_clients_count_day (OWNER);

    alter table ssid_clients_count_day 
        add constraint FK86F49F319587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_SSID_CLIENTCOUNT_HOUR_OWNER on ssid_clients_count_hour (OWNER);

    create index IDX_SSID_CLIENTCOUNT_HOUR_TIMESTAMP on ssid_clients_count_hour (timeStamp, apMac, OWNER);

    alter table ssid_clients_count_hour 
        add constraint FK57A14CEF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index IDX_SSID_CLIENTCOUNT_WEEK_OWNER on ssid_clients_count_week (OWNER);

    create index IDX_SSID_CLIENTCOUNT_WEEK_TIMESTAMP on ssid_clients_count_week (timeStamp, apMac, OWNER);

    alter table ssid_clients_count_week 
        add constraint FK57A7F6FF9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index sctk_idx1 on sync_task_on_hmol (vhmName, createTime);

    create index idx_usernamedevices_username on user_name_devices (userName);

    create index idx_usernamedevices_owner on user_name_devices (OWNER);

    create index idx_usernamedevices_networkdevicemac on user_name_devices (networkDeviceMAC);

    alter table user_name_devices 
        add constraint FKE1E7531D9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index idx_usernameinfo_owner on user_name_info (OWNER);

    create index idx_usernameinfo_username on user_name_info (userName);

    alter table user_name_info 
        add constraint FKA6EE666E9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index idx_usernameseen_networkdevicemac on user_name_seen (networkDeviceMAC);

    create index idx_UserNameSeen_username on user_name_seen (userName);

    create index idx_usernameseen_SSId on user_name_seen (SSId);

    create index idx_usernameinfo_owner on user_name_seen (OWNER);

    alter table user_name_seen 
        add constraint FKA6F2D03B9587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create index idx_userprofileshistory_owner on user_profiles_history (OWNER);

    alter table user_profiles_history 
        add constraint FKCE98D3339587E4C2 
        foreign key (OWNER) 
        references HM_DOMAIN;

    create sequence hibernate_sequence;
