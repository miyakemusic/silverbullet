package jp.silverbullet.dependency;

public class DependencyTargetConverter {

//	private static final String VISIBLE = "visible";
//	private static final String ENABLED = "enabled";
	private DependencyTargetElement element;
	private String selectionId = "";
	
	public DependencyTargetConverter(String elementName) {
		if (elementName.contains(".")) {
			String[] tmp = elementName.split("\\.");
			if (tmp[1].equals(DependencyEditorModel.ENABLED)) {
				this.element = DependencyTargetElement.ListItemEnabled;
			}
			else if (tmp[1].equals(DependencyEditorModel.VISIBLE)) {
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

	public static String convertToString(DependencyTargetElement element, String selectionId) {
		String toElement = "";
		if (element.equals(DependencyTargetElement.ListItemEnabled)) {
			toElement = selectionId + "." + DependencyEditorModel.ENABLED;
		}
		else {
			toElement = element.toString();
		}
		return toElement;
	}
}
