package jp.silverbullet.core.dependency2;

import jp.silverbullet.core.sequncer.Sequencer.Actor;

public class DepenendencyRequest {
	private Actor actor;
	private Id id;
	private String value;
	private boolean forceChange;
	private CommitListener commitListener;
	
	public DepenendencyRequest(Id id, String value2, boolean forceChange2,
			CommitListener commitListener2, Actor actor) {

		this.id = id;
		this.value = value2;
		this.forceChange = forceChange2;
		this.commitListener = commitListener2;
		this.actor = actor;
		
	}

	public Id getId() {
		return id;
	}

	public Actor getActor() {
		return actor;
	}

	public String getValue() {
		return value;
	}

	public boolean isForceChange() {
		return forceChange;
	}

	public CommitListener getCommitListener() {
		return commitListener;
	}

}
