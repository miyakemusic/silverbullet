package jp.silverbullet.trash.speceditor2;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.javafx.MyDialogFx;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;

public class EquationEditorFx extends VBox {

	private EquationEditorModel model;
	private ComboBox<String> idCombo;
	private ComboBox<String> answerCombo;
	private ComboBox<String> operatorCombo;
	private ComboBox<String> elementCombo;
	private ComboBox<String> resultCombo;
	private boolean eventDisabled;
	
	public EquationEditorFx(final EquationEditorModel model) {
		this.model = model;
		this.model.setOnChange(new EquationEditorModelListener() {
			@Override
			public void onChanged() {
				updateEquation();
			}
		});
		createIdSelector(model);
		
		HBox hbox = new HBox();
		elementCombo = new ComboBox<>();
		operatorCombo = new ComboBox<>();
		answerCombo = new ComboBox<>();
		answerCombo.setEditable(true);
		resultCombo = new ComboBox<>();
		resultCombo.setEditable(true);
		
		elementCombo.setPrefWidth(300);
		operatorCombo.setPrefWidth(100);
		answerCombo.setPrefWidth(400);
		resultCombo.setPrefWidth(400);
		
		elementCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (eventDisabled)return;
				model.setElement(elementCombo.getSelectionModel().getSelectedItem());
			}
		});
		operatorCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (eventDisabled)return;
				model.setOperator(operatorCombo.getSelectionModel().getSelectedItem());
			}
		});
		answerCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (eventDisabled)return;
				model.setAnswer(answerCombo.getSelectionModel().getSelectedItem());
			}
		});
		resultCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (eventDisabled)return;
				model.setResult(resultCombo.getSelectionModel().getSelectedItem());
			}
		});

		this.getChildren().add(new Label("Condition:"));
		hbox.getChildren().add(elementCombo);
		hbox.getChildren().add(operatorCombo);
		hbox.getChildren().add(answerCombo);
		this.getChildren().add(hbox);		
		this.getChildren().add(new Label("Result:"));
		
		getChildren().add(resultCombo);
		
		this.updateEquation();
	}

	protected void createIdSelector(final EquationEditorModel model) {
		HBox hbox = new HBox();
		this.getChildren().add(hbox);
		Button idButton = new Button("Select");
		idButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showIdSelector();
			}
		});
		hbox.getChildren().add(idButton);
		
		idCombo = new ComboBox<String>();
		hbox.getChildren().add(idCombo);
		idCombo.getItems().addAll(model.getAllIds());
		
		idCombo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.setId(idCombo.getSelectionModel().getSelectedItem());
			}
		});
		idCombo.getSelectionModel().select(model.getId());
	}

	protected void updateEquation() {
		eventDisabled = true;
		
		answerCombo.getItems().clear();
		operatorCombo.getItems().clear();
		elementCombo.getItems().clear();
		resultCombo.getItems().clear();
			
		idCombo.getSelectionModel().select(model.getId());
		
		operatorCombo.getItems().addAll(model.getOperatorCandicates());
		operatorCombo.getSelectionModel().select(model.getOperator());

		answerCombo.getItems().addAll(model.getAnswerCandidates());
		answerCombo.getSelectionModel().select(model.getAnswer());

		elementCombo.getItems().addAll(model.getElementCandidates());
		elementCombo.getSelectionModel().select(model.getElement());

		resultCombo.getItems().addAll(model.getResultCandidates());
		resultCombo.getSelectionModel().select(model.getResult());

		eventDisabled = false;
		answerCombo.autosize();
		operatorCombo.autosize();
		elementCombo.autosize();
	}

	protected void showIdSelector() {
		final MyDialogFx dlg = new MyDialogFx("ID Selector", this);
		PropertyEditorPaneFx node = new PropertyEditorPaneFx(model.getPropertiesHolder()) {
			@Override
			protected void onClose() {
				removeListener();
				dlg.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				model.setId(selected.get(0));
				if (!subs.isEmpty()) {
					model.setDefaultChoice(subs.get(0));
				}
			}
		};
		dlg.showModal(node);
	}
}
