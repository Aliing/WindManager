package com.ah.util.ahdes;

import java.nio.charset.StandardCharsets;

import org.apache.log4j.Logger;

public class AhCliSec {
	private static Logger		logf					= Logger.getLogger("console");

	private static final int	AH_MAX_SECRET_STR_LEN	= 128;
	private static final int	AH_MD5_OUT_LEN			= 16;
	private static final int	AH_MD5_USE_LEN			= 6;
	private static final int	AH_MAX_PASSWD_LEN		= 64;
	private static final int	AH_PADDING_LEN			= 24;
	private static final int	AH_ENCRYPT_ORG_LEN		= 5;

	private static final int[]	ah_rand					= new int[] { 39, 6, 41, 51, 17, 63, 10,
			44, 41, 13, 58, 43, 50, 59, 35, 6, 60, 2, 20, 56, 27, 40, 39, 13, 54 };
	private static final byte[]	abcdef_key				= new byte[] { 0, 2, 0, 0, 9, 3, 5, 1, 9,
			8, 0, 0, 9, 1, 7							};
	private static final String	pwd						= "sorea";

	private int ah_cli_des_2_str(byte[] in, int in_len, byte[] out) {
		int i = 0;
		int idx = 0;

		if ((in_len % 3) != 0) {
			logf.warn("The password length must be divisible by 3.\n");
			return -1;
		}

		for (i = 0; i < in_len; i += 3) {
			out[idx++] = (byte) ((in[i] >> 2) & 0x3f);
			out[idx++] = (byte) (((in[i] << 4) & 0x30) | ((in[i + 1] >> 4) & 0x0f));
			out[idx++] = (byte) (((in[i + 1] << 2) & 0x3c) | ((in[i + 2] >> 6) & 0x03));
			out[idx++] = (byte) (in[i + 2] & 0x3f);
		}
		for (i = 0; i < idx; i++) {
			if (out[i] < 10) {
				out[i] += '0';
			} else if (out[i] < 36) {
				out[i] += ('A' - 10);
			} else if (out[i] < 62) {
				out[i] += ('a' - 36);
			} else if (out[i] < 64) {
				out[i] += ('#' - 62);
			} else {
				logf.warn("Wrong input password.\n");
				return -1;
			}
		}
		return idx;
	}
	
	private int ah_cli_str_2_des(byte[] in, int in_len, byte[] out){
        int i = 0;
        int idx =0;

        if (in_len % 4 != 0) {
        	logf.warn("Ciphertext length must be divisible by 4.\n");
            return -1;
        }
        for (i=0; i<in_len; i++) {
            if (in[i] >= '0' && in[i] <= '9') {
                in[i] -= '0';
            } else if (in[i] >= 'A' && in[i] <= 'Z') {
                in[i] -= ('A' - 10);
            } else if (in[i] >= 'a' && in[i] <= 'z') {
                in[i] -= ('a' - 36);
            } else if (in[i] == '#' || in[i] == '$'){
                in[i] -= ('#' - 62);
            } else {
            	logf.warn("Wrong input ciphertext.\n");
                return -1;
            }
        }
        for (i=0; i<in_len; i+=4) {
            out[idx++] = (byte) ((((in[i])<<2) & 0xfc) | (((in[i+1])>>4) & 0x03));
            out[idx++] = (byte) ((((in[i+1])<<4) & 0xf0) | (((in[i+2])>>2) & 0x0f));
            out[idx++] = (byte) ((((in[i+2])<<6) & 0xc0) | ((in[i+3]) & 0x3f));
        }
        return idx;
    }

