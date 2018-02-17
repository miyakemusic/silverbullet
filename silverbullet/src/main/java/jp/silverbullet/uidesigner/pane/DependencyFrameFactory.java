package jp.silverbullet.uidesigner.pane;

import java.util.List;

import jp.silverbullet.MyDialogFx;
import jp.silverbullet.dependency.analyzer.DependencyDiagramModel;
import jp.silverbullet.dependency.analyzer.DependencyFrameFx;
import jp.silverbullet.dependency.speceditor2.DependencySpec;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorModelImpl;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorPaneFx;

public class DependencyFrameFactory {

	public static DependencyFrameFx create(final String id, final SvPanelModel svPanelModel) {
		DependencyDiagramModel depModel = new DependencyDiagramModel() {
			@Override
			public List<DependencySpecDetail> getPassiveDependencySpecDetail(
					String id) {
				List<DependencySpecDetail> relations = svPanelModel.getDi().getDependencySpecHolder().getPassiveRelations(id);;
				return relations;
			}

			@Override
			public List<DependencySpecDetail> getActiveDependencySpecDetail(
					String id) {
				return svPanelModel.getDi().getDependencySpecHolder().getActiveRelations(id);
			}

			@Override
			public DependencySpec getDependencySpec() {
				return svPanelModel.getDi().getDependencySpecHolder().getSpecs().get(id);
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
