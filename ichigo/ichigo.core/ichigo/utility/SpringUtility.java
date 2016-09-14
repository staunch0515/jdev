package com.sjs.ichigo.utility;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringUtility {

	public static String getWebRoot(String dir) {
		Object obj = new Object();
		String path = obj.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		if (path.indexOf(dir) > 0) {
			path = path.substring(0, path.indexOf(dir) + dir.length());
		} else {
			return "";
		}
		return path;
	}

	private static ApplicationContext ac = null;

	public static ApplicationContext GetApplicationContext() {

		if (ac == null)
			ac = new ClassPathXmlApplicationContext("applicationContext.xml");
		return ac;
	}

	public static Object getBean(String key) {
		try {
			return ac.getBean(key);
		} catch (org.springframework.beans.factory.NoSuchBeanDefinitionException nex) {
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static String[] getBeanNamesForType(Class class1) {
		return ac.getBeanNamesForType(class1);
	}

}
