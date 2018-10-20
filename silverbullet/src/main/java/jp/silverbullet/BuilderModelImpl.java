package jp.silverbullet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.StringArray;
import jp.silverbullet.register.RegisterProperty;
import jp.silverbullet.register.RegisterShortCutHolder;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.spec.SpecElement;
import jp.silverbullet.web.ui.PropertyGetter;
import jp.silverbullet.web.ui.UiLayout;
import jp.silverbullet.dependency.DepPropertyStore;
import jp.silverbullet.dependency.DependencyEngine;
import jp.silverbullet.dependency.DependencyInterface;
import jp.silverbullet.dependency.DependencySpecHolder;
import jp.silverbullet.dependency.RequestRejectedException;
import jp.silverbullet.handlers.EasyAccessModel;
import jp.silverbullet.handlers.HandlerPropertyHolder;
import jp.silverbullet.handlers.RegisterAccess;
import jp.silverbullet.handlers.SvDevice;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BuilderModelImpl implements BuilderModel {
	
	private static final String ID_DEF_XML = "id_def.xml";
	private static final String HANDLER_XML = "handlers.xml";
	private static final String REMOTE_XML = "remote.xml";
	private static final String REGISTER_XML = "register.xml";
	private static final String HARDSPEC_XML = "hardspec.xml";
	private static final String USERSTORY_XML = "userstory.xml";
	private static final String REGISTERSHORTCUT = "registershortcuts.xml";
	private static final String DEPENDENCYSPEC2_XML = "dependencyspec2.xml";
	private static final String GUI_LAYOUT_JSON = "layout.json";	
	
	private static BuilderModelImpl instance;
	
	private List<String> selectedId;
	private SvPropertyStore store;
	private SpecElement hardSpec = new SpecElement();
	private String userApplicationPath = "";
	private Sequencer sequencer;
	private PropertyHolder propertiesHolder = new PropertyHolder();
	private HandlerPropertyHolder handlerPropertyHolder = new HandlerPropertyHolder();
	private SvTexHolder texHolder = new SvTexHolder();
	private RegisterProperty registerProperty = new RegisterProperty();
	private DependencySpecHolder dependencySpecHolder = new DependencySpecHolder();
	private SpecElement userStory = new SpecElement();
	private RegisterShortCutHolder registerShortCuts = new RegisterShortCutHolder();
	
	@Override
	public HandlerPropertyHolder getHandlerPropertyHolder() {
		return handlerPropertyHolder;
	}
	
	private DependencyEngine dependency = new DependencyEngine() {
		@Override
		protected DependencySpecHolder getDependencyHolder() {
			return dependencySpecHolder;
		}

		@Override
		protected DepPropertyStore getPropertiesStore() {
			return new DepPropertyStore() {

				@Override
				public SvProperty getProperty(String id) {
					return store.getProperty(id);
				}

				@Override
				public void add(SvProperty createListProperty) {
				}
			};
		}

	};
	
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
		public SvProperty getProperty(String id) {
			return store.getProperty(id);
		}
	};
	
	private RegisterAccess regiseterAccess;
	private UiLayout uiLayout;

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

		this.loadDefault();
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
	public List<SvProperty> getAllProperties(String type) {
		return store.getAllProperties(type);
	}

	@Override
	public List<String> getSelectedIds() {
		return selectedId;
	}

	@Override
	public List<String> getIds(String type) {
		return store.getIds(type);
	}

	@Override
	public DependencyInterface getDependency() {
		return this.sequencer;
	}

	@Override
	public void load(String folder) {

		propertiesHolder = load(PropertyHolder.class, folder + "/" + ID_DEF_XML);
		propertiesHolder.initialize();

		this.store = new SvPropertyStore(propertiesHolder);
		this.registerProperty = load(RegisterProperty.class, folder + "/" + REGISTER_XML);
		this.handlerPropertyHolder = load(HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		this.texHolder = load(SvTexHolder.class, folder + "/" + REMOTE_XML);
		this.hardSpec = load(SpecElement.class, folder + "/" + HARDSPEC_XML);
		this.userStory = load(SpecElement.class, folder + "/" + USERSTORY_XML);
		this.registerShortCuts = load(RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		this.dependencySpecHolder = load(DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC2_XML);
		this.uiLayout = loadJson(UiLayout.class, folder + "/" + GUI_LAYOUT_JSON);
		this.uiLayout.setPropertyGetter(new PropertyGetter() {
			@Override
			public SvProperty getProperty(String id) {
				return store.getProperty(id);
			}
		});
//		UiLayout.getInstance().initialize();
	}

	private <T> T loadJson(Class<T> clazz, String filename) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			T ret = mapper.readValue(new File(filename), clazz);
			if (ret == null) {
				try {
					ret = clazz.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			return ret;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		try {
			return clazz.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void save(String folder) {
		save(this.propertiesHolder, PropertyHolder.class, folder + "/" + ID_DEF_XML);
		save(this.handlerPropertyHolder, HandlerPropertyHolder.class, folder + "/" + HANDLER_XML);
		save(this.texHolder, SvTexHolder.class, folder + "/" + REMOTE_XML);
		save(this.registerProperty, RegisterProperty.class, folder + "/" + REGISTER_XML);
		save(this.hardSpec, SpecElement.class, folder + "/" + HARDSPEC_XML);
		save(this.userStory, SpecElement.class, folder + "/" + USERSTORY_XML);
		save(this.registerShortCuts, RegisterShortCutHolder.class, folder + "/" + REGISTERSHORTCUT);
		save(this.dependencySpecHolder, DependencySpecHolder.class, folder + "/" + DEPENDENCYSPEC2_XML);
		saveJson(this.uiLayout, folder + "/" + GUI_LAYOUT_JSON);
	}
	
	private void saveJson(Object object, String filename) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String s = mapper.writeValueAsString(object);
			Files.write(Paths.get(filename), Arrays.asList(s));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void importFile(String folder) {
		PropertyHolder tmpProps = loadTestProp(folder + "/" + ID_DEF_XML);
		tmpProps.initialize();
		this.propertiesHolder.addAll(tmpProps);
		this.store.importProperties(tmpProps);
		RegisterProperty tmpRegister = load(RegisterProperty.class, folder + "/" + REGISTER_XML);
		this.registerProperty.addAll(tmpRegister.getRegisters());
		
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
		this.propertiesHolder.getTypes().getDefinitions().put("ListProperty", new StringArray(Arrays.asList("unit", "choices", "defaultKey", "persistent")));
		this.propertiesHolder.getTypes().getDefinitions().put("ImageProperty", new StringArray(Arrays.asList("persistent")));
		this.propertiesHolder.getTypes().getDefinitions().put("TextProperty", new StringArray(Arrays.asList("defaultValue", "maxLength", "persistent")));
		this.propertiesHolder.getTypes().getDefinitions().put("BooleanProperty", new StringArray(Arrays.asList("defaultValue", "persistent")));
		this.propertiesHolder.getTypes().getDefinitions().put("LongProperty", new StringArray(Arrays.asList("unit", "defaultValue", "min", "max", "persistent")));
		this.propertiesHolder.getTypes().getDefinitions().put("ChartProperty", new StringArray());
		this.propertiesHolder.getTypes().getDefinitions().put("TableProperty", new StringArray());
		
		uiLayout = new UiLayout();
		
	
	}
	
	private <T> T load(Class<T> clazz, String filename) {
		XmlPersistent<T> propertyPersister = new XmlPersistent<>();
		try {
			return propertyPersister.load(filename, clazz);
		} catch (Exception e) {
			try {
				return clazz.newInstance();
			} catch (InstantiationException | IllegalAccessException e1) {
				// TODO Auto-generated catch block
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

	@Override
	public DependencySpecHolder getDependencySpecHolder() {
		return dependencySpecHolder;
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
		return this.uiLayout;
	}

	@Override
	public void changeId(String prevId, String newId) {
		this.dependencySpecHolder.changeId(prevId, newId);
	    this.propertiesHolder.changeId(prevId, newId);
	// TODO	this.handlerPropertyHolder.changeId(prevId, newId);
		this.uiLayout.changeId(prevId, newId);
		
	}
}
