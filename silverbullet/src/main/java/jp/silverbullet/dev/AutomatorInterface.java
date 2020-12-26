package jp.silverbullet.dev;

public interface AutomatorInterface {

	void write(String device, String id, String value);

	String read(String device, String query);

	String message(String device, String message, String controls);

	void debug(String text);
}
