package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeDataCollectionInfoEvent extends BeCapwapClientResultEvent {
	
	private String[] files;
	
	public BeDataCollectionInfoEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_DATACOLLECTIONINFO;
	}

	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			short fileCount = buf.getShort();
			
			int i = 0;
			if (fileCount > 0) {
				files = new String[fileCount];
				
				while(buf.hasRemaining()) {
					files[i] = AhDecoder.getString(buf);
					i++;
					if (i >= fileCount)break;
				}
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException("BeDataCollectionInfoEvent.parsePacket() catch exception", e);
		}
	}

	public String[] getFiles() {
		return files;
	}

	public void setFiles(String[] files) {
		this.files = files;
	}
}
