package jp.silverbullet;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javafx.application.Platform;
import jp.silverbullet.dependency.engine.DependencyEngine;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor2.DependencySpecHolder;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.PropertyType;
import jp.silverbullet.property.editor.PropertyListModel2;
import jp.silverbullet.register.RegisterProperty;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.spec.SpecElement;
import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;

import javax.xml.bind.JAXBException;

public class BuilderModelImpl implements BuilderModel {
	
	private static final String DEPENDENCYSPEC_XML = "/dependencyspec.xml";
	private static final String ID_DEF_XML = "id_def.xml";
	private static final String HANDLER_XML = "handlers.xml";
	private static final String REMOTE_XML = "remote.xml";
	private static final String REGISTER_XML = "register.xml";
	private static final String HARDSPEC_XML = "hardspec.xml";
	private static final String USERSTORY_XML = "userstory.xml";
	
	private List<String> selectedId;
	private SvPropertyStore store;
	private SpecElement hardSpec = new SpecElement();
	private String userApplicationPath = "";
	private Sequencer sequencer;
	private PropertyHolder propertiesHolder = new PropertyHolder();
	private HandlerPropertyHolder handlerPropertyHolder = new HandlerPropertyHolder();
	private SvTexHolder texHolder = new SvTexHolder();
	private RegisterProperty registerProperty = new RegisterProperty();
//	private UserRegisterControl userRegisterAccess;// 
	private SpecElement userStory = new SpecElement();
	
	@Override
	public HandlerPropertyHolder getHandlerPropertyHolder() {
		return handlerPropertyHolder;
	}

	private DependencyEngine dependency = new DependencyEngine() {
		@Override
		protected SvPropertyStore getPropertiesStore() {
			return store;
		}
	};
	
	private EasyAccessModel easyAccessModel = new EasyAccessModel() {
		@Override
		public void requestChange(final String id, final String value) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					try {
						dependency.requestChange(id, value);
					} catch (RequestRejectedException e) {
						e.printStackTrace();
					}
				}
			});
		}

		@Override
		public SvProperty getProperty(String id) {
			return store.getProperty(id);
		}
	};
	
