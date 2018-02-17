package jp.silverbullet.dependency.speceditor2;

public interface DependencySpecHolderListener {
	public enum ChangeType {
		Append,
		Remove,
		Edit
	}
	void onUpdate(ChangeType type);

}
