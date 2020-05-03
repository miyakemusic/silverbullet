package jp.silverbullet.web;

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
			e.printStackTrace();
		}
	}
	public void put(String sessionID, PersonalResponse value) {
		if (data.getMap().containsKey(value.id)) {
			PersonalCookie pc = data.getMap().get(value.id);
			pc.setSessionID(sessionID);
			pc.setPersonal(value);
		}
		else {
			this.data.getMap().put(value.id, new PersonalCookie(sessionID, value));
		}
		save();
	}

	public boolean containsCookie(String sessionID) {
		for (PersonalCookie p : data.getMap().values()) {
			if (p.getSessionID().equals(sessionID)) {
				return true;
			}
		}
		return false;
	}

	public PersonalResponse getBySessionID(String sessionID) {
		for (PersonalCookie p : data.getMap().values()) {
			if (p.getSessionID().equals(sessionID)) {
				return p.getPersonal();
			}
		}
		return null;
	}

	public void remove(String sessionID) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getSessionID().equals(sessionID)) {
				//this.map.remove(key);
				c.setSessionID("");
				return;
			}
		}
		
	}

	public boolean containsNativeUser(String username) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getPersonal().name.equalsIgnoreCase(username)) {
				return true;
			}
		}
		return false;
	}

	
	private PersonalCookie findByUser(String username) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getPersonal().name.equals(username)) {
				return c;
			}
		}
		return null;
	}
	
	public boolean matchesNativePassword(String username, String password) {
		PersonalCookie c= this.findByUser(username);
		if (DigestUtils.shaHex(password).equals(c.getPersonal().basicPassword)) {
			return true;
		}
		else {
			return false;
		}
	}

	public void updateCookie(String username, String sessionID) {
		PersonalCookie c= this.findByUser(username);
		c.setSessionID(sessionID);
	}

	public PersonalCookie findBySessionID(String sessionID) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getSessionID().equals(sessionID)) {
				return c;
			}
		}
		return null;
	}

	public PersonalCookie findByUsername(String username) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getPersonal().name.equals(username)) {
				return c;
			}
		}
		return null;
	}

	public Map<String, PersonalCookie> getData() {
		return data.getMap();
	}
	
	
}
