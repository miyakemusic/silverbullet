package jp.silverbullet.web;

import jp.silverbullet.web.auth.PersonalResponse;

public class PersonalCookie {
	public PersonalCookie() {}
	public PersonalCookie(String cookie2, PersonalResponse value) {
		this.personal = value;
		this.sessionID = cookie2;
	}
	public PersonalResponse personal;
	public String sessionID;
}
