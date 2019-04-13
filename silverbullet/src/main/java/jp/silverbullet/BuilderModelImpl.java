package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;

import jp.silverbullet.dependency2.DependencySpecRebuilder;
import jp.silverbullet.dependency2.IdValue;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.dependency2.design.RestrictionMatrix;
import jp.silverbullet.dependency2.CommitListener;
import jp.silverbullet.dependency2.DependencyEngine;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterController;
import jp.silverbullet.register2.RegisterShortCutHolder;
import jp.silverbullet.register2.RegisterSpecHolder;
import jp.silverbullet.register2.RuntimeRegisterMap;
import jp.silverbullet.register2.RuntimeRegisterMap.DeviceType;
import jp.silverbullet.sequncer.EasyAccessInterface;
import jp.silverbullet.sequncer.Sequencer;
import jp.silverbullet.test.TestRecorder;
import jp.silverbullet.test.TestRecorderInterface;
import jp.silverbullet.web.ValueSetResult;
import jp.silverbullet.web.ui.PropertyGetter;
import jp.silverbullet.web.ui.UiLayout;
import jp.silverbullet.web.ui.UiLayoutHolder;
import jp.silverbullet.web.ui.part2.UiBuilder;

public class BuilderModelImpl {

	private static final String ID_DEF_JSON = "id_def.json";
	private static final String REGISTER_XML = "register.xml";
	private static final String REGISTERSHORTCUT = "registershortcuts.xml";
	private static final String DEPENDENCYSPEC3_XML = "dependencyspec3.xml";
	private static final String UIBUILDER = "uibuilder.json";

	private RuntimePropertyStore store;
	private Sequencer sequencer;
	private PropertyHolder2 propertiesHolder2 = new PropertyHolder2();
	private RegisterSpecHolder registerProperty = new RegisterSpecHolder();
	private DependencySpecHolder dependencySpecHolder2 = new DependencySpecHolder();
	private RegisterShortCutHolder registerShortCuts = new RegisterShortCutHolder();
	
	private RegisterController registerController = new RegisterController();
	private RuntimeRegisterMap runtimeRegisterMap = new RuntimeRegisterMap();
	
	private RegisterAccessor currentRegisterAccessor = runtimeRegisterMap;
	private RestrictionMatrix restrictionMatrix = new RestrictionMatrix() {

		@Override
		protected DependencySpecHolder getDependencySpecHolder() {
			return dependencySpecHolder2;
		}

		@Override
		protected void resetMask() {
			store.resetMask();
		}

		@Override
		protected RuntimeProperty getRuntimeProperty(String id, int index) {
			return store.get(id, index);
		}

		@Override
		protected RuntimeProperty getRuntimeProperty(String id) {
			return store.get(id);
		}

		@Override
		protected PropertyDef2 getPropertyDef(String id) {
			return propertiesHolder2.get(id);
		}

		@Override
		protected List<PropertyDef2> getAllPropertieDefs() {
			return new ArrayList<PropertyDef2>(propertiesHolder2.getProperties());
		}
		
	};
	
