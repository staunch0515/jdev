package com.sjs.ichigo.core;

import com.sjs.ichigo.utility.AppUtility;

public class AppObject {

	// AppClient Part
	protected AppClient appClient;

	public AppClient getAppClient() {
		return appClient;
	}

	public void setAppClient(AppClient appClient) {
		this.appClient = appClient;
	}

	// UID Part
	protected String UID = "";

	public String getUid() {
		return UID;
	}

	// AppData Part
	protected AppData appData;

	public AppData getAppData() {
		if (appData == null) {
			appData = appClient.getAppData(UID);
			if (appData == null) {
				appData = appClient.getAppData(getDataName(), UID);
			}
		}
		return appData;
	}

	public static String DataName = "UserInfo";

	public String getDataName() {
		return DataName;
	}

	// 通过ID新建一个对象
	public AppObject(AppClient appClient) {
		this.appClient = appClient;
		this.UID = AppUtility.GetUUID();
		this.appData = new AppData(getDataName());
		this.appData.setKeyValue(this.UID);
		this.appData.setAppDataStuats(AppDataStatus.New);
		this.appData.add("CREATETIME", "@NOW");
		this.appClient.setAppData(appData);
	}

	public AppObject(AppClient appClient, String uid) {
		this.appClient = appClient;
		this.UID = uid;
	}

	public AppObject(AppClient appClient, AppData appData) {
		this.appClient = appClient;
		this.UID = appData.getKeyValue();
		this.appData = appData;
	}

	public void delete() {
		this.getAppData().add("DELFLG", "1");
	}

	public boolean hasDeleted() {
		if (this.getAppData().getInt("DELFLG") == 1) {
			return true;
		}
		return false;
	}

	public String getHtml() {
		return "";
	}
}
