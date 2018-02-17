package jp.silverbullet.dependency.analyzer;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorModelImpl;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorPaneFx;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolderListener;
import jp.silverbullet.uidesigner.pane.SvPanelModel;

public abstract class DependencyFrameFx extends VBox {
	protected abstract void showEdit(String id);
	protected abstract void showNewWindow(String id);
	
	private DependencySummaryPaneFx summary;
	DependencySpecHolderListener dependencySpecHolderListener = new DependencySpecHolderListener() {
		@Override
		public void onUpdate(ChangeType type) {
			summary.update();
		}
	};
	private SvPanelModel model;
	
	public DependencyFrameFx(String id, final DependencyDiagramModel depModel, final SvPanelModel model) {	
		this.model = model;
		final DependencySpecEditorPaneFx tree = new DependencySpecEditorPaneFx(new DependencySpecEditorModelImpl(id, model));
		final DependencyDiagramPaneFx diagram = new DependencyDiagramPaneFx(depModel) {
			@Override
			protected void showEdit(String id) {
				DependencyFrameFx.this.showEdit(id);
				update();
				summary.update();
			}

			@Override
			protected void showNewWindow(String id) {
				DependencyFrameFx.this.showNewWindow(id);
			}

			@Override
			protected void onSelect(String id) {
				summary.setDependencyDetailSpec(depModel.getPassiveDependencySpecDetail(id));
				tree.setModel(new DependencySpecEditorModelImpl(id, model));
			}
			
		};
		diagram.setPropertyId(id);
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(diagram);
		this.getChildren().add(scroll);
		
		TabPane tabPane = new TabPane();
		Tab tab1 = new Tab("Defined");
		tab1.setContent(summary = new DependencySummaryPaneFx());
		
		Tab tab2 = new Tab("Editor");
		tab2.setContent(tree);
		summary.setDependencyDetailSpec(depModel.getPassiveDependencySpecDetail(id));
		
		Tab tab3 = new Tab("Confirmation");
		tab3.setContent(new ConfirmationPaneFx(depModel.getDependencySpec()));
		tabPane.getTabs().addAll(tab1, tab2, tab3);
		
		this.getChildren().add(tabPane);
		model.getDi().getDependencySpecHolder().addDependencySpecHolderListener(dependencySpecHolderListener);
		
	}
	public void removeListeners() {
		model.getDi().getDependencySpecHolder().removeDependencySpecHolderListener(dependencySpecHolderListener);
	}

}
