package jp.silverbullet.dependency2.design;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import jp.silverbullet.JsonPersistent;
import jp.silverbullet.XmlPersistent;
import jp.silverbullet.dependency2.DependencySpecHolder;
import jp.silverbullet.property2.PropertyDefHolderListener;
import jp.silverbullet.property2.PropertyDef2;
import jp.silverbullet.property2.PropertyHolder2;
import jp.silverbullet.property2.RuntimeProperty;

public abstract class DependencyDesigner {

	private static final String FILENAME = "DependencyDesigner2.json";
	private static final String CONFIGFILENAME = "DependencyDesignerConfig2.json";

	abstract protected RuntimeProperty getRuntimeProperty(String id);

	abstract protected List<PropertyDef2> getAllPropertieDefs();

	abstract protected PropertyDef2 getPropertyDef(String trigger);

	abstract protected RuntimeProperty getRuntimeProperty(String id, int index);

	abstract protected DependencySpecHolder getDependencySpecHolder();

	abstract protected void resetMask();
	private DependencyDesignConfigs configs = new DependencyDesignConfigs();
	private RestrictionData2 data = new RestrictionData2();
	
	private List<String> mainIds = new ArrayList<>();
	private List<String> options = new ArrayList<>();
	private Map<String, String> mainIdOfOption = new HashMap<>();
	
	private SpecBuilder specBuilder = new SpecBuilder() {
		@Override
		protected boolean isOptionId(String id) {
			return DependencyDesigner.this.isOptionId(id);
		}

		@Override
		protected PropertyDef2 getPropertyDef(String id) {
			return DependencyDesigner.this.getPropertyDef(id);
		}

		@Override
		protected Map<Integer, List<String>> getPriorities() {
			return DependencyDesigner.this.getPriorities();
		}

		@Override
		protected List<Integer> getDefinedPriorities() {
			return DependencyDesigner.this.getDefinedPriorities();
		}

		@Override
		protected String getMainId(String id) {
			return DependencyDesigner.this.getMainId(id);
		}

		@Override
		protected DependencySpecHolder getDependencySpecHolder() {
			return DependencyDesigner.this.getDependencySpecHolder();
		}

		@Override
		protected boolean isMainId(String id) {
			return DependencyDesigner.this.isMainId(id);
		}

		@Override
		protected RestrictionData2 getData() {
			return DependencyDesigner.this.data;
		}
	};
	private PropertyHolder2 defHolder;
	
	public DependencyDesigner(PropertyHolder2 defHolder) {
		this.defHolder = defHolder;
	}

	public void init() {
		defHolder.addListener(new PropertyDefHolderListener() {
			@Override
			public void onChange(String id, String field, Object value, Object prevValue) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAdd(String id) {
				registerNewId(defHolder.get(id));
			}

			@Override
			public void onRemove(String id, String replacedId) {
				// TODO Auto-generated method stub
				
			}
		});
		
		defHolder.getProperties().forEach(def -> {
			registerNewId(def);
		});
	}
	
	private void registerNewId(PropertyDef2 def) {
		if (def.isList()) {
			options.addAll(def.getOptionIds());
			def.getOptionIds().forEach(option -> {
				mainIdOfOption.put(option, def.getId());
			});
		}
		mainIds.add(def.getId());
	}
	
	public DependencyDesignConfig getDependencyDesignConfig(String name) {
		return configs.get(name);
	}

	protected boolean isMainId(String id) {
		return this.mainIds.contains(id);
	}

	protected String getMainId(String id) {
		if (this.isMainId(id)) {
			return id;
		}
		return mainIdOfOption.get(id);
	}

	public List<Integer> getDefinedPriorities() {
		List<Integer> ret = new ArrayList<>();
		
		for (String id : this.getUsedIds()) {
			String mainId = this.getMainId(id);
			
			int priority = Integer.valueOf(this.getPriority(mainId));
			if (!ret.contains(priority)) {
				ret.add(priority);
			}
		}
		Collections.sort(ret, Comparator.reverseOrder());
		
		return ret;
	}

	private Set<String> getUsedIds() {
		return data.getUserdIds();
	}

