package com.sjs.ichigo.data;

import com.sjs.ichigo.core.DataException;
import com.sjs.ichigo.utility.LogUtility;
import com.sjs.ichigo.utility.SpringUtility;

public class DataServerFactory {

	public static IDataServer getDataServer() throws DataException {
		LogUtility.info("  DataServerFactory  getDataServer");
		return getDataServer("datasource.default");
	}

	public static IDataServer getDataServer(String name) throws DataException {
		IDataServer dataServer = null;
		try {
			name = "datasource.default";
			dataServer = (IDataServer) SpringUtility.getBean(name);
		} catch (Exception ex) {
			throw new DataException("getDataServer(String name)", ex);
		}
		return dataServer;
	}
}
