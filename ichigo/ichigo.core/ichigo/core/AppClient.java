package com.sjs.ichigo.core;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.sjs.ichigo.data.IDataServer;

public interface AppClient {

	// 对异常的处理
	public abstract void addError(String errName) throws AppException;

	public abstract void addError(String errName, Object... obj1) throws AppException;

	public abstract void save() throws AppException;

	public Boolean hasLogin();

	public abstract String getSpaces();

	public abstract String getNow();

	public abstract void log(String log);

	public abstract String getDataSourceName();

	public abstract void setDataSourceName(String dataSourceName);

	public abstract IDataServer getDefaultDataServer() throws DataException;

	public abstract void setAppData(AppData appData);

	public abstract AppData getAppData(String dataName, String uid);

	public abstract AppData getAppData(String dataName, String field, String value) throws AppException;

	public abstract List<AppData> getAppDataList(String dataName, String field, String value) throws DataException;

	public abstract AppData getAppData(String dataName, String field1, String value1, String field2, String value2);

	public abstract List<AppData> getAppDataList(String dataName, String field1, String value1, String field2,
			String value2);

	public abstract void fireEvent(String eventname);

	public abstract void fireEvent(AppEvent appEvent);

	public abstract void fireEvent(String eventname, AppObject appObject);

	public abstract void fireEvent(AppObject appObject, String methodName, MethodStatus status);

	public abstract Date getContextDate(String string);

	public abstract int getContextInt(String string);

	public abstract double getContextDouble(String string);

	public abstract String getContextValue(String string);

	public abstract Object getContextObject(String key);

	void setContextObject(String key, Object obj);

	void setClientObject(String key, Object obj);

	void setAppObject(String key, Object obj);

	public abstract void close();

	public abstract void open() throws AppException;

	public abstract AppData getAppData(String uid);

	public abstract void output();

	public abstract void clear();

	public abstract AppData getAppDataCopy(AppData appData);

	public abstract AppData getAppDataFromDataSet(String datasetName) throws AppException;

	public abstract List<AppData> getAppDataListFromDataSet(String datasetName) throws AppException;

	public abstract Set<String> getContextNames();

	public abstract List<String> getContextList(String string);

	public abstract String[] getContexttArray(String string);

	public abstract void ExeService(String string) throws AppException;

	public abstract void log(String errName, Object... obj1);

	public abstract void setOutObject(String key, Object obj);

	public abstract AppData getAppDataByCondition(String dataName, String conditionString, String orderString);

	public abstract List<AppData> getAppDataListByCondition(String dataName, String string, String string2)
			throws AppException;

	public abstract List<AppData> getAppDataListByCondition(String dataName, String string, String string2, int i,
			int j) throws AppException;

	public abstract AppUser getLoginUser() throws AppException;

	public abstract void setLoginUser(AppUser user);

	void end();

	void setException(String str, Exception ex);

	public abstract void setLanguage(String contextValue);

	String getLanguage();

	boolean isRepeat(String actionid);

	boolean canOpen();

}