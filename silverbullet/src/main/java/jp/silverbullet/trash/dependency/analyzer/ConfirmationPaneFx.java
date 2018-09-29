package jp.silverbullet.trash.dependency.analyzer;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.silverbullet.trash.speceditor2.DependencyFormula;
import jp.silverbullet.trash.speceditor2.DependencySpec;
import jp.silverbullet.trash.speceditor2.DependencySpecDetail;

public class ConfirmationPaneFx extends VBox {

	
	public ConfirmationPaneFx(DependencySpec dependencySpec) {
		TableView<MyData> tableView = new TableView<>();
		
		TableColumn<MyData, String> itemCol = new TableColumn<>("Element");
		itemCol.setCellValueFactory(new Callback<CellDataFeatures<MyData, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<MyData, String> p) {
		         return p.getValue().element;
		     }
		});
		
        TableColumn<MyData, Boolean> condCol = new TableColumn<>("Enabled");
        condCol.setCellValueFactory(new Callback<CellDataFeatures<MyData, Boolean>, ObservableValue<Boolean>>() {
		     public ObservableValue<Boolean> call(CellDataFeatures<MyData, Boolean> p) {
		         return p.getValue().value;
		     }
		});
        condCol.setCellFactory(CheckBoxTableCell.<MyData>forTableColumn(condCol));
        
        tableView.setEditable(true);
        tableView.getColumns().addAll(itemCol, condCol);
        tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyData>() {
			@Override
			public void changed(ObservableValue<? extends MyData> observable,
					MyData oldValue, MyData newValue) {

			}
        	
        });
        
        this.getChildren().add(tableView);
        
        if (dependencySpec == null)
        	return;
        
        ObservableList<MyData> data = FXCollections.observableArrayList();
        Set<String> elements = new LinkedHashSet<>();
        for (DependencySpecDetail e : dependencySpec.getSpecs()) {
        	elements.add(e.getPassiveElement());
        }
        elements.add(DependencySpecDetail.VALUE);
        for (String e : elements) {
        	data.add(new MyData(e, dependencySpec.isConfirmEnabled(e), dependencySpec));
        }
        tableView.getItems().addAll(data);
	}
	
	
	class MyData {
		public MyData(String e, Boolean v, final DependencySpec dependencySpec) {
			this.element = new SimpleStringProperty(e);
			this.value = new SimpleBooleanProperty(v);
			this.value.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					
					dependencySpec.setConfirmEnabled(element.get(), newValue);
				}
			});
		}
		public SimpleStringProperty element;// = new SimpleStringProperty();
		public SimpleBooleanProperty value;// = new SimpleBooleanProperty();
	}
}

