package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.mo.VhmRumStatus;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

public class BeReportVhmRevertStatusEvent extends BePortalHMPayloadEvent {
	private static final long	serialVersionUID	= 1L;

	private VhmRumStatus		revertStatus;

	public BeReportVhmRevertStatusEvent() {
		super();
		operationType = OPERATIONTYPE_VHM_REPORT_REVERT_STATUS;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {
		if (revertStatus == null) {
			throw new BeCommunicationEncodeException("revertStatus is a necessary field!");
		}

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		AhEncoder.putString(buf, revertStatus.getVhmName());
		AhEncoder.putString(buf, revertStatus.getSrcHmolAddress());
		AhEncoder.putString(buf, revertStatus.getDestHmolAddress());
		buf.putInt(revertStatus.getStatus());
		buf.putInt(revertStatus.getProcessStatus());
		buf.put(revertStatus.isSuccess() ? (byte) 1 : (byte) 0);
		AhEncoder.putString(buf, revertStatus.getFailureInfo());

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		revertStatus = new VhmRumStatus();
		revertStatus.setVhmName(AhDecoder.getString(buf));
		revertStatus.setSrcHmolAddress(AhDecoder.getString(buf));
		revertStatus.setDestHmolAddress(AhDecoder.getString(buf));
		revertStatus.setStatus(buf.getInt());
		revertStatus.setProcessStatus(buf.getInt());
		if (buf.get() == (byte) 1) {
			revertStatus.setSuccess(true);
		} else {
			revertStatus.setSuccess(false);
		}
		revertStatus.setFailureInfo(AhDecoder.getString(buf));

		return null;
	}

	public void setRevertStatus(VhmRumStatus revertStatus) {
		this.revertStatus = revertStatus;
	}

	public VhmRumStatus getRevertStatus() {
		return revertStatus;
	}

}
