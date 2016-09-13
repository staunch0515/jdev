package com.sjs.ichigo.core;

import java.util.Map;

public interface IService {
	AppClient getAppClient();

	void setAppClient(AppClient appClient);

	void exeService() throws AppException, DataException;

	Boolean checkPermission();

	void setContextMap(Map<String, String> contextMap);

	Map<String, String> getContextMap();
}