	private int ah_cli_des_passwd_padding(byte[] out, String in, int max_str_len) {
		int len;
		int out_len;
		int i = 0;
		byte[] str_md5 = new byte[AH_MD5_OUT_LEN];

		MD5 m = new MD5();

		m.md5Init();
		m.md5Update(in.getBytes(), in.length());
		m.md5Final();
		str_md5 = m.digest;

		for (i = 0; i < AH_MD5_USE_LEN; i++) {
			int aa = str_md5[i];
			if (aa < 0) {
				aa = aa & 0x7f + 128;
			}
			int a = aa % ('z' - 'a');
			str_md5[i] = (byte) a;
			str_md5[i] += 'a';
		}

		len = in.length();
		if (len > AH_MAX_PASSWD_LEN) {
			logf.warn("Too long password.\n");
			return -1;
		}

		if (len + AH_MD5_USE_LEN + 1 > max_str_len) {
			logf.warn("Too small output buffer.\n");
			return -1;
		}

		System.arraycopy(in.getBytes(), 0, out, AH_MD5_USE_LEN + 1, len);
		out[AH_MD5_USE_LEN] = (byte) len;
		System.arraycopy(str_md5, 0, out, 0, AH_MD5_USE_LEN);

		len += (AH_MD5_USE_LEN + 1 + 1);
		if (len <= AH_PADDING_LEN) {
			out_len = AH_PADDING_LEN;
		} else if (len <= 2 * AH_PADDING_LEN) {
			out_len = 2 * AH_PADDING_LEN;
		} else if (len <= 3 * AH_PADDING_LEN) {
			out_len = 3 * AH_PADDING_LEN;
		} else {
			// Can't over 72 bytes after padding, or the encrypt text will be over 128
			logf.warn("Too long password.\n");
			return -1;
		}
		if (out_len > max_str_len) {
			logf.warn("Too small output buffer.\n");
			return -1;
		}

		for (i = len + 1; i < out_len; i++) {
			out[i] = (byte) ah_rand[i - 1 - len];
		}

		/* XOR MD5_str */
		for (i = AH_MD5_USE_LEN; i < out_len; i++) {
			out[i] ^= str_md5[i % AH_MD5_USE_LEN];
		}

		return out_len;
	}

	private int ah_encrypt_pwd(byte[] dst, String src) {
		int interval = 0;
		int len;
		int[] out_len = new int[] { 0 };

		byte[] pwd_crp = new byte[pwd.length()];
		System.arraycopy(pwd.getBytes(), 0, pwd_crp, 0, pwd.length());

		byte[] bufin = new byte[AH_MAX_SECRET_STR_LEN + 1];
		byte[] bufout = new byte[AH_MAX_SECRET_STR_LEN + 1];
		byte[] out = new byte[AH_MAX_SECRET_STR_LEN + 1];

		len = ah_cli_des_passwd_padding(bufin, src, AH_MAX_SECRET_STR_LEN);
		if (len < 0) {
			return -1;
		}

		new AhScrtDes().ah_des_go(bufout, bufin, len, abcdef_key, 15, AhScrtDes.desType.ENCRYPT,
				out_len);

		len = ah_cli_des_2_str(bufout, out_len[0], out);
		if (len < 0) {
			return -1;
		}

		interval = (len - 1) / AH_ENCRYPT_ORG_LEN;
		if (interval != 0) {
			int i = 0;
			int index = 0;
			for (i = 0; i < len + AH_ENCRYPT_ORG_LEN; i++) {
				dst[i] = out[i - index];
				if ((i % interval) == 0 && (i > 0 && index < AH_ENCRYPT_ORG_LEN)) {
					dst[i] = pwd_crp[index++];
				}
			}
		} else {
			System.arraycopy(out, 0, dst, 0, AH_MAX_SECRET_STR_LEN);
		}

		return len + AH_ENCRYPT_ORG_LEN;
	}

	public static String ah_encrypt(String src) {
		if (src.length() > AH_MAX_PASSWD_LEN) {
			logf.error("the source string can not be more than " + AH_MAX_SECRET_STR_LEN
					+ " characters.");
			return src;
		}
		byte[] dst = new byte[AH_MAX_PASSWD_LEN * 2 + 1];

		int len = new AhCliSec().ah_encrypt_pwd(dst, src);

		if (len < 0) {
			logf.error("encrypt error");
			return src;
		}

		char[] chardata = new char[len];
		for (int i = 0; i < len; i++) {
			chardata[i] = (char) dst[i];
		}
		return String.copyValueOf(chardata);
	}
	
