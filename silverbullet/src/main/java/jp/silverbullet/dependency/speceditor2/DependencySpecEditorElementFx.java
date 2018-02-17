package jp.silverbullet.dependency.speceditor2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

public class DependencySpecEditorElementFx extends HBox {

	public DependencySpecEditorElementFx(String title, DependencySpec dependencySpec) {
		TableView<SpecData> tableView = new TableView<>();
		TableColumn<SpecData, String> columnCond = new TableColumn<>("Condition");
		TableColumn<SpecData, String> columnAnswer = new TableColumn<>("Answer");
		tableView.getColumns().addAll(columnCond, columnAnswer);
		tableView.setPrefHeight(100);
		tableView.setPrefWidth(800);
		
		Button label = new Button(title);
		label.setPrefWidth(300);
		this.getChildren().add(label);
		this.getChildren().add(tableView);
		
		columnCond.setCellValueFactory(
		    new PropertyValueFactory<SpecData,String>("condition")
			);
		columnAnswer.setCellValueFactory(
		    new PropertyValueFactory<SpecData,String>("answer")
		);
			
		ObservableList<SpecData> data = FXCollections.observableArrayList();

		for (DependencySpecDetail detail : dependencySpec.getSpecs()) {
			if (detail.getPassiveElement().equals(title)) {
				data.add(new SpecData(detail.getSpecification().getSample(), detail.getSpecification().getValueMatched()));
			}
		}
		
		
		tableView.setItems(data);
		
	}

}


