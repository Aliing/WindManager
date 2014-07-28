package com.ah.test;

import com.ah.util.ahdes.AhCliSec;

public class PasswordTest {

	public static void main(String[] args){
		String password = "OpAlhJsiVyCVonEyyHr2DkBVe5wga1acudiqo";
		String clearStr = AhCliSec.ah_decrypt(password);
		String pasStr = AhCliSec.ah_encrypt(clearStr);
		System.out.println(clearStr);
		System.out.println(pasStr);
	}
}
