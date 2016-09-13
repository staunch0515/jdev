package com.sjs.ichigo.service;

import com.sjs.ichigo.core.AbstractService;
import com.sjs.ichigo.core.AppException;

public class SimpeService extends AbstractService {

	@Override
	public void exeService() throws AppException {

		System.out.println(this.getAppClient().getClass().getName());

	}

}
