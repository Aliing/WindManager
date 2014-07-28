/**
 *@filename		CommConst.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.ls.util;

public class CommConst {

	public static final String Charset_Name = "iso-8859-1";

	public static final int Type_Util_Length = 1;
	public static final int Packet_Length_Util_Length = 4;
	public static final int Protocol_Version_Util_Length = 1;
	public static final int Packet_Secret_Flag_Length = 1;
	public static final int Data_Length_Util_Length = 2;
	public static final int Act_Code_Util_Length = 4;
	public static final int Act_key_Util_Length = 41;
	public static final int IP_Util_Length = 4;
	public static final int System_ID_Util_Length = 39;
	public static final int Period_Util_Length = 2;
	public static final int Retry_Times_Util_Length = 1;
	public static final int Retry_Interval_Util_Length = 4;
	public static final int New_Version_Flag_Util_Length = 1;
	public static final int Info_Length_Util_Length = 1;
	public static final int Update_Limit_Util_Length = 1;
	public static final int Inner_Ver_Util_Length = 3;
	public static final int Operate_Util_Length = 1;
	public static final int Version_Count_Util_Length = 1;
	public static final int Description_Legth_Util_Length = 1;
	public static final int Trap_Severity_Util_Length = 1;
	public static final int Trap_Time_Util_Length = 8;
	public static final int Trap_Reason_Util_Length = 2;
	public static final int File_Size_Util_Length = 8;
	public static final int Ap_Count_Util_Length = 4;
	public static final int Ap_Client_Type_Count_Util_Length = 2;
	public static final int Ap_Client_Mac_Prefix_Util_Length = 6;
	public static final int Ap_Client_Count_Util_Length = 4;

	public static final int Ordery_key_HMTYPE_UNIT_LENGTH = 1;
	public static final int User_Manager_Flag_Unit_length = 1;

	public static final int Common_String_Length_Unit_Length = 1;
	public static final int Common_INT_Length_Unit_Length = 4;
	public static final int Common_Type_Unit_Length = 1;
	public static final int Common_Err_Code_Length_Unit_Length = 1;

	public static final int Upload_Send_Status_Util_Length = 1;
	public static final int Upload_Offset_Util_Length = 8;
	public static final int Upload_Data_Size_Util_Length = 4;

	public static final byte HM_Update_Limit = 2;

	// product type
	public static final byte Product_Type_UNKNOW = 0;
	public static final byte Product_Type_HIVEAP = 1;
	public static final byte Product_Type_1U_HM = 2;
	public static final byte Product_Type_2U_HM = 3;
	public static final byte Product_Type_GM = 4;

	// data type
	public static final byte Act_Query_Packet_Type = 1;
	public static final byte Act_Query_Data_Type = 1;

	public static final byte Act_Response_Packet_Type = 2;
	public static final byte Act_Response_Data_Type = 1;
	public static final byte Act_Response_Deny_Data_Type = 2;

	public static final byte Download_Query_Packet_Type = 3;
	public static final byte Download_Query_Data_Type = 1;

	public static final byte Download_Response_Packet_Type = 4;
	public static final byte Download_Response_Data_Type = 1;
	public static final byte Download_Response_Deny_Type = 2;

	public static final byte Upload_Request_Packet_Type = 5;
	public static final byte Upload_Statistic_Info_Data_Type = 1;
	public static final byte Upload_Response_Packet_Type = 6;

	public static final byte New_Version_Query_Packet_Type = 7;
	public static final byte New_version_Response_Packet_Type = 8;

	public static final byte Ap_Summary_Info_Packet_Type = 9;
	public static final byte Ap_Client_Info_Packet_Type = 10;

	public static final byte Order_Key_Query_Packet_Type = 11;
	public static final byte Order_key_Query_Data_Type = 1;
	public static final byte Order_Key_Response_Packet_Type = 12;
	public static final byte Order_key_Response_Data_type = 1;
	public static final byte Order_key_err_Response_Data_Type = 2;
	
	public static final byte Order_Key_Err_Packet_Type = 31;
	public static final byte Order_Key_Err_Data_Type = 1;
	public static final byte Order_Key_Err_Response_Packet_Type = 32;
	public static final byte Order_Key_Err_Response_Data_Type = 1;
	public static final byte Order_Key_Err_err_Response_Data_Type = 2;

	public static final byte Vm_Verify_Query_Packet_Type = 13;
	public static final byte Vm_verify_Query_Data_Type = 1;
	public static final byte Vm_Verify_Response_Packet_Type = 14;
	public static final byte Vm_Verify_Response_Data_Type = 2;

	public static final byte Version_List_Query_Packet_Type = 15;
	public static final byte Version_List_Query_Data_Type = 1;
	public static final byte Version_List_Response_Packet_Type = 16;
	public static final byte Version_List_Response_Data_Type = 1;
	public static final byte Version_List_Response_Deny_Type = 2;

	// 17,18 has be used by portal user register request
	public static final byte PacketType_CheckConnection_Request = 19;

	public static final byte PacketType_ApConnectStat_UploadRequest = 21;

	public static final byte PacketType_ApUsageStat_UploadRequest = 23;
	
	// send end user info to license server
	public static final byte User_Reg_Info_Packet_Type = 24;

	public static final byte PacketType_Error_Response = 91;

	public static final byte PacketType_Sample_Request = 98;
	public static final byte PacketType_Sample_Response = PacketType_Sample_Request + 1;

	public static final byte Trap_Inform_Packet_Type = 100;
	public static final byte Trap_Receive_Packet_Type = 101;

	public static final byte Need_Act_Key_Packet_Type = 1;
	public static final byte Not_Need_Act_Key_Packet_Type = 2;

	// hm type (whole HM, VHM)
	public static final int HM_TYPE1_WHOLEHM = 0x10;
	public static final int HM_TYPE1_VHM = 0x20;

	// hm type (1u, 2u, vm, hmol)
	public static final int HM_TYPE2_1U = 0x01;
	public static final int HM_TYPE2_VM = 0x02;
	public static final int HM_TYPE2_2U = 0x03;
	public static final int HM_TYPE2_HMOL = 0x04;

	// packet type and data type for download
	public static final byte Packet_Type_Download_Request = 0x1a;
	public static final byte Packet_Type_Download_Response = Packet_Type_Download_Request + 1;
	public static final byte Data_Type_File_Query_Request = 0x01;
	public static final byte Data_Type_File_Query_Success_Response = Data_Type_File_Query_Request + 1;
	public static final byte Data_Type_File_Query_Deny_Response = Data_Type_File_Query_Request + 2;
	public static final byte Data_Type_File_Download_Request = 0x04;
	public static final byte Data_Type_File_Download_Success_Response = Data_Type_File_Download_Request + 1;
	public static final byte Data_Type_File_Download_Deny_Response = Data_Type_File_Download_Request + 2;

	public static final int BUFFER_SIZE = 1024 * 50; // 5k
	public static final byte Protocol_Version = 9;

	public static final int TIME_OUT = 300000;
	public static final byte Error_Response_Flag = -1;
	public static final int Packet_Head_Size = 7;
	public static final byte Secret_Flag_Yes = 1;
	public static final byte Secret_Flag_No = 2;
	public static final byte Permission_Yes = 1;
	public static final byte Permission_Unknow = 0;

	public static final int Valid_Response = 1;
	public static final int Invalid_Response = 2;

	public static final byte ACT_RES_OPERATION_UNKNOW = 1;
	public static final byte ACT_RES_OPERATION_SHUT = 2;

	public static final byte ACT_RES_NEWVERSION_TRUE = 1;
	public static final byte ACT_RES_NEWVERSION_FALSE = 2;

	public static final byte Upload_Send_Status_Begin = 1;
	public static final byte Upload_Send_Status_Continue = 2;
	public static final byte Upload_Send_Status_End = 3;
	public static final int Upload_Fix_Head_Size = 97;
	public static final int Upload_Data_Size = 1024 * 5;

	// hm type
	public static final short PRODUCT_TYPE_HIVEMANAGER = 1;
	public static final short PRODUCT_TYPE_HM_ONLINE = 4;

	public static final String Upload_Dir = "/HiveManager/statistic/upload";
	public static final String Upload_Static_Name = "statistic.tar.gz";
	public static final String Upload_Static_Status_Name = "status.txt";
	public static final String No_Act_key = "N/A";
	
	// move
	public static final byte RESTORE_RUNNING = 1;
	public static final byte RESTORE_FINISH = 2;
	public static final byte RESTORE_ERROR = 3;
}
