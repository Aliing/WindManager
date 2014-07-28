package com.ah.apiengine.element;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.ah.apiengine.AbstractElement;
import com.ah.apiengine.DecodeException;
import com.ah.apiengine.EncodeException;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

public class ExecutionResult extends AbstractElement {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log = new Tracer(ExecutionResult.class.getSimpleName());

	private static final byte SYNC_EXEC = 0;

	private static final byte ASYNC_EXEC = 1;

	private static final int SUCC_RESULT = 0;

	private static final int FAIL_RESULT = 1;

	/* Sync Execute */
	private byte syncExec;

	/* Result Code */
	private int resultCode = FAIL_RESULT;

	/* Failure Reason  */
	private String failureReason = "";

	public ExecutionResult() {
		super();
	}

	public ExecutionResult(boolean isSync, boolean isSucc, String failureReason) {
		syncExec = isSync ? SYNC_EXEC : ASYNC_EXEC;
		resultCode = isSucc ? SUCC_RESULT : FAIL_RESULT;

		if (failureReason != null) {
			this.failureReason = failureReason;
		}
	}

	public boolean isSyncExecution() {
		return syncExec == SYNC_EXEC;
	}

	public boolean isSuccResult() {
		return resultCode == SUCC_RESULT;
	}

	public String getFailureReason() {
		return failureReason;
	}

	@Override
	public short getElemType() {
		return EXEC_RESULT;
	}

	@Override
	public String getElemName() {
		return "Execution Result";
	}

	/*-
	 * API Engine/Client Execution Result Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |  Sync Execute |          Result Code
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *   Result Code   |     Failure Reason Length     |Fail Reason ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 7
     *
     * Sync Flag:
     *
     * 0 - synchronized response.
     * 1 - asynchronized response. API-Client need to use scheduled queries to get the progress and final result, e.g. backup or restore.
     *
     * Result Code:
     *
     * 0 - success, the other values denote failure.
	 */
	@Override
	public int encode(ByteBuffer bb) throws EncodeException {
		try {
			// Element Header
			int elemHeaderLen = encodeElementHeader(bb, 0);
			log.debug("encode", "Element Header Length: " + elemHeaderLen);

			// Start Position
			int startPos = bb.position();
			log.debug("encode", "Start Position: " + startPos);

			/* Sync Execute */
			log.debug("encode", "Sync Result: " + syncExec);
			bb.put(syncExec);

			/* Result Code */
			log.debug("encode", "Result Code: " + resultCode);
			bb.putInt(resultCode);

			/* Failure Reason */
			log.debug("encode", "Failure Reason: " + failureReason);
			byte[] toBytes = failureReason.getBytes("iso-8859-1");
			int failReasonLen = toBytes.length;
			bb.putShort((short) failReasonLen);
			bb.put(toBytes);

			// End Position
			int endPos = bb.position();
			log.debug("encode", "End Position: " + endPos);

			// Element Length
			int elemBodyLen = endPos - startPos;
			log.debug("encode", "Element Length: " + elemBodyLen);

			// Fill pending element length.
			fillPendingElementLength(bb, startPos, elemBodyLen);

			return elemHeaderLen + elemBodyLen;
		} catch (Exception e) {
			throw new EncodeException("Encoding '" + getElemName() + "' Error.", e);
		}
	}

	/*-
	 * API Engine/Client Execution Result Format.
	 *
     * 0                   1                   2                   3
     * 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     * |  Sync Execute |          Result Code
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *   Result Code   |     Failure Reason Length     |Fail Reason ...
     * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
     *
     * Length > 7
     *
     * Sync Flag:
     *
     * 0 - synchronized response.
     * 1 - asynchronized response. API-Client need to use scheduled queries to get the progress and final result, e.g. backup or restore.
     *
     * Result Code:
     *
     * 0 - success, the other values denote failure.
	 */
	@Override
	public int decode(ByteBuffer bb, int len) throws DecodeException {
		try {
			// Start Position
			int startPos = bb.position();
			log.debug("decode", "Start Position: " + startPos);

			/* Sync Execute */
			syncExec = bb.get();
			log.debug("decode", "Sync Result; " + syncExec);

			/* Result Code */
			resultCode = bb.getInt();
			log.debug("decode", "Result Code: " + resultCode);

			/* Failure Reason */
			short failReasonLen = bb.getShort();
			failureReason = AhDecoder.bytes2String(bb, failReasonLen);
			log.debug("decode", "Failure Reason[len/value]: " + failReasonLen + "/" + failureReason);

			// End Position
			int endPos = bb.position();
			log.debug("decode", "End Position: " + endPos);

			return endPos - startPos;
		} catch (BufferUnderflowException e) {
			throw new DecodeException("Incorrect element length '" + len + "' for " + getElemName(), e);
		} catch (Exception e) {
			throw new DecodeException("Decoding '" + getElemName() + "' Error.", e);
		}
	}

}