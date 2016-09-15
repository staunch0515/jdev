package com.sjs.ichigo.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.sjs.ichigo.core.AppClient;
import com.sjs.ichigo.core.IService;
import com.sjs.ichigo.utility.LogUtility;
import com.sjs.ichigo.utility.SpringUtility;

public class WebAppServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String APPCLIENT = "SESSION_APPCLIENT";

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		invokeService(req, resp, false);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		invokeService(req, resp, true);
	}

	private void invokeService(HttpServletRequest req, HttpServletResponse resp, boolean checkReturn)
			throws IOException {
		JSONObject json = new JSONObject();

		try {
			resp.setContentType("text/html;charset=UTF-8");
			resp.setHeader("Cache-Control", "no-cache");

			String contextPath = req.getContextPath();

			LogUtility.debug(this, "doPost", "contextPath:" + contextPath);

			AppClient appClient = (AppClient) req.getSession().getAttribute(APPCLIENT);

			if (appClient == null) {
				appClient = ((AppClient) new WebAppClient());
				req.getSession().setAttribute(APPCLIENT, appClient);
			}

			String actionName = req.getRequestURI().substring(1 + contextPath.length()).replace('/', '.');

			LogUtility.debug(this, "doPost", "actionName:" + actionName);

			IService service = null;

			String actionid = req.getParameter("actionid");

			if (checkReturn) {
				if (!((WebAppClient) appClient).canActionId(actionid)) {
					json.put("error", "the action donot be used like this.");
					throw new Exception();
				}
			}

			// check submitted twice
			if (!appClient.canOpen()) {
				json.put("error", "processing,please waiting.");
			}

			try {

				((WebAppClient) appClient).setRequest(req);
				((WebAppClient) appClient).setResponse(resp);

				appClient.open();

				service = (IService) SpringUtility.getBean(actionName);
				service.setAppClient((AppClient) appClient);
				service.exeService();

				appClient.save();

			} catch (Exception ex) {
				ex.printStackTrace();
				appClient.setException("WebAppServlet-->doPost", ex);
			} finally {
				appClient.output();
				appClient.end();
				appClient.clear();
			}

		} catch (Exception ex) {

		} finally {
			resp.getWriter().write(json.toString());
		}
	}

}
