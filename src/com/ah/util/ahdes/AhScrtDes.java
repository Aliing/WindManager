package com.ah.util.ahdes;

public class AhScrtDes {
	private byte[]		bitInit	= new byte[256];

	private byte[]		tmp		= new byte[256];
	private byte[][][]	subkey	= new byte[2][16][48];
	private boolean		is3des;
	private byte[]		deskey	= new byte[16];

	public enum desType {
		ENCRYPT, DECRYPT
	}

	public int ah_des_go(byte[] out, byte[] in, int datalen, byte[] key, int keylen, desType type,
			int[] out_len) {
		int i, j;
		out_len[0] = 0;
		datalen = (datalen + 7) & 0xfffffff8;
		if (!(out.length > 0 && in.length > 0 && key.length > 0 && (datalen > 0))) {
			return -1;
		}

		setkey(key, keylen);

		byte[] outb = new byte[8];
		byte[] inb = new byte[8];
		if (!is3des) {
			for (i = 0, j = datalen >> 3; i < j; ++i) {
				System.arraycopy(in, 8 * i, inb, 0, 8);
				ah_des(outb, inb, subkey[0], type);
				System.arraycopy(outb, 0, out, 8 * i, 8);
			}
			out_len[0] += 8;
		} else {
			for (i = 0, j = datalen >> 3; i < j; ++i) {
				System.arraycopy(in, 8 * i, inb, 0, 8);
				ah_des(outb, inb, subkey[0], type);
				if (type.equals(desType.DECRYPT)) {
					ah_des(outb, outb, subkey[1], desType.ENCRYPT);
				} else {
					ah_des(outb, outb, subkey[1], desType.DECRYPT);
				}
				ah_des(outb, outb, subkey[0], type);

				out_len[0] += 8;
				System.arraycopy(outb, 0, out, 8 * i, 8);
			}
		}
		return 0;
	}

	private void ah_des(byte[] out, byte[] in, byte[][] psubkey, desType type) {
		byte[] m = new byte[64];
		byte[] tmp = new byte[32];
		byte[] li = new byte[32];
		byte[] ri = new byte[64 - 32];

		int i;
		byte2bit(m, in, 64);
		transform(m, m, AhScrtDesTableConst.ip_table, 64);

		System.arraycopy(m, 0, li, 0, 32);
		System.arraycopy(m, 32, ri, 0, 64 - 32);

		if (type == desType.ENCRYPT) {
			for (i = 0; i < 16; ++i) {
				System.arraycopy(ri, 0, tmp, 0, 32);
				f_func(ri, psubkey[i]);
				xor(ri, li, 32);
				System.arraycopy(tmp, 0, li, 0, 32);
			}
		} else {
			for (i = 15; i >= 0; --i) {
				System.arraycopy(li, 0, tmp, 0, 32);
				f_func(li, psubkey[i]);
				xor(li, ri, 32);
				System.arraycopy(tmp, 0, ri, 0, 32);
			}
		}

		System.arraycopy(li, 0, m, 0, 32);
		System.arraycopy(ri, 0, m, 32, 32);
		transform(m, m, AhScrtDesTableConst.ipr_table, 64);

		bit2byte(out, m, 64);
	}

	private void setkey(byte[] key, int len) {
		subkey = new byte[2][16][48];

		int len1 = len > 16 ? 16 : len;
		System.arraycopy(key, 0, deskey, 0, len1);

		setsubkey(subkey[0], deskey);

		is3des = len > 8 ? true : false;

		if (is3des) {
			byte[] aa = new byte[8];
			System.arraycopy(deskey, 8, aa, 0, 8);
			setsubkey(subkey[1], aa);
		}
	}

	private void setsubkey(byte[][] psubkey, byte key[]) {
		byte[] k = new byte[64];
		int i;

		byte2bit(k, key, 64);
		transform(k, k, AhScrtDesTableConst.pc1_table, 56);
		byte[] kl = k.clone();
		byte[] kr = new byte[64 - 28];
		System.arraycopy(k, 28, kr, 0, 64 - 28);

		for (i = 0; i < 16; ++i) {
			rotate(kl, 28, AhScrtDesTableConst.loop_table[i]);
			rotate(kr, 28, AhScrtDesTableConst.loop_table[i]);
			System.arraycopy(kl, 0, k, 0, 28);
			System.arraycopy(kr, 0, k, 28, 28);
			transform(psubkey[i], k, AhScrtDesTableConst.pc2_table, 48);
		}
	}

	private void f_func(byte[] in, byte[] ki) {
		byte[] mr = new byte[48];
		transform(mr, in, AhScrtDesTableConst.e_table, 48);
		xor(mr, ki, 48);
		s_func(in, mr);
		transform(in, in, AhScrtDesTableConst.p_table, 32);
	}

	private void s_func(byte[] out, byte[] in) {
		byte[] ina = new byte[48];
		byte[] inb = new byte[6];
		byte[] outb = new byte[4];
		System.arraycopy(in, 0, ina, 0, 48);
		int i, j, k;
		for (i = 0; i < 8; ++i) {
			System.arraycopy(ina, i * 6, inb, 0, 6);
			j = (inb[0] << 1) + inb[5];
			k = (inb[1] << 3) + (inb[2] << 2) + (inb[3] << 1) + inb[4];
			byte2bit(outb, new byte[] { AhScrtDesTableConst.s_box[i][j][k] }, 4);
			System.arraycopy(outb, 0, out, i * 4, 4);
		}
	}

	private void rotate(byte[] in, int len, int loop) {
		System.arraycopy(in, 0, tmp, 0, loop);
		System.arraycopy(in, loop, in, 0, len - loop);
		System.arraycopy(tmp, 0, in, len - loop, loop);
	}

	private void transform(byte[] out, byte[] in, byte[] table, int len) {
		int i;
		for (i = 0; i < len; ++i) {
			tmp[i] = in[table[i] - 1];
		}
		System.arraycopy(tmp, 0, out, 0, len);
	}

	private void byte2bit(byte[] out, byte[] in, int bitlen) {
		for (int i = 0; i < bitlen; ++i) {
			out[i] = (byte) ((in[i >> 3] >> (i & 7)) & 1);
		}
	}

	private void bit2byte(byte[] out, byte[] in, int bitlen) {
		int i;
		System.arraycopy(bitInit, 0, out, 0, bitlen >> 3);

		for (i = 0; i < bitlen; ++i) {
			out[i >> 3] |= in[i] << (i & 7);
		}
	}

	private void xor(byte[] ina, byte[] inb, int len) {
		for (int i = 0; i < len; i++) {
			ina[i] ^= inb[i];
		}
	}
}
