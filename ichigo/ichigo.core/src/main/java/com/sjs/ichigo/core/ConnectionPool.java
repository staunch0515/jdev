package com.sjs.ichigo.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sjs.ichigo.utility.LogUtility;

public class ConnectionPool {

	private String driveName = "";
	private String connUrl = "";
	private String username = "";
	private String password = "";

	public static int connNums = 0;
	public static List<Connection> freeList = new ArrayList<Connection>();
	public static List<Connection> activeList = new ArrayList<Connection>();
	public static List<Connection> closeList = new ArrayList<Connection>();

	public synchronized Connection getConnnection() throws DataException {
		Connection conn = null;
		if (freeList.size() > 0) {
			try {
				for (Connection _conn : freeList) {
					if (!_conn.isClosed()) {
						conn = _conn;
					} else {
						closeList.add(_conn);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new DataException("ConnectionPool-->getConnnection", e);
			}

			for (Connection _conn : closeList) {
				freeList.remove(_conn);
				_conn = null;
			}
			closeList.clear();
			freeList.remove(conn);
		}

		if (conn == null) {
			for (int i = 0; i < 20; i++) {
				Connection conn2 = null;
				connNums++;
				conn2 = newConnection();
				freeList.add(conn2);
			}
			conn = getConnnection();
		}
		activeList.add(conn);
		return conn;
	}

	// public static void log(String logstr) {
	// String logFilePath = "C:/golden888l_logs/connectpool.txt";
	// logstr = DateTimeUtility.GetCurTime("yyyy-MM-dd HH:mm:ss") + logstr;
	// BufferedWriter bw;
	// try {
	// bw = new BufferedWriter(new FileWriter(logFilePath, true));
	// bw.write("\r\n" + logstr);
	// bw.close();
	// } catch (IOException e1) {
	// e1.printStackTrace();
	// }
	// }

	public static synchronized void returnConnection(Connection conn) {
		freeList.add(conn);
		activeList.remove(conn);
	}

	public Connection newConnection() throws DataException {
		Connection conn = null;
		try {
			Class.forName(driveName).newInstance();
			String url = this.getConnUrl();
			String username = this.getUsername();
			String password = this.getPassword();

			LogUtility.info(" url=" + url);
			LogUtility.info(" username = " + username);
			LogUtility.info(" password = " + password);

			conn = DriverManager.getConnection(url, username, password);

		} catch (Exception e) {
			e.printStackTrace();
			throw new DataException("ConnectionPool-->newConnection", e);
		}
		return conn;
	}

	public String getDriveName() {
		return driveName;
	}

	public void setDriveName(String driveName) {
		this.driveName = driveName;
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

	public String getConnUrl() {
		return connUrl;
	}

	public void setConnUrl(String connUrl) {
		this.connUrl = connUrl;
	}
}
