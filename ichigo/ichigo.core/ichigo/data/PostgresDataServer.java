package com.sjs.ichigo.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sjs.ichigo.core.AppData;
import com.sjs.ichigo.core.AppDataStatus;
import com.sjs.ichigo.core.ConnectionPool;
import com.sjs.ichigo.core.DataException;
import com.sjs.ichigo.utility.AppUtility;
import com.sjs.ichigo.utility.LogUtility;
import com.sjs.ichigo.utility.StringUtility;

public class PostgresDataServer extends AbstractDataServer implements IDataServer {

	private ConnectionPool connectionPool;

	public PostgresDataServer() {

	}

	public Connection getConnection() throws DataException {
		try {
			if (connectionPool == null) {
				connectionPool = (ConnectionPool) this.appClient.getContextObject(this.getDbConnName());
			}
			Connection conn = connectionPool.getConnnection();
			conn.setReadOnly(true);
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataException("MySqlDataServer-->getConnection ", e);
		}
	}

	public void returnConnection(Connection conn) {
		if (connectionPool == null) {
			this.appClient.getAppData("datasource.connectionpool");
		}
		if (conn != null) {
			this.appClient.log("MySqlDataServer->returnConnection" + conn.toString());
			ConnectionPool.returnConnection(conn);
		}
	}

	public void execute(String sql) throws DataException {
		Connection conn = null;
		try {
			conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			this.getAppClient().log(sql);
			pstmt.executeUpdate(sql);
			pstmt.close();
			pstmt = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DataException(" execute(String sql) ", ex);
		} finally {
			returnConnection(conn);
		}
	}

	public int readCount(String sql) throws DataException {
		this.appClient.log("MySqlDataServer->readCount(String sql)");
		int nCount = 0;
		Connection conn = null;
		try {
			conn = this.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			this.getAppClient().log(sql);
			ResultSet tmp = null;
			tmp = pstmt.executeQuery(sql);
			if (tmp != null && tmp.next()) {
				nCount = tmp.getInt(1);
			} else {
				nCount = 0;
			}
			tmp.close();
			tmp = null;
			pstmt.close();
			pstmt = null;
		} catch (SQLException e) {
			e.printStackTrace();
			this.appClient.log("MySqlDataServer-->readCount" + e.getMessage());
		} finally {
			returnConnection(conn);
			this.appClient.log("MySqlDataServer->readCount(String sql)");
		}
		return nCount;
	}

