package com.sjs.ichigo.data;

import com.sjs.ichigo.core.AppClient;

public abstract class AbstractDataServer implements IDataServer {

	private String dbConnName = "";
	private String connString = "";
	private String username = "";
	private String password = "";

	public String getConnString() {
		return this.connString;
	}

	public void setConnString(String connstring) {
		this.connString = connstring;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTotalSql(String sql) {
		return "select count(*) from (  " + sql + "  )   as total";
	}

	protected AppClient appClient;

	public AppClient getAppClient() {
		return appClient;
	}

	public void setAppClient(AppClient appClient) {
		this.appClient = appClient;
	}

	public String getDbConnName() {
		return dbConnName;
	}

	public void setDbConnName(String dbConnName) {
		this.dbConnName = dbConnName;
	}
}
