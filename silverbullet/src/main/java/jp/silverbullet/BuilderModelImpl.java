package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.bind.JAXBException;

import jp.silverbullet.dependency2.DependencySpecRebuilder;
import jp.silverbullet.dependency2.IdValue;
import jp.silverbullet.dependency2.RequestRejectedException;
import jp.silverbullet.dependency2.DependencyEngine;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.handlers.EasyAccessInterface;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.PropertyType2;
import jp.silverbullet.property2.RuntimeProperty;
import jp.silverbullet.property2.RuntimePropertyStore;
import jp.silverbullet.register2.RegisterAccessor;
import jp.silverbullet.register2.RegisterController;
import jp.silverbullet.register2.RegisterShortCutHolder;
import jp.silverbullet.register2.RegisterSpecHolder;
import jp.silverbullet.register2.RuntimeRegisterMap;
//import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.spec.SpecElement;
import jp.silverbullet.test.TestRecorder;
import jp.silverbullet.test.TestRecorderInterface;
import jp.silverbullet.web.ui.PropertyGetter;
import jp.silverbullet.web.ui.UiLayout;
import jp.silverbullet.web.ui.UiLayoutHolder;

public class BuilderModelImpl {

	private static final String ID_DEF_JSON = "id_def.json";
	private static final String HANDLER_XML = "handlers.xml";
	private static final String REGISTER_XML = "register.xml";
	private static final String HARDSPEC_XML = "hardspec.xml";
	private static final String USERSTORY_XML = "userstory.xml";
	private static final String REGISTERSHORTCUT = "registershortcuts.xml";
	private static final String DEPENDENCYSPEC3_XML = "dependencyspec3.xml";

	private List<String> selectedId;
	private RuntimePropertyStore store;
	private SpecElement hardSpec = new SpecElement();
	private String userApplicationPath = "";
	private Sequencer sequencer;
	private PropertyHolder2 propertiesHolder2 = new PropertyHolder2();
	private HandlerPropertyHolder handlerPropertyHolder = new HandlerPropertyHolder();
	private RegisterSpecHolder registerProperty = new RegisterSpecHolder();
	private jp.silverbullet.dependency2.DependencySpecHolder dependencySpecHolder2 = new jp.silverbullet.dependency2.DependencySpecHolder();
	private SpecElement userStory = new SpecElement();
	private RegisterShortCutHolder registerShortCuts = new RegisterShortCutHolder();
	private RuntimeRegisterMap registerMap = new RuntimeRegisterMap();
	
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
	
	public RuntimeRegisterMap getRuntimRegisterMap() {
		return this.registerMap;
	}
		