	private int getPriority(String id) {
		return this.data.getPriority(id);
	}

	public Map<Integer, List<String>> getPriorities() {
		//List<KeyValue> ret = new ArrayList<>();
		Map<Integer, List<String>> ret = new HashMap<>();
		
		for (String id : this.getUsedIds()) {
			//ret.add(new KeyValue(id, String.valueOf(this.getPriority(id))));
			String mainId = this.getMainId(id);
			int priority = Integer.valueOf(this.getPriority(mainId));
			if (!ret.containsKey(priority)) {
				ret.put(priority, new ArrayList<String>());
			}
			if (!ret.get(priority).contains(mainId)) {
				ret.get(priority).add(mainId);
			}
		}
		return ret;
	}

	protected boolean isOptionId(String id) {
		return this.options.contains(id);
	}

	public List<String> getConfigList() {
		return new ArrayList<String>(this.configs.keySet());
	}

	public String updateConfig(String name, String triggers, String targets) {
		if (configs.containsKey(name)) {
			this.configs.get(name).update(triggers, targets);
		}
		else {
			this.configs.put(name, new DependencyDesignConfig(triggers.split(","), targets.split(",")));
		}
		return null;
	}

	public RestrictionMatrix getMatrix(String triggers, String targets) {
		RestrictionMatrix matrix = createMatrix();	
		//matrix.xTitle = Arrays.asList(triggers.split(","));
		//matrix.yTitle = Arrays.asList(targets.split(","));
		matrix.setTriggers(new HashSet<String>(Arrays.asList(triggers.split(","))));
		matrix.setTargets(new HashSet<String>(Arrays.asList(targets.split(","))));
		matrix.initValue();
	//	matrix.collectId();
		return matrix;
	}

	private RestrictionMatrix createMatrix() {
		RestrictionMatrix matrix = new RestrictionMatrix() {

			@Override
			protected DependencySpecHolder getDependencySpecHolder() {
				return DependencyDesigner.this.getDependencySpecHolder();
			}

			@Override
			protected void resetMask() {
				DependencyDesigner.this.resetMask();	
			}

			@Override
			protected RuntimeProperty getRuntimeProperty(String id, int index) {
				return DependencyDesigner.this.getRuntimeProperty(id, index);
			}

			@Override
			protected RuntimeProperty getRuntimeProperty(String id) {
				return DependencyDesigner.this.getRuntimeProperty(id);
			}

			@Override
			protected PropertyDef2 getPropertyDef(String trigger) {
				return DependencyDesigner.this.getPropertyDef(trigger);
			}

			@Override
			protected List<PropertyDef2> getAllPropertieDefs() {
				return DependencyDesigner.this.getAllPropertieDefs();
			}
			
		};
		matrix.setData(this.data);
		return matrix;
	}

	public void setSpecValue(String trigger, String target, String value) {
		this.data.setValue(trigger, target, value);
//		this.data.addPriorityIfNotExists(this.getMainId(trigger));
//		this.data.addPriorityIfNotExists(this.getMainId(target));
		buildSpec();
	}

	public void buildSpec() {
		this.specBuilder.buildSpec();
		this.resetMask();
	}

	public void setSpecEnabled(String trigger, String target, Boolean enabled) {
		this.data.set(trigger, target, enabled);
//		this.data.addPriorityIfNotExists(this.getMainId(trigger));
//		this.data.addPriorityIfNotExists(this.getMainId(target));
		buildSpec();
	}

	public void setPriority(String id, Integer value) {
		this.data.setPriority(id, value);
	}

	private String getFilename(String folder) {
		return folder + "/" + FILENAME;
	}
	private String getConfigFilename(String folder) {
		return folder + "/" + CONFIGFILENAME;
	}
	public void save(String folder) {
		new JsonPersistent().saveJson(this.data, getFilename(folder));
		new JsonPersistent().saveJson(this.configs, getConfigFilename(folder));
	}

	public void load(String folder) {
		this.data = new JsonPersistent().loadJson(RestrictionData2.class, getFilename(folder));
		this.configs = new JsonPersistent().loadJson(DependencyDesignConfigs.class, getConfigFilename(folder));
		init();
	}
	
}
