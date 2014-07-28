package com.ah.be.ls.sample;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.ls.ClientSenderCenter;
import com.ah.util.HmBeManager;

public class SampleTest {
	private void test_1() throws Exception {
		String desc = "desc";
		int number = 90000;

		List<String> names = new ArrayList<String>();
		names.add("Avanda");
		names.add("Tom");

		ObjectSample testo = new ObjectSample();
		testo.setField1("field1");
		testo.setField2((byte) 0x05);

		ResponseObjectSample obj = ClientSenderCenter.doSample(desc, number, names, false,
				testo);
		System.out.println("sample..obj=" + obj);
		if (obj != null) {
			System.out.println("sample..getDesc()=" + obj.getDesc());
			System.out.println("sample..getAmount()=" + obj.getAmount());
		}
	}

	public static void main(String[] args) {
		SampleTest test = new SampleTest();
		try {
			HmBeManager be = new HmBeManager();

			be.startBe();

			test.test_1();

			be.stopBe();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