	public HandlerPropertyHolder getHandlerPropertyHolder() {
		return handlerPropertyHolder;
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
		
		public int getRegisterValue(String regName, String bitName) {
			return BuilderModelImpl.this.getRuntimRegisterMap().readRegister(regName, bitName);
		}
		
		public RegisterController getRegisterController() {
			return BuilderModelImpl.this.getRuntimRegisterMap().getRegisterController();
		}
	});
	private DependencySpecHolder defaultDependency;
	private RegisterSpecHolder registerSpecHolder = new RegisterSpecHolder();
	private List<RegisterAccessor> simulators;

	public BuilderModelImpl() {
		store = new RuntimePropertyStore(propertiesHolder2);
		this.getRuntimRegisterMap().addListener(this.testRecorder);
		
		this.sequencer = new Sequencer() {
			protected RuntimePropertyStore getPropertiesStore() {
				return store;
			}

			protected HandlerPropertyHolder getHandlerPropertyHolder() {
				return handlerPropertyHolder;
			}

			protected DependencyEngine getDependency() {
				return dependency;
			}

			protected String getUserApplicationPath() {
				return userApplicationPath;
			}
			
			protected EasyAccessInterface getEasyAccessInterface() {
				return easyAccessInterface;
			}
			
			protected RegisterAccessor getRegisterAccessor() {
				return registerMap;
			}
		};

		sequencer.addSequencerListener(testRecorder);
		this.loadDefault();
	}

	public RuntimeProperty getProperty(String id) {
		return store.get(id);
	}

	public List<String> getAllTypes() {
		return propertiesHolder2.getTypes();
	}

	public List<RuntimeProperty> getAllProperties() {
		return store.getAllProperties();
	}
	
	public RuntimePropertyStore getPropertyStore() {
		return store;
	}

	public List<RuntimeProperty> getAllProperties(PropertyType2 type) {
		return store.getAllProperties(type);
	}

	public List<String> getSelectedIds() {
		return selectedId;
	}

	public List<String> getIds(PropertyType2 type) {
		return store.getIds(type);
	}
	
	public void load(String folder) {

		propertiesHolder2.load(folder + "/" + ID_DEF_JSON);
		
		this.store = new RuntimePropertyStore(propertiesHolder2);
		this.registerProperty = load(RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		this.handlerPropertyHolder = load(HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		this.hardSpec = load(SpecElement.class, folder + "/" + HARDSPEC_XML);
		this.userStory = load(SpecElement.class, folder + "/" + USERSTORY_XML);
		this.registerShortCuts = load(RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		this.dependencySpecHolder2 = loadJson(jp.silverbullet.dependency2.DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC3_XML);
		defaultDependency = this.dependencySpecHolder2;
	
		uiLayoutHolder.load(folder);
	
		registerSpecHolder.load(folder);// = load(RegisterSpecHolder.class, folder);

		dependency = new DependencyEngine(dependencySpecHolder2, new PropertyGetter () {
			public RuntimeProperty getProperty(String id) {
				return store.get(id);
			}
			
			public RuntimeProperty getProperty(String id, int index) {
				return store.get(id, index);
			}
		}) {

			protected jp.silverbullet.dependency2.DependencySpecHolder getSpecHolder() {
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
		this.propertiesHolder2.save(folder + "/" + ID_DEF_JSON);
		save(this.handlerPropertyHolder, HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		save(this.registerProperty, RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		save(this.hardSpec, SpecElement.class, folder + "/" + HARDSPEC_XML);
		save(this.userStory, SpecElement.class, folder + "/" + USERSTORY_XML);
		save(this.registerShortCuts, RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		saveJson(this.dependencySpecHolder2, folder + "/" + DEPENDENCYSPEC3_XML);

		this.registerSpecHolder.save(folder);
		this.uiLayoutHolder.save(folder);
	}

	private void saveJson(Object object, String filename) {
		new JsonPersistent().saveJson(object, filename);
	}

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
	
	public RegisterSpecHolder getRegisterProperty() {
		return registerProperty;
	}
	
	public SpecElement getHardwareContorlProcedure() {
		return hardSpec;
	}
	
	public SpecElement getUserStory() {
		return userStory;
	}
	
	public String getUserApplicationPath() {
		return this.userApplicationPath;
	}

	public Sequencer getSequencer() {
		return this.sequencer;
	}
	
	public void setUserPath(String userPath) {
		userApplicationPath = userPath;
	}
	
	public DependencySpecHolder getDependencySpecHolder2() {
		return dependencySpecHolder2;
	}
	
	
	public RegisterShortCutHolder getRegisterShortCut() {
		return this.registerShortCuts;
	}
	
	public UiLayout getUiLayout() {
		return uiLayoutHolder.getCurrentUi();
	}

	
	public void changeId(String prevId, String newId) {
		this.dependencySpecHolder2.changeId(prevId, newId);
		this.uiLayoutHolder.changeId(prevId, newId);
	}

	
	public void saveParameters(String filename) {
		this.getPropertyStore().save(filename);
	}

	
	public void loadParameters(String filename) {
		List<IdValue> changedIds = this.getPropertyStore().load(filename).idValue;

		try {
			this.dependency.requestChanges(changedIds);
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

	
	public List<String> getUiFiles() {
		return uiLayoutHolder.getFileList();
	}

	
	public List<String> createUiFile(String filename) {
		return uiLayoutHolder.createNewFile(filename);
	}

	
	public UiLayout switchUiFile(String filename) {
		return this.uiLayoutHolder.switchFile(filename);
	}

	
	public void removeUiFile(String filename) {
		this.uiLayoutHolder.removeFile(filename);
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
}