package jp.silverbullet.dev;

public interface AutomatorInterface {

	void write(String device, String id, String value);

	String read(String addr, String query);

}