//	private UserEasyAccess access = new UserEasyAccess(easyAccessModel);
	private RegisterAccess regiseterAccess;

	public BuilderModelImpl() {
		store = new SvPropertyStore(propertiesHolder);	
		
		this.sequencer = new Sequencer() {
			@Override
			protected SvPropertyStore getPropertiesStore() {
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

	}
	
	@Override
	public SvProperty getProperty(String id) {
		return store.getProperty(id);
	}

	@Override
	public List<String> getAllTypes() {
		return store.getAllTypes();
	}

	@Override
	public List<SvProperty> getAllProperties() {
		return store.getAllProperties();
	}

	@Override
	public PropertyHolder getPropertyHolder() {
		return this.propertiesHolder;
	}


	@Override
	public SvPropertyStore getPropertyStore() {
		return store;
	}

	@Override
	public DependencySpecHolder getDependencySpecHolder() {
		return this.dependency.getSpecHolder();
	}

	@Override
	public List<SvProperty> getAllProperties(String type) {
		return store.getAllProperties(type);
	}

	@Override
	public List<String> getSelectedIds() {
		PropertyListModel2 model = new PropertyListModel2(this.propertiesHolder);
		return selectedId;
	}

	@Override
	public List<String> getIds(String type) {
		return store.getIds(type);
	}
	@Override
	public void save(String folder) {
		this.dependency.saveSpec(folder + DEPENDENCYSPEC_XML);
		saveTestProp(folder + "/" + ID_DEF_XML);
		saveHandlerPropertyHolder(folder + "/" + HANDLER_XML);
		saveRemote(folder + "/" + REMOTE_XML);
		saveRegister(folder + "/" + REGISTER_XML);
		saveSpec(this.hardSpec, folder + "/" + HARDSPEC_XML);
		saveSpec(this.userStory, folder + "/" + USERSTORY_XML);
	}

	private void saveSpec(SpecElement spec, String filename) {
		XmlPersistent<SpecElement> propertyPersister = new XmlPersistent<>();
		try {
			propertyPersister.save(spec, filename, SpecElement.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void saveRegister(String filename) {
		XmlPersistent<RegisterProperty> propertyPersister = new XmlPersistent<RegisterProperty>();
		try {
			propertyPersister.save(this.registerProperty, filename, RegisterProperty.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void saveRemote(String filename) {
		XmlPersistent<SvTexHolder> propertyPersister = new XmlPersistent<SvTexHolder>();
		try {
			propertyPersister.save(this.texHolder, filename, SvTexHolder.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void saveHandlerPropertyHolder(String filename) {
		XmlPersistent<HandlerPropertyHolder> propertyPersister = new XmlPersistent<HandlerPropertyHolder>();
		try {
			propertyPersister.save(this.handlerPropertyHolder, filename, HandlerPropertyHolder.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public DependencyInterface getDependency() {
		return this.sequencer;
	}

	@Override
	public void load(String folder) {

		propertiesHolder = loadTestProp(folder + "/" + ID_DEF_XML);
		propertiesHolder.initialize();

		this.store = new SvPropertyStore(propertiesHolder);
		this.dependency.loadSpec(folder + DEPENDENCYSPEC_XML);
		this.registerProperty = loadRegisterProperty(folder + "/" + REGISTER_XML);
		handlerPropertyHolder = loadHandlerPropertyHolder(folder + "/" + HANDLER_XML);
		if (handlerPropertyHolder == null) {
			handlerPropertyHolder = new HandlerPropertyHolder();
		}
		
		this.texHolder = loadRemote(folder + "/" + REMOTE_XML);
		this.hardSpec = loadSpec(folder + "/" + HARDSPEC_XML);
		this.userStory = loadSpec(folder + "/" + USERSTORY_XML);
	}

	@Override
	public void importFile(String folder) {
		PropertyHolder tmpProps = loadTestProp(folder + "/" + ID_DEF_XML);
		tmpProps.initialize();
		this.propertiesHolder.addAll(tmpProps);
		this.store.importProperties(tmpProps);
		this.dependency.importSpec(folder + DEPENDENCYSPEC_XML);
		RegisterProperty tmpRegister = loadRegisterProperty(folder + "/" + REGISTER_XML);
		this.registerProperty.addAll(tmpRegister.getRegisters());
		
		HandlerPropertyHolder tmpHandler = loadHandlerPropertyHolder(folder + "/" + HANDLER_XML);
		if (handlerPropertyHolder == null) {
			handlerPropertyHolder = new HandlerPropertyHolder();
		}
		handlerPropertyHolder.addAll(tmpHandler);
		
		this.texHolder.addAll(loadRemote(folder + "/" + REMOTE_XML));
		this.hardSpec.addAll(loadSpec(folder + "/" + HARDSPEC_XML));
		this.userStory.addAll(loadSpec(folder + "/" + USERSTORY_XML));	
	}
	
	@Override
	public void loadDefault() {
		XmlPersistent<PropertyType> per = new XmlPersistent<>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("defaultprottype.xml")));
			
			String xml = "";
			String s = "";
	         while((s = reader.readLine()) != null) {
	        	 xml = xml + s;
	         } 
	         PropertyType propType = per.loadFromXml(xml, PropertyType.class);
			this.propertiesHolder.setTypes(propType);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private SpecElement loadSpec(String filename) {
		XmlPersistent<SpecElement> propertyPersister = new XmlPersistent<>();
		try {
			return propertyPersister.load(filename, SpecElement.class);
		} catch (FileNotFoundException e) {
			return new SpecElement();
		}
	}

	private RegisterProperty loadRegisterProperty(String filename) {
		XmlPersistent<RegisterProperty> propertyPersister = new XmlPersistent<>();
		try {
			return propertyPersister.load(filename, RegisterProperty.class);
		} catch (FileNotFoundException e) {
			return new RegisterProperty();
		}
	}

	private SvTexHolder loadRemote(String filename) {
		XmlPersistent<SvTexHolder> propertyPersister = new XmlPersistent<SvTexHolder>();
		try {
			return propertyPersister.load(filename, SvTexHolder.class);
		} catch (FileNotFoundException e) {
			return new SvTexHolder();
		}
	}

	private HandlerPropertyHolder loadHandlerPropertyHolder(String filename) {
		XmlPersistent<HandlerPropertyHolder> propertyPersister = new XmlPersistent<HandlerPropertyHolder>();
		try {
			return propertyPersister.load(filename, HandlerPropertyHolder.class);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	private void saveTestProp(String filename) {
		XmlPersistent<PropertyHolder> propertyPersister = new XmlPersistent<PropertyHolder>();
		try {
			propertyPersister.save(this.propertiesHolder, filename, PropertyHolder.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	private PropertyHolder loadTestProp(String filename) {
		XmlPersistent<PropertyHolder> propertyPersister = new XmlPersistent<PropertyHolder>();
		try {
			return propertyPersister.load(filename, PropertyHolder.class);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	@Override
	public SvTexHolder getTexHolder() {
		return this.texHolder;
	}

	@Override
	public RegisterProperty getRegisterProperty() {
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
//		userRegisterAccess = new UserRegisterControl(regiseterAccess);
	}

	@Override
	public RegisterAccess getRegisterAccess() {
		return this.regiseterAccess;
	}

	@Override
	public void setUserPath(String userPath) {
		userApplicationPath = userPath;
	}
}
