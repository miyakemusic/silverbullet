package jp.silverbullet.core.sequncer;

import jp.silverbullet.core.dependency2.Id;

public interface SequencerListener {

	void onChangedBySystem(Id id, String value);

	void onChangedByUser(Id id, String value);

}
