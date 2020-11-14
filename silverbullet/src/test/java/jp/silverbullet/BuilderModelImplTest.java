package jp.silverbullet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.core.dependency2.ChangedItemValue;
import jp.silverbullet.core.dependency2.DependencyListener;
import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.PropertyFactory;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.PropertyType2;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.register2.BitValue;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RegisterAccessorListener;
import jp.silverbullet.core.register2.RuntimeRegisterMap.DeviceType;
import jp.silverbullet.core.sequncer.SequencerListener;
import jp.silverbullet.core.sequncer.SvHandlerModel;
import jp.silverbullet.core.sequncer.UserSequencer;
import jp.silverbullet.core.sequncer.Sequencer.Actor;
import jp.silverbullet.dev.BuilderModelImpl;
import jp.silverbullet.dev.BuilderModelImpl.RegisterTypeEnum;
import jp.silverbullet.register.RegisterAccessorImpl;
import jp.silverbullet.web.ValueSetResult;
import jp.silverbullet.web.ui.UiLayout;

public class BuilderModelImplTest {

	protected Object written;
	
	@Test
	public void test() throws Exception {
		System.out.println("test");
		BuilderModelImpl builder = new BuilderModelImpl() {

			@Override
			protected String getAccessToken() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		builder.setRegisterType(RegisterTypeEnum.Simulator);
		PropertyFactory factory = new PropertyFactory();

		builder.getPropertiesHolder2().addProperty(factory.create("ID_MODE", PropertyType2.List).
				option("ID_MODE_A", "A", "").option("ID_MODE_B", "B", "").defaultId("ID_MODE_A"));
		builder.getPropertiesHolder2().addProperty(factory.create("ID_START", PropertyType2.List).
				option("ID_START_OFF", "OFF", "").option("ID_START_ON", "ON", "").defaultId("ID_START_OFF"));
		builder.getPropertiesHolder2().addProperty(factory.create("ID_PRODUCT", PropertyType2.List).
				option("ID_PRODUCT_A", "A", "").option("ID_PRODUCT_B", "B", "").defaultId("ID_PRODUCT_A"));
		builder.getPropertiesHolder2().addProperty(factory.create("ID_PRODUCT2", PropertyType2.List).
				option("ID_PRODUCT2_A", "A", "").option("ID_PRODUCT2_B", "B", "").defaultId("ID_PRODUCT2_A"));

		
		builder.getRuntimRegisterMap().addDevice(DeviceType.HARDWARE, new RegisterAccessor() {
			@Override
			public void write(Object regName, List<BitValue> data) {
				written = regName;
			}

			@Override
			public long readRegister(Object regName, Object bitName) {
				return 0;
			}

			@Override
			public void clear(Object regName) {
			}

			@Override
			public void addListener(RegisterAccessorListener listener) {
			}

			@Override
			public byte[] readRegister(Object regName) {
				return null;
			}

			@Override
			public void write(Object regName, Object bitName, int value) {
				written = regName;
			}
			
		});
		builder.getSequencer().addUserSequencer(new UserSequencer() {
			@Override
			public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed, Id sourceId)
					throws RequestRejectedException {
				model.getRegisterAccessor().write("START", Arrays.asList(new BitValue("BIT", 2)));
			}

			@Override
			public List<String> targetIds() {
				return Arrays.asList("ID_START");
			}
		});
//		UiLayout layout = builder.getUiLayoutHolder().createNewFile("newUi.ui");
//		builder.getUiLayoutHolder().switchFile("newUi.ui");
//		layout.addWidget(layout.root.getUniqueText(), Arrays.asList("ID_MODE"));
		builder.getDependencySpecHolder2().newSpec("ID_START").
			addValue("ID_START_ON", "$ID_MODE==%ID_MODE_B");
		builder.getDependencySpecHolder2().newSpec("ID_PRODUCT").
			addOptionEnabled("ID_PRODUCT_A", DependencySpec.True, "$ID_PRODUCT==%ID_PRODUCT_A", "$ID_PRODUCT==%ID_PRODUCT_B");
		builder.getDependencySpecHolder2().newSpec("ID_PRODUCT2").
			addOptionEnabled("ID_PRODUCT2_A", DependencySpec.True, "$ID_PRODUCT2==%ID_PRODUCT2_A", "$ID_PRODUCT2==%ID_PRODUCT2_B");

		try {
			builder.getSequencer().requestChange("ID_MODE", "ID_MODE_B");
			builder.getSequencer().syncDependency();
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		

		assertEquals("START", written);
		
		builder.save("testFoler");
		
		BuilderModelImpl builder2 = new BuilderModelImpl() {

			@Override
			protected String getAccessToken() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		builder2.load("testFoler");
		assertEquals("ID_MODE", builder2.getPropertiesHolder2().get("ID_MODE").getId());
		assertEquals("ID_MODE", builder2.getRuntimePropertyStore().get("ID_MODE").getId());
		
		builder.saveParameters("param");
		
		builder.getSequencer().requestChange("ID_MODE", "ID_MODE_A");
		builder.getSequencer().syncDependency();
		
		builder.loadParameters("param");
		assertEquals("ID_MODE_B", builder.getRuntimePropertyStore().get("ID_MODE").getCurrentValue());
		
		/// test hardware
		builder.getRuntimePropertyStore().get("ID_MODE").setCurrentValue("ID_MODE_A");
		builder.getRuntimePropertyStore().get("ID_START").setCurrentValue("ID_START_OFF");
		RegisterAccessorImpl hardware = new RegisterAccessorImpl();
		builder.setHardwareAccessor(hardware);
		builder.setRegisterType(RegisterTypeEnum.Hardware);
		try {
			builder.getSequencer().requestChange("ID_MODE", "ID_MODE_B");
			builder.getSequencer().syncDependency();
//			Thread.sleep(1000);
			
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}

		assertEquals(true, hardware.isWritten());
		
		// Test ID edited
		assertEquals("$ID_MODE==%ID_MODE_B", builder.getDependencySpecHolder2().getSpec("ID_START").getExpression(DependencySpec.Value).get(0).getTrigger());

		builder.getTestRecorder().addPropertyCommand("ID_MODE");
		builder.getTestRecorder().addPropertyCommand("ID_PRODUCT");
		builder.getTestRecorder().addPropertyCommand("ID_PRODUCT2");
		
		builder.getPropertiesHolder2().addProperty(factory.create("ID_MODE", PropertyType2.List).
				option("ID_MODE_A", "A", "").option("ID_MODE_B", "B", "").defaultId("ID_MODE_A"));
		//builder.changeId("ID_MODE", "ID_NEWMODE");
		builder.getPropertiesHolder2().get("ID_MODE").setId("ID_NEWMODE");		
		assertEquals("ID_NEWMODE", builder.getRuntimePropertyStore().get("ID_NEWMODE").getId());
		assertEquals("ID_NEWMODE_A", builder.getRuntimePropertyStore().get("ID_NEWMODE").getOptionIds().get(0));
		assertEquals("ID_NEWMODE_B", builder.getRuntimePropertyStore().get("ID_NEWMODE").getOptionIds().get(1));

//		assertEquals("ID_NEWMODE", builder.getUiLayoutHolder().getCurrentUi().getRoot().getChildren().get(0).getId());
		
		
		// lil bit complex condition
		{
			DependencySpec spec = builder.getDependencySpecHolder2().getSpec("ID_PRODUCT2");
			assertEquals("ID_PRODUCT2", spec.getId());
			assertEquals("$ID_PRODUCT2==%ID_PRODUCT2_A", spec.getExpression("ID_PRODUCT2_A").get(0).getTrigger());
			assertEquals("$ID_PRODUCT2==%ID_PRODUCT2_B", spec.getExpression("ID_PRODUCT2_A").get(0).getCondition());

			assertEquals("ID_PRODUCT", builder.getTestRecorder().getScript(1).getTarget());
			assertEquals("ID_PRODUCT_A", builder.getTestRecorder().getScript(1).getValue());
			assertEquals("ID_PRODUCT2", builder.getTestRecorder().getScript(2).getTarget());
			assertEquals("ID_PRODUCT2_A", builder.getTestRecorder().getScript(2).getValue());
		}


		{
			DependencySpec spec = builder.getDependencySpecHolder2().getSpec("ID_PRODUCT");
			assertEquals("ID_PRODUCT", spec.getId());
			assertEquals("$ID_PRODUCT==%ID_PRODUCT_A", spec.getExpression("ID_PRODUCT_A").get(0).getTrigger());
			assertEquals("$ID_PRODUCT==%ID_PRODUCT_B", spec.getExpression("ID_PRODUCT_A").get(0).getCondition());
		}

		Thread.sleep(1000);
		
		// Change ID
		builder.getPropertiesHolder2().get("ID_PRODUCT").setId("ID_NEWPRODUCT");
//		Thread.sleep(1000);
//		System.out.println("Taisuke");
		{
			DependencySpec spec = builder.getDependencySpecHolder2().getSpec("ID_NEWPRODUCT");
			assertEquals("ID_NEWPRODUCT", spec.getId());
			assertEquals("$ID_NEWPRODUCT==%ID_NEWPRODUCT_A", spec.getExpression("ID_NEWPRODUCT_A").get(0).getTrigger());
			assertEquals("$ID_NEWPRODUCT==%ID_NEWPRODUCT_B", spec.getExpression("ID_NEWPRODUCT_A").get(0).getCondition());
		}
		{ // dont affect this condition
			DependencySpec spec = builder.getDependencySpecHolder2().getSpec("ID_PRODUCT2");
			assertEquals("ID_PRODUCT2", spec.getId());
			assertEquals("$ID_PRODUCT2==%ID_PRODUCT2_A", spec.getExpression("ID_PRODUCT2_A").get(0).getTrigger());
			assertEquals("$ID_PRODUCT2==%ID_PRODUCT2_B", spec.getExpression("ID_PRODUCT2_A").get(0).getCondition());
		}
		assertEquals("ID_NEWMODE", builder.getTestRecorder().getScript(0).getTarget());
		assertEquals("ID_NEWMODE_B", builder.getTestRecorder().getScript(0).getValue());
		assertEquals("ID_NEWPRODUCT", builder.getTestRecorder().getScript(1).getTarget());
		assertEquals("ID_NEWPRODUCT_A", builder.getTestRecorder().getScript(1).getValue());
		assertEquals("ID_PRODUCT2", builder.getTestRecorder().getScript(2).getTarget());
		assertEquals("ID_PRODUCT2_A", builder.getTestRecorder().getScript(2).getValue());
		
		List<String> debugLog = new ArrayList<>();
		builder.getDependency().addDependencyListener(new DependencyListener() {

			@Override
			public boolean confirm(String history) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onResult(Map<String, List<ChangedItemValue>> changedHistory) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCompleted(String message) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStart(Id id, String value) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProgress(List<String> log) {
				debugLog.addAll(log);
			}

			@Override
			public void onRejected(Id id, String message) {
				// TODO Auto-generated method stub
				
			}
			
		});
		// test from web server
		builder.getRuntimePropertyStore().get("ID_START").setCurrentValue("ID_START_OFF");
		ValueSetResult result = builder.requestChange("ID_NEWMODE", "ID_NEWMODE_B", Actor.User);
		builder.getSequencer().syncDependency();
		
		assertEquals("ID_NEWMODE#0:Value:ID_NEWMODE_B", debugLog.get(0));
		assertEquals("ID_START#0:Value:ID_START_ON", debugLog.get(1));
		
	}
	
	@Test
	public void testRespondMessage() throws Exception {
		System.out.println("testRespondMessage");
		BuilderModelImpl builder = new BuilderModelImpl() {

			@Override
			protected String getAccessToken() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		builder.setRegisterType(RegisterTypeEnum.Simulator);
		PropertyFactory factory = new PropertyFactory();

		builder.getPropertiesHolder2().addProperty(factory.create("ID_MESSAGE", PropertyType2.List).
				option("ID_MESSAGE_NONE", "", "").option("ID_MESSAGE_ERROR", "ERROR", "").defaultId("ID_MESSAGE_NONE"));

		builder.getSequencer().requestChange("ID_MESSAGE", "ID_MESSAGE_ERROR");
//		builder.getSequencer().syncDependency();
		
//		builder.respondToMessage("ID_MESSAGE", "OK");
//		assertEquals("ID_MESSAGE_NONE", builder.getRuntimePropertyStore().get("ID_MESSAGE").getCurrentValue());
	}
	
	class Sim1 implements RegisterAccessor {
		@Override
		public void write(Object regName, List<BitValue> data) {}
		@Override
		public long readRegister(Object regName, Object bitName) {
			return 0;
		}

		@Override
		public void clear(Object regName) {	}

		@Override
		public void addListener(RegisterAccessorListener listener) {}

		@Override
		public byte[] readRegister(Object regName) {
			return null;
		}
		@Override
		public void write(Object regName, Object bitName, int value) {}
	};
	class Sim2 implements RegisterAccessor {
		@Override
		public void write(Object regName, List<BitValue> data) {}
		@Override
		public long readRegister(Object regName, Object bitName) {
			return 0;
		}

		@Override
		public void clear(Object regName) {	}

		@Override
		public void addListener(RegisterAccessorListener listener) {}

		@Override
		public byte[] readRegister(Object regName) {
			return null;
		}
		@Override
		public void write(Object regName, Object bitName, int value) {}
	};
	@Test
	public void testSimulator() {
		System.out.println("testSimulator");
		BuilderModelImpl builder = new BuilderModelImpl() {

			@Override
			protected String getAccessToken() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		RegisterAccessor sim1;
		builder.setSimulators(Arrays.asList(sim1 = new Sim1(), new Sim2()));
		assertEquals(sim1, builder.getSimulator("Sim1"));
	}
	
	@Test
	public void testSource() {
		System.out.println("testSource");
		BuilderModelImpl builder = new BuilderModelImpl() {

			@Override
			protected String getAccessToken() {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		builder.getSourceInfo();
	}
}
