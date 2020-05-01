package jp.silverbullet.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import jp.silverbullet.core.JsonPersistent;
import jp.silverbullet.web.auth.PersonalResponse;

class UserStoreData {
	private Map<String, PersonalCookie> map = new HashMap<>();

	public Map<String, PersonalCookie> getMap() {
		return map;
	}

	public void setMap(Map<String, PersonalCookie> map) {
		this.map = map;
	}
	
}

public class UserStore {
	private static final String USERS_JSON = "users.json";
	private UserStoreData data = new UserStoreData();
	
	public UserStore() {
		loadJson();
	}

	public void loadJson() {
		try {
			UserStoreData loaded =  new JsonPersistent().loadJson(UserStoreData.class, USERS_JSON);
			this.data = loaded;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void save() {
		try {
			new JsonPersistent().saveJson(this.data, USERS_JSON);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void put(String cookie, PersonalResponse value) {
		if (data.getMap().containsKey(value.id)) {
			PersonalCookie pc = data.getMap().get(value.id);
			pc.cookie = cookie;
			pc.personal= value;
		}
		else {
			this.data.getMap().put(value.id, new PersonalCookie(cookie, value));
		}
		save();
	}

	public boolean containsCookie(String cookie) {
		for (PersonalCookie p : data.getMap().values()) {
			if (p.cookie.equals(cookie)) {
				return true;
			}
		}
		return false;
	}

	public PersonalResponse getByCookie(String cookie) {
		for (PersonalCookie p : data.getMap().values()) {
			if (p.cookie.equals(cookie)) {
				return p.personal;
			}
		}
		return null;
	}

	public void remove(String cookie) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.cookie.equals(cookie)) {
				//this.map.remove(key);
				c.cookie = "";
				return;
			}
		}
		
	}

	public boolean containsNativeUser(String username) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.personal.name.equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}

	
	private PersonalCookie findByUser(String username) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.personal.name.equals(username)) {
				return c;
			}
		}
		return null;
	}
	
	public boolean matchesNativePassword(String username, String password) {
		PersonalCookie c= this.findByUser(username);
		if (DigestUtils.shaHex(password).equals(c.personal.basicPassword)) {
			return true;
		}
		else {
			return false;
		}
	}

	public void updateCookie(String username, String sessionName) {
		PersonalCookie c= this.findByUser(username);
		c.cookie = sessionName;
	}

	public PersonalCookie findByCookie(String cookie) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.cookie.equals(cookie)) {
				return c;
			}
		}
		return null;
	}
}
class PersonalCookie {
	public PersonalCookie() {}
	public PersonalCookie(String cookie2, PersonalResponse value) {
		this.personal = value;
		this.cookie = cookie2;
	}
	public PersonalResponse personal;
	public String cookie;
}