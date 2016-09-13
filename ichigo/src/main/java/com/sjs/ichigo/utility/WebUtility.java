package com.sjs.ichigo.utility;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class WebUtility {

	public static String getRequestRight(HttpServletRequest request, String startWith) {
		String right = "";
		Enumeration<String> e = (Enumeration<String>) request.getParameterNames();
		while (e.hasMoreElements()) {
			String paraName = (String) e.nextElement();
			if (paraName.startsWith(startWith))
				right = StringUtility.TrimLeft(paraName, startWith);
		}
		return right;
	}

	public static String[] getRequestArray(HttpServletRequest request, String paraName) {
		String strValue = WebUtility.getRequestValue(request, paraName);
		strValue = StringUtility.TrimRigth(strValue, ",");
		return strValue.split(",");
	}

	public static List<String> getRequestList(HttpServletRequest request, String startWith) {
		List<String> fList = new ArrayList<String>();
		Enumeration<String> e = (Enumeration<String>) request.getParameterNames();
		while (e.hasMoreElements()) {
			String paraName = (String) e.nextElement();
			if (paraName.startsWith(startWith)) {
				String field = StringUtility.TrimLeft(paraName, startWith);
				fList.add(field);
			}
		}
		return fList;
	}

	public static int getRequestInt(HttpServletRequest request, String fieldName) {
		if (request.getParameter(fieldName) != null) {
			try {
				return Integer.parseInt(request.getParameter(fieldName).toString());
			} catch (Exception ex) {

			}
		}
		return 0;
	}

	public static String getRequestValue(HttpServletRequest request, String fieldName) {
		if (request.getParameter(fieldName) != null) {
			String value = request.getParameter(fieldName).toString();
			value = StringUtility.Replace(value, "#curtime#", DateTimeUtility.GetCurTime());
			return value;
		}
		return null;
	}

	public static Cookie getCookieByName(HttpServletRequest request, String name) {
		Map<String, Cookie> cookieMap = ReadCookieMap(request);
		if (cookieMap.containsKey(name)) {
			Cookie cookie = (Cookie) cookieMap.get(name);
			return cookie;
		} else {
			return null;
		}
	}

	private static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();
		if (null != cookies) {
			for (Cookie cookie : cookies) {
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return cookieMap;
	}
}