	private UiLayoutHolder uiLayoutHolder = new UiLayoutHolder(new PropertyGetter() {
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}
		
		public RuntimeProperty getProperty(String id, int index) {
			return store.get(id, index);
		}
	});

	public UiLayoutHolder getUiLayoutHolder() {
		return uiLayoutHolder;
	}

	private EasyAccessInterface easyAccessInterface = new EasyAccessInterface() {
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}

		public void requestChange(String id, String value) throws RequestRejectedException {
			getSequencer().requestChange(id, value);
		}
		
		public void requestChange(String id, int index, String value) throws RequestRejectedException {
			getSequencer().requestChange(id, index, value);
		}
	};
	
	public RegisterAccessor getRegisterAccessor() {
		return this.currentRegisterAccessor;
	}

	private DependencyEngine dependency = null;

	public PropertyHolder2 getPropertiesHolder2() {
		return propertiesHolder2;
	}

	private TestRecorder testRecorder = new TestRecorder(new TestRecorderInterface() {
		
		public void saveParameters(String filename) {
			BuilderModelImpl.this.saveParameters(filename);
		}

		public void requestChange(String id, String value) throws RequestRejectedException {
			sequencer.requestChange(id, value);
		}
		
		public List<RuntimeProperty> getProperties() {
			return store.getAllProperties();
		}
		
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}
		
		public long getRegisterValue(String regName, String bitName) {
			return BuilderModelImpl.this.getRegisterAccessor().readRegister(regName, bitName);
		}
		
		public RegisterController getRegisterController() {
			return registerController;
		}
	});
	private DependencySpecHolder defaultDependency;
	private RegisterSpecHolder registerSpecHolder = new RegisterSpecHolder();
	private List<RegisterAccessor> simulators;
	private String sourceInfo;
	private RegisterAccessor hardwareAccessor;
	private UiBuilder uiBuilder  = new UiBuilder();

	public enum RegisterTypeEnum {
		Simulator,
		Hardware,
	};

	public BuilderModelImpl() {
		store = new RuntimePropertyStore(propertiesHolder2);
		this.getRegisterAccessor().addListener(this.testRecorder);
		this.getRuntimRegisterMap().addDevice(DeviceType.CONTROLLER, this.registerController);
		this.sequencer = new Sequencer() {
			protected RuntimePropertyStore getPropertiesStore() {
				return store;
			}

			protected DependencyEngine getDependency() {
				return dependency;
			}
			
			protected EasyAccessInterface getEasyAccessInterface() {
				return easyAccessInterface;
			}
			
			protected RegisterAccessor getRegisterAccessor() {
				return currentRegisterAccessor;
			}
		};

		sequencer.addSequencerListener(testRecorder);
		this.uiLayoutHolder.createDefault();
		createDependencyEngine();
		
//		restrictionMatrix.load();
//		this.setRegisterType(RegisterTypeEnum.Hardware);
	}
	
	public RuntimePropertyStore getRuntimePropertyStore() {
		return store;
	}
	
	public void load(String folder) {

		propertiesHolder2.load(folder + "/" + ID_DEF_JSON);
		
		this.store = new RuntimePropertyStore(propertiesHolder2);
		this.registerProperty = load(RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		this.registerShortCuts = load(RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		this.dependencySpecHolder2 = loadJson(DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC3_XML);
		defaultDependency = this.dependencySpecHolder2;
	
		uiLayoutHolder.load(folder);
	
		registerSpecHolder.load(folder);// = load(RegisterSpecHolder.class, folder);
		restrictionMatrix.load(folder);
		
		this.uiBuilder = this.load(UiBuilder.class, folder + "/" + UIBUILDER);
		this.uiBuilder.nameAll();
	}

	public void createDependencyEngine() {
		PropertyGetter getter = new PropertyGetter () {
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}
			
			public RuntimeProperty getProperty(String id, int index) {
				return store.get(id, index);
			}
		};
		
		dependency = new DependencyEngine(getter) {
			protected DependencySpecHolder getSpecHolder() {
				return dependencySpecHolder2;
			}
		};
	}

	private <T> T  loadJson(
			Class<T> clazz, String filename) {
		return new JsonPersistent().loadJson(clazz, filename);
	}

	
	public void save(String folder) {
		try {
			Files.newDirectoryStream(Paths.get(folder)).forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (Exception e) {

		}
		if (!Files.exists(Paths.get(folder))) {
			try {
				Files.createDirectory(Paths.get(folder));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.propertiesHolder2.save(folder + "/" + ID_DEF_JSON);
		save(this.registerProperty, RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		save(this.registerShortCuts, RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		saveJson(this.dependencySpecHolder2, folder + "/" + DEPENDENCYSPEC3_XML);

		this.registerSpecHolder.save(folder);
		this.uiLayoutHolder.save(folder);
		save(this.uiBuilder, UiBuilder.class, folder + "/" + UIBUILDER);
		this.restrictionMatrix.save(folder);
	}

	private void saveJson(Object object, String filename) {
		new JsonPersistent().saveJson(object, filename);
	}


	private <T> T load(Class<T> clazz, String filename) {
		XmlPersistent<T> propertyPersister = new XmlPersistent<>();
		try {
			return propertyPersister.load(filename, clazz);
		} catch (Exception e) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	private <T> void save(T object, Class<T> clazz, String filename) {
		XmlPersistent<T> propertyPersister = new XmlPersistent<>();
		try {
			propertyPersister.save(object, filename, clazz);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
		
	public Sequencer getSequencer() {
		return this.sequencer;
	}
	
	public DependencySpecHolder getDependencySpecHolder2() {
		return dependencySpecHolder2;
	}
	
	
	public RegisterShortCutHolder getRegisterShortCut() {
		return this.registerShortCuts;
	}
	
	public void changeId(String prevId, String newId) {
		this.propertiesHolder2.get(prevId).setId(newId);
		this.dependencySpecHolder2.changeId(prevId, newId);
		this.uiLayoutHolder.changeId(prevId, newId);
		this.testRecorder.changeId(prevId, newId);
	}

	
	public void saveParameters(String filename) {
		this.getRuntimePropertyStore().save(filename);
	}

	
	public void loadParameters(String filename) {
		List<IdValue> changedIds = this.getRuntimePropertyStore().load(filename).idValue;

		try {
			this.dependency.requestChanges(changedIds);
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

		
	public UiLayout switchUiFile(String filename) {
		return this.uiLayoutHolder.switchFile(filename);
	}

	public TestRecorder getTestRecorder() {
		return this.testRecorder;
	}
	
	public void switchDependency(String type) {
		this.store.resetMask();
		
		if (type.equals("Normal")) {
			this.dependencySpecHolder2 = this.defaultDependency;
		}
		else if (type.equals("Alternative")) {
			PropertyGetter getter = new PropertyGetter() {
				
				public RuntimeProperty getProperty(String id) {
					return store.get(id);
				}

				
				public RuntimeProperty getProperty(String id, int index) {
					return store.get(id, index);
				}
			};
			this.dependencySpecHolder2 = new DependencySpecRebuilder(this.dependencySpecHolder2, getter).getNewHolder();
		}
	}

	
	public EasyAccessInterface getEasyAccessInterface() {
		return easyAccessInterface;
	}

	public DependencyEngine getDependency() {
		return this.dependency;
	}

	public RegisterSpecHolder getRegisterSpecHolder() {
		return this.registerSpecHolder;
	}

	public List<RegisterAccessor> getSimulators() {
		return simulators;
	}

	public void setSimulators(List<RegisterAccessor> simulators) {
		this.simulators = simulators;
	}

	public RegisterAccessor getSimulator(String simulator) {
		for (RegisterAccessor a : this.simulators) {
			if (a.getClass().getSimpleName().equals(simulator)) {
				return a;
			}
		}
		return null;
	}

	public void setSourceInfo(String baseFolderAndPackage) {
		this.sourceInfo = baseFolderAndPackage;
	}

	public String getSourceInfo() {
		return sourceInfo;
	}

	public RuntimeRegisterMap getRuntimRegisterMap() {
		return this.runtimeRegisterMap;
	}

	public void setRegisterType(RegisterTypeEnum type) {
		if (type.equals(RegisterTypeEnum.Simulator)) {
			this.currentRegisterAccessor = this.runtimeRegisterMap;
		}
		else if (type.equals(RegisterTypeEnum.Hardware)) {
			this.currentRegisterAccessor = this.hardwareAccessor;
		}
	}

	public void setHardwareAccessor(RegisterAccessor hardwareAccessor2) {
		this.hardwareAccessor = hardwareAccessor2;
	}

	public void respondToMessage(String id, String type) {
		String defaultVal = getPropertiesHolder2().get(id).getDefaultId();
		try {
			getDependency().requestChange(id, defaultVal);
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

	public ValueSetResult requestChange(String id, Integer index, String value) {
		ValueSetResult ret = new ValueSetResult();
		
		try {
			sequencer.requestChange(id, index, value, new CommitListener() {
				@Override
				public Reply confirm(String message) {
					return Reply.Accept;
				}
			});
			getUiLayoutHolder().getCurrentUi().doAutoDynamicPanel();

			ret.result = "Accepted";
		} catch (RequestRejectedException e) {
			ret.message = e.getMessage();
			ret.result = "Rejected";
		} finally {
			ret.debugLog = sequencer.getDebugDepLog();		
		}

		return ret;
	}

	public void setUiBuilder(UiBuilder ui) {
		ui.addListener(this.uiBuilder.getListener());
		this.uiBuilder = ui;
	}

	public UiBuilder getUiBuilder() {
		return uiBuilder;
	}

	public RestrictionMatrix getDependencyDesigner() {
		return this.restrictionMatrix;
	}

}