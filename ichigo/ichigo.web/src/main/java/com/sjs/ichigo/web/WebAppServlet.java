package com.sjs.ichigo.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.sjs.ichigo.core.AppClient;
import com.sjs.ichigo.core.IService;
import com.sjs.ichigo.utility.SpringUtility;

public class WebAppServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/html;charset=UTF-8");
		resp.setHeader("Cache-Control", "no-cache");

		String contextPath = req.getContextPath();

		System.out.println("contextPath=" + contextPath);

		AppClient appClient = (AppClient) req.getSession().getAttribute("appclient");

		if (appClient == null) {
			appClient = ((AppClient) new WebAppClient());
			req.getSession().setAttribute("appclient", appClient);
		}



		String actionName = req.getRequestURI().substring(1 + contextPath.length()).replace('/', '.');

		System.out.println("actionName=" + actionName);

		IService service = null;

		String actionid = req.getParameter("actionid");

		if (((WebAppClient) appClient).canActionId(actionid) || actionName.equals("app.logout")) {
			if (appClient.canOpen()) {
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

			} else {
				JSONObject json = new JSONObject();
				json.put("error", "processing,please waiting.");
				resp.getWriter().write(json.toString());
			}
		} else {
			JSONObject json = new JSONObject();
			json.put("contextPath", contextPath);
			json.put("actionName", actionName);
			json.put("error", "cannot double submit.");
			resp.getWriter().write(json.toString());
		}

	}

}
