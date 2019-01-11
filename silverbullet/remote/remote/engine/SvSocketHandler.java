package jp.silverbullet.remote.engine;

public interface SvSocketHandler {

	String onReceived(String line);

	boolean isQuery(String line);

}
