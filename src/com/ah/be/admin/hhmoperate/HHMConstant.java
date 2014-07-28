package com.ah.be.admin.hhmoperate;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import com.ah.be.log.BeLogTools;


public class HHMConstant {

	public static final String BACKUP_DOWNLOAD_HOME               = "/tmp/backup";
	public static final String RESTORE_UPLOAD_HOME                = "/tmp/restore";

	public static final int TIME_OUT                              = 300000;
	public static final int HHM_update_Time_Out                   = 1000*60*60*10;
	//public static final int HHM_UPLOD_TIME_OUT                    = 1000*60*60*10;

	public static final int  Upload_Back_Data_Size                = 1024*100;
	public static final int  Buffer_size                          = 1024*100;
    public static final String HHM_Fix_Flag                       = "a3r0h1v3_HHM";
    public static final int HHM_Fix_Flag_Length                   = 12;

    public static final String Upload_Static_Status_Name = "status.txt";
    public static final byte Upload_Send_Status_Begin    = 1;
	public static final byte Upload_Send_Status_Continue = 2;
	public static final byte Upload_Send_Status_End      = 3;


	//the pack length for upload head
	public static final int Packet_Type_Util_Length               = 1;
	public static final int Packet_Length_Util_Length             = 2;
	public static final int Packet_Version_Util_Length            = 1;
	public static final int Packet_File_Path_Length_Util_Length   = 1;
	public static final int Upload_Send_Status_Util_Length = 1;
	public static final int Upload_Offset_Util_Length    = 8;
	public static final int Upload_Data_Size_Util_Length = 4;
	//end

	public static final int Packet_Version_Length_Util_Length     = 1;
	public static final int Packet_DomainName_Length_Util_Length  = 1;
	public static final int Packet_MaxAp_Util_Length              = 4;
	public static final int Packet_Gmflag_util_Length             = 1;
	public static final int Packet_DomainID_Util_Length           = 1;
	public static final int Packet_Owneruser_Util_Length          = 1;
	public static final byte Gm_Flag_True                         = 1;
	public static final byte Gm_Flag_False                        = 2;

	public static final byte Packet_protocol_version             = 0x04;

	//type
	public static final byte Error_Response_Flag                        = -1;
	public static final byte Success_Response_FLag                      = 0x7f;
	public static final byte Packet_Type_Upload_Backup_Info             = 0x01;
    public static final byte Packet_Type_Upload_backup_Response         = 0x02;
    public static final byte Packet_Type_HHM_Update                     = 0x03;
    public static final byte Packet_Type_HHM_Revert                     = 0x05;



    public static void sendSuessResponse(HttpServletResponse response) {

		byte[] bBuffer = new byte[1];
		bBuffer[0] = HHMConstant.Success_Response_FLag;

		try {
			OutputStream os = response.getOutputStream();
			os.write(bBuffer, 0, 1);
			os.flush();
			os.close();
		} catch (Exception ex) {
			// add log
			BeLogTools.commonLog(BeLogTools.ERROR, ex);
		}
	}

}
