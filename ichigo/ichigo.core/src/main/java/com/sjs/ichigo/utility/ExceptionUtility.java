package com.sjs.ichigo.utility;

import com.sjs.ichigo.core.AppException;
import com.sjs.ichigo.core.DataException;
import com.sjs.ichigo.core.SystemException;

public class ExceptionUtility {

	public static void dispose(Object curObj, String method, Throwable ex) throws SystemException {
		if (ex.getClass().getName().equals(SystemException.class.getName())) {
			throw (SystemException) ex;
		}
		if (ex.getClass().getName().equals(DataException.class.getName())) {
			throw (SystemException) ex;
		}
		if (ex.getClass().getName().equals(AppException.class.getName())) {
			throw (SystemException) ex;
		}
		ex.printStackTrace();
		String message = curObj.getClass().getSimpleName() + "_" + method;
		throw new SystemException(message.toUpperCase(), ex);
	}
}
