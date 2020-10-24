package jp.silverbullet.web;

import jp.silverbullet.web.auth.PersonalResponse;

public class PersonalCookie {
	public PersonalCookie() {}
	public PersonalCookie(String sessionName, PersonalResponse value) {
		this.personal = value;
		this.sessionName = sessionName;
	}
	private PersonalResponse personal;
	private String sessionName;
	public PersonalResponse getPersonal() {
		return personal;
	}
	public void setPersonal(PersonalResponse personal) {
		this.personal = personal;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	
	
}
