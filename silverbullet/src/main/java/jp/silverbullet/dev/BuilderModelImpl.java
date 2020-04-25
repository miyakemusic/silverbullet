package jp.silverbullet.dev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.JsonPersistent;
import jp.silverbullet.core.PropertyGetter;
import jp.silverbullet.core.RuntimeListener;
import jp.silverbullet.core.XmlPersistent;
import jp.silverbullet.core.dependency2.CommitListener;
import jp.silverbullet.core.dependency2.DependencyEngine;
import jp.silverbullet.core.dependency2.DependencySpecHolder;
import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.PropertyDef2;
import jp.silverbullet.core.property2.PropertyHolder2;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.property2.SvFileException;
import jp.silverbullet.core.register2.RegisterAccessor;
import jp.silverbullet.core.register2.RegisterController;
import jp.silverbullet.core.register2.RegisterShortCutHolder;
import jp.silverbullet.core.register2.RegisterSpecHolder;
import jp.silverbullet.core.register2.RuntimeRegisterMap;
import jp.silverbullet.core.register2.RuntimeRegisterMap.DeviceType;
import jp.silverbullet.core.sequncer.EasyAccessInterface;
import jp.silverbullet.core.sequncer.Sequencer;
import jp.silverbullet.core.sequncer.SystemAccessor;
import jp.silverbullet.core.sequncer.UserSequencer;
import jp.silverbullet.core.sequncer.Sequencer.Actor;
import jp.silverbullet.core.sequncer.SystemAccessor.DialogAnswer;
import jp.silverbullet.core.ui.part2.UiBuilder;
import jp.silverbullet.dev.dependency2.design.DependencyDesigner;
import jp.silverbullet.dev.selfbuild.SelfBuilder;
import jp.silverbullet.dev.test.TestRecorder;
import jp.silverbullet.dev.test.TestRecorderInterface;
import jp.silverbullet.web.ValueSetResult;

public class BuilderModelImpl implements Cloneable {

	private static final String ID_DEF_JSON = "id_def.json";
	private static final String REGISTERSHORTCUT = "registershortcuts.xml";
	private static final String DEPENDENCYSPEC3_XML = "dependencyspec3.xml";
	private static final String UIBUILDER2 = "uibuilder2.json";
	
	private RuntimePropertyStore store;
	private Sequencer sequencer;
	private PropertyHolder2 propertiesHolder2 = new PropertyHolder2();
	private DependencySpecHolder dependencySpecHolder2 = new DependencySpecHolder();
	private RegisterShortCutHolder registerShortCuts = new RegisterShortCutHolder();
	private SelfBuilder selfBuilder = new SelfBuilder();
	
	private RegisterController registerController = new RegisterController();
	private RuntimeRegisterMap runtimeRegisterMap = new RuntimeRegisterMap();
	private BlobStore blobStore = new BlobStore();
	private RefactorManager undoManager = new RefactorManager();
	
