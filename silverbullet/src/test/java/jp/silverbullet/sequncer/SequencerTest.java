package jp.silverbullet.sequncer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.dependency2.DependencyEngine;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.dependency2.PropertyStoreForTest;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.web.ui.PropertyGetter;

public class SequencerTest {
	
	UserSequencer userSequencer = new UserSequencer() {
		private Map<String, List<ChangedItemValue>> changed;
		
		@Override
		public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed2)
				throws RequestRejectedException {
			changed = changed2;

		}

		@Override
		public List<String> targetIds() {
			return Arrays.asList("ID_VALUE");
		}

		public Map<String, List<ChangedItemValue>> getChanged() {
			return changed;
		}
		
	};
	
	@Test
	public void test() throws RequestRejectedException {
		PropertyFactory factory = new PropertyFactory();
		PropertyHolder2 holder = new PropertyHolder2();
		holder.addProperty(factory.createNumeric("ID_VALUE").defaultValue(10).max(100).min(-100));
		RuntimePropertyStore store = new RuntimePropertyStore(holder);
		
		DependencySpecHolder specHolder = new DependencySpecHolder();
		DependencyEngine engine = new DependencyEngine(new PropertyGetter() {
			@Override
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}

			@Override
			public RuntimeProperty getProperty(String id, int index) {
				return store.get(id, index);
			}
		}) {

			@Override
			protected DependencySpecHolder getSpecHolder() {
				return specHolder;
			}
			
		};
		
		Sequencer sequencer = new Sequencer() {
			@Override
			protected RuntimePropertyStore getPropertiesStore() {
				return store;
			}

			@Override
			protected DependencyEngine getDependency() {
				return engine;
			}

			@Override
			protected EasyAccessInterface getEasyAccessInterface() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected RegisterAccessor getRegisterAccessor() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		sequencer.addUserSequencer(userSequencer);
		sequencer.requestChange("ID_VALUE", "20");
	}

}
