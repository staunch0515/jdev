package com.sjs.ichigo.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class LogUtility {

	static Logger logger = (Logger) LogManager.getLogger(LogUtility.class.getName());

	public static void throwException(String message, Exception e) {
		logger.fatal(message, e);
	}

	public static void debug(String message) {
		logger.debug(message);
	}

	public static void debug(Object curObj, String method, String message) {
		logger.debug(curObj.toString() + "." + method + "->" + message);
	}

	public static void info(String message) {
		logger.info(message);
	}

}
