package com.ah.be.admin.hhmoperate.https;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import com.ah.be.admin.hhmoperate.HHMConstant;
import com.ah.be.admin.hhmoperate.HHMoperate;
import com.ah.be.admin.hhmoperate.https.data.HHMuploadHeadData;
import com.ah.be.admin.hhmoperate.https.data.HHMuploadResponseData;
import com.ah.be.admin.hhmoperate.https.packet.HHMuploadHead;
import com.ah.be.admin.hhmoperate.https.packet.HHMuploadResponse;

import com.ah.be.debug.DebugConstant;
import com.ah.be.log.BeLogTools;

import com.ah.be.ls.util.CommTool;

public class Upload_client {
	public static boolean uploadFile(String Host, int port, String Query, String strPath, String strFileName) {

		//look the upload files
		File oSendFile = new File(strPath + "/" + strFileName);

		if (!oSendFile.isFile() || !oSendFile.exists()) {
			BeLogTools.commonLog(BeLogTools.ERROR, "upoload file was not found");
			return false;
		}

		// the status file
		File oStatusFile = new File(strPath + "/"+ HHMConstant.Upload_Static_Status_Name);

		HttpsURLConnection conn = null;
		OutputStream os = null;
		InputStream is = null;
		RandomAccessFile oReadeStatus = null;

		try {
			if (!oStatusFile.exists() || !oStatusFile.isFile()) {
				// create that file
				oStatusFile.createNewFile();
				oReadeStatus = new RandomAccessFile(oStatusFile, "rw");

				oReadeStatus.setLength(0);
				oReadeStatus.writeBytes("status=" + HHMConstant.Upload_Send_Status_Begin);
				oReadeStatus.writeBytes("\r\n");
				oReadeStatus.writeBytes("offset=" + 0);
				oReadeStatus.writeBytes("\r\n");
				oReadeStatus.close();
			}

			while (true) {
				oReadeStatus = new RandomAccessFile(oStatusFile, "rw");

				String strContent;

				strContent = oReadeStatus.readLine();

				if (null == strContent) {
					BeLogTools.commonLog(BeLogTools.ERROR, "upload status file error: no status");
					return false;
				}

				int iStatus = Integer.parseInt(CommTool.subString(strContent, "="));

				if (iStatus == HHMConstant.Upload_Send_Status_End) {
					BeLogTools.commonLog(BeLogTools.INFO, "file has upload completed.");
					break;
				}

				strContent = oReadeStatus.readLine();

				if (null == strContent) {
					BeLogTools.commonLog(BeLogTools.ERROR, "upload status file error: no offset");
					return false;
				}

				long lOffset = Long.parseLong(CommTool.subString(strContent, "="));

				byte[] bBuffer = new byte[HHMConstant.Upload_Back_Data_Size];
				Arrays.fill(bBuffer, (byte) 0);

				//build packet header
				HHMuploadHeadData oPacketHead = new HHMuploadHeadData();
				oPacketHead.setPackType(HHMConstant.Packet_Type_Upload_Backup_Info);
				oPacketHead.setProtocolVersion(HHMConstant.Packet_protocol_version);
				oPacketHead.setHHMFlag(HHMConstant.HHM_Fix_Flag);
				oPacketHead.setFilePath(strPath);
				//oPacketHead.setFilePath("/tmp/upload-test");
				oPacketHead.setFileName(strFileName);
				oPacketHead.setSendStatus((byte) (iStatus & 0xFF));
				oPacketHead.setOffset(lOffset);
				oPacketHead.setDataLength(0);

				int iPacketHeadLength = HHMuploadHead.buildHHMuploadHead(bBuffer, 0, oPacketHead);

				if(0 == iPacketHeadLength)
				{
					BeLogTools.commonLog(BeLogTools.ERROR,
							"build upload Packet Head failed");
					return false;
				}

				RandomAccessFile oReadeStatic = new RandomAccessFile(oSendFile, "r");

				oReadeStatic.seek(lOffset);

				int iDataLength = oReadeStatic.read(bBuffer, iPacketHeadLength,
						HHMConstant.Upload_Back_Data_Size - iPacketHeadLength);

				if (iDataLength == -1) {
					// read finish
					iStatus = HHMConstant.Upload_Send_Status_End;
					iDataLength = 0;
				}

				oReadeStatic.close();

				//build data length and status again
				oPacketHead.setSendStatus((byte) (iStatus & 0xFF));
				oPacketHead.setDataLength(iDataLength);

				iPacketHeadLength = HHMuploadHead.buildHHMuploadHead(bBuffer, 0, oPacketHead);

				if(0 == iPacketHeadLength)
				{
					BeLogTools.commonLog(BeLogTools.ERROR,
							"build upload Packet Head failed");
					return false;
				}

				// send the data
				URL surl = new URL("https", Host, port, Query);

				CommTool.initHttps();

				conn = (HttpsURLConnection) surl.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setConnectTimeout(HHMConstant.TIME_OUT);
				conn.setReadTimeout(HHMConstant.HHM_update_Time_Out);

				os = conn.getOutputStream();
				os.write(bBuffer, 0, iDataLength+iPacketHeadLength);
				os.flush();
				os.close();
				conn.getResponseCode();

				Arrays.fill(bBuffer, (byte) 0);

				is = conn.getInputStream();

				// get response packet
				is.read(bBuffer, 0, HHMConstant.Upload_Back_Data_Size);

				is.close();

				if (HHMConstant.Error_Response_Flag == bBuffer[0]) {
					BeLogTools.commonLog(BeLogTools.ERROR, "receive error response");
					return false;
				}

				HHMuploadResponseData oRecvData = new HHMuploadResponseData();

				HHMuploadResponse.parseHHMuploadResponse(bBuffer, 0, oRecvData);

				if (oRecvData.getPacketType() != oPacketHead.getPackType() + 1) {
					BeLogTools.commonLog(BeLogTools.ERROR, "respone is error type");
					return false;
				}


				conn.disconnect();

				// write file change the file status
				oReadeStatus.setLength(0);
				oReadeStatus.writeBytes("status=" + oRecvData.getSendStatus());
				oReadeStatus.writeBytes("\r\n");
				oReadeStatus.writeBytes("offset=" + oRecvData.getOffset());
				oReadeStatus.writeBytes("\r\n");
				oReadeStatus.close();
			}

			return true;
		} catch (Exception ex) {
			BeLogTools.commonLog(BeLogTools.ERROR, ex);
			return false;
		} finally {
			try {
				if (null != os) {
					os.close();
				}

				if (null != is) {
					is.close();
				}

				if (null != oReadeStatus) {
					oReadeStatus.close();
				}

				if (null != conn) {
					conn.disconnect();
				}
			} catch (Exception ex) {
				BeLogTools.commonLog(DebugConstant.WARNING, "exception", ex);
			}
		}
	}

	public static void main(String[] args)
	{
		if(uploadFile("127.0.0.1",443,HHMoperate.Https_Upload_Query,"/tmp/upload-src","test.tar.gz"))
		{
			System.out.println("upload file okay!");
		}else
		{
			System.out.println("upload file failed!");
		}
	}

}
