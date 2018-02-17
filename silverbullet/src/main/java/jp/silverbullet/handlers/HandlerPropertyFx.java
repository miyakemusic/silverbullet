package jp.silverbullet.handlers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class HandlerPropertyFx extends VBox {

	public HandlerPropertyFx(final HandlerPropertyHolder handlerPropertyHolder,
			final String id) {

		final TableView<HandlerProperty> tableView = new TableView<HandlerProperty>();
		HBox tool = new HBox();
		this.getChildren().add(new Label(id));
		this.getChildren().add(tool);
		Button addButton = new Button("Add New Spec.");
		tool.getChildren().add(addButton);
		addButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				HandlerProperty ret = handlerPropertyHolder.addHandler("NewHandler", "No description", false, id);
				tableView.getItems().add(ret);
			}		
		});
		Button idButton = new Button("Add ID to selected Spec.");
		idButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				tableView.selectionModelProperty().getValue().getSelectedItem().addId(id);
			}
		});
		tool.getChildren().add(idButton);
		
		Button removeButton = new Button("Remove");
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				handlerPropertyHolder.removeHanlder(tableView.getSelectionModel().getSelectedItem());
				updateData(handlerPropertyHolder, tableView);
			}
		});
		tool.getChildren().add(removeButton);
		
		TableColumn<HandlerProperty, String> handlerCol = new TableColumn<>("Handler");
		handlerCol.setCellValueFactory(new Callback<CellDataFeatures<HandlerProperty, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<HandlerProperty, String> p) {
		    	 SimpleStringProperty obj = new SimpleStringProperty(p.getValue().getName());
		    	 obj.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> arg0,
							String arg1, String arg2) {
						p.getValue().setName(arg2);
					} 
		    	 });
		    	 return obj;
		     }
		});
		handlerCol.setEditable(true);
		handlerCol.setCellFactory(TextFieldTableCell.<HandlerProperty>forTableColumn());
		
		TableColumn<HandlerProperty, String> descriptionCol = new TableColumn<>("Description");
		descriptionCol.setCellValueFactory(new Callback<CellDataFeatures<HandlerProperty, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<HandlerProperty, String> p) {
		    	 SimpleStringProperty obj = new SimpleStringProperty(p.getValue().getDescription());
		    	 obj.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> arg0,
							String arg1, String arg2) {
						p.getValue().setDescription(arg2);
					} 
		    	 });
		    	 return obj;
		     }
		});
		descriptionCol.setEditable(true);
		descriptionCol.setCellFactory(TextFieldTableCell.<HandlerProperty>forTableColumn());
		
		TableColumn<HandlerProperty, Boolean> asyncCol = new TableColumn<>("Async");
		asyncCol.setCellValueFactory(new Callback<CellDataFeatures<HandlerProperty, Boolean>, ObservableValue<Boolean>>() {
		     public ObservableValue<Boolean> call(final CellDataFeatures<HandlerProperty, Boolean> p) {
		    	 SimpleBooleanProperty obj = new SimpleBooleanProperty(p.getValue().getAsync());
		    	 obj.addListener(new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> arg0,
							Boolean arg1, Boolean arg2) {
						p.getValue().setAsync(arg2);
					} 
		    	 });
		    	 return obj;
		     }
		});
		asyncCol.setEditable(true);
		asyncCol.setCellFactory(CheckBoxTableCell.<HandlerProperty>forTableColumn(asyncCol));
		
		TableColumn<HandlerProperty, String> idCol = new TableColumn<>("ID");
		idCol.setCellValueFactory(new Callback<CellDataFeatures<HandlerProperty, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<HandlerProperty, String> p) {
		    	 SimpleStringProperty obj = new SimpleStringProperty(p.getValue().getIds().toString().replace("[", "").replace("]", "").replace(",", "\n"));
		    	 obj.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> arg0,
							String arg1, String arg2) {
				//		p.getValue().setIds(arg2);
					} 
		    	 });
		    	 return obj;
		     }
		});
		
		TableColumn<HandlerProperty, String> externalCol = new TableColumn<>("External Class/Method");
		externalCol.setCellValueFactory(new Callback<CellDataFeatures<HandlerProperty, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(final CellDataFeatures<HandlerProperty, String> p) {
		    	 SimpleStringProperty obj = new SimpleStringProperty(p.getValue().getExternalClass());
		    	 obj.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> arg0,
							String arg1, String arg2) {
						p.getValue().setExternalClass(arg2);
					} 
		    	 });
		    	 return obj;
		     }
		});
		externalCol.setEditable(true);
		externalCol.setCellFactory(TextFieldTableCell.<HandlerProperty>forTableColumn());
		
		tableView.setEditable(true);
		tableView.getColumns().addAll(descriptionCol, asyncCol, idCol, externalCol);
		this.getChildren().add(tableView);
		updateData(handlerPropertyHolder, tableView);
	}

	protected void updateData(
			final HandlerPropertyHolder handlerPropertyHolder,
			final TableView<HandlerProperty> tableView) {
		tableView.getItems().clear();
		tableView.getItems().addAll(handlerPropertyHolder.getHandlers());
	}

}
