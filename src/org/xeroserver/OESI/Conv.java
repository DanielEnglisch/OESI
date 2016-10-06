package org.xeroserver.OESI;

public class Conv {
	public static int Int(String s) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static double Dou(String s) {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
