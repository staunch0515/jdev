package com.sjs.ichigo.core;

import java.util.Map;

public class ErrorMessage {

	private Map<String, String> langList;

	public void setLangList(Map<String, String> langList) {
		this.langList = langList;
	}

	public Map<String, String> getLangList() {
		return this.langList;
	}

	
	public String getMessage(String lang)
	{
		return langList.get(lang);
	}
	
}
