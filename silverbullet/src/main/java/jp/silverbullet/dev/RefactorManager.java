package jp.silverbullet.dev;

import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.property2.PropertyDef2;
import jp.silverbullet.core.property2.PropertyDefHolderListener;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.ui.part2.UiBuilder;
import jp.silverbullet.dev.test.TestRecorder;

public class RefactorManager {

	PropertyDefHolderListener listener = new PropertyDefHolderListener() {
		@Override
		public void onChange(String id, String field, Object value, Object prevValue) {
			if (field.equals(PropertyDef2.ID)) {
				String prevId = prevValue.toString();
				String newId = value.toString();
				dependencySpecHolder2.changeId(prevId, newId);
				uiBuilder.changeId(prevId, newId);
				testRecorder.changeId(prevId, newId);
			}
		}

		@Override
		public void onAdd(String id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRemove(String id, String replacedId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoad() {
			// TODO Auto-generated method stub
			
		}

	};

	private UiBuilder uiBuilder;
	private DependencySpecHolder dependencySpecHolder2;
	private TestRecorder testRecorder;	
	public void set(PropertyHolder2 propertiesHolder2, DependencySpecHolder dependencySpecHolder2,
			UiBuilder uiBuilder, TestRecorder testRecorder) {

		this.dependencySpecHolder2 = dependencySpecHolder2;
		this.uiBuilder = uiBuilder;
		this.testRecorder = testRecorder;
		propertiesHolder2.removeListener(listener);
		propertiesHolder2.addListener(listener);
	}

}
