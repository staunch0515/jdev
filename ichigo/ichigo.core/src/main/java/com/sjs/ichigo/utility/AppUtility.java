package com.sjs.ichigo.utility;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class AppUtility {

	public static String GetUUID() {
		return java.util.UUID.randomUUID().toString();
	}

	public static String GetMemberNO() {
		return "";
	}

	public static String GetDefaultPassword() {
		Random rd = new Random();
		String n = "";
		int getNum;
		do {
			getNum = Math.abs(rd.nextInt()) % 10 + 48;// 产生数字0-9的随机数
			// getNum = Math.abs(rd.nextInt())%26 + 97;//产生字母a-z的随机数
			char num1 = (char) getNum;
			String dn = Character.toString(num1);
			n += dn;
		} while (n.length() < 6);
		return n;
	}

	public static String GetToday() {
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy-MM-dd");
		return matter1.format(dt);
	}

	public static String GetNow() {
		Date dt = new Date();
		SimpleDateFormat matter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return matter1.format(dt);
	}

	public static final String PREFIX = "DD";
	private static long code;

	public static synchronized String nextCode() {
		code++;
		String str = new SimpleDateFormat("yyyyMMHHMMSS").format(new Date());
		long m = Long.parseLong((str)) * 10000;
		m += code;
		return PREFIX + m;
	}

	public static final String SORTPREFIX = "No";
	private static long code2;

	public static synchronized String sortCode() {
		code2++;
		String str = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		long m = Long.parseLong((str)) * 10000;
		m += code2;
		return SORTPREFIX + m;
	}

	private static long code3;
	private static int codeint = 0;

	public static synchronized String nextUserNo() {
		Date d = new Date();
		Long l = 0l;
		if (code3 == d.getTime()) {
			codeint = codeint + 1;
			l = d.getTime() + codeint;
		} else {
			codeint = 0;
			code3 = d.getTime();
			l = d.getTime();
		}

		String result = String.valueOf(l).substring(4);

		return result;
	}

}
