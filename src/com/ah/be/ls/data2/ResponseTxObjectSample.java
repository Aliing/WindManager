package com.ah.be.ls.data2;

import java.nio.ByteBuffer;

import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * @description The object is a sample. it is intent to introduce. detailed help info please see
 *              RequestTxObjectForTest
 * @author jyu
 */
public class ResponseTxObjectSample implements TxObject {
	private String desc;
	private double amount;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public ByteBuffer pack() {
		ByteBuffer buf = ByteBuffer.allocate(8192);

		AhEncoder.putString(buf, desc);
		buf.putDouble(amount);

		buf.flip();
		return buf;
	}

	public void unpack(ByteBuffer buf) {
		desc = AhDecoder.getString(buf);
		amount = buf.getDouble();

		printTheTxObject();
	}

	private void printTheTxObject() {
		System.out.println("print test tx object..");
		System.out.println("txo...desc = " + desc);
		System.out.println("txo...amount = " + amount);
	}

}
