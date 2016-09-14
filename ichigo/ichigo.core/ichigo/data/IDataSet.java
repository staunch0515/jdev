package com.sjs.ichigo.data;

import java.util.List;

import com.sjs.ichigo.core.AppClient;
import com.sjs.ichigo.core.AppData;
import com.sjs.ichigo.core.AppException;
import com.sjs.ichigo.core.DataException;

public interface IDataSet {
	AppClient getAppClient();
	void setAppClient(AppClient appClient);
	List<AppData>  getDataList() throws DataException, AppException;
	List<AppData>  getDataList(int start,int end) throws AppException;
	int getTotal() throws DataException;
}
