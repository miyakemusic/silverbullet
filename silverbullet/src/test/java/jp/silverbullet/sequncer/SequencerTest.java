package jp.silverbullet.sequncer;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.PropertyGetter;
import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.DependencyEngine;
import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.PropertyFactory;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.sequncer.EasyAccessInterface;
import jp.silverbullet.core.sequncer.Sequencer;
import jp.silverbullet.core.sequncer.SvHandlerModel;
import jp.silverbullet.core.sequncer.SystemAccessor;
import jp.silverbullet.core.sequncer.UserSequencer;
import jp.silverbullet.dependency2.PropertyStoreForTest;

public class SequencerTest {
	
	UserSequencer userSequencer = new UserSequencer() {
		private Map<String, List<ChangedItemValue>> changed;
		
		@Override
		public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed2, Id sourceId)
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
			protected RegisterAccessor getRegisterAccessor() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected BlobStore getBlobStore() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected SystemAccessor getSystemAccessor() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		
		sequencer.addUserSequencer(userSequencer);
		sequencer.requestChange("ID_VALUE", "20");
	}

}
