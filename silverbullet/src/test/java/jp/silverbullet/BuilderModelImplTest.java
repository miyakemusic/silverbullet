package jp.silverbullet;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import jp.silverbullet.BuilderModelImpl.RegisterTypeEnum;
import jp.silverbullet.dependency2.ChangedItemValue;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.property2.PropertyFactory;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register.RegisterAccessorImpl;
import jp.silverbullet.register2.BitValue;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterAccessorListener;
import jp.silverbullet.register2.RuntimeRegisterMap.DeviceType;
import jp.silverbullet.sequncer.SvHandlerModel;
import jp.silverbullet.sequncer.UserSequencer;
import jp.silverbullet.web.ui.UiLayout;

public class BuilderModelImplTest {

	protected Object written;

	@Test
	public void test() throws Exception {
		BuilderModelImpl builder = new BuilderModelImpl();
		builder.setRegisterType(RegisterTypeEnum.Simulator);
		PropertyFactory factory = new PropertyFactory();

		builder.getPropertiesHolder2().addProperty(factory.create("ID_MODE", PropertyType2.List).
				option("ID_MODE_A", "A", "").option("ID_MODE_B", "B", "").defaultId("ID_MODE_A"));
		builder.getPropertiesHolder2().addProperty(factory.create("ID_START", PropertyType2.List).
				option("ID_START_OFF", "OFF", "").option("ID_START_ON", "ON", "").defaultId("ID_START_OFF"));
		
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
			public void handle(SvHandlerModel model, Map<String, List<ChangedItemValue>> changed)
					throws RequestRejectedException {
				model.getRegisterAccessor().write("START", Arrays.asList(new BitValue("BIT", 2)));
			}

			@Override
			public List<String> targetIds() {
				return Arrays.asList("ID_START");
			}
		});
		UiLayout layout = builder.getUiLayoutHolder().createNewFile("newUi.ui");
		builder.getUiLayoutHolder().switchFile("newUi.ui");
		layout.addWidget(layout.root.getUniqueText(), Arrays.asList("ID_MODE"));
		builder.getDependencySpecHolder2().newSpec("ID_START").addValue("ID_START_ON", "$ID_MODE==%ID_MODE_B");
		try {
			builder.getSequencer().requestChange("ID_MODE", "ID_MODE_B");
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		
		assertEquals("START", written);
		
		builder.save("testFoler");
		
		BuilderModelImpl builder2 = new BuilderModelImpl();
		builder2.load("testFoler");
		assertEquals("ID_MODE", builder2.getPropertiesHolder2().get("ID_MODE").getId());
		assertEquals("ID_MODE", builder2.getRuntimePropertyStore().get("ID_MODE").getId());
		
		builder.saveParameters("param");
		
		builder.getSequencer().requestChange("ID_MODE", "ID_MODE_A");
		
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
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
		assertEquals(true, hardware.isWritten());
		
		builder.changeId("ID_MODE", "ID_NEWMODE");
		assertEquals("ID_NEWMODE", builder.getRuntimePropertyStore().get("ID_NEWMODE").getId());
		assertEquals("ID_NEWMODE", builder.getDependencySpecHolder2().getSpec("ID_NEWMODE").getId());
		assertEquals("ID_NEWMODE", builder.getUiLayoutHolder().getCurrentUi().getRoot().getChildren().get(0).getId());
	}


	@Test
	public void testRespondMessage() throws Exception {
		BuilderModelImpl builder = new BuilderModelImpl();
		builder.setRegisterType(RegisterTypeEnum.Simulator);
		PropertyFactory factory = new PropertyFactory();

		builder.getPropertiesHolder2().addProperty(factory.create("ID_MESSAGE", PropertyType2.List).
				option("ID_MESSAGE_NONE", "", "").option("ID_MESSAGE_ERROR", "ERROR", "").defaultId("ID_MESSAGE_NONE"));

		builder.getDependency().requestChange("ID_MESSAGE", "ID_MESSAGE_ERROR");
		builder.respondToMessage("ID_MESSAGE", "OK");
		assertEquals("ID_MESSAGE_NONE", builder.getRuntimePropertyStore().get("ID_MESSAGE").getCurrentValue());
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
		BuilderModelImpl builder = new BuilderModelImpl();
		RegisterAccessor sim1;
		builder.setSimulators(Arrays.asList(sim1 = new Sim1(), new Sim2()));
		assertEquals(sim1, builder.getSimulator("Sim1"));
	}
	
	@Test
	public void testSource() {
		BuilderModelImpl builder = new BuilderModelImpl();
		builder.setSourceInfo("tmp/src:mypackage");
		builder.getSourceInfo();
	}
}
