/**
 *@filename		AhWtpEvent.java
 *@version
 *@author		Francis
 *@createtime	2007-10-10 02:43:13 PM.
 *Copyright (c) 2006-2008 Aerohive Co., Inc.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.be.capwap.event;

// java import
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// aerohive import
import com.ah.be.capwap.AhCapwapException;
import com.ah.be.capwap.AhCapwapFsm;
import com.ah.be.capwap.event.impl.AhCapwapEventMgmtImpl;

/**
 * @author Francis
 * @version V1.0.0.0
 */
public abstract class AhWtpEvent extends AhCapwapEvent {

	private static final long	serialVersionUID	= 1L;
	
	protected int fragId;
	protected int totalFragNum;
	protected int timer;
	protected final Map<Integer, byte[]> frags = Collections.synchronizedMap(new HashMap<Integer, byte[]>());
	protected AhCapwapFsm fsm;

	protected AhWtpEvent(int type) {
		super(type);
	}

	protected AhWtpEvent(int type, byte[] packet) {
		this(type);
		this.packet = packet;
	}

	public int getFragId() {
		return fragId;
	}

	public void setFragId(int fragId) {
		this.fragId = fragId;
	}

	public int getTotalFragNum() {
		return totalFragNum;
	}

	public void setTotalFragNum(int totalFragNum) {
		this.totalFragNum = totalFragNum;
	}

	public AhCapwapFsm getFsm() {
		return fsm;
	}

	public void setFsm(AhCapwapFsm fsm) {
		this.fsm = fsm;
	}

	public void startTimer(int timer) {
		this.timer = 1000 * timer;
	}

	public int getRemainTimer() {
		return timer;
	}

	public int decreaseTimer(int decValue) {
		timer -= decValue;

		return timer;
	}

	public void addFrag(int offset, byte[] msg) {
		synchronized (frags) {
			frags.put(offset, msg);
		}
	}

	public boolean allFragsPresent() {
		return totalFragNum == frags.size();
	}

	public int size() {
		return frags.size();
	}

	protected boolean combineFrags() {
		if (packet != null) {
			return true;
		}

		if (allFragsPresent()) {
			int capacity = 0;

			for (byte[] b : frags.values()) {
				capacity += b.length;
			}

			byte[] bytes;
			int pos = 0;
			packet = new byte[capacity];
			List<Integer> list = new ArrayList<Integer>(frags.keySet());
			Collections.sort(list);

			for (int offset : list) {
				// Combine each fragment with other corresponding fragments to reassemble a complete idp message.
				bytes = frags.get(offset);
				System.arraycopy(bytes, 0, packet, pos, bytes.length);
				pos += bytes.length;
			}

			return true;
		} else {
			return false;
		}
	}

	/**
	 * Process WTP Event.
	 * <p>
	 * @param fsm  Fsm of capwap in which some useful messages are saved.
	 * @throws AhCapwapException  if event fragments which is received from WTP are not complete.
	 */
	public void processEvent(AhCapwapFsm fsm) throws AhCapwapException {
		if (!combineFrags()) {
			throw new AhCapwapException("WTP event fragments are not complete.");
		}

		this.fsm = fsm;
		AhCapwapEventMgmtImpl.getInstance().notify(this);
	}

}