	private DependencyDesigner dependencyDesigner = new DependencyDesigner(propertiesHolder2) {

		@Override
		public DependencySpecHolder getDependencySpecHolder() {
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
	
	private RegisterAccessor currentRegisterAccessor = runtimeRegisterMap;

	private EasyAccessInterface easyAccessInterface = new EasyAccessInterface() {

		public void requestChange(String id, String value) throws RequestRejectedException {
			getSequencer().requestChange(id, value, false);
		}
		
		public void requestChange(String id, int index, String value) throws RequestRejectedException {
			getSequencer().requestChange(id, index, value, false);
		}

		@Override
		public void requestChange(String id, Object blobData, String name) throws RequestRejectedException {
			System.err.println("requestChange blob");
		}

		@Override
		public String getCurrentValue(String id) {
			return store.get(id).getCurrentValue();
		}

		@Override
		public String getSelectedListTitle(String id) {
			return store.get(id).getSelectedListTitle();
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
			try {
				BuilderModelImpl.this.saveParameters(filename);
			} catch (SvFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void requestChange(String id, String value) throws RequestRejectedException {
			sequencer.requestChange(id, value, false);
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
	
	private SystemAccessor systemAccessor = new SystemAccessor() {
		@Override
		public void saveProperties(String fileName) throws SvFileException {
			saveParameters(fileName);
		}

		@Override
		public void loadProperties(String fileName) throws SvFileException {
			loadParameters(fileName);
		}

		DialogAnswer ret = DialogAnswer.OK;
		@Override
		public DialogAnswer dialog(String message) {
			
			runtimeListeners.forEach(listener -> {
				ret = listener.dialog(message);
			});
			return ret;
		}

		@Override
		public void message(String message) {
			runtimeListeners.forEach(listener -> listener.message(message));
		}
	};
	
	private RegisterSpecHolder registerSpecHolder = new RegisterSpecHolder();
	private List<RegisterAccessor> simulators;
	private RegisterAccessor hardwareAccessor;
	private UiBuilder uiBuilder  = new UiBuilder();
	private Set<RuntimeListener> runtimeListeners = new HashSet<>();

	public enum RegisterTypeEnum {
		Simulator,
		Hardware,
	};

	public BuilderModelImpl() {
		store = new RuntimePropertyStore(propertiesHolder2);
		this.getRegisterAccessor().addListener(this.testRecorder);
		this.getRuntimRegisterMap().addDevice(DeviceType.CONTROLLER, this.registerController);
		this.sequencer = new Sequencer() {
			@Override
			protected RuntimePropertyStore getPropertiesStore() {
				return store;
			}

			@Override
			protected DependencyEngine getDependency() {
				return dependency;
			}
			
			@Override
			protected RegisterAccessor getRegisterAccessor() {
				return currentRegisterAccessor;
			}

			@Override
			protected BlobStore getBlobStore() {
				return blobStore;
			}

			@Override
			protected SystemAccessor getSystemAccessor() {
				return systemAccessor;
			}
		};

		sequencer.addSequencerListener(testRecorder);
//		this.uiLayoutHolder.createDefault();
		createDependencyEngine();
		
		this.undoManager.set(propertiesHolder2, dependencySpecHolder2, uiBuilder, this.testRecorder);
	}

	public RuntimePropertyStore getRuntimePropertyStore() {
		return store;
	}
	
	public void load(String folder) {

		propertiesHolder2.load(folder + "/" + ID_DEF_JSON);
		
		//this.store = new RuntimePropertyStore(propertiesHolder2);
//		this.registerProperty = load(RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		this.registerShortCuts = load(RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		this.dependencySpecHolder2 = loadJson(DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC3_XML);
		
	
//		uiLayoutHolder.load(folder);
	
		registerSpecHolder.load(folder);// = load(RegisterSpecHolder.class, folder);
//		restrictionMatrix.load(folder);
		
		//this.uiBuilder = this.load(UiBuilder.class, folder + "/" + UIBUILDER);
		//this.uiBuilder = this.loadJson(UiBuilder.class, folder + "/" + UIBUILDER2);
		this.uiBuilder.loadJson(folder + "/" + UIBUILDER2);
		if (this.uiBuilder != null) {
			this.uiBuilder.nameAll();
		}
		this.dependencyDesigner.load(folder);
		this.selfBuilder.load(folder);
		
		this.undoManager.set(propertiesHolder2, dependencySpecHolder2, uiBuilder, this.testRecorder);
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
		try {
			return new JsonPersistent().loadJson(clazz, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
//		save(this.registerProperty, RegisterSpecHolder.class, folder + "/" + REGISTER_XML);
		save(this.registerShortCuts, RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		saveJson(this.dependencySpecHolder2, folder + "/" + DEPENDENCYSPEC3_XML);

		this.registerSpecHolder.save(folder);

//		save(this.uiBuilder, UiBuilder.class, folder + "/" + UIBUILDER);
//		saveJson(this.uiBuilder, folder + "/" + UIBUILDER2);
		this.uiBuilder.saveJson(folder + "/" + UIBUILDER2);
		
		this.dependencyDesigner.save(folder);
		this.selfBuilder.save(folder);
	}

	private void saveJson(Object object, String filename) {
		try {
			new JsonPersistent().saveJson(object, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		if (object == null) return;
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
	
//	public void changeId(String prevId, String newId) {
//		this.propertiesHolder2.get(prevId).setId(newId);
//		this.dependencySpecHolder2.changeId(prevId, newId);
//		this.uiBuilder.changeId(prevId, newId);
//		this.testRecorder.changeId(prevId, newId);
//	}

	
	public void saveParameters(String filename) throws SvFileException {
		this.getRuntimePropertyStore().save(filename);
	}

	
	public void loadParameters(String filename) throws SvFileException {
		try {
			List<IdValue> changedIds = this.getRuntimePropertyStore().load(filename).idValue;
			this.dependency.requestChanges(changedIds);
		} catch (NullPointerException e) {
			throw new SvFileException();
		} catch (RequestRejectedException e) {
			e.printStackTrace();
		}
	}

		
//	public UiLayout switchUiFile(String filename) {
//		return this.uiLayoutHolder.switchFile(filename);
//	}

	public TestRecorder getTestRecorder() {
		return this.testRecorder;
	}
	
//	public void switchDependency(String type) {
//		this.store.resetMask();
//		
//		if (type.equals("Normal")) {
//			this.dependencySpecHolder2 = this.defaultDependency;
//		}
//		else if (type.equals("Alternative")) {
//			PropertyGetter getter = new PropertyGetter() {
//				
//				public RuntimeProperty getProperty(String id) {
//					return store.get(id);
//				}
//
//				
//				public RuntimeProperty getProperty(String id, int index) {
//					return store.get(id, index);
//				}
//			};
//			this.dependencySpecHolder2 = new DependencySpecRebuilder(this.dependencySpecHolder2, getter).getNewHolder();
//		}
//	}

	
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

//	public void setSourceInfo(String baseFolderAndPackage) {
//		this.sourceInfo = baseFolderAndPackage;
//	}

	public String getSourceInfo() {
		return selfBuilder.getPath() + ";" + selfBuilder.getPackage();
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
	
	private CommitListener commitListener = new CommitListener() {
		@Override
		public Reply confirm(Set<IdValue> message) {
			//String tmp = createConfirmMessage(map);
			if (message.isEmpty()) {
				return Reply.Accept;
			}
			
			String msg = "Following parameters will be changed by this action.\nDo you continue?\n\n" 
						+createConfirmMessage(message);

			runtimeListeners.forEach(listener -> {
				dialogReply = listener.dialog(msg);
			});
			if (dialogReply.compareTo(DialogAnswer.OK) == 0) {
				return Reply.Accept;
			}
			else {
				return Reply.Reject;
			}
		}
		
		private String createConfirmMessage(Set<IdValue> message) {
			StringBuilder sb = new StringBuilder();
			for (IdValue s : message) {
				RuntimeProperty prop = getRuntimePropertyStore().get(s.getId().toString());
				String value = s.getValue();
				if (prop.isList()) {
					value = prop.getOptionTitle(value);
				}
				sb.append(prop.getTitle() + " -> " + value + "\n");
			}
			return sb.toString();
		}
		
	};
	
	private DialogAnswer dialogReply = DialogAnswer.OK;
	public ValueSetResult requestChange(String id, int index, String value, 
			boolean forceChange, Actor actor) {
		
		ValueSetResult ret = new ValueSetResult();
		
		try {
			sequencer.requestChange(id, index, value, forceChange, 
					forceChange ? null : commitListener, actor);
			
//			getUiLayoutHolder().getCurrentUi().doAutoDynamicPanel();

			ret.result = "Accepted";
		} catch (RequestRejectedException e) {
			ret.message = e.getMessage();
			ret.result = "Rejected";
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ret.debugLog = sequencer.getDebugDepLog();		
		}

		return ret;
	}
	
	public ValueSetResult requestChange(String id, Integer index, String value, Actor actor) {
		return requestChange(id, index, value, false, actor);
	}

//	public void setUiBuilder(UiBuilder ui) {
//		if (this.uiBuilder == null) {
//			this.uiBuilder = ui;
//			
//		}
//	}

	public UiBuilder getUiBuilder() {
		return uiBuilder;
	}

	public BlobStore getBlobStore() {
		return blobStore;
	}

	public DependencyDesigner getDependencyDesigner() {
		return this.dependencyDesigner;
	}

	public void setDefaultValues() {
		this.getRuntimePropertyStore().getAllProperties().forEach(prop -> {
			if (prop.isAction()) {
				return;
			}
			prop.resetValue();
			this.requestChange(prop.getId(), prop.getIndex(), prop.getCurrentValue(), true, Actor.System);
		});
	}

	public void addRuntimeListener(RuntimeListener runtimeListener) {
		runtimeListeners.add(runtimeListener);
	}

	public void replyDialog(String messageId, String reply) {
		this.runtimeListeners.forEach(listener -> listener.onReply(messageId, reply));
	}

	public SelfBuilder getSelfBuilder() {
		return selfBuilder;
	}

	public ValueSetResult requestChangeByUser(String id, Integer index, String value) {
		return this.requestChange(id, index, value, false, Actor.User);
	}

	public void addUserSequencer(UserSequencer sequencer2) {
		this.getSequencer().addUserSequencer(sequencer2);
	}

	public void requestChangeBySystem(String id, int index, String value) {
		try {
			this.getSequencer().getAccessFromSystem().requestChange(id, index, value);
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String requestBlobChangeBySystem(String id, Integer index, Object object, String name) {
		try {
			this.getSequencer().getAccessFromSystem().requestChange(id, object, name);
		} catch (RequestRejectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "OK";
	}

	public BuilderModelImpl clone() {
		try {
			return (BuilderModelImpl)super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}