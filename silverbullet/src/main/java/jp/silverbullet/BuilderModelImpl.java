package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import jp.silverbullet.dependency2.DependencySpecRebuilder;
import jp.silverbullet.dependency2.IdValue;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.dependency2.DepPropertyStore;
import jp.silverbullet.dependency2.DependencyEngine;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.handlers.EasyAccessInterface;
import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.SvProperty;
import jp.silverbullet.property.SvPropertyStore;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register.RegisterMapModel;
import jp.silverbullet.register.RegisterMapModelInterface;
import jp.silverbullet.register.RegisterSpecHolder;
import jp.silverbullet.register.RegisterShortCutHolder;
import jp.silverbullet.register.SvSimulator;
import jp.silverbullet.register2.RuntimeRegisterMap;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.spec.SpecElement;
import jp.silverbullet.test.TestRecorder;
import jp.silverbullet.test.TestRecorderInterface;
import jp.silverbullet.web.ui.PropertyGetter;
import jp.silverbullet.web.ui.UiLayout;
import jp.silverbullet.web.ui.UiLayoutHolder;
import obsolute.BuilderModel;

public class BuilderModelImpl implements BuilderModel {

	private static final String ID_DEF_XML = "id_def.xml";
	private static final String ID_DEF_JSON = "id_def.json";
	private static final String HANDLER_XML = "handlers.xml";
	private static final String REMOTE_XML = "remote.xml";
	private static final String REGISTER_XML = "register.xml";
	private static final String HARDSPEC_XML = "hardspec.xml";
	private static final String USERSTORY_XML = "userstory.xml";
	private static final String REGISTERSHORTCUT = "registershortcuts.xml";
	private static final String DEPENDENCYSPEC2_XML = "dependencyspec2.xml";
	private static final String DEPENDENCYSPEC3_XML = "dependencyspec3.xml";

	private List<String> selectedId;
	private RuntimePropertyStore store;
	private SpecElement hardSpec = new SpecElement();
	private String userApplicationPath = "";
	private Sequencer sequencer;
	private PropertyHolder propertiesHolder = new PropertyHolder();
	private PropertyHolder2 propertiesHolder2 = new PropertyHolder2();
	private HandlerPropertyHolder handlerPropertyHolder = new HandlerPropertyHolder();
	private SvTexHolder texHolder = new SvTexHolder();
	private RegisterSpecHolder registerProperty = new RegisterSpecHolder();
//	private DependencySpecHolder dependencySpecHolder = new DependencySpecHolder();
	private jp.silverbullet.dependency2.DependencySpecHolder dependencySpecHolder2 = new jp.silverbullet.dependency2.DependencySpecHolder();
	private SpecElement userStory = new SpecElement();
	private RegisterShortCutHolder registerShortCuts = new RegisterShortCutHolder();
	private UiLayoutHolder uiLayoutHolder = new UiLayoutHolder(new PropertyGetter() {
		@Override
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}

		@Override
		public RuntimeProperty getProperty(String id, int index) {
			return store.get(id, index);
		}
	});

	public UiLayoutHolder getUiLayoutHolder() {
		return uiLayoutHolder;
	}

	private EasyAccessInterface easyAccessInterface = new EasyAccessInterface() {

		@Override
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}

		@Override
		public void requestChange(String id, String value) throws RequestRejectedException {
			getSequencer().requestChange(id, value);
		}

		@Override
		public void requestChange(String id, int index, String value) throws RequestRejectedException {
			getSequencer().requestChange(id, index, value);
		}
	
	};
	
	private RuntimeRegisterMap registerMap = new RuntimeRegisterMap();
	public RuntimeRegisterMap getRuntimRegisterMap() {
		return this.registerMap;
	}
	
	private RegisterMapModel registerMapModel = new RegisterMapModel(new RegisterMapModelInterface() {
		@Override
		public RegisterSpecHolder getRegisterSpecHolder() {
			return registerProperty;
		}
	});

	@Override
	public HandlerPropertyHolder getHandlerPropertyHolder() {
		return handlerPropertyHolder;
	}

	private DependencyEngine dependency = null;
