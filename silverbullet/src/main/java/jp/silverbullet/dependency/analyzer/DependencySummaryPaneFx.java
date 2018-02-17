package jp.silverbullet.dependency.analyzer;

import java.util.List;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jp.silverbullet.dependency.speceditor2.DependencySpec;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;

public class DependencySummaryPaneFx extends VBox {
	private TableView<DependencySpecDetail> tableView;
	private List<DependencySpecDetail> specs;

	public DependencySummaryPaneFx() {
		tableView = new TableView<DependencySpecDetail>();
		
		TableColumn<DependencySpecDetail, String> itemCol = new TableColumn<>("Item");
		itemCol.setCellValueFactory(new Callback<CellDataFeatures<DependencySpecDetail, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<DependencySpecDetail, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getPassiveElement());
		     }
		});
		
        TableColumn<DependencySpecDetail, String> condCol = new TableColumn<>("Condition");
        condCol.setCellValueFactory(new Callback<CellDataFeatures<DependencySpecDetail, String>, ObservableValue<String>>() {
		     public ObservableValue<String> call(CellDataFeatures<DependencySpecDetail, String> p) {
		         return new ReadOnlyObjectWrapper<String>(p.getValue().getSpecification().getSample());
		     }
		});
		
        tableView.getColumns().addAll(itemCol, condCol);
        
        this.getChildren().add(tableView);
	}
	
	public void setDependencyDetailSpec(List<DependencySpecDetail> specs) {
		this.tableView.getItems().clear();
		this.tableView.getItems().addAll(specs);
		this.specs = specs;
	}

	public void update() {
		setDependencyDetailSpec(specs);
	}
}
