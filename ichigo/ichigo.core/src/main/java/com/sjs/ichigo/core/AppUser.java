package com.sjs.ichigo.core;

public class AppUser extends AppObject {

	public AppUser(AppClient appClient) {
		super(appClient);
	}

	private String Name;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	private boolean login;

	public boolean isLogin() {
		return login;
	}

	public void setLogin(boolean login) {
		this.login = login;
	}
}