package com.sjs.ichigo.data;

import java.math.BigDecimal;
import java.util.List;

import com.sjs.ichigo.core.AppClient;
import com.sjs.ichigo.core.AppData;
import com.sjs.ichigo.core.DataException;




public interface IDataServer {

	public AppClient getAppClient();

	public void setAppClient(AppClient appClient);

	public void clearTable(String tableName) throws DataException;

	public List<AppData> getAppDataList(String sql) throws DataException;

	public List<AppData> getAppDataList(String dataName,String field,String value) throws DataException;

	public List<AppData> getAppDataList(String dataName,String field1,String value1,String field2,String value2) throws DataException;

	public List<AppData> getAppDataListByCondition(String dataName,String conditionString,String orderString,int start, int end) throws DataException;

	public AppData getAppData(String dataName,String Uid) throws DataException;

	public AppData getAppData(String dataName,String field,String value) throws DataException;

	public AppData getAppData(String dataName,String field1,String value1,String field2,String value2) throws DataException;

	public void SaveDataList(List<AppData> list) throws DataException;

	public String getSingleValue(String sql) throws DataException;

	public int getNext(String tableName,String nextField) throws DataException;

	public String getGUID() throws DataException;

	public int readCount(String sql) throws DataException;

	public BigDecimal readDecimal(String sql) throws DataException;

	public String getTotalSql(String sql);

	public String getPagerSql(String sql,int start, int end);

}
