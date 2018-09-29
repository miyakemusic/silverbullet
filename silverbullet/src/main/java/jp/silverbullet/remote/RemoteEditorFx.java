package jp.silverbullet.remote;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;
import jp.silverbullet.handlers.HandlerProperty;
import jp.silverbullet.javafx.MyDialogFx;

public class RemoteEditorFx extends VBox {
	public RemoteEditorFx(final RemoteEditorModel model) {
		Button autoGenerate = new Button("Auto Generate");
		Button texGenerate = new Button("Generate Tex");
		Button htmlGenerate = new Button("Generate Manual");
		
		Button removeButton = new Button("Remove");
		
		final TableView<SvTex> tableView = new TableView<>();
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.remove(tableView.getSelectionModel().getSelectedItem());
				updateTableView(tableView, model);
			}
		});
		
		TableColumn colSection = new TableColumn("Section");
		colSection.setCellValueFactory(new PropertyValueFactory<>("section"));

		TableColumn colCommand = new TableColumn("Command");
		colCommand.setCellValueFactory(new PropertyValueFactory<>("command"));

		TableColumn colDescription = new TableColumn("Description");
		colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
		
		TableColumn colSyntax = new TableColumn("Syntax");
		colSyntax.setCellValueFactory(new PropertyValueFactory<>("syntax"));

		TableColumn colParameter = new TableColumn("Parameters");
		colParameter.setCellValueFactory(new PropertyValueFactory<>("parameters"));

		TableColumn colResponse = new TableColumn("Response");
		colResponse.setCellValueFactory(new PropertyValueFactory<>("response"));

		TableColumn colNote = new TableColumn("Note");
		colNote.setCellValueFactory(new PropertyValueFactory<>("note"));
		
		TableColumn colExample = new TableColumn("Example");
		colExample.setCellValueFactory(new PropertyValueFactory<>("example"));
		
		TableColumn colAsync = new TableColumn("Async");
		colAsync.setCellValueFactory(new PropertyValueFactory<SvTex, Boolean>("async"));
		colAsync.setEditable(true);
		
		TableColumn colAsyncCond = new TableColumn("Async Cond.");
		colAsyncCond.setCellValueFactory(new PropertyValueFactory<SvTex, String>("asyncCompleteCondition"));
		colAsyncCond.setCellFactory(TextFieldTableCell.forTableColumn());
		colAsyncCond.setEditable(true);
		colAsyncCond.setOnEditCommit(new EventHandler<CellEditEvent<SvTex, String>>() {
			@Override
			public void handle(CellEditEvent<SvTex, String> arg0) {
				arg0.getRowValue().setAsyncCompleteCondition(arg0.getNewValue());
			} 
		});
		
//		TableColumn colRARAMS = new TableColumn("PARAMS");
//		colRARAMS.setCellValueFactory(new PropertyValueFactory<>("params"));
		
		TableColumn colVLIST = new TableColumn("ID");
		colVLIST.setCellValueFactory(new PropertyValueFactory<>("vlist"));	

//		TableColumn colEXECID = new TableColumn("EXECID");
//		colEXECID.setCellValueFactory(new PropertyValueFactory<>("execid"));	
		
		tableView.getColumns().addAll(colSection, colCommand, colDescription, 
				colSyntax, colParameter, colResponse, colNote, colExample, colVLIST, colAsync, colAsyncCond);
		
		tableView.setEditable(true);
		
		autoGenerate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				List<String> ids = showPropertyEditor(model);
				AutoGeneratorModel autoGeneratorModel = new AutoGeneratorModel() {
					@Override
					public boolean containsAsyncHandler(String id) {
						List<HandlerProperty> handlers = model.getHandlers();
						for (HandlerProperty handlerProperty : handlers) {
							if (handlerProperty.getIds().contains(id)) {
								if (handlerProperty.getAsync()) {
									return true;
								}
							}
						}
						return false;
					}
					
				};
				List<SvTex>  texes = new AutoGenerator(autoGeneratorModel).generate(model.getProperties(ids));
				model.getTexHolder().addAll("Section", texes);
				updateTableView(tableView, model);
			}
		});
		texGenerate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				String path = "C:/Projects/MT1000_OSA/trunk/server/remote_control_engine/doc/";
				path += "auto.tex";
				new SvTexGenerator(model.getTexHolder()).generateToFile(path);
				try {
					Desktop.getDesktop().open(new File(path));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		htmlGenerate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				new SvTexHtml().generateFile("scpi.html", model.getTexHolder());
				try {
					Desktop.getDesktop().open(new File("scpi.html"));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		HBox hbox = new HBox();
		hbox.getChildren().add(autoGenerate);
		hbox.getChildren().add(texGenerate);
		hbox.getChildren().add(htmlGenerate);
		hbox.getChildren().add(removeButton);
		
		this.getChildren().add(hbox);
		this.getChildren().add(tableView);
		
		this.updateTableView(tableView, model);
		tableView.setPrefHeight(800);
	}

	protected void updateTableView(TableView<SvTex> tableView, RemoteEditorModel model) {
		tableView.getItems().clear();
		tableView.getItems().addAll(model.getTexHolder().getAllTexs());
	}

	private List<String> selectedIds = new ArrayList<String>();
	private List<String> showPropertyEditor(RemoteEditorModel model) {
		final MyDialogFx dialg = new MyDialogFx("IDs for remote control", this);
		PropertyEditorPaneFx node = new PropertyEditorPaneFx(model.getPropertyHolder()) {
			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				selectedIds = selected;
				//dialg.close();
			}

			@Override
			protected void onClose() {
				dialg.close();
			}
			
		};
		dialg.setControl(new HBox());
		dialg.showModal(node);
		return selectedIds;
	}
}
