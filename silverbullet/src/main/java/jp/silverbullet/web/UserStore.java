package jp.silverbullet.web;

import java.util.HashMap;
import java.util.Map;

import jp.silverbullet.web.auth.PersonalResponse;

public class UserStore {
	private Map<String, PersonalCookie> map = new HashMap<>();
	
	public void put(String cookie, PersonalResponse value) {
		this.map.put(value.id, new PersonalCookie(cookie, value));
//		this.map.put(cookie, value);
	}

	public boolean containsCookie(String cookie) {
		for (PersonalCookie p : map.values()) {
			if (p.cookie.equals(cookie)) {
				return true;
			}
		}
		return false;
	}

	public PersonalResponse getByCookie(String cookie) {
		for (PersonalCookie p : map.values()) {
			if (p.cookie.equals(cookie)) {
				return p.personal;
			}
		}
		return null;
	}

	public void remove(String cookie) {
		for (String key : this.map.keySet()) {
			PersonalCookie c = this.map.get(key);
			if (c.cookie.equals(cookie)) {
				this.map.remove(key);
				return;
			}
		}
		
	}
}
class PersonalCookie {
	public PersonalCookie(String cookie2, PersonalResponse value) {
		this.personal = value;
		this.cookie = cookie2;
	}
	public PersonalResponse personal;
	public String cookie;
}