package jp.silverbullet.web.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import jp.silverbullet.JsonPersistent;
import jp.silverbullet.StaticInstances;
import jp.silverbullet.web.Pair;

public class UiLayoutHolder {

	private PropertyGetter propertyGetter;
	private Map<String, UiLayout> layouts = new LinkedHashMap<>();
	private UiLayout currentUi;// = new UiLayout();
	private String currentFilename;
	private Set<UiLayoutListener> listeners = new HashSet<>();
	private CustomProperties customProperties = new CustomProperties();
	
	private UiLayoutListener listener = new UiLayoutListener() {
		@Override
		public void onLayoutChange(String div, String filename) {
			for (UiLayoutListener listener : listeners) {
				listener.onLayoutChange(div, currentFilename);
			}
		}
	};
	
	public UiLayoutHolder(PropertyGetter propertyGetter) {
		this.propertyGetter = propertyGetter;
//		layouts.put("default.ui", new UiLayout());
	}
	
	public void load(String folder) {
		this.layouts.clear();
		try {
			Files.list(Paths.get(folder))./*filter(file -> file.getFileName().endsWith(".ui")).*/forEach(p -> {
				String filename = p.toAbsolutePath().toString();
				if (!filename.endsWith(".ui")) {
					return;
				}
				UiLayout tmpLayout = loadJson(UiLayout.class, filename);
				tmpLayout.setPropertyGetter(this.propertyGetter);
				tmpLayout.collectDynamicArrays();
				tmpLayout.addListener(listener);
				layouts.put(p.getFileName().toFile().getName(), tmpLayout);
			});
			
//			if (layouts.size() == 0) {
//				layouts.put("default.ui", new UiLayout());
//			}
			this.currentFilename = layouts.keySet().iterator().next();
			this.currentUi = layouts.get(this.currentFilename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private UiLayout loadJson(Class<UiLayout> class1, String filename) {
		try {
			return new JsonPersistent().loadJson(class1, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	

	public void save(String folder) {
		this.layouts.forEach((filename, uilayout) -> saveJson(uilayout, folder + "/" + filename));
	}
		
	private void saveJson(UiLayout uilayout, String filename) {
		try {
			new JsonPersistent().saveJson(uilayout, filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};
	}

	public UiLayout getCurrentUi() {
		return this.currentUi;
	}

	public void changeId(String prevId, String newId) {
		this.layouts.forEach((filename, uiLayout) -> uiLayout.changeId(prevId, newId));
	}

	public List<String> getFileList() {
		List<String> ret = new ArrayList<String>(this.layouts.keySet());
//		ret.remove(this.currentFilename);
//		ret.add(0, this.currentFilename);
		return ret;
	}

	private UiLayout createFile(String filename) {
		UiLayout uiLayout = new UiLayout(propertyGetter);
		uiLayout.addListener(listener);
		this.layouts.put(filename, uiLayout);
		return uiLayout;
	}

	public UiLayout createNewFile(String filename) {
		if (filename.isEmpty()) {
			filename = Calendar.getInstance().getTimeInMillis() + ".ui";
		}
		else if (!filename.toUpperCase().endsWith(".UI")){
			Pattern illegalFileNamePattern = Pattern.compile("[(\\|/|:|\\*|?|\"|<|>|\\\\|)]");
			illegalFileNamePattern.matcher(filename).replaceAll("-");
			filename = filename + ".ui";
		}
		return this.createFile(filename);
//		return StaticInstances.getInstance().getBuilderModel().createUiFile(filename);
	}
	
	public UiLayout switchFile(String filename) {
		this.currentFilename = filename;
		this.currentUi = this.layouts.get(filename);
		return this.currentUi;
	}

	public boolean removeFile(String filename) {
		if (this.layouts.size() == 1) {
			return false;
		}
		this.layouts.get(filename).removeListener(listener);
		this.layouts.remove(filename);
		
		if (this.currentFilename.equals(filename)) {
			this.currentFilename = this.layouts.keySet().iterator().next();
			this.currentUi = this.layouts.get(currentFilename);
		}
		return true;
	}

	public void createDefault() {
		createFile("default.ui");
		this.switchFile("default.ui");
	}

	public void addListener(UiLayoutListener uiLayoutListener) {
		this.listeners.add(uiLayoutListener);
	}
	
	public void removeListener(UiLayoutListener uiLayoutListener) {
		this.listeners.remove(uiLayoutListener);
	}

	public String getCurrentFilename() {
		return currentFilename;
	}

	public List<String> getStyleClasses(String type) {
		return Arrays.asList("tabs-top", "tabs-bottom", "itemBox", 
				"BigGrid", "noborder", "fontVeryBig", "fontBig", "fontMedium", "fontSmall");
	}

	public Map<String, List<Pair>> getCustomDefinitions() {
		return customProperties.getMap();
	}

	
}