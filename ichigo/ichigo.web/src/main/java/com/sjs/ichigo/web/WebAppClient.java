package com.sjs.ichigo.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sjs.ichigo.core.AppClient;
import com.sjs.ichigo.core.AppData;
import com.sjs.ichigo.core.AppDataStatus;
import com.sjs.ichigo.core.AppEvent;
import com.sjs.ichigo.core.AppException;
import com.sjs.ichigo.core.AppObject;
import com.sjs.ichigo.core.AppUser;
import com.sjs.ichigo.core.DataException;
import com.sjs.ichigo.core.ErrorMessage;
import com.sjs.ichigo.core.IService;
import com.sjs.ichigo.core.MethodStatus;
import com.sjs.ichigo.core.SystemException;
import com.sjs.ichigo.data.DataServerFactory;
import com.sjs.ichigo.data.IDataServer;
import com.sjs.ichigo.data.IDataSet;
import com.sjs.ichigo.utility.AppUtility;
import com.sjs.ichigo.utility.DateTimeUtility;
import com.sjs.ichigo.utility.LogUtility;
import com.sjs.ichigo.utility.SpringUtility;
import com.sjs.ichigo.utility.WebUtility;

public class WebAppClient implements AppClient {

	// 客户端请求
	private HttpServletRequest request;

	// 客户端响应
	private HttpServletResponse response;

	public HttpServletResponse getResponse() {
		return response;
	}

	// 全局属性
	private static HashMap<String, Object> appMap = new HashMap<String, Object>();

	// 客户属性 Session内有效
	private HashMap<String, Object> clientMap = new HashMap<String, Object>();

	// 全局数据缓冲
	private static HashMap<String, AppData> appObjectList = new HashMap<String, AppData>();

	private HashMap<String, AppData> clientObjectList = new HashMap<String, AppData>();

	// 客户数据缓冲，如果客户的数据全部缓从
	private HashMap<String, Object> contextMap = new HashMap<String, Object>();

	// 本次操作的数据缓，在请求结束时，提交数据处理后，全部清理
	private List<AppData> requestDataList = new ArrayList<AppData>();

	// 客户数据缓冲，如果客户的数据全部缓从
	private HashMap<String, AppData> requestDataListMap = new HashMap<String, AppData>();

	// 输出
	private static HashMap<String, Object> outMap = new HashMap<String, Object>();

	//
	private List<String> logList = new ArrayList<String>();
	//
	private String errorMessage = "";

	private static HashMap<String, WebAppClient> appclientlist = new HashMap<String, WebAppClient>();

	public WebAppClient() {
		appclientlist.put("", this);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#setRequest(javax.servlet.http.
	 * HttpServletRequest )
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
		// 在有新申请时，清理请求数据列表
		requestDataList.clear();
		requestDataListMap.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#setResponse(javax.servlet.http.
	 * HttpServletResponse)
	 */
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	private AppUser loginuser = null;

	public AppUser getLoginUser() throws AppException {
		if (loginuser == null) {
			this.addError("err.user.donotlogin");
		}
		return loginuser;
	}

	public void setLoginUser(AppUser loginuser) {
		logFilePath = getLogFilePath(loginuser.getName());
		this.loginuser = loginuser;
	}

	public Boolean hasLogin() {
		if (loginuser != null) {
			return loginuser.isLogin();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#addError(java.lang.String)
	 */

	public void addError(String errName) throws AppException {
		this.log(errName);
		AppException exception = new AppException(errName);
		throw exception;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#addError(java.lang.String,
	 * java.lang.Object)
	 */

	public void addError(String errName, Object... obj1) throws AppException {
		String errMsg = errName;

		for (int i = 0; i < obj1.length; i++) {
			if (obj1[i] != null) {
				errMsg = errMsg + " " + String.valueOf(i) + "=" + obj1[i].toString();
			}
		}
		this.log(errMsg);
		AppException exception = new AppException(errName);
		throw exception;
	}

	public void setException(String str, Exception ex) throws SystemException {
		this.log(str, ex.toString(), ex.getMessage());
		this.log(getStackTrace(ex));
		errorMessage = "_" + ex.getMessage();
		if (ex.getMessage() == null || ex.getMessage().length() == 0) {
			this.log("  ex.getMessage()  is empty ");
		} else {

			ErrorMessage message = (ErrorMessage) SpringUtility.getBean(ex.getMessage());
			if (message != null) {
				String msg = message.getMessage(this.getLanguage());
				if (msg != null) {
					errorMessage = msg;
				}
			}
		}
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	private String logFilePath = "C:/golden888l_logs/system.txt";

	private String getLogFilePath(String name) {
		String folder = "C:/golden888l_logs/" + name.substring(0, 2);

		File file = new File(folder);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdir();
		}
		return folder + "/" + name + ".log";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#closeRequest()
	 */

	public void output() {
		try {
			if (response != null) {
				response.getWriter().write(getJson().toString());
			}
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void end() {
		this.log("WebAppClient->end At " + DateTimeUtility.GetCurTime("yyyy-MM-dd HH:mm:ss"));
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(logFilePath, true));

			bw.write("\r\n");
			for (int i = 0; i < logList.size(); i++) {
				bw.write("\r\n" + logList.get(i));
			}

			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		isOpen = false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#closeRequest()
	 */

	public void close() {
		this.request.getSession().removeAttribute("appclient");
		this.loginuser = null;
	}

	public JSONObject getJson() {
		JSONObject json = new JSONObject();

		// 日志输出
		JSONArray logs = new JSONArray();
		for (int i = 0; i < logList.size(); i++) {
			JSONObject member = new JSONObject();
			member.put(String.valueOf(i), logList.get(i));
			logs.put(member);
		}
		// json.put("log", logs);

		// 错误输出
		if (errorMessage != null && errorMessage.length() > 0) {
			json.put("error", errorMessage);

			this.holdctionId(actionid);

		} else {
			json.put("success", "true");
			this.endActionId(actionid);
		}

		// 数据输出
		Iterator iter = this.outMap.keySet().iterator();
		if (iter.hasNext()) {
			String name = (String) iter.next();
			json.put(name, this.outMap.get(name));
		}

		return json;
	}

	protected JSONArray getJsonArray(List dataList) {
		JSONArray members = new JSONArray();
		for (int i = 0; i < dataList.size(); i++) {
			JSONObject member = new JSONObject();
			HashMap map = (HashMap) dataList.get(i);
			Set set = map.keySet();
			for (Iterator it = set.iterator(); it.hasNext();) {
				String key = it.next().toString();
				member.put(key, map.get(key));
			}
			members.put(member);
		}
		return members;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getContextDate(java.lang.String)
	 */

	public Date getContextDate(String key) {
		try {
			Date date = (Date) getContextObject(key);
			if (date != null)
				return date;
		} catch (Exception ex) {

		}
		return new Date();
	}

	/*
	 * (non-Javadoc) 先从request中取，然后再从AppClient中取
	 *
	 * @see com.sjs.ichigo.web.AppClient#getContextValue(java.lang.String)
	 */

	public String getContextValue(String key) throws SystemException {
		Object obj = getContextObject(key);

		if (obj != null) {
			try {
				return (String) obj;
			} catch (Exception e) {
				return obj.toString();
			}
		}

		return "";
	}

	/*
	 * (non-Javadoc) 先从request中取，然后再从AppClient中取
	 *
	 * @see com.sjs.ichigo.web.AppClient#getContextValue(java.lang.String)
	 */

	public Object getContextObject(String key) throws SystemException {
		if (this.contextMap.get(key) != null) {
			return this.contextMap.get(key);
		}

		if (this.request != null && this.request.getParameter(key) != null) {
			return this.request.getParameter(key);
		}

		if (clientMap.get(key) != null) {
			return clientMap.get(key);
		}
		if (appMap.get(key) != null) {
			return appMap.get(key);
		}
		return SpringUtility.getBean(key);
	}

	public Set<String> getContextNames() {
		HashMap<String, String> mm = new HashMap<String, String>();
		Iterator<String> appset = appMap.keySet().iterator();
		while (appset.hasNext()) {
			String key = appset.next();
			if (!mm.containsKey(key)) {
				mm.put(key, null);
			}
		}
		Iterator<String> client = clientMap.keySet().iterator();
		while (client.hasNext()) {
			String key = client.next();
			if (!mm.containsKey(key)) {
				mm.put(key, null);
			}
		}
		Iterator<String> context = contextMap.keySet().iterator();
		while (context.hasNext()) {
			String key = context.next();
			if (!mm.containsKey(key)) {
				mm.put(key, null);
			}
		}
		if (this.request != null) {
			Enumeration<String> reqset = this.request.getParameterNames();
			while (reqset.hasMoreElements()) {
				String key = reqset.nextElement();
				if (!mm.containsKey(key)) {
					mm.put(key, null);
				}
			}
		}
		return mm.keySet();
	}

	public void setContextObject(String key, Object obj) {
		if (this.contextMap.containsKey(key)) {
			this.contextMap.remove(key);
		}
		this.contextMap.put(key, obj);
	}

	public void setClientObject(String key, Object obj) {
		if (this.clientMap.get(key) != null) {
			this.clientMap.remove(key);
		}
		this.clientMap.put(key, obj);
	}

	public void setOutObject(String key, Object obj) {
		if (this.outMap.get(key) != null) {
			this.outMap.remove(key);
		}
		this.outMap.put(key, obj);
		setContextObject(key, obj);
	}

	public void setAppObject(String key, Object obj) {
		if (appMap.containsKey(key)) {
			appMap.remove(key);
		}
		appMap.put(key, obj);
	}

	// 应该有一个树形结构，在属性结构的上面
	private int i = 1;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getSpaces()
	 */

	public String getSpaces() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getNow()
	 */

	public String getNow() {
		return null;
	}

	AppData appData = null;
	String methodContent = "";

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#log(java.lang.String)
	 */

	public void log(String logstr) {
		logstr = String.valueOf(System.currentTimeMillis()) + "  " + logstr;
		logList.add(logstr);
	}

	public void log(String errName, Object... obj1) {
		String logstr = errName;

		for (int i = 0; i < obj1.length; i++) {
			if (obj1[i] != null) {
				logstr = logstr + " " + String.valueOf(i) + "=" + obj1[i].toString();
			}
		}
		System.currentTimeMillis();
		logstr = String.valueOf(System.currentTimeMillis()) + "  " + logstr;
		logList.add(logstr);
	}

	// 以下是数据查找体系
	private String dataSourceName = "";

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getDataSourceName()
	 */

	public String getDataSourceName() {
		return this.dataSourceName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#setDataSourceName(java.lang.String)
	 */

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getDefaultDataServer()
	 */

	public IDataServer getDefaultDataServer() throws DataException {
		IDataServer dataServer = DataServerFactory.getDataServer(this.dataSourceName);
		dataServer.setAppClient(this);
		this.log("WebAppClient-->getDefaultDataServer()");
		return dataServer;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getAppData(java.lang.String,
	 * java.lang.String)
	 */

	public AppData getAppData(String dataName, String uid) {
		try {
			if (uid == null && uid.length() == 0) {
				try {
					throw new Exception("uid is empty.");
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
			AppData appData = getAppData(uid);
			if (appData != null) {
				return appData;
			}
			appData = getDefaultDataServer().getAppData(dataName, uid);
			if (appData != null) {
				setAppData(appData);
				return appData;
			}
			return null;
		} catch (DataException e) {
			e.printStackTrace();
			this.log("exe getAppData(String dataName, String uid) -->" + e.getMessage());
		} finally {

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getAppData(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */

	public AppData getAppData(String dataName, String field, String value) throws AppException {
		try {
			AppData appData = getDefaultDataServer().getAppData(dataName, field, value);
			if (appData != null) {
				appData.setAppDataStuats(AppDataStatus.Save);
				setAppData(appData);
				return appData;
			}
			log("err.appdata.cannotfindbyfield", dataName, field, value);
		} catch (DataException e) {

			e.printStackTrace();
			this.clear();
			throw new AppException(e.getMessage());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getAppData(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */

	public AppData getAppDataByCondition(String dataName, String conditionString, String orderString) {
		try {
			AppData appData = getDefaultDataServer().getAppData(dataName, conditionString, orderString);
			if (appData != null) {
				appData.setAppDataStuats(AppDataStatus.Save);
				setAppData(appData);
				return appData;
			}
			log("err.appdata.cannotfindbyfield", dataName, conditionString, orderString);
		} catch (DataException e) {

			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sjs.ichigo.web.AppClient#getAppData(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */

	public AppData getAppData(String dataName, String field1, String value1, String field2, String value2) {
		try {
			AppData appData = getDefaultDataServer().getAppData(dataName, field1, value1, field2, value2);
			if (appData != null) {
				appData.setAppDataStuats(AppDataStatus.Save);
				setAppData(appData);
				return appData;
			}
			log("err.appdata.cannotfindbyfield", dataName, field1, value2, field2, value2);
		} catch (DataException e) {

			e.printStackTrace();
		}
		return null;
	}

	// 以下是三级缓冲体系 Request-->Session-->Golbal

	public AppData getAppData(String uid) {
		if (requestDataListMap.containsKey(uid)) {
			return requestDataListMap.get(uid);
		}
		AppData appData = getAppDataFromSession(uid);
		if (appData != null) {
			setAppData(appData);
		}
		return appData;
	}

	public void setAppData(AppData appData) {
		if (appData.getKeyValue() == null) {
			try {
				// throw new Exception("getKeyValue is null ");
			} catch (Exception e) {

				e.printStackTrace();
			}
			return;
		}

		if (requestDataListMap.containsKey(appData.getKeyValue())) {
			return;
		}

		requestDataList.add(appData);
		requestDataListMap.put(appData.getKeyValue(), appData);
		setAppDataToSession(appData);
	}

	protected AppData getAppDataFromSession(String uid) {
		if (clientObjectList.containsKey(uid)) {
			return (AppData) clientObjectList.get(uid);
		}
		return getAppDataFromGolbal(uid);
	}

	protected void setAppDataToSession(AppData appData) {
		if (clientObjectList.containsKey(appData.getKeyValue())) {
			clientObjectList.remove(appData.getKeyValue());
		}
		clientObjectList.put(appData.getKeyValue(), appData);
		// setAppDataToGolbal(appData);
	}

	protected static AppData getAppDataFromGolbal(String uid) {
		if (appObjectList.containsKey(uid)) {
			return (AppData) appObjectList.get(uid);
		}
		return null;
	}

	protected static void setAppDataToGolbal(AppData appData) {
		if (appObjectList.containsKey(appData.getKeyValue())) {
			appObjectList.remove(appData.getKeyValue());
		}
		appObjectList.put(appData.getKeyValue(), appData);
	}

	public void fireEvent(String eventname) {
		// 首先记录日志
		String logstr = "eventName:" + eventname;
		this.log(logstr);
	}

	public void fireEvent(AppEvent appEvent) {
	}

	public void fireEvent(String eventname, AppObject appObject) {
	}

	public void fireEvent(AppObject appObject, String methodName, MethodStatus status) {
		// 首先记录日志
		String logstr = "eventName:" + methodName + "_" + status;
		if (appObject != null) {
			logstr = logstr + "|eventOjb:" + appObject.toString();
		}
		logList.add(logstr);
	}

	private HashMap actionMap = new HashMap();

	public String getActionId() {
		String str = AppUtility.GetUUID();
		actionMap.put(str, null);
		return str;
	}

	public void endActionId(String actionid) {
		if (actionMap.containsKey(actionid)) {
			actionMap.remove(actionid);
		}
	}

	public void extActionId(String actionid) {
		if (actionMap.containsKey(actionid)) {
			actionMap.remove(actionid);
		}
		actionMap.put(actionid, "doing");
	}

	public boolean canActionId(String actionid) {
		LogUtility.debug(this, "canActionId", "actionMap:" + actionMap.size());
		if (actionMap.containsKey(actionid)) {
			Object action = actionMap.get(actionid);
			if (action == null) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public void holdctionId(String actionid) {
		if (actionMap.containsKey(actionid)) {
			actionMap.remove(actionid);
		}
		actionMap.put(actionid, null);
	}

	private String actionid = "";

	private boolean isOpen = false;

	public boolean canOpen() {
		if (!isOpen)
			return true;
		return isOpen;
	}

	public boolean isRepeat(String actionid) {

		if (!actionMap.containsKey(actionid)) {
			return true;
		}
		if (actionMap.get(actionid) != null) {
			return true;
		}
		actionMap.remove(actionid);
		actionMap.put(actionid, "doing");
		return false;
	}

	public void open() throws AppException {
		isOpen = true;

		this.errorMessage = "";
		this.logList.clear();
		this.contextMap.clear();
		this.requestDataList.clear();

		this.log("WebAppClient->open  At " + DateTimeUtility.GetCurTime("yyyy-MM-dd HH:mm:ss"));
		this.log("Request URL=" + this.request.getRequestURL());
		Enumeration<String> ee = this.request.getParameterNames();
		if (this.request.getParameter("actionid") != null) {
			this.log("actionid=" + this.request.getParameter("actionid"));

			this.actionid = this.request.getParameter("actionid");
			this.extActionId(actionid);
		}

		while (ee.hasMoreElements()) {
			String paraname = ee.nextElement();
			if (!paraname.equals("actionid") && !paraname.equals("_"))
				this.log(paraname + "=" + this.request.getParameter(paraname));
		}
	}

	public void clear() {
		this.errorMessage = "";
		this.logList.clear();
		this.requestDataList.clear();
	}

	public AppData getAppDataCopy(AppData appData) {
		AppData _appData = appData.copy();
		setAppData(_appData);
		return _appData;
	}

	public void save() throws AppException, SystemException {
		this.log("start  save data ");
		try {
			requestDataList.clear();
			Object[] li = requestDataListMap.values().toArray();
			for (int i = 0; i < li.length; i++) {
				AppData appData = (AppData) li[i];
				requestDataList.add(appData);
			}
			this.getDefaultDataServer().SaveDataList(requestDataList);
			for (int i = 0; i < requestDataList.size(); i++) {
				AppData appData = requestDataList.get(i);
				appData.setAppDataStuats(AppDataStatus.Save);
			}
		} catch (DataException e) {
			e.printStackTrace();
			this.setException("WebAppClient-->save_1", e);
			this.addError(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			this.setException("WebAppClient-->save_2", e);
			this.addError(e.getMessage());

		} finally {
			this.log("end  save data ");
		}
	}

	public List<AppData> getAppDataList(String dataName, String field, String value) throws DataException {
		List<AppData> list = null;
		try {
			list = getDefaultDataServer().getAppDataList(dataName, field, value);
			for (int i = 0; i < list.size(); i++) {
				this.setAppData(list.get(i));
			}
		} catch (DataException e) {
			throw e;
		}
		return list;
	}

	public List<AppData> getAppDataList(String dataName, String field1, String value1, String field2, String value2) {
		List<AppData> list = null;
		try {
			list = getDefaultDataServer().getAppDataList(dataName, field1, value1, field2, value2);
			for (int i = 0; i < list.size(); i++) {
				this.setAppData(list.get(i));
			}
		} catch (DataException e) {

			e.printStackTrace();
		}
		return list;
	}

	public int getContextInt(String key) {
		int result = 0;
		try {
			Object obj = getContextObject(key);
			if (obj != null) {
				return Integer.parseInt(obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public double getContextDouble(String key) {
		double result = 0d;
		try {
			Object obj = getContextObject(key);
			if (obj != null) {
				return Double.parseDouble(obj.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<AppData> getAppDataListFromDataSet(String datasetName) throws AppException {
		try {
			IDataSet dataSet = (IDataSet) getContextObject(datasetName);
			dataSet.setAppClient(this);
			return dataSet.getDataList();
		} catch (Exception ex) {
			this.log(ex.getMessage());
			throw new AppException(ex.getMessage());
		}
	}

	public List getContextList(String string) {
		return WebUtility.getRequestList(this.request, string);
	}

	public String[] getContexttArray(String string) {
		return WebUtility.getRequestArray(this.request, string);
	}

	public void ExeService(String serviceName) throws AppException, SystemException {
		IService service = (IService) this.getContextObject(serviceName);
		if (service != null) {
			service.setAppClient((AppClient) this);
			try {
				service.exeService();
			} catch (DataException e) {
				this.log(e.getMessage());
				throw new AppException(e.getMessage());
			}
		} else {
			addError("Service " + serviceName + " cannot find.");
		}
	}

	public AppData getAppDataFromDataSet(String datasetName) throws AppException {
		try {
			IDataSet dataSet = (IDataSet) getContextObject(datasetName);
			dataSet.setAppClient(this);

			List<AppData> list = dataSet.getDataList();
			if (list.size() > 0) {
				return list.get(0);
			}
		} catch (Exception ex) {
			this.log(ex.getMessage());
			throw new AppException(ex.getMessage());
		}
		return null;
	}

	public List<AppData> getAppDataListByCondition(String dataName, String conditionString, String orderString)
			throws AppException {
		List<AppData> list = null;
		try {
			list = getDefaultDataServer().getAppDataListByCondition(dataName, conditionString, orderString, 0, 99);
			for (AppData appData : list) {
				appData.setAppDataStuats(AppDataStatus.Save);
				setAppData(appData);
			}
		} catch (DataException ex) {
			this.log(ex.getMessage());
			throw new AppException(ex.getMessage());
		}
		return list;
	}

	public List<AppData> getAppDataListByCondition(String dataName, String conditionString, String orderString,
			int start, int maxnums) throws AppException {
		List<AppData> list = null;
		try {
			list = getDefaultDataServer().getAppDataListByCondition(dataName, conditionString, orderString, start,
					maxnums);
			for (AppData appData : list) {
				appData.setAppDataStuats(AppDataStatus.Save);
				setAppData(appData);
			}
		} catch (DataException ex) {
			this.log(ex.getMessage());
			throw new AppException(ex.getMessage());
		}
		return list;
	}

	private String lang = "en";

	public void setLanguage(String lang) {
		this.lang = lang;
	}

	public String getLanguage() {
		return lang;
	}
}
