package com.ah.be.ls;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Arrays;

import javax.net.ssl.HttpsURLConnection;

import com.ah.be.app.HmBeActivationUtil;
import com.ah.be.debug.DebugConstant;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.log.BeLogTools;
import com.ah.be.ls.action.PacketUploadFileRequest;
import com.ah.be.ls.action.PacketUploadFileResponse;
import com.ah.be.ls.data.UploadFileInfoRequestData;
import com.ah.be.ls.data.UploadFileInfoResponseData;
import com.ah.be.ls.util.CommConst;
import com.ah.be.ls.util.CommTool;

public class Upload_client {

	public static boolean uploadFile(String Host, int port, String Query, byte bSendType) {
		// Is send file exist
		if (CommConst.Upload_Statistic_Info_Data_Type != bSendType) {
			BeLogTools.commonLog(BeLogTools.ERROR, "statistic type error");
			return false;
		}

		File oSendFile = new File(CommConst.Upload_Dir + "/" + CommConst.Upload_Static_Name);

		if (!oSendFile.isFile() || !oSendFile.exists()) {
			BeLogTools.commonLog(BeLogTools.ERROR, "upoload file was not found");
			return false;
		}

		// the status file
		File oStatusFile = new File(CommConst.Upload_Dir + "/"
				+ CommConst.Upload_Static_Status_Name);

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
				oReadeStatus.writeBytes("status=" + CommConst.Upload_Send_Status_Begin);
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

				if (iStatus == CommConst.Upload_Send_Status_End) {
					BeLogTools.commonLog(BeLogTools.INFO, "file has upload completed.");
					break;
				}

				strContent = oReadeStatus.readLine();

				if (null == strContent) {
					BeLogTools.commonLog(BeLogTools.ERROR, "upload status file error: no offset");
					return false;
				}

				long lOffset = Long.parseLong(CommTool.subString(strContent, "="));

				byte[] bBuffer = new byte[CommConst.Upload_Data_Size];
				Arrays.fill(bBuffer, (byte) 0);

				RandomAccessFile oReadeStatic = new RandomAccessFile(oSendFile, "r");

				oReadeStatic.seek(lOffset);

				int iDataLength = oReadeStatic.read(bBuffer, CommConst.Upload_Fix_Head_Size,
						CommConst.Upload_Data_Size - CommConst.Upload_Fix_Head_Size);

				if (iDataLength == -1) {
					// read finish
					iStatus = CommConst.Upload_Send_Status_End;
					iDataLength = 0;
				}

				oReadeStatic.close();

				UploadFileInfoRequestData oSendData = new UploadFileInfoRequestData();

				oSendData.setPacketType(CommConst.Upload_Request_Packet_Type);
				oSendData.setProtocolVersion(CommConst.Protocol_Version);
				oSendData.setType(bSendType);
				oSendData.setSendStatus((byte) (iStatus & 0xFF));
				oSendData.setOffset(lOffset);
				
				/*
				 * get the flag of if use activation key
				 */
				String actKey = HmBeActivationUtil.getActivationKey();
				if (null == actKey || "".equals(actKey)) {
					oSendData.setNeedActKeyFlag(false);
				} else {
					oSendData.setNeedActKeyFlag(true);
					
					// set activation key
					oSendData.setActKey(actKey);
				}

				oSendData.setSystemId(BeLicenseModule.HIVEMANAGER_SYSTEM_ID);

				oSendData.setDataLength(iDataLength);

				// get the data
				iDataLength = PacketUploadFileRequest.buildUploadFileReq(bBuffer, 0, oSendData);

				if (iDataLength == 0) {
					BeLogTools.commonLog(BeLogTools.ERROR,
							"build Upload File Request packet error");
					return false;
				}

				// send the data
				URL surl = new URL("https", Host, port, Query);

				CommTool.initHttps();

				conn = (HttpsURLConnection) surl.openConnection();
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.setConnectTimeout(CommConst.TIME_OUT);
				conn.setReadTimeout(CommConst.TIME_OUT);

				os = conn.getOutputStream();
				os.write(bBuffer, 0, iDataLength);
				os.flush();
				os.close();
				conn.getResponseCode();

				Arrays.fill(bBuffer, (byte) 0);

				is = conn.getInputStream();

				// get response packet
				is.read(bBuffer, 0, CommConst.Upload_Data_Size);

				is.close();

				if (CommConst.Error_Response_Flag == bBuffer[0]) {
					BeLogTools.commonLog(BeLogTools.ERROR, "receive error response");
					return false;
				}

				UploadFileInfoResponseData oRecvData = new UploadFileInfoResponseData();

				PacketUploadFileResponse.parseUploadFileResponse(bBuffer, 0, oRecvData);

				if (oRecvData.getPacketType() != oSendData.getPacketType() + 1) {
					BeLogTools.commonLog(BeLogTools.ERROR, "respone is error type");
					return false;
				}

				if (oRecvData.getType() != oSendData.getType()) {
					BeLogTools.commonLog(BeLogTools.ERROR,
							"send and response not the same feature");
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
			BeLogTools.commonLog(BeLogTools.ERROR, ex.getMessage());
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

	public static void main(String[] args) {

		if (uploadFile("127.0.0.1", 443, "/uploadserver", CommConst.Upload_Statistic_Info_Data_Type)) {
			System.out.println("transfer is okay!");
		} else {
			System.out.println("transfer is failed!");
		}
	}
}
