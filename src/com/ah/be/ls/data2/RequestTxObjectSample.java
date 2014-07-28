package com.ah.be.ls.data2;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.ls.sample.ObjectSample;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

/**
 * @description The object is a sample. it is intent to introduce every one to write real business
 *              communication object
 * @author jyu
 */
public class RequestTxObjectSample implements TxObject {
	private String desc;
	private int number;
	private List<String> names;
	private boolean isOk;
	private ObjectSample testo;

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	public ObjectSample getTesto() {
		return testo;
	}

	public void setTesto(ObjectSample testo) {
		this.testo = testo;
	}

	public ByteBuffer pack() {
		// print all the test request object value.
		// it is just for this sample, please note that it is not need in real business.
		printTheTxObject();

		// 1. allocate byte buffer
		ByteBuffer buf = ByteBuffer.allocate(8192);

		// 2. put each values to byte buffer
		AhEncoder.putString(buf, desc);
		buf.putInt(number);

		// note for 2.1: list should be initialize when it is null
		if (names == null) {
			names = new ArrayList<String>();
		}
		// note for 2.2: list size should be put into buffer
		buf.putInt(names.size());
		for (String name : names) {
			AhEncoder.putString(buf, name);
		}

		// note for 2.3: put 0x01/0x00 into buffer when data is boolean
		buf.put(isOk ? (byte) 0x01 : (byte) 0x00);

		// note for 2.4: 0x00/0x01 should be put into buff when object is not pure
		buf.put((testo != null) ? (byte) 0x01 : (byte) 0x00);
		if (testo != null) {
			AhEncoder.putString(buf, testo.getField1());
			buf.put(testo.getField2());
		}

		// 3. flip the buffer
		buf.flip();
		return buf;
	}

	public void unpack(ByteBuffer buf) {
		// parse pure data
		desc = AhDecoder.getString(buf);
		number = buf.getInt();

		// parse list data
		int size = buf.getInt();
		names = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			String name = AhDecoder.getString(buf);
			names.add(name);
		}

		// parse boolean data
		isOk = (buf.get() == 0x01) ? true : false;

		// parse non-pure data
		boolean isObjectNotNull = (buf.get() == 0x01) ? true : false;

		if (isObjectNotNull) {
			testo = new ObjectSample();
			testo.setField1(AhDecoder.getString(buf));
			testo.setField2(buf.get());
		}

		// print all the test request object value.
		// it is just for this sample, please note that it is not need in real business.
		printTheTxObject();
	}

	private void printTheTxObject() {
		System.out.println("print test tx object..");
		System.out.println("txo...desc = " + desc);
		System.out.println("txo...number = " + number);
		System.out.println("txo...names.size() = " + names.size());
		for (String name : names) {
			System.out.println("txo...name = " + name);
		}
		System.out.println("txo...isOk = " + isOk);
		System.out.println("txo...testo = " + testo);
		if (testo != null) {
			System.out.println("txo...testo.getField1() = " + testo.getField1());
			System.out.println("txo...testo.getField2() = " + testo.getField2());
		}
	}
}
