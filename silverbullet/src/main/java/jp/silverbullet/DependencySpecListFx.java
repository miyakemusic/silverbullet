package jp.silverbullet;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.silverbullet.dependency.analyzer.DependencyFrameFx;
import jp.silverbullet.dependency.engine.DependencyBuilder;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolder;
import jp.silverbullet.uidesigner.pane.DependencyFrameFactory;
import jp.silverbullet.uidesigner.pane.SvPanelModel;

public class DependencySpecListFx extends VBox {

	public DependencySpecListFx(List<String> allIds, DependencySpecHolder dependencySpecHolder, final SvPanelModel svPanelModel) {
		final TableView<MyData> tableView = new TableView<>();
		
		TableColumn<MyData, String> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(new Callback<CellDataFeatures<MyData, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<MyData, String> p) {
		         return p.getValue().id;
		     }
		});
		TableColumn<MyData, String> reactiveColumn = new TableColumn<>("Changed By");
		reactiveColumn.setCellValueFactory(new Callback<CellDataFeatures<MyData, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<MyData, String> p) {
		         return p.getValue().reactive;
		     }
		});
		TableColumn<MyData, String> activeColumn = new TableColumn<>("Changes");
		activeColumn.setCellValueFactory(new Callback<CellDataFeatures<MyData, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<MyData, String> p) {
		         return p.getValue().active;
		     }
		});
		TableColumn<MyData, String> warningColumn = new TableColumn<>("Warning");
		warningColumn.setCellValueFactory(new Callback<CellDataFeatures<MyData, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<MyData, String> p) {
		         return p.getValue().warning;
		     }
		});
		tableView.getColumns().addAll(idColumn, reactiveColumn, activeColumn, warningColumn);
		tableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (arg0.getClickCount() < 2) {
					return;
				}
				String id = tableView.getSelectionModel().getSelectedItem().id.get();
				DependencyFrameFx pane = DependencyFrameFactory.create(id, svPanelModel);
				MyDialogFx dialog = new MyDialogFx("Dependency", DependencySpecListFx.this);
				dialog.showModal(pane);
				pane.removeListeners();
			}
		});
		
        this.getChildren().add(tableView);
        tableView.setPrefHeight(800);
        
        ObservableList<MyData> data = FXCollections.observableArrayList();
        for (String id : allIds) {
        	String active = createActiveText(dependencySpecHolder.getActiveRelations(id));
        	String reactive = createReactiveText(dependencySpecHolder.getPassiveRelations(id));
        	if (active.isEmpty() && reactive.isEmpty()) {
        		continue;
        	}
        	data.add(new MyData(id, reactive, active, getWarning(id, dependencySpecHolder)));
        }
        tableView.getItems().addAll(data);
        
	}
	private String getWarning(String id,
			DependencySpecHolder dependencySpecHolder) {
//		List<String> history = new ArrayList<String>();
//		history.add(id);
//		List<DependencySpecDetail> details = dependencySpecHolder.getActiveRelations(id);
//		boolean ret = analyzeWarning(history, details, dependencySpecHolder);
//		
//		if (!ret) {
//			return "Loop!! " + history.get(history.size()-1);
//		}
		Set<String> ret = new LinkedHashSet<String>();
		for (String warning : new DependencyBuilder(id, dependencySpecHolder).getWarnings()) {
			ret.add(warning);
		}
		return ret.toString().replace("[", "").replace("]", "").replace(", ", "\n");
		
	}
	protected boolean analyzeWarning(List<String> history,
			List<DependencySpecDetail> details, DependencySpecHolder dependencySpecHolder) {
		
		for (DependencySpecDetail detail : details) {
			List<String> newHistory = new ArrayList<String>();
			newHistory.addAll(new ArrayList<String>(history));
			
			if (newHistory.contains(detail.getPassiveId())) {
				return false;
			}
			newHistory.add(detail.getPassiveId());
			
			boolean ret = analyzeWarning(newHistory, dependencySpecHolder.getPassiveRelations(detail.getPassiveId()), dependencySpecHolder);
			if (!ret) {
				history.addAll(newHistory);
				return ret;
			}
		}
		return true;
	}
	private String createReactiveText(
			List<DependencySpecDetail> passiveRelations) {

		Set<String> set = new LinkedHashSet<String>();
		for (DependencySpecDetail d : passiveRelations) {
			set.add("\""+d.getPassiveElement() + "\"" + " by " + d.getSpecification().getId());
		}
		return set.toString().replace(", ", "\n").replace("[", "").replace("]", "");
	}
	private String createActiveText(List<DependencySpecDetail> activeRelations) {
		Set<String> set = new LinkedHashSet<String>();
		for (DependencySpecDetail d : activeRelations) {
			set.add("\""+d.getPassiveElement() +"\""+ " of " + d.getPassiveId());
		}
		return set.toString().replace(", ", "\n").replace("[", "").replace("]", "");
	}
	class MyData {
		public MyData(String id, String reactive, String active, String warning) {
			this.id = new SimpleStringProperty(id);
			this.reactive = new SimpleStringProperty(reactive);
			this.active = new SimpleStringProperty(active);
			this.warning = new SimpleStringProperty(warning);
		}
		public SimpleStringProperty id;// = new SimpleStringProperty();
		public SimpleStringProperty reactive;
		public SimpleStringProperty active;
		public SimpleStringProperty warning;
	}
}
