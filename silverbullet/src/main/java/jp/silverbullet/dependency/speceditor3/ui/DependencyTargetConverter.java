package jp.silverbullet.dependency.speceditor3.ui;

import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;

public class DependencyTargetConverter {

	private DependencyTargetElement element;
	private String selectionId = "";
	
	public DependencyTargetConverter(String elementName) {
		if (elementName.contains(".")) {
			String[] tmp = elementName.split("\\.");
			if (tmp[1].equals("enabled")) {
				this.element = DependencyTargetElement.ListItemEnabled;
			}
			else if (tmp[1].equals("visible")) {
				this.element = DependencyTargetElement.ListItemVisible;
			}
			this.selectionId = tmp[0];
		}
		else {
			this.element = DependencyTargetElement.valueOf(elementName);
		}
	}

	public DependencyTargetElement getElement() {
		return element;
	}

	public String getSelectionId() {
		return selectionId;
	}

}
