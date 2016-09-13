package com.sjs.ichigo.batch;

import java.util.HashMap;
import java.util.Iterator;

import org.springframework.context.ApplicationContext;

import com.sjs.ichigo.core.AppClient;
import com.sjs.ichigo.core.AppException;
import com.sjs.ichigo.core.DataException;
import com.sjs.ichigo.core.IService;
import com.sjs.ichigo.utility.SpringUtility;

public class BatchServer {

	private static HashMap<String, String> context;

	public static void main(String[] args) {

		String serviceName = "";

		if (args.length > 0) {
			serviceName = args[0];
		}

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int index = 1;
		for (int i = 0; i < args.length; i++) {
			if (i == index) {
				String argString = args[index];
				if (argString.startsWith("-") && argString.length() > 1) {
					map.put(argString.substring(1), index);
					index++;
				}
			}
			index++;
		}

		context = new HashMap<String, String>();
		Iterator<String> it = map.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			int i = (Integer) map.get(key);
			if (args.length > i) {
				context.put(key, args[i]);
			}
		}

		BatchServer server = new BatchServer();
		server.start(serviceName, context);
	}

	public void start(String serviceName, HashMap<String, String> context) {

		ApplicationContext applicationContext = SpringUtility.GetApplicationContext();

		IService service = null;

		AppClient appClient = ((AppClient) new BatchAppClient());

		Iterator<String> it = context.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			appClient.setClientObject(key, context.get(key));
		}

		service = (IService) applicationContext.getBean(serviceName);
		service.setAppClient((AppClient) appClient);
		try {
			//
			service.exeService();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (AppException e) {
			e.printStackTrace();
		}
	}

}