//	private DependencyEngine dependency = new DependencyEngine() {
//		@Override
//		protected DependencySpecHolder getDependencyHolder() {
//			return dependencySpecHolder;
//		}
//
//		@Override
//		protected DepPropertyStore getPropertiesStore() {
//			return new DepPropertyStore() {
//
//				@Override
//				public SvProperty getProperty(String id) {
//					return store.getProperty(id);
//				}
//
//				@Override
//				public void add(SvProperty createListProperty) {
//				}
//			};
//		}
//
//	};

	private EasyAccessModel easyAccessModel = new EasyAccessModel() {
		@Override
		public void requestChange(final String id, final String value) {
			try {
				sequencer.requestChange(id, value);
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}
	};

	public PropertyHolder2 getPropertiesHolder2() {
		return propertiesHolder2;
	}

	private RegisterAccess regiseterAccess;

	private TestRecorder testRecorder = new TestRecorder(new TestRecorderInterface() {
		@Override
		public void saveParameters(String filename) {
			BuilderModelImpl.this.saveParameters(filename);
		}

		@Override
		public void requestChange(String id, String value) throws RequestRejectedException {
			sequencer.requestChange(id, value);
		}

		@Override
		public SvSimulator createSimulator() {
			SvSimulator simulator = new SvSimulator() {
				@Override
				protected void writeIo(long address, BitSet data, BitSet mask) {
				}

				@Override
				protected void writeBlock(long address, byte[] data) {
				}
			};
			BuilderModelImpl.this.registerMapModel.addSimulator(simulator);
			return simulator;
		}

		@Override
		public long getAddress(String regName) {
			return registerProperty.getRegisterByName(regName).getDecAddress();
		}

		@Override
		public List<RuntimeProperty> getProperties() {
			return store.getAllProperties();
		}

		@Override
		public RuntimeProperty getProperty(String id) {
			return store.get(id);
		}

	});
	private DependencySpecHolder defaultDependency;
	private RegisterSpecHolder registerSpecHolder;

	public BuilderModelImpl() {
		store = new RuntimePropertyStore(propertiesHolder2);

		this.setDeviceDriver(registerMapModel);
		registerMapModel.addListener(this.testRecorder);

		this.sequencer = new Sequencer() {
			@Override
			protected RuntimePropertyStore getPropertiesStore() {
				return store;
			}

			@Override
			protected HandlerPropertyHolder getHandlerPropertyHolder() {
				return handlerPropertyHolder;
			}

			@Override
			protected DependencyEngine getDependency() {
				return dependency;
			}

			@Override
			protected String getUserApplicationPath() {
				return userApplicationPath;
			}

			@Override
			protected EasyAccessModel getEasyAccessModel() {
				return easyAccessModel;
			}

			@Override
			protected RegisterAccess getRegisterAccess() {
				return regiseterAccess;
			}
		};

		sequencer.addSequencerListener(testRecorder);
		this.loadDefault();
	}

	@Override
	public RuntimeProperty getProperty(String id) {
		return store.get(id);
	}

	@Override
	public List<String> getAllTypes() {
		return propertiesHolder2.getTypes();
	}

	@Override
	public List<RuntimeProperty> getAllProperties() {
		return store.getAllProperties();
	}

	@Override
	public PropertyHolder getPropertyHolder() {
		return this.propertiesHolder;
	}

	@Override
	public RuntimePropertyStore getPropertyStore() {
		return store;
	}

	@Override
	public List<RuntimeProperty> getAllProperties(PropertyType2 type) {
		return store.getAllProperties(type);
	}

	@Override
	public List<String> getSelectedIds() {
		return selectedId;
	}

	@Override
	public List<String> getIds(PropertyType2 type) {
		return store.getIds(type);
	}

	@Override
	public void load(String folder) {

		propertiesHolder = load(PropertyHolder.class, folder + "/" + ID_DEF_XML);
		propertiesHolder.initialize();

		propertiesHolder2.load(folder + "/" + ID_DEF_JSON);
		
		this.store = new RuntimePropertyStore(propertiesHolder2);
		this.registerProperty = load(RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		this.handlerPropertyHolder = load(HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		this.texHolder = load(SvTexHolder.class, folder + "/" + REMOTE_XML);
		this.hardSpec = load(SpecElement.class, folder + "/" + HARDSPEC_XML);
		this.userStory = load(SpecElement.class, folder + "/" + USERSTORY_XML);
		this.registerShortCuts = load(RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
//		this.dependencySpecHolder = load(DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC2_XML);
		this.dependencySpecHolder2 = loadJson(jp.silverbullet.dependency2.DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC3_XML);
		defaultDependency = this.dependencySpecHolder2;
//		dependencySpecHolder = new AlternativeDependencyGenerator().convert(dependencySpecHolder);
		
		uiLayoutHolder.load(folder);
		registerSpecHolder = load(RegisterSpecHolder.class, folder);
		// UiLayout.getInstance().initialize();
		
		dependency = new DependencyEngine(dependencySpecHolder2, new PropertyGetter () {
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
			protected jp.silverbullet.dependency2.DependencySpecHolder getSpecHolder() {
				return dependencySpecHolder2;
			}
			
		};
	}

	private <T> T  loadJson(
			Class<T> clazz, String filename) {
		return new JsonPersistent().loadJson(clazz, filename);
	}

	@Override
	public void save(String folder) {
		try {
			Files.newDirectoryStream(Paths.get(folder)).forEach(path -> {
				try {
					Files.delete(path);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (Exception e) {

		}
		save(this.propertiesHolder, PropertyHolder.class, folder + "/" + ID_DEF_XML);
		this.propertiesHolder2.save(folder + "/" + ID_DEF_JSON);
		save(this.handlerPropertyHolder, HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		save(this.texHolder, SvTexHolder.class, folder + "/" + REMOTE_XML);
		save(this.registerProperty, RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		save(this.hardSpec, SpecElement.class, folder + "/" + HARDSPEC_XML);
		save(this.userStory, SpecElement.class, folder + "/" + USERSTORY_XML);
		save(this.registerShortCuts, RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
//		save(this.dependencySpecHolder, DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC2_XML);
		saveJson(this.dependencySpecHolder2, folder + "/" + DEPENDENCYSPEC3_XML);

		// saveJson(this.uiLayout, folder + "/" + "default.ui");
		this.uiLayoutHolder.save(folder);
	}

	private void saveJson(Object object, String filename) {
		new JsonPersistent().saveJson(object, filename);
	}

	@Override
	public void importFile(String folder) {
		PropertyHolder tmpProps = loadTestProp(folder + "/" + ID_DEF_XML);
		tmpProps.initialize();
		this.propertiesHolder.addAll(tmpProps);
//		this.store.importProperties(tmpProps);
		RegisterSpecHolder tmpRegister = load(RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
//		this.registerProperty.addAll(tmpRegister.getRegisters());

		HandlerPropertyHolder tmpHandler = load(HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		if (handlerPropertyHolder == null) {
			handlerPropertyHolder = new HandlerPropertyHolder();
		}
		handlerPropertyHolder.addAll(tmpHandler);

		this.texHolder.addAll(load(SvTexHolder.class, folder + "/" + REMOTE_XML));
		this.hardSpec.addAll(load(SpecElement.class, folder + "/" + HARDSPEC_XML));
		this.userStory.addAll(load(SpecElement.class, folder + "/" + USERSTORY_XML));
	}

	@Override
	public void loadDefault() {
		this.uiLayoutHolder.createDefault();
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

	private PropertyHolder loadTestProp(String filename) {
		XmlPersistent<PropertyHolder> propertyPersister = new XmlPersistent<PropertyHolder>();
		try {
			return propertyPersister.load(filename, PropertyHolder.class);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public SvTexHolder getTexHolder() {
		return this.texHolder;
	}

	@Override
	public RegisterSpecHolder getRegisterProperty() {
		return registerProperty;
	}

	@Override
	public SpecElement getHardwareContorlProcedure() {
		return hardSpec;
	}

	@Override
	public SpecElement getUserStory() {
		return userStory;
	}

	@Override
	public String getUserApplicationPath() {
		return this.userApplicationPath;
	}

	@Override
	public Sequencer getSequencer() {
		return this.sequencer;
	}

	@Override
	public void setDeviceDriver(SvDevice deviceDriver) {
		regiseterAccess = new RegisterAccess(deviceDriver);
	}

	@Override
	public RegisterAccess getRegisterAccess() {
		return this.regiseterAccess;
	}

	@Override
	public void setUserPath(String userPath) {
		userApplicationPath = userPath;
	}

//	@Override
//	public DependencySpecHolder getDependencySpecHolder() {
//		return dependencySpecHolder;
//	}

	@Override
	public
	jp.silverbullet.dependency2.DependencySpecHolder getDependencySpecHolder2() {
		return dependencySpecHolder2;
	}
	
	@Override
	public RegisterShortCutHolder getRegisterShortCut() {
		return this.registerShortCuts;
	}

	@Override
	public EasyAccessModel getEasyAccess() {
		return this.easyAccessModel;
	}

	@Override
	public UiLayout getUiLayout() {
		return uiLayoutHolder.getCurrentUi();
	}

	@Override
	public void changeId(String prevId, String newId) {
		this.dependencySpecHolder2.changeId(prevId, newId);
		this.propertiesHolder.changeId(prevId, newId);
		// TODO this.handlerPropertyHolder.changeId(prevId, newId);
		this.uiLayoutHolder.changeId(prevId, newId);

	}

	@Override
	public void saveParameters(String filename) {
		this.getPropertyStore().save(filename);
	}

	@Override
	public void loadParameters(String filename) {
		List<IdValue> changedIds = this.getPropertyStore().load(filename).idValue;

		try {
			this.dependency.requestChanges(changedIds);
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getUiFiles() {
		return uiLayoutHolder.getFileList();
	}

	@Override
	public List<String> createUiFile(String filename) {
		return uiLayoutHolder.createNewFile(filename);
	}

	@Override
	public UiLayout switchUiFile(String filename) {
		return this.uiLayoutHolder.switchFile(filename);
	}

	@Override
	public void removeUiFile(String filename) {
		this.uiLayoutHolder.removeFile(filename);
	}

	@Override
	public TestRecorder getTestRecorder() {
		return this.testRecorder;
	}

	@Override
	public RegisterMapModel getRegisterMapModel() {
		return this.registerMapModel;
	}

	@Override
	public void switchDependency(String type) {
		this.store.resetMask();
		
		if (type.equals("Normal")) {
			this.dependencySpecHolder2 = this.defaultDependency;
		}
		else if (type.equals("Alternative")) {
			PropertyGetter getter = new PropertyGetter() {
				@Override
				public RuntimeProperty getProperty(String id) {
					return store.get(id);
				}

				@Override
				public RuntimeProperty getProperty(String id, int index) {
					return store.get(id, index);
				}
			};
			this.dependencySpecHolder2 = new DependencySpecRebuilder(this.dependencySpecHolder2, getter).getNewHolder();
		}
	}

	@Override
	public EasyAccessInterface getEasyAccessInterface() {
		return easyAccessInterface;
	}

	public DependencyEngine getDependency() {
		return this.dependency;
	}

	public RegisterSpecHolder getRegisterSpecHolder() {
		return this.registerSpecHolder;
	}
}