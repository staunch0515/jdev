package com.sjs.ichigo.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.sjs.ichigo.utility.AppUtility;
import com.sjs.ichigo.utility.StringUtility;

public class AppData {

	private HashMap<String, Object> dataTable = new HashMap<String, Object>();

	public HashMap<String, Object> getDataMap() {
		return dataTable;
	}

	public AppData() {
	}

	public AppData(String tableName) {
		this.tableName = tableName;
	}

	private AppDataStatus appDataStatus = AppDataStatus.New;

	public void setAppDataStuats(AppDataStatus appDataStatus) {
		this.appDataStatus = appDataStatus;
	}

	public AppDataStatus getAppDataStuats() {
		return this.appDataStatus;
	}

	private String tableName = "";

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void add(String fieldName, Object object) {
		if (fieldName.toUpperCase().equals(keyField.toUpperCase())) {
			return;
		}
		if (appDataStatus == AppDataStatus.Save) {
			appDataStatus = AppDataStatus.NoSave;
		}

		dataTable.put(fieldName.toUpperCase(), object);
	}

	public String getString(String fieldName) {
		fieldName = fieldName.toUpperCase();
		if (this.dataTable.get(fieldName) != null) {
			if (this.dataTable.get(fieldName).getClass().getSimpleName().equals("Date")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				java.util.Date date = (Date) this.dataTable.get(fieldName);
				return sdf.format(date);
			}
			if (this.dataTable.get(fieldName).getClass().getSimpleName().equals("Integer")) {
				this.dataTable.get(fieldName).toString();
			}
			return (String) this.dataTable.get(fieldName).toString();
		}
		return "";
	}

	public Date getDate(String fieldName) {
		fieldName = fieldName.toUpperCase();
		Date result = null;
		try {
			String strValue = getString(fieldName);
			if (StringUtility.HasContent(strValue)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				result = sdf.parse(strValue);
			}
		} catch (Exception ex) {
		}
		return result;
	}

	public int getInt(String fieldName) {
		int result = 0;
		try {
			String strValue = getString(fieldName);
			if (StringUtility.HasContent(strValue)) {
				result = Integer.parseInt(strValue);
			}
		} catch (Exception ex) {
			result = 0;
		}
		return result;
	}

	public float getFloat(String fieldName) {
		float result = 0;
		try {
			String strValue = getString(fieldName);
			if (StringUtility.HasContent(strValue)) {
				result = Float.parseFloat(strValue);
			}
		} catch (Exception ex) {
		}
		return result;
	}

	public Boolean getBoolean(String fieldName) {
		Boolean result = false;
		try {
			String strValue = getString(fieldName);
			if (StringUtility.HasContent(strValue)) {
				result = Boolean.parseBoolean(strValue);
			}
		} catch (Exception ex) {
		}
		return result;
	}

	private String keyField = "UID";

	public String getKeyField() {
		return keyField;
	}

	public void setKeyField(String keyField) {
		this.keyField = keyField;
	}

	private String keyValue;

	public String getKeyValue() {
		return keyValue;
	}

	public void setKeyValue(String keyValue) {
		this.keyValue = keyValue;
	}

	private String seqName = null;

	public String getSeqName() {
		return seqName;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

	public List<String> getFieldList() {
		List<String> list = new ArrayList<String>();

		Set<String> set = dataTable.keySet();

		for (Iterator<String> it = set.iterator(); it.hasNext();) {
			String key = it.next().toString();
			list.add(key);
		}

		return list;
	}

	public void delete() {
		if (appDataStatus == AppDataStatus.New) {
			appDataStatus = AppDataStatus.Save;
		} else {
			appDataStatus = AppDataStatus.Delete;
		}
	}

	public double getDouble(String fieldName) {
		double result = 0;
		try {
			String strValue = getString(fieldName);
			if (StringUtility.HasContent(strValue)) {
				result = Double.parseDouble(strValue);
			}
		} catch (Exception ex) {
		}
		return result;
	}

	public AppData copy() {
		AppData appData = new AppData(this.getTableName());
		appData.appDataStatus = AppDataStatus.New;
		appData.setKeyValue(AppUtility.GetUUID());

		List<String> flist = this.getFieldList();

		for (int i = 0; i < flist.size(); i++) {
			String fieldName = flist.get(i).toString();
			Object fieldValue = dataTable.get(fieldName);
			appData.add(fieldName, fieldValue);
		}

		return appData;
	}
}
