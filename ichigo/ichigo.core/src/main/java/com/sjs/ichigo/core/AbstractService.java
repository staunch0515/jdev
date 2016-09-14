package com.sjs.ichigo.core;

import java.util.Map;

public abstract class AbstractService implements IService {

	protected AppClient appClient = null;

	public AppClient getAppClient() {
		return appClient;
	}

	public void setAppClient(AppClient appClient) {
		this.appClient = appClient;
	}

	public abstract void exeService() throws AppException;

	protected Map<String, String> contextMap;

	public void setContextMap(Map<String, String> contextMap) {
		this.contextMap = contextMap;
	}

	public Map<String, String> getContextMap() {
		return this.contextMap;
	}

	public Boolean checkPermission() {
		return true;
	}

}
