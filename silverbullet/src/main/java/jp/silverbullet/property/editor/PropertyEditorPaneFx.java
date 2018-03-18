package jp.silverbullet.property.editor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.register.MyMessageBox;

public abstract class PropertyEditorPaneFx extends VBox {
	private PropertyListModel2 model;
	private ListDetailModel2 listModel = new ListDetailModel2();;
	protected int selectedRow;
	protected int subSelectedRow;
	private TextField text;
	private ComboBox<String> typeCombo;
	
	abstract protected void onClose();
	abstract protected void onSelect(List<String> selected, List<String> subs);
	
	public PropertyEditorPaneFx(PropertyHolder newHolder) {
		model = new PropertyListModel2(newHolder);
		createMenu();
		createTools();
		
		// Main Table
		final TableView<ObservableList<SimpleStringProperty>> mainTableView = new TableView<>();
		updateTable(mainTableView, model);
		this.getChildren().add(mainTableView);
		mainTableView.setPrefHeight(500);
		mainTableView.setEditable(true);
		mainTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		mainTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ObservableList<SimpleStringProperty>>() {
			@Override
			public void changed(
					ObservableValue<? extends ObservableList<SimpleStringProperty>> arg0,
					ObservableList<SimpleStringProperty> arg1, ObservableList<SimpleStringProperty> arg2) {
				selectedRow= mainTableView.getSelectionModel().getSelectedIndex();
				if (selectedRow >= 0) {
					//selectedRow = mainTable.convertRowIndexToModel(selectedRow);
					listModel.setListDetail(model.getPropertyRowAt(selectedRow));
				}
			}
			
		});
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				updateTable(mainTableView, model);
			}
		});
		
		// Sub Table
		final TableView<ObservableList<SimpleStringProperty>> subTableView = new TableView<>();
		updateTable(subTableView, listModel);
		this.getChildren().add(subTableView);
		subTableView.setPrefHeight(200);
		subTableView.setEditable(true);
		subTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ObservableList<SimpleStringProperty>>() {
			@Override
			public void changed(
					ObservableValue<? extends ObservableList<SimpleStringProperty>> arg0,
					ObservableList<SimpleStringProperty> arg1, ObservableList<SimpleStringProperty> arg2) {
				subSelectedRow= subTableView.getSelectionModel().getSelectedIndex();
			}
			
		});
		// Control
		HBox hbox = new HBox();
		hbox.setStyle("-fx-alignment:center; -fx-spacing:10;");
		Button close = new Button("Close");
		hbox.getChildren().add(close);
		Button select = new Button("Select&Close");
		hbox.getChildren().add(select);
		this.getChildren().add(hbox);
		close.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				onClose();
			}
		});
		select.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				List<String> ids = new ArrayList<String>();
				ObservableList<ObservableList<SimpleStringProperty>> d = mainTableView.getSelectionModel().getSelectedItems();
				ObservableList<ObservableList<SimpleStringProperty>> dsub = subTableView.getSelectionModel().getSelectedItems();
				for (ObservableList<SimpleStringProperty> e : d) {
					ids.add(e.get(model.getIdColumn()).get());
				}
				List<String> subs = new ArrayList<String>();
				for (ObservableList<SimpleStringProperty> e : dsub) {
					subs.add(e.get(0).get());
				}
				onSelect(ids, subs);
				onClose();
			}
		});

	}


	private void createMenu() {
		MenuBar menuBar = new MenuBar();
		this.getChildren().add(menuBar);
		
		Menu editMenu = new Menu("Edit");
		MenuItem add = new MenuItem("Add");
		editMenu.getItems().add(add);
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addNewId();
			}
		});
		
		MenuItem duplicate = new MenuItem("Duplicate");
		editMenu.getItems().add(duplicate);
		duplicate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.duplicate(selectedRow);
			}
		});
		MenuItem remove = new MenuItem("Remove");
		editMenu.getItems().add(remove);
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.remove(selectedRow);
			}
		});
		MenuItem replaceText = new MenuItem("Replace text");
		editMenu.getItems().add(replaceText);
		replaceText.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showReplaceEditor();
			}
		});
		
		MenuItem addListItem = new MenuItem("Add List Item");
		editMenu.getItems().add(addListItem);
		addListItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.addListItem(selectedRow, subSelectedRow);
			}
		});
		
		MenuItem removeListItem = new MenuItem("Remove List Item");
		editMenu.getItems().add(removeListItem);
		removeListItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.removeListItem(selectedRow, subSelectedRow);
			}
		});
		
		menuBar.getMenus().add(editMenu);
	}
	protected void showReplaceEditor() {
		VBox vbox = new VBox();
		TextField current = new TextField();
		TextField newtext = new TextField();
		
		vbox.getChildren().add(new Label("Current Text:"));
		vbox.getChildren().add(current);
		vbox.getChildren().add(new Label("New Text"));
		vbox.getChildren().add(newtext);
		
		MyDialogFx dlg = new MyDialogFx("Replace Editor", this) {
			
		};
		dlg.showModal(vbox);
		if (dlg.isOkClicked()) {
			model.replaceText(current.getText(), newtext.getText());
		}
	}
	protected void createTools() {
		// Tools
		HBox hbox = new HBox();
		this.getChildren().add(hbox);
		typeCombo = new ComboBox<String>();
		typeCombo.getItems().add(model.getAllText());
		typeCombo.getItems().addAll(model.getUsedPropertyType());
		hbox.getChildren().add(new Label("Type:"));
		hbox.getChildren().add(typeCombo);
		typeCombo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				model.setFilterProperty(typeCombo.getSelectionModel().getSelectedItem().toString());
			}
		});
		typeCombo.getSelectionModel().select(0);
		hbox.getChildren().add(new Label("Filter:"));
		text = new TextField();
		hbox.getChildren().add(text);
		text.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().equals(KeyCode.ENTER)) {
					model.setFilterKeyword(text.getText());
				}
			}
		});
		
		
		
	}


	protected void updateTable(final TableView<ObservableList<SimpleStringProperty>> tableView, final TableModel model) {
		fillData(tableView, model);
		
		model.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent arg0) {
				fillData(tableView, model);
			}
		});
	}

	ObservableList<ObservableList<SimpleStringProperty>> allData;
	
	protected void fillData(final TableView<ObservableList<SimpleStringProperty>> tableView,
			final TableModel model) {
		tableView.getColumns().clear();
		tableView.getItems().clear();
		
		for (int col = 0; col < model.getColumnCount(); col++) {
			final int j = col;
			TableColumn<ObservableList<SimpleStringProperty>,String> column = new TableColumn<>(model.getColumnName(col));
	
			column.setCellValueFactory(new Callback<CellDataFeatures<ObservableList<SimpleStringProperty>, String>, ObservableValue<String>>(){                   
                public ObservableValue<String> call(CellDataFeatures<ObservableList<SimpleStringProperty>, String> param) {                                                                                             
                     return param.getValue().get(j);                     
                 }                   
             });
			
			column.setOnEditCommit(new EventHandler<CellEditEvent<ObservableList<SimpleStringProperty>, String>>() {
				@Override
				public void handle(
						CellEditEvent<ObservableList<SimpleStringProperty>, String> event) {
					String oldValue = event.getOldValue();
					String value = event.getNewValue();
					event.getRowValue().get(j).set(value);
					for (ObservableList<SimpleStringProperty> list : allData) {

					}
				}
			});
			column.setCellFactory(TextFieldTableCell.<ObservableList<SimpleStringProperty>>forTableColumn());

			tableView.getColumns().add(column);
		}
		
		allData = FXCollections.observableArrayList();
		for (int row = 0; row < model.getRowCount(); row++) {
			final int rowFinal = row;
			ObservableList<SimpleStringProperty> rowData = FXCollections.observableArrayList();
			for (int col = 0; col < model.getColumnCount(); col++) {
				final int colFinal = col;
				SimpleStringProperty data = new SimpleStringProperty(model.getValueAt(row, col).toString());
				rowData.add(data);
				data.addListener(new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> arg0,
							String arg1, String arg2) {
						model.setValueAt(arg2, rowFinal, colFinal);
					}
				});
			}
			allData.add(rowData);
		}
		
		tableView.getItems().addAll(allData);
	}
	public void setFilterText(String id, String type) {
		this.text.setText(id);
		this.typeCombo.getSelectionModel().select(type);
		this.model.setFilterProperty(type);
		this.model.setFilterKeyword(id);
	}
	public void removeListener() {
		model.removeListener();
	}
	private void addNewId() {
		try {
			String id = MyMessageBox.showInput("ID_", this);
			model.addNew(selectedRow, id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
