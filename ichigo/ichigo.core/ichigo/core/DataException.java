package com.sjs.ichigo.core;

public class DataException extends AppException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8615608703025642085L;
	
	
	private Exception ex;
	
	public Exception getRawException()
	{
		return ex;
	}
	
	public DataException(String method,Exception ex) {
		super(ex.getMessage());
		this.ex=ex;
	}

}
