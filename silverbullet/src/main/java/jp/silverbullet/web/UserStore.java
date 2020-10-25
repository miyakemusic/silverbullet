package jp.silverbullet.web;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import jp.silverbullet.core.JsonPersistent;
import jp.silverbullet.web.auth.PersonalResponse;

class UserStoreData {
	// key is user serial number
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
	public void put(String sessionName, PersonalResponse value) {
		if (data.getMap().containsKey(value.id)) {
			PersonalCookie pc = data.getMap().get(value.id);
			pc.setSessionName(sessionName);
			pc.setPersonal(value);
		}
		else {
			this.data.getMap().put(value.id, new PersonalCookie(sessionName, value));
		}
		save();
	}

	public boolean containsCookie(String sessionName) {
		for (PersonalCookie p : data.getMap().values()) {
			if (p.getSessionName().equals(sessionName)) {
				return true;
			}
		}
		return false;
	}

	public PersonalResponse getBySessionName(String sessionName) {
		for (PersonalCookie p : data.getMap().values()) {
			if (p.getSessionName().equals(sessionName)) {
				return p.getPersonal();
			}
		}
		return null;
	}

	public void remove(String sessionName) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getSessionName().equals(sessionName)) {
				//this.map.remove(key);
				c.setSessionName("");
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

	public void updateCookie(String username, String sessionName) {
		PersonalCookie c= this.findByUser(username);
		c.setSessionName(sessionName);
	}

	public PersonalCookie findBySessionName(String sessionName) {
		for (String key : this.data.getMap().keySet()) {
			PersonalCookie c = this.data.getMap().get(key);
			if (c.getSessionName().equals(sessionName)) {
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

	public PersonalResponse findByUseID(String userid) {
		return this.data.getMap().get(userid).getPersonal();
	}	
}
