package jp.silverbullet.web;

import java.util.HashMap;
import java.util.Map;

import jp.silverbullet.web.auth.PersonalResponse;

public class AuthStore {
	private Map<String, PersonalResponse> map = new HashMap<>();

	public void add(PersonalResponse personal) {
		map.put(personal.id, personal);
	}

	public boolean stores(String code) {
		for (PersonalResponse r : map.values()) {
			if (r.auth_code.equals(code)) {
				return true;
			}
		}
		return false;
	}

	public PersonalResponse getByCode(String code) {
		for (PersonalResponse r : map.values()) {
			if (r.auth_code.equals(code)) {
				return r;
			}
		}
		return null;
	}
	
}
