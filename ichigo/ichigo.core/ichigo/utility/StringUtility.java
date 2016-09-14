package com.sjs.ichigo.utility;

/**
 * @author user

 */
public class StringUtility {


	/**
	 * @param intString
	 * @return
	 */
	public static int getInt(String intString) {
		int result = 0;
		try {
			if (intString != null && intString.length() > 0) {
				result = Integer.parseInt(intString);
			}
		} catch (Exception ex) {

		}
		return result;
	}

	public static double getDouble(String douleString) {
		double result = 0;
		try {
			if (douleString != null && douleString.length() > 0) {
				result = Double.parseDouble(douleString);
			}
		} catch (Exception ex) {

		}
		return result;
	}

	public static String Replace(String source, String from, String to) {
		if (source.indexOf(from) > -1) {
			return SplitLeft(source, from) + to + SplitRight(source, from);
		} else {
			return source;
		}
	}

	public static boolean IsEmptyOrNull(String strValue) {
		boolean b = false;
		if (strValue == null || strValue.length() == 0)
			return true;
		return b;
	}

	public static boolean HasContent(String strValue) {
		boolean b = true;
		if (strValue == null || strValue.length() == 0)
			return false;
		return b;
	}

	public static boolean ContainContent(String strValue, String strContent) {
		if (strValue.toLowerCase().indexOf(strContent.toLowerCase()) > -1) {
			return true;
		}
		return false;
	}

	public static String TrimLeft(String strValue, String strContent) {
		return strValue.substring(strContent.length());
	}

	public static String TrimRigth(String strValue, String strContent) {
		return strValue.substring(0, strValue.length() - strContent.length());
	}

	public static String SplitLeft(String strValue, String strContent) {
		return strValue.substring(0, strValue.lastIndexOf(strContent));
	}

	public static String SplitRight(String strValue, String strContent) {
		int start = strValue.indexOf(strContent) + strContent.length();
		return strValue.substring(start, strValue.length());
	}

	public static boolean validateEmail(String email) {
		// Pattern pattern =
		// Pattern.compile("[0-9a-zA-Z]*.[0-9a-zA-Z]*@[a-zA-Z]*.[a-zA-Z]*",
		// Pattern.LITERAL);
		if (email == null) {
			return false;
		}

		// 验证开始

		// 不能有连续的.
		if (email.indexOf("..") != -1) {
			return false;
		}

		// 必须带有@
		int atCharacter = email.indexOf("@");
		if (atCharacter == -1) {
			return false;
		}

		// 最后一个.必须在@之后,且不能连续出现
		if (atCharacter > email.lastIndexOf('.')
				|| atCharacter + 1 == email.lastIndexOf('.')) {
			return false;
		}

		// 不能以.,@结束和开始
		if (email.endsWith(".") || email.endsWith("@") || email.startsWith(".")
				|| email.startsWith("@")) {
			return false;
		}

		return true;
	}
}
