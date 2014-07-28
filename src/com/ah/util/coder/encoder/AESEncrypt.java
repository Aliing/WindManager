package com.ah.util.coder.encoder;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.*;

public class AESEncrypt {
    
     private static final String ALGO = "AES";
    private static final byte[] keyValue = 
        new byte[] { 's', 'c', 'u', 'b', 'a', 'p', 'a',
'r', 'a', 'c', 'h','u', 't', 'i', 'n', 'g' };


public static String encrypt(String Data) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGO);
        return key;
}

    public static void main(String[] args) throws Exception {
        String password = "00:19:77:81:B7:C0";
        String passwordEnc = AESEncrypt.encrypt(password);
        String passwordDec = AESEncrypt.decrypt(passwordEnc);

        System.out.println("Plain Text : " + password);
        System.out.println("Encrypted Text : " + passwordEnc);
        System.out.println("Decrypted Text : " + passwordDec);
    }

}

