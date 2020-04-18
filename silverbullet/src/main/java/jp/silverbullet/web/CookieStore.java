package jp.silverbullet.web;

import java.util.HashMap;
import java.util.Map;

import jp.silverbullet.web.auth.GooglePersonalResponse;

public class CookieStore {
	private Map<String, GooglePersonalResponse> map = new HashMap<>();
	
	public void put(String cookie, GooglePersonalResponse value) {
		this.map.put(cookie, value);
	}

	public boolean containsCookie(String cookie) {
		return this.map.containsKey(cookie);
	}

	public GooglePersonalResponse get(String cookie) {
		// TODO Auto-generated method stub
		return this.map.get(cookie);
	}
}
