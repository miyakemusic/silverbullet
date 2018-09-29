package jp.silverbullet.register;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.silverbullet.javafx.MyDialogFx;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.register.RegisterBit.ReadWriteType;

public class HardwarePane extends VBox {

	public HardwarePane(final HardwarePaneModel model) {
		HBox hbox = new HBox();
		this.getChildren().add(hbox);
		Button addButton = new Button("Add Register");
		hbox.getChildren().add(addButton);
		Button addTextButton = new Button("Add Register From Text");
		hbox.getChildren().add(addTextButton);
		Button bitButton = new Button("Edit Bits");
		hbox.getChildren().add(bitButton);
		Button removeButton = new Button("Remove");
		hbox.getChildren().add(removeButton);		
		
		final TableView<SvRegister> tableView = new TableView<>();
		tableView.setEditable(true);
		tableView.setPrefHeight(700);
		this.getChildren().add(tableView);
		
		TableColumn colName = new TableColumn("Name");
		colName.setCellValueFactory(new PropertyValueFactory<>("name"));
		colName.setCellFactory(TextFieldTableCell.forTableColumn());
		colName.setOnEditCommit(new EventHandler<CellEditEvent<SvRegister, String>>() {
			@Override
			public void handle(CellEditEvent<SvRegister, String> arg0) {
				arg0.getRowValue().setName(arg0.getNewValue());
			} 
		});
		colName.setPrefWidth(200);
		
		TableColumn colAddress = new TableColumn("Address");
		colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
		colAddress.setCellFactory(TextFieldTableCell.forTableColumn());
		colAddress.setOnEditCommit(new EventHandler<CellEditEvent<SvRegister, String>>() {
			@Override
			public void handle(CellEditEvent<SvRegister, String> arg0) {
				arg0.getRowValue().setAddress(arg0.getNewValue());
			} 
		});
		colAddress.setPrefWidth(150);
		
		TableColumn colDescription = new TableColumn("Description");
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
		colDescription.setOnEditCommit(new EventHandler<CellEditEvent<SvRegister, String>>() {
			@Override
			public void handle(CellEditEvent<SvRegister, String> arg0) {
				arg0.getRowValue().setDescription(arg0.getNewValue());
			} 
		});
		colDescription.setPrefWidth(200);
		
		TableColumn colBit = new TableColumn("Bit");
		//colBit.setCellValueFactory(new PropertyValueFactory<>("bits"));
		TableColumn colBitName = new TableColumn("Name");
		colBitName.setCellValueFactory(new Callback<CellDataFeatures<SvRegister, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<SvRegister, String> p) {
		         // p.getValue() returns the Person instance for a particular TableView row
		    	 String ret = "";
		         for (RegisterBit bit : p.getValue().getBits().getBits()) {
		        	 ret += bit.getName() + "\n";
		         }
		         return new SimpleStringProperty(ret);
		     }
		  });
		colBitName.setPrefWidth(200);
		
		TableColumn colBitRW = new TableColumn("R/W");
		colBitRW.setCellValueFactory(new Callback<CellDataFeatures<SvRegister, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<SvRegister, String> p) {
		         // p.getValue() returns the Person instance for a particular TableView row
		    	 String ret = "";
		         for (RegisterBit bit : p.getValue().getBits().getBits()) {
		        	 ret += bit.getType() + "\n";
		         }
		         return new SimpleStringProperty(ret);
		     }
		});
		colBitRW.setPrefWidth(50);
		
		TableColumn colBitRange = new TableColumn("Bit");
		colBitRange.setCellValueFactory(new Callback<CellDataFeatures<SvRegister, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<SvRegister, String> p) {
		         // p.getValue() returns the Person instance for a particular TableView row
		    	 String ret = "";
		         for (RegisterBit bit : p.getValue().getBits().getBits()) {
		        	 ret += bit.toString() + "\n";
		         }
		         return new SimpleStringProperty(ret);
		     }
		});
		colBitRange.setPrefWidth(300);
		
		TableColumn colBitDescription = new TableColumn("Description");
		colBitDescription.setCellValueFactory(new Callback<CellDataFeatures<SvRegister, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<SvRegister, String> p) {
		         // p.getValue() returns the Person instance for a particular TableView row
		    	 String ret = "";
		         for (RegisterBit bit : p.getValue().getBits().getBits()) {
		        	 ret += bit.getDescription() + "\n";
		         }
		         return new SimpleStringProperty(ret);
		     }
		});
		colBitDescription.setPrefWidth(300);
		
		colBit.getColumns().addAll(colBitName, colBitRW, colBitRange, colBitDescription);
		
		tableView.getColumns().addAll(colName, colAddress, colBit, colDescription);
		
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.getRegisterProperty().addRegister("AA", "0x1234", "aaaa");
				update(model.getRegisterProperty(), tableView);
			}
		});
		bitButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				showDetailEditor(tableView.getSelectionModel().getSelectedItem(), model);
				update(model.getRegisterProperty(), tableView);
			}
			
		});
		addTextButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				showAddFromText(model.getRegisterProperty());
				update(model.getRegisterProperty(), tableView);
			}
			
		});
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.getRegisterProperty().remove(tableView.getSelectionModel().getSelectedItem());
				update(model.getRegisterProperty(), tableView);
			}
		});
		
		update(model.getRegisterProperty(), tableView);
		
//		tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
//			@Override
//			public void handle(MouseEvent arg0) {
//				if (arg0.getClickCount() < 2) {
//					return;
//				}
//				
//
//			}
//		});
	}

	protected void showAddFromText(RegisterProperty hardware) {
		VBox vbox = new VBox();
		TextArea text = new TextArea();
		vbox.getChildren().add(text);
		
		MyDialogFx dialog = new MyDialogFx("From Text", this);
		
		SvRegister register = new SvRegister();
		dialog.showModal(vbox);
		if (dialog.isOkClicked()) {
			for (String line : text.getText().split("\n")) {
				RegisterBit bit = new RegisterBit();
				String[] tmp = line.split("\t");
				if (tmp.length < 3) {
					continue;
				}
				bit.setBit(tmp[0]);
				bit.setName(tmp[1]);
				bit.setType(ReadWriteType.valueOf(tmp[2]));
				bit.setDescription(tmp[3]);
				register.getBits().add(bit);
			}
			hardware.getRegisters().add(register);
		}
	}

	protected void showDetailEditor(final SvRegister svRegister, final HardwarePaneModel model) {
		MyDialogFx dialog = new MyDialogFx("Register Bit", this);
		dialog.showModal(new RegisterBitPane(new RegisterBitPaneModel(){

			@Override
			public PropertyHolder getPropertyHolder() {
				return model.getPropertyHolder();
			}

			@Override
			public SvRegister getRegisterBit() {
				return svRegister;
			} 
			
		}));
	}

	protected void update(final RegisterProperty hardware, TableView tableView) {
		tableView.getItems().clear();
		tableView.getItems().addAll(hardware.getRegisters());
	}
}
