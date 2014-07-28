package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeReportVhmUpgradeStatusEvent extends BePortalHMPayloadEvent {

	private static final long	serialVersionUID	= 1L;

	private VhmRumStatus		upgradeStatus;

	public BeReportVhmUpgradeStatusEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_REPORT_UPGRADE_STATUS;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (upgradeStatus == null) {
			throw new BeCommunicationEncodeException("upgradeStatus is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, upgradeStatus.getVhmName());
		AhEncoder.putString(buf, upgradeStatus.getSrcHmolAddress());
		AhEncoder.putString(buf, upgradeStatus.getDestHmolAddress());
		buf.putInt(upgradeStatus.getStatus());
		buf.putInt(upgradeStatus.getProcessStatus());
		buf.put(upgradeStatus.isSuccess() ? (byte) 1 : (byte) 0);
		AhEncoder.putString(buf, upgradeStatus.getFailureInfo());

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		upgradeStatus = new VhmRumStatus();
		upgradeStatus.setVhmName(AhDecoder.getString(buf));
		upgradeStatus.setSrcHmolAddress(AhDecoder.getString(buf));
		upgradeStatus.setDestHmolAddress(AhDecoder.getString(buf));
		upgradeStatus.setStatus(buf.getInt());
		upgradeStatus.setProcessStatus(buf.getInt());
		if (buf.get() == (byte) 1) {
			upgradeStatus.setSuccess(true);
		} else {
			upgradeStatus.setSuccess(false);
		}
		upgradeStatus.setFailureInfo(AhDecoder.getString(buf));

		return null;
	}

	public VhmRumStatus getUpgradeStatus() {
		return upgradeStatus;
	}

	public void setUpgradeStatus(VhmRumStatus upgradeStatus) {
		this.upgradeStatus = upgradeStatus;
	}

}
