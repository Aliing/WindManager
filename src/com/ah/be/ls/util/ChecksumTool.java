package com.ah.be.ls.util;

import com.ah.be.admin.BeAdminCentOSTools;

public class ChecksumTool {
	public static String generateChecksum(String file) {
		String cmd = "md5sum " + file;
		String result = BeAdminCentOSTools.getOutStreamExecCmd(cmd);

		if (result.length() > 32) {
			return result.substring(0, 32);
		} else {
			return "";
		}
	}

	public static boolean checksum(String file, String checksum) {
		String sum2 = generateChecksum(file);
		return sum2.equals(checksum);
	}

}
