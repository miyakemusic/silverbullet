package jp.silverbullet.sequncer;

public interface SequencerListener {

	void onChangedBySystem(String id, String value);

	void onChangedByUser(String id, String value);

}