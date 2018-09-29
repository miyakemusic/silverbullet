package jp.silverbullet.uidesigner.pane;

import java.util.List;

import jp.silverbullet.javafx.MyDialogFx;
import jp.silverbullet.trash.dependency.analyzer.DependencyDiagramModel;
import jp.silverbullet.trash.dependency.analyzer.DependencyFrameFx;
import jp.silverbullet.trash.speceditor2.DependencySpec;
import jp.silverbullet.trash.speceditor2.DependencySpecDetail;
import jp.silverbullet.trash.speceditor2.DependencySpecEditorModelImpl;
import jp.silverbullet.trash.speceditor2.DependencySpecEditorPaneFx;

public class DependencyFrameFactory {

	public static DependencyFrameFx create(final String id, final SvPanelModel svPanelModel) {
		DependencyDiagramModel depModel = new DependencyDiagramModel() {
			@Override
			public List<DependencySpecDetail> getPassiveDependencySpecDetail(
					String id) {
//				List<DependencySpecDetail> relations = svPanelModel.getDi().getDependencySpecHolder().getPassiveRelations(id);;
//				return relations;
				return null;
			}

			@Override
			public List<DependencySpecDetail> getActiveDependencySpecDetail(
					String id) {
				//return svPanelModel.getDi().getDependencySpecHolder().getActiveRelations(id);
				return null;
			}

			@Override
			public DependencySpec getDependencySpec() {
				//return svPanelModel.getDi().getDependencySpecHolder().getSpecs().get(id);
				return null;
			}
			
		};
		DependencyFrameFx node = new DependencyFrameFx(id, depModel, svPanelModel) {
			@Override
			protected void showEdit(String id) {
				DependencySpecEditorPaneFx node = new DependencySpecEditorPaneFx(new DependencySpecEditorModelImpl(id, svPanelModel));
				MyDialogFx dialog = new MyDialogFx("Dependency Editor", this);
				dialog.showModal(node);
			}

			@Override
			protected void showNewWindow(String id) {
				MyDialogFx dialog = new MyDialogFx("Dependency Diagram", this);
				DependencyFrameFx node = create(id, svPanelModel);
				dialog.showModal(node);
				node.removeListeners();
			}
		};
		return node;
	}

//	protected void showDependencySpecUi(String id) {
//		DependencySpecEditorPaneFx node = new DependencySpecEditorPaneFx(new DependencySpecEditorModelImpl(id, model));
//		MyDialogFx dialog = new MyDialogFx("Dependency Editor", this);
//		dialog.showModal(node);
//	}


}
