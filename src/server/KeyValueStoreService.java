package server;

import java.util.HashMap;

/**
 * @author Shiqi Luo
 *
 */
public class KeyValueStoreService {

	HashMap<String, String> data = null;
	String clientId = "";
	/**
	 * 
	 */
	public KeyValueStoreService() {
		this.data = new HashMap<>();
	}
	
	public String get(String key) {
		if(this.data.containsKey(key)){
			return this.data.get(key);
		}
		return null;
	}
	
	public boolean put(String key, String value) {
		if(value == null || key == null){
			return false;
		}

		this.data.put(key, value);
		return true;
	}
	
	public boolean delete(String key) {
		if(this.data.remove(key) == null){
			return false;
		}
		return true;
	}
}
