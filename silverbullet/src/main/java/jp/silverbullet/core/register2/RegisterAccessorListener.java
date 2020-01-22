package jp.silverbullet.core.register2;

public interface RegisterAccessorListener {

	void onUpdate(Object regName, Object bitName, int value);

	void onUpdate(Object regName, byte[] image);

	void onInterrupt();

}
