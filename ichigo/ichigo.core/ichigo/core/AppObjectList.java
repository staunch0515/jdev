package com.sjs.ichigo.core;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("hiding")
public class AppObjectList<AppObject> {

	protected List<AppObject> dataList = new ArrayList<AppObject>();

	protected AppClient appClient;

	public AppClient getAppClient() {
		return appClient;
	}

	public void setAppClient(AppClient appClient) {
		this.appClient = appClient;
	}

	public void add(AppObject appObject) {
		dataList.add(appObject);
	}

	public List<AppObject> getAppObjectList() {
		return dataList;
	}

	public void AddAppObject(AppObject appObject) {
		dataList.add(appObject);
	}

	public int size() {
		return dataList.size();
	}

}
