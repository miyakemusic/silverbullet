package jp.silverbullet.dependency.speceditor3.ui;

import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import jp.silverbullet.property.PropertyHolder;

public class ExpressionEditorUi extends VBox {
	
	private TextArea textCondition;
	private TextArea textValue;
	protected String lastestId = "";
	private ExpressionEditorPane value;
	private ExpressionEditorPane condition;

	public ExpressionEditorUi(PropertyHolder propertyHolder) {
		this.getChildren().add(value = new ExpressionEditorPane("Value", propertyHolder));
		this.getChildren().add(condition = new ExpressionEditorPane("Condition", propertyHolder));
	}


	public String getValue() {
		return value.getText();
	}
	
	public String getCondition() {
		return this.condition.getText();
	}
}
