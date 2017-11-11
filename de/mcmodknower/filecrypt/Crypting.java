package de.mcmodknower.filecrypt;

import java.math.BigDecimal;

public class Crypting {

	private static final int charmax = ((int) Math.pow(2, 8));

	static String encrypt(short[] key, String text) {
		int pos = 0;
		char[] ctext = text.toCharArray();
		int tmp;
		for (int i = 0; i < ctext.length; i++) {
			tmp = ctext[i] + key[pos];
			pos = (pos + 1) % key.length;
			tmp = tmp % charmax;
			ctext[i] = (char) tmp;
		}
		return new String(ctext);
	}

	static String decrypt(short[] key, String text) {
		int pos = 0;
		char[] ctext = text.toCharArray();
		int tmp;
		for (int i = 0; i < ctext.length; i++) {
			tmp = ctext[i] - key[pos];
			pos = (pos + 1) % key.length;
			while (tmp < 0)
				tmp = tmp + charmax;
			ctext[i] = (char) tmp;
		}
		return new String(ctext);
	}
	
	static short[] getKey(String password) {
		double x[] = new double[512];
		{
			double nummer = nummer(password);
			for (int i = 0; i < 512; i++) {
				x[i] = nummer("" + nummer + i);
			}
		}
		for (int pos = 0; pos < 512; pos++) {
			double tmp = x[pos];
			for (int i = 0; i < 1000000; i++) {
				tmp = 3.9 * tmp * (1 - tmp);
			}
			x[pos] = tmp;
		}

		short[] Schluessel = new short[1024];

		{
			int tmp = (int) Math.pow(2, 8);
			int tmp2 = (int) Math.pow(2, 16);
			for (int i = 0; i < 512; i++) {
				Schluessel[i * 2] = (short) (x[i] * tmp);
				Schluessel[i * 2 + 1] = (short) (((int) (x[i] * tmp2)) >>> 8);
			}
		}
		x = null;
		return Schluessel;
	}
	
	private static double nummer(String text) {
		BigDecimal number = new BigDecimal(0);
		char c = ' ';
		char[] txt = text.toCharArray();
		for (int i = 0; i < txt.length; i++) {
			c = txt[i];
			number = number.add(new BigDecimal(c));
		}
		while (number.compareTo(BigDecimal.ONE) > 0) {
			number = number.movePointLeft(1);
		}
		return number.doubleValue();
	}

}
