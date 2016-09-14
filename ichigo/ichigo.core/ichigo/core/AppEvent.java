package com.sjs.ichigo.core;

public class AppEvent {

	private String name;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name=name;
	}
	
	private Object eventObj;
	
	public Object getEventSrc()
	{
		return eventObj;
	}
	
	public void setEventSrc(Object eventObj)
	{
		this.eventObj=eventObj;
	}
	
	@Override
	public String toString()
	{
		String result="eventName:"+name;
		result=result+"|eventObj:"+eventObj.toString();
		return result;
	}
}
