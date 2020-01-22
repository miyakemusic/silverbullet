package jp.silverbullet.dev.test;

public interface TestRecorderListener {
	void onTestFinished();
	void onTestStart();
	void onAdd(String string);
	void onUpdate();
}
