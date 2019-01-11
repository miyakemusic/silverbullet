package jp.silverbullet.test;

public interface TestRecorderListener {
	void onTestFinished();
	void onTestStart();
	void onAdd(String string);
	void onUpdate();
}
