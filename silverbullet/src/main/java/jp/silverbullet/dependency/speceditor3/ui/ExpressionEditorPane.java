package jp.silverbullet.dependency.speceditor3.ui;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.dependency.speceditor3.DependencyTargetElement;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;

public abstract class ExpressionEditorPane extends VBox {

	abstract protected boolean isIdVisible();
	abstract protected List<String> getComparators();
	abstract protected List<String> getResultCandidates();
	abstract protected List<String> getComparisonTargets();
	
	private TextArea textArea = new TextArea();
	protected String lastestId = "";
	
	public ExpressionEditorPane(String title, PropertyHolder propertyHolder, String defaultValue) {
		this.setStyle("-fx-border-width:1);-fx-border-color:black;-fx-padding:5;");
		textArea.setPrefWidth(800);
		textArea.setPrefHeight(200);
		textArea.setText(defaultValue);
		
		Label label = new Label(title);
		label.setPrefWidth(100);
		HBox hbox = new HBox();
		hbox.getChildren().add(label);
		hbox.getChildren().add(textArea);
		this.getChildren().add(hbox);
		
		HBox valueBox = new HBox();
		this.getChildren().add(valueBox);
		
		if(isIdVisible()) {
			Button idSelector = new Button("ID");
			valueBox.getChildren().add(idSelector);
			idSelector.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					showIdSelector(propertyHolder);
				}		
			});
		}
		for (String val : getResultCandidates()) {
			valueBox.getChildren().add(createCommonButton(val));
		}

		HBox comparator = new HBox();
		this.getChildren().add(comparator);
		for (String comp : getComparators()) {
			comparator.getChildren().add(createCommonButton(comp));
		}
		HBox comparisonTarget = new HBox();
		this.getChildren().add(comparisonTarget);

		for (String compTarget : getComparisonTargets()) {
			comparisonTarget.getChildren().add(createCommonButton(compTarget));
		}
	}
	
	protected void showIdSelector(PropertyHolder propertyHolder) {
		MyDialogFx dialog = new MyDialogFx("ID", this);
		final PropertyEditorPaneFx node = new PropertyEditorPaneFx(propertyHolder) {
			@Override
			protected void onClose() {
				removeListener();
				dialog.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				String id = selected.get(0);
				String first = "$" + id + "." + DependencyTargetElement.Value.name();
				if (subs.isEmpty()) {
					insertText(first);
				}
				else {
					insertText(first + " == " + "%"  + subs.get(0));
				}
			}
		};
		if (!lastestId.isEmpty()) {
			node.setFilterText(lastestId, "All");
		}
		dialog.showModal(node);
	}

	protected void insertText(String string) {
		int pos = textArea.getSelection().getStart();
		if (pos < 0) {
			pos = this.textArea.getText().length() - 1;
		}
		this.textArea.insertText(pos, string + " ");
	}

	private Button createCommonButton(String comparator) {
		Button button = new Button(comparator);
		button.setTooltip(new Tooltip(comparator));
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				insertText(button.getText());
			}
			
		});
		return button;
	}
	
	public String getText() {
		return this.textArea.getText().trim();
	}
}
