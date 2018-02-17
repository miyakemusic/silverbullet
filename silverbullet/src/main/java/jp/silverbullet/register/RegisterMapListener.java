package jp.silverbullet.register;

public interface RegisterMapListener {

	void onDataUpdate(int regIndex, int blockNumber, int value);

	void onInterrupt();

}