	public String getSingleValue(String sql) throws DataException {

		String result = "";
		Connection conn = null;
		try {
			conn = this.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			this.getAppClient().log(sql);
			if (pstmt != null) {
				ResultSet tmp = null;
				tmp = pstmt.executeQuery(sql);
				if (tmp != null && tmp.next()) {
					result = tmp.getString(1);
				} else {
					result = "";
				}
				tmp.close();
				tmp = null;
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.appClient.log("MySqlDataServer-->getSingleValue" + e.getMessage());
		} finally {
			this.returnConnection(conn);
		}
		return result;
	}

	public BigDecimal readDecimal(String sql) throws DataException {
		BigDecimal nCount = new BigDecimal("0.0");

		Connection conn = null;
		try {
			conn = this.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			this.getAppClient().log(sql);
			if (pstmt != null) {
				ResultSet tmp = null;
				tmp = pstmt.executeQuery(sql);
				if (tmp != null && tmp.next()) {
					nCount = tmp.getBigDecimal(1);
				}
				tmp.close();
				tmp = null;
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			this.appClient.log("MySqlDataServer-->readDecimal" + e.getMessage());
		} finally {
			this.returnConnection(conn);
		}
		return nCount;
	}

	private String getLocal(String txt) {

		if (txt.equals("NOW")) {
			return "current_timestamp";
		}

		return txt;
	}

	public String SaveData(AppData data) throws DataException {
		String sql = "";

		if (data.getAppDataStuats() == AppDataStatus.New) {

			String fieldlist = data.getKeyField() + ",";

			String valuelist = "'" + data.getKeyValue() + "',";

			if (data.getSeqName() != null && (!data.getSeqName().equals(""))) {
				valuelist = data.getSeqName() + ".nextval,";
			} else {
				if (data.getKeyValue() == null || data.getKeyValue().equals("")) {
					data.setKeyValue(AppUtility.GetUUID());
				}
			}

			List<String> flist = data.getFieldList();

			for (int i = 0; i < flist.size(); i++) {
				String fieldName = flist.get(i);
				String fieldValue = data.getString(fieldName);

				if (!fieldValue.equals("")) {
					fieldlist = fieldlist + flist.get(i).toString() + ",";

					String vv = data.getString(flist.get(i).toString());
					if (vv.indexOf("'") > 0 || vv.indexOf('@') == 0) {
						if (vv.indexOf('@') == 0) {
							vv = getLocal(vv.substring(1));
						}
						valuelist = valuelist + vv + ",";
					} else {
						valuelist = valuelist + "'" + vv + "',";
					}
				}
			}

			fieldlist = StringUtility.TrimRigth(fieldlist, ",");
			valuelist = StringUtility.TrimRigth(valuelist, ",");

			sql = " INSERT INTO " + data.getTableName() + " (" + fieldlist + ") VALUES (" + valuelist + ") ";

			return sql;
		}

		if (data.getAppDataStuats() == AppDataStatus.NoSave) {

			String condition = " And " + data.getKeyField() + "='" + data.getKeyValue() + "'";

			List<String> flist = data.getFieldList();

			String fieldlist = "";

			for (int i = 0; i < flist.size(); i++) {
				String fieldName = flist.get(i);
				String fieldValue = data.getString(fieldName);

				if (!fieldValue.equals("")) {

					String vv = data.getString(flist.get(i).toString());
					if (fieldValue.toUpperCase().equals("NULL")) {
						fieldlist = fieldlist + flist.get(i).toString() + "=NULL,";
					} else {
						if (vv.indexOf("'") > 0 || vv.indexOf('@') == 0) {
							if (vv.indexOf('@') == 0) {
								vv = getLocal(vv.substring(1));
							}
							fieldlist = fieldlist + flist.get(i).toString() + "=" + vv + ",";
						} else {
							fieldlist = fieldlist + flist.get(i).toString() + "='" + vv + "',";
						}
					}
				}
			}

			fieldlist = StringUtility.TrimRigth(fieldlist, ",");

			sql = " Update " + data.getTableName() + " Set " + fieldlist + "  Where 1=1 " + condition;
			return sql;
		}

		if (data.getAppDataStuats() == AppDataStatus.Delete) {

			String condition = " ";
			if (!data.getKeyValue().equals("")) {
				condition = " And " + data.getKeyField() + "='" + data.getKeyValue() + "'";

			} else {

				List<String> flist = data.getFieldList();

				for (int i = 0; i < flist.size(); i++) {
					String fieldName = flist.get(i);
					String fieldValue = data.getString(flist.get(i).toString());

					LogUtility.info("  Delete Data---  " + fieldName + "=" + fieldValue);

					if (!fieldValue.equals("")) {
						condition = condition + " And " + flist.get(i).toString() + "='"
								+ data.getString(flist.get(i).toString()) + "'  ";
					}
				}
			}

			sql = " Delete From " + data.getTableName() + " Where 1=1 " + condition;

			return sql;
		}
		return sql;
	}

	public void SaveDataList(List<AppData> list) throws DataException {
		Connection conn = null;
		PreparedStatement pstat = null;
		try {
			List<String> sqlList = new ArrayList<String>();
			for (int i = 0; i < list.size(); i++) {
				AppData data = (AppData) list.get(i);
				String sql = this.SaveData(data);
				if (sql.length() > 0)
					sqlList.add(this.SaveData(data));
			}
			try {
				conn = this.getConnection();
				conn.setReadOnly(false);
				conn.setAutoCommit(false);
				for (String sql : sqlList) {
					pstat = conn.prepareStatement(sql);
					this.getAppClient().log(sql);
					pstat.executeUpdate(sql);
				}
				conn.commit();
			} catch (Exception ex) {
				conn.rollback();
				ex.printStackTrace();
				throw new DataException("MySqlDataServer-->SaveDataList()", ex);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DataException("MySqlDataServer-->SaveDataList()", ex);
		} finally {
			this.returnConnection(conn);
		}
	}

	public int getNext(String tableName, String nextField) throws DataException {
		String maxSql = " select max(" + nextField + ")+1 from  " + tableName;
		LogUtility.info("  maxSql=" + maxSql);
		return this.readCount(maxSql);
	}

	public String getGUID() throws DataException {
		String maxSql = " select uuid() ";
		LogUtility.info("  maxSql=" + maxSql);
		return this.getSingleValue(maxSql);
	}

	public String getPagerSql(String sql, int start, int end) {
		String _sql = " {0} LIMIT {1},{2} ";

		_sql = sql + " LIMIT " + start + "," + (end - start);

		return _sql;
	}

	public AppData getAppData(String dataName, String uid) throws DataException {
		String sql = " SELECT * FROM " + dataName + " Where DELFLG=0 AND  UID='" + uid + "' LIMIT 0,1";
		List<AppData> list = this.getAppDataList(sql);
		if (list.size() > 1) {
			LogUtility.info("  sql=" + sql);
		}

		if (list.size() == 1) {
			AppData appData = list.get(0);
			List<String> strList = appData.getFieldList();
			for (String str : strList) {
				LogUtility.info("                 " + str + "=" + appData.getString(str));
			}
			LogUtility.info("  sql=" + sql);
			return list.get(0);
		}

		return null;
	}

	public AppData getAppData(String dataName, String field, String value) throws DataException {
		String sql = " SELECT * FROM " + dataName + " Where DELFLG=0 AND " + field + "='" + value + "' LIMIT 0,1 ";
		List<AppData> list = this.getAppDataList(sql);
		if (list.size() > 1) {

		}

		if (list.size() == 1) {
			return list.get(0);
		}

		return null;
	}

	public AppData getAppData(String dataName, String field1, String value1, String field2, String value2)
			throws DataException {
		String sql = " SELECT * FROM " + dataName + " Where DELFLG=0 AND " + field1 + "='" + value1 + "' And " + field2
				+ "='" + value2 + "' LIMIT 0,1";
		List<AppData> list = this.getAppDataList(sql);
		if (list.size() > 1) {

		}

		if (list.size() == 1) {
			return list.get(0);
		}

		return null;
	}

	public List<AppData> getAppDataList(String sql) throws DataException {
		List<AppData> list = new ArrayList<AppData>();
		Connection conn = null;
		try {
			ResultSet rs = null;
			conn = this.getConnection();
			conn.setReadOnly(true);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			this.appClient.log(sql);
			if (pstmt != null) {
				rs = pstmt.executeQuery(sql);
			}

			if (rs != null) {
				while (rs.next()) {
					int colNums = rs.getMetaData().getColumnCount();
					// actionResult.addData(map);
					AppData data = new AppData(rs.getMetaData().getTableName(1));
					for (int i = 1; i <= colNums; i++) {
						String columnName = rs.getMetaData().getColumnLabel(i);
						String strValue = "";
						try {
							strValue = rs.getString(columnName);
						} catch (Exception e) {
						}
						if (columnName.toUpperCase().equals("UID")) {
							data.setKeyValue(strValue);
						} else {
							data.add(columnName.toUpperCase(), strValue);
						}
					}

					data.setAppDataStuats(AppDataStatus.Save);
					list.add(data);
				}
				rs.close();
				rs = null;
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DataException(" getAppDataList(String sql) ", e);
		} finally {
			this.returnConnection(conn);
		}
		return list;
	}

	public void clearTable(String tableName) throws DataException {
		String sql = " delete from " + tableName;
		Connection conn = null;
		try {
			conn = getConnection();
			conn.setReadOnly(false);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			this.getAppClient().log(sql);
			pstmt.executeUpdate(sql);
			pstmt.close();
			pstmt = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new DataException(" execute(String sql) ", ex);
		} finally {
			returnConnection(conn);
		}
	}

	public List<AppData> getAppDataList(String dataName, String field, String value) throws DataException {
		String sql = " SELECT * FROM " + dataName + " Where DELFLG=0 And " + field + "='" + value + "' ";
		return getAppDataList(sql);
	}

	public List<AppData> getAppDataList(String dataName, String field1, String value1, String field2, String value2)
			throws DataException {
		String sql = " SELECT * FROM " + dataName + " Where DELFLG=0 And " + field1 + "='" + value1 + "' And " + field2
				+ "='" + value2 + "' ";
		return getAppDataList(sql);
	}

	public List<AppData> getAppDataListByCondition(String dataName, String conditionString, String orderString,
			int start, int end) throws DataException {

		String sql = " SELECT * FROM " + dataName + " Where DELFLG=0  And " + conditionString + " order by "
				+ orderString;
		sql = getPagerSql(sql, start, end);
		return getAppDataList(sql);
	}
}
