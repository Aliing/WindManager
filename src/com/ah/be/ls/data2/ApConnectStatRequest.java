package com.ah.be.ls.data2;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import com.ah.be.hiveap.HiveApInfoForLs;
import com.ah.util.coder.AhEncoder;

public class ApConnectStatRequest implements FileTxObject {
	List<HiveApInfoForLs> infos;

	public List<HiveApInfoForLs> getInfos() {
		return infos;
	}

	public void setInfos(List<HiveApInfoForLs> infos) {
		this.infos = infos;
	}

	public void write(OutputStream out) throws IOException {
		AhEncoder.putInt(out, infos.size());

		for (HiveApInfoForLs info : infos) {
			ByteBuffer buf = ByteBuffer.allocate(1024);

			AhEncoder.putString(buf, info.getSerialNumber());
			AhEncoder.putString(buf, info.getMacAddress());
			buf.putLong(info.getFirstConnectTime());
			buf.putLong(info.getLastConnectTime());
			buf.putLong(info.getTotalConnectTime());
			buf.putLong(info.getTotalConnectTimes());
			AhEncoder.putString(buf, info.getProductName());
			AhEncoder.putString(buf, info.getSoftVer());
			AhEncoder.putString(buf, info.getTimeZone());
			AhEncoder.putString(buf, info.getVhmName());
			AhEncoder.putString(buf, info.getHmId());

			buf.flip();
			byte[] aa = new byte[buf.limit()];
			buf.get(aa);
			out.write(aa);
		}
		out.flush();
		out.close();
	}

}
