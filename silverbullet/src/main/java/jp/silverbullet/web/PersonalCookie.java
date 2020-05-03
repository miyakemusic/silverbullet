package jp.silverbullet.web;

import jp.silverbullet.web.auth.PersonalResponse;

public class PersonalCookie {
	public PersonalCookie() {}
	public PersonalCookie(String cookie2, PersonalResponse value) {
		this.personal = value;
		this.sessionID = cookie2;
	}
	private PersonalResponse personal;
	private String sessionID;
	public PersonalResponse getPersonal() {
		return personal;
	}
	public void setPersonal(PersonalResponse personal) {
		this.personal = personal;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	
}
