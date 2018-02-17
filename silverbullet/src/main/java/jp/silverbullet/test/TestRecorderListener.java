package jp.silverbullet.test;

public interface TestRecorderListener {

	void onUpdate();

	void onTestFinished();

	void onRecoredStopped();

	void onTestProgress(int number);

}