	private int ah_cli_des_passwd_unpadding(byte[] out, byte[] in, int max_str_len)
    {
        byte[] str_md5 = new byte[AH_MD5_USE_LEN];
        int i = 0;
        int len = 0;

        // get passwd len
        in[AH_MD5_USE_LEN] ^= in[0];
        len = (int)in[AH_MD5_USE_LEN]; 
        if (len > max_str_len) {
        	logf.warn("Wrong password length.\n");
            return -1;
        }
        System.arraycopy(in, 0, str_md5, 0, AH_MD5_USE_LEN);
        // XOR MD5_str
        for(i=AH_MD5_USE_LEN+1; i<(len+AH_MD5_USE_LEN+1); i++) {
                in[i] ^= str_md5[i%AH_MD5_USE_LEN];
        }
        System.arraycopy(in, AH_MD5_USE_LEN+1, out, 0, len);

        return 0;
    }
    
    public static boolean ah_is_encrypted_pwd(String src)
    {
        int interval = 0;
        int len = src.length();
        int i = 0;
        int index = 0;
        
        byte[] bufin = new byte[AH_MAX_SECRET_STR_LEN + 1];
        byte[] crp = new byte[AH_ENCRYPT_ORG_LEN];

        System.arraycopy(src.getBytes(StandardCharsets.UTF_8), 0, bufin, 0, len);
        interval = (len - 1) / AH_ENCRYPT_ORG_LEN;
        interval--;
        if(len > AH_ENCRYPT_ORG_LEN){
            if (interval > 0 ){
                for (i = 0; (i + index) < len; i++){
                    if((((i+1+index) % interval) == 0) && ((i > 0) && (index < AH_ENCRYPT_ORG_LEN))){
                        crp[index] = bufin[i + index + 1];
                        index++;
                    }
                }           
            }
        }

        String crpStr = new String(crp);

        if(crpStr.equals(pwd)){
            return true;
        }else{
            //log.info(src + " is not encrypted password");
            return false;
        }
    }
    
    private int ah_decrypt_pwd(byte[] dst, String src)
    {
        int interval = 0;
        int len = src.length();
        int i = 0;
        int index = 0;
        int bufout_len = 0;
        int[] out_len = new int[] {0};
        
        byte[] buf = new byte[AH_MAX_SECRET_STR_LEN + 1];
        byte[] bufin = new byte[AH_MAX_SECRET_STR_LEN + 1];
        byte[] bufout = new byte[AH_MAX_SECRET_STR_LEN + 1];

        System.arraycopy(src.getBytes(StandardCharsets.UTF_8), 0, bufin, 0, len);
        interval = (len - 1) / AH_ENCRYPT_ORG_LEN;
        interval--;

        if (interval > 0 ){
            for (i = 0; (i + index) < len; i++){
                bufout[i] = bufin[i + index ];
                bufout_len++;
                if((((i+1+index) % interval) == 0) && ((i > 0) && (index < AH_ENCRYPT_ORG_LEN))){
                    index++;
                }
            }           
        }else{
            System.arraycopy(bufin, 0, bufout, 0, len);
            bufout_len = len;
        }

        len = ah_cli_str_2_des(bufout, bufout_len, buf);
        if (len < 0) {
            return -1;
        }

        new AhScrtDes().ah_des_go(bufout, buf, len, abcdef_key, 15, AhScrtDes.desType.DECRYPT, out_len);
        if (ah_cli_des_passwd_unpadding(dst, bufout, AH_MAX_SECRET_STR_LEN) < 0) {
            return -1;
        }
        return 0;
    }

    public static String ah_decrypt(String src) {
        if (src.length() > AH_MAX_SECRET_STR_LEN) {
        	logf.error("the source string can not be more than " + AH_MAX_SECRET_STR_LEN
                    + " characters.\n");
            return null;
        }
        
        byte[] dst = new byte[AH_MAX_SECRET_STR_LEN + 1];

        int result = new AhCliSec().ah_decrypt_pwd(dst, src);

        if (result < 0) {
        	logf.error("decrypt error\n");
            return null;
        }
        int len = byteLen(dst);
        byte[] realDst= new byte[len];
        System.arraycopy(dst, 0, realDst, 0, len);
        
        return new String(realDst);
    }
    
    public static int byteLen(byte[] bt){
        int len = 0;
        if(bt!=null){
            for(int i=0;i<bt.length;i++){
                byte b = bt[i];
                if(b!=0x0000){
                    len ++;
                }else{
                    break;
                }
            }
        }
        return len;
    }
}
