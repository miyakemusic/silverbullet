package jp.silverbullet.web;

import java.util.HashMap;
import java.util.Map;

import jp.silverbullet.web.auth.GooglePersonalResponse;

public class AuthStore {
	private Map<String, GooglePersonalResponse> map = new HashMap<>();

	public void add(GooglePersonalResponse personal) {
		map.put(personal.id, personal);
	}

	public boolean stores(String code) {
		for (GooglePersonalResponse r : map.values()) {
			if (r.auth_code.equals(code)) {
				return true;
			}
		}
		return false;
	}

	public GooglePersonalResponse getByCode(String code) {
		for (GooglePersonalResponse r : map.values()) {
			if (r.auth_code.equals(code)) {
				return r;
			}
		}
		return null;
	}
	
}
