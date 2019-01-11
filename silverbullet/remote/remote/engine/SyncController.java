package jp.silverbullet.remote.engine;

public class SyncController {

	private boolean completed = false;
	private String asyncCompleteCondition = "";

	public void clearAsyncCondition() {
		asyncCompleteCondition = "";
	}

	public synchronized void waitComplete() {
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void notifyComplete() {
		completed = true;
		this.notify();	
	}

	public void setAsyncCondition(String asyncCompleteCondition) {
		this.asyncCompleteCondition = asyncCompleteCondition;
	}

	public void reset() {
		completed = false;
	}

	public boolean isCompleted() {
		return this.completed;
	}

	public boolean isWaitingAsyncCompletion() {
		return !asyncCompleteCondition.isEmpty();
	}

	public boolean matchesSyncCompletion(String id, String value) {
		String aSyncId = asyncCompleteCondition.split(".Value")[0];
		String aSyncValue = asyncCompleteCondition.split("=")[1];
		return aSyncId.equals(id) && aSyncValue.equals(value);
	}
}
