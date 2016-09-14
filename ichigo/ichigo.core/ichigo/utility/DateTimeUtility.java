package com.sjs.ichigo.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author user
 *
 */
public class DateTimeUtility {

	public static String GetToday() {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		return f.format(date.getTime());
	}

	public static String GetCurTime() {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
		return f.format(date.getTime());
	}

	public static String GetCurTime(String formatString) {
		String strValue = "yyyy-MM-dd kk:mm:ss";
		if (formatString == "")
			formatString = strValue;
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat(formatString);
		return f.format(date.getTime());
	}

	public static Date GetDate(String dateString, String formatString) {
		Date date = new Date();
		SimpleDateFormat f = new SimpleDateFormat(formatString);
		try {
			return f.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static Date GetDate(String string) {
		return GetDate(string, "yyyy-MM-dd");
	}

	public static Date GetDateAfterMinute(Date startdate, int i) {
		if (startdate == null)
			startdate = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);
		calendar.add(Calendar.MINUTE, i);
		return calendar.getTime();
	}

	public static Date GetDateAfterMinute(int i) {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, i);
		return calendar.getTime();
	}

	public static Date GetDateAfterDay(int i) {
		Date date = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, i);
		return calendar.getTime();
	}

	public static Date GetDateAfterYear(int i) {
		return GetDateAfterYear(null, i);
	}

	public static Date GetDateAfterYear(Date startdate, int i) {
		if (startdate == null) {
			startdate = new Date();
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(startdate);
		calendar.add(Calendar.YEAR, i);
		return calendar.getTime();
	}

	public static String GetDataTimeString(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd kk:mm");
		return f.format(date.getTime());
	}

	public static String GetDataTimeString(Date date, String fomatstr) {
		SimpleDateFormat f = new SimpleDateFormat(fomatstr);
		return f.format(date.getTime());
	}

	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	public static String getMonthStart() throws ParseException {
		return getMinMonthDate(new Date());
	}

	public static String getNextMonthStart() throws ParseException {
		return getNextMinMonthDate(new Date());
	}

	public static String getNextMinMonthDate(Date date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return dateFormat.format(calendar.getTime());
	}

	public static String getMinMonthDate(Date date) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return dateFormat.format(calendar.getTime());
	}

}
