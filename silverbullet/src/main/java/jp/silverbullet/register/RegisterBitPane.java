package jp.silverbullet.register;

import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;
import jp.silverbullet.register.RegisterBit.ReadWriteType;

public class RegisterBitPane extends VBox {

	public RegisterBitPane(final RegisterBitPaneModel model) {		
		HBox hbox = new HBox();
		this.getChildren().add(hbox);
		Button bitButton = new Button("Add Bit");
		hbox.getChildren().add(bitButton);
		Button propertyButton = new Button("Property");
		hbox.getChildren().add(propertyButton);
		
		final TableView<RegisterBit> tableView = new TableView<RegisterBit>();
		tableView.setEditable(true);
		tableView.setPrefHeight(700);
		
		TableColumn colBit = new TableColumn("bit");
		colBit.setCellValueFactory(new PropertyValueFactory<>("bit"));
		colBit.setCellFactory(TextFieldTableCell.forTableColumn());
		colBit.setOnEditCommit(new EventHandler<CellEditEvent<RegisterBit, String>>() {
			@Override
			public void handle(CellEditEvent<RegisterBit, String> arg0) {
				arg0.getRowValue().setBit(arg0.getNewValue());
			} 
		});
		
		TableColumn colName = new TableColumn("Name");
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colName.setCellFactory(TextFieldTableCell.forTableColumn());
		colName.setOnEditCommit(new EventHandler<CellEditEvent<RegisterBit, String>>() {
			@Override
			public void handle(CellEditEvent<RegisterBit, String> arg0) {
				arg0.getRowValue().setName(arg0.getNewValue());
			} 
		});
		colName.setPrefWidth(150);
		
		TableColumn colRw = new TableColumn("R/W");
		colRw.setCellValueFactory(new Callback<CellDataFeatures<RegisterBit, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<RegisterBit, String> p) {
		         return new SimpleStringProperty(p.getValue().getType().toString());
		     }
		});
		colRw.setCellFactory(TextFieldTableCell.forTableColumn());
		colRw.setOnEditCommit(new EventHandler<CellEditEvent<RegisterBit, String>>() {
			@Override
			public void handle(CellEditEvent<RegisterBit, String> arg0) {
				arg0.getRowValue().setType(ReadWriteType.valueOf(arg0.getNewValue()));
			} 
		});
		colRw.setPrefWidth(50);
		
		TableColumn colDescription = new TableColumn("Description");
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
		colDescription.setOnEditCommit(new EventHandler<CellEditEvent<RegisterBit, String>>() {
			@Override
			public void handle(CellEditEvent<RegisterBit, String> arg0) {
				arg0.getRowValue().setDescription(arg0.getNewValue());
			} 
		});
		colDescription.setPrefWidth(300);
		
		TableColumn colDefinition = new TableColumn("Definition");
		colDefinition.setCellValueFactory(new PropertyValueFactory<>("definition"));
		colDefinition.setCellFactory(TextFieldTableCell.forTableColumn());
		colDefinition.setOnEditCommit(new EventHandler<CellEditEvent<RegisterBit, String>>() {
			@Override
			public void handle(CellEditEvent<RegisterBit, String> arg0) {
				arg0.getRowValue().setDefinition(arg0.getNewValue());
			} 
		});
		colDefinition.setPrefWidth(400);
		
		TableColumn colSvProperty = new TableColumn("ID");
		colSvProperty.setCellValueFactory(new PropertyValueFactory<>("propertyFormula"));
//		colSvProperty.setCellFactory(TextFieldTableCell.forTableColumn());
		colSvProperty.setOnEditCommit(new EventHandler<CellEditEvent<RegisterBit, String>>() {
			@Override
			public void handle(CellEditEvent<RegisterBit, String> arg0) {
				arg0.getRowValue().setDescription(arg0.getNewValue());
			} 
		});
		colSvProperty.setPrefWidth(150);
		
		tableView.getColumns().addAll(colBit, colName, colRw, colDescription, colDefinition, colSvProperty);
		
		this.getChildren().add(tableView);
		
		update(model.getRegisterBit(), tableView);
		
		bitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.getRegisterBit().addBit("new bit", 0, 15, ReadWriteType.RW, "Description", "Value Definition");
				update(model.getRegisterBit(), tableView);
				
			}
		});
		
		propertyButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String ret = showPropertyEditor(model);
				tableView.getSelectionModel().getSelectedItem().setPropertyFormula(ret);
			}
		});
	}

	private String formula = "";
	protected String showPropertyEditor(RegisterBitPaneModel model) {
		formula = "";
		final MyDialogFx dialog = new MyDialogFx("Property", this);
		PropertyEditorPaneFx node = new PropertyEditorPaneFx(model.getPropertyHolder()) {

			@Override
			protected void onClose() {
				dialog.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				formula = selected.get(0);
			}
		};
		dialog.showModal(node);
		return formula;
	}

	protected void update(SvRegister svRegister,
			TableView<RegisterBit> tableView) {
		tableView.getItems().clear();
		tableView.getItems().addAll(svRegister.getBits().getBits());
	}

}
