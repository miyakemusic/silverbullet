package jp.silverbullet.test;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TestRecorderUi extends VBox implements TestRecorderListener {
//	private TextArea textArea = new TextArea();
	private TextField scriptName;
	private TableView<TableData> tableView;
	private TestRecorder testRecorder;
	private Button play;
	private Button record;
		
	public TestRecorderUi(final TestRecorder testRecorder) {
		testRecorder.setListener(this);
		this.testRecorder = testRecorder;
		HBox hbox = new HBox();
		play = new Button("Play");
		record = new Button("Record");
		play.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (play.getText().equals("Play")) {
					testRecorder.play();
					play.setText("Stop");
				}
				else if (play.getText().equals("Stop")) {
					testRecorder.stopPlay();
				//	play.setText("Play");
				}
			}
		});
		
//		ToggleGroup group = new ToggleGroup();
				
		record.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (record.getText().equals("Record")) {
					testRecorder.record(scriptName.getText());
					record.setText("Stop");
				}
				else {
					testRecorder.stopRecord();
			//		record.setText("Record");
				}
			}
		});
		
//		group.getToggles().addAll(play, record, stop);
		
		Button insert = new Button("Insert");
		insert.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				testRecorder.insert(tableView.getSelectionModel().getSelectedIndex());
			}
		});
		
		scriptName = new TextField(testRecorder.getCurrentScriptName());
		Button load = new Button("Load");
		load.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				testRecorder.loadFile(scriptName.getText());
			}
		});
		
		Button save = new Button("Save");
		save.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				testRecorder.save(scriptName.getText());
			}
		});
		
		Button update = new Button("Update");
		update.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateTable();
			}
		});
		
		Button clear = new Button("Clear");
		clear.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				testRecorder.clear();
			}
		});
		
		hbox.getChildren().addAll(play, record, scriptName, load, insert, save, update, clear);
		this.getChildren().add(hbox);
		tableView = new TableView<>();
		tableView.setEditable(true);
		
		TableColumn<TableData, String> testColumn = new TableColumn<>("Test");
		testColumn.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("test"));
		testColumn.setCellFactory(TextFieldTableCell.<TableData>forTableColumn());
		testColumn.setEditable(true);
		testColumn.setOnEditCommit(
			    new EventHandler<CellEditEvent<TableData, String>>() {
			        @Override
			        public void handle(CellEditEvent<TableData, String> t) {
			   	
			            ((TableData) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setTest(t.getNewValue());
			        }
			    }
			);

		 
		TableColumn<TableData, String> resultColumn = new TableColumn<>("Result");
		resultColumn.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("result"));
	
		TableColumn<TableData, String> passFailColumn = new TableColumn<>("Pass/Fail");
		passFailColumn.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("passFail"));
	
		TableColumn<TableData, String> dependencyColumn = new TableColumn<>("Dependency");
		dependencyColumn.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("dependency"));
		
		TableColumn<TableData, String> registerColumn = new TableColumn<>("Register");
		registerColumn.setCellValueFactory(
                new PropertyValueFactory<TableData, String>("register"));
		
		tableView.getColumns().addAll(testColumn, resultColumn, passFailColumn, dependencyColumn, registerColumn);
		
		tableView.setItems(testRecorder.getTableData());
		this.getChildren().add(tableView);
		tableView.setMinHeight(800);
		
	}

	@Override
	public void onUpdate() {
		updateTable();
	}

	protected void updateTable() {
		
		ObservableList<TableData>	dummy = FXCollections.observableArrayList();
		tableView.setItems(dummy);
		this.tableView.layout();
		tableView.setItems(testRecorder.getTableData());
		this.tableView.layout();
	}

	@Override
	public void onTestFinished() {
		this.play.setText("Play");
		updateTable();
	}

	@Override
	public void onRecoredStopped() {
		this.record.setText("Record");
	}

	@Override
	public void onTestProgress(int number) {
		updateTable();
		this.tableView.getSelectionModel().select(number);
	}

}
