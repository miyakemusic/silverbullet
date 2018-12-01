package jp.silverbullet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.silverbullet.web.ui.PropertyGetter;
import jp.silverbullet.web.ui.UiLayout;

public class UiLayoutHolder {

	private PropertyGetter propertyGetter;
	private Map<String, UiLayout> layouts = new HashMap<>();
	private UiLayout currentUi;// = new UiLayout();
	private String currentFilename;
	
	public UiLayoutHolder(PropertyGetter propertyGetter) {
		this.propertyGetter = propertyGetter;
//		layouts.put("default.ui", new UiLayout());
	}
	
	public void load(String folder) {
		try {
			Files.list(Paths.get(folder))./*filter(file -> file.getFileName().endsWith(".ui")).*/forEach(p -> {
				String filename = p.toAbsolutePath().toString();
				if (!filename.endsWith(".ui")) {
					return;
				}
				UiLayout tmpLayout = loadJson(UiLayout.class, filename);
				tmpLayout.setPropertyGetter(this.propertyGetter);
				
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
		return new JsonPersistent().loadJson(class1, filename);
	}	

	public void save(String folder) {
		this.layouts.forEach((filename, uilayout) -> saveJson(uilayout, folder + "/" + filename));
	}
		
	private void saveJson(UiLayout uilayout, String filename) {
		new JsonPersistent().saveJson(uilayout, filename);;
	}

	public UiLayout getCurrentUi() {
		return this.currentUi;
	}

	public void changeId(String prevId, String newId) {
		this.layouts.forEach((filename, uiLayout) -> uiLayout.changeId(prevId, newId));
	}

	public List<String> getFileList() {
		List<String> ret = new ArrayList<String>(this.layouts.keySet());
		ret.remove(this.currentFilename);
		ret.add(0, this.currentFilename);
		return ret;
	}

	public List<String> createNewFile(String filename) {
		this.layouts.put(filename, new UiLayout(propertyGetter));
		return getFileList();
	}

	public UiLayout switchFile(String filename) {
		this.currentFilename = filename;
		this.currentUi = this.layouts.get(filename);
		return this.currentUi;
	}

	public void removeFile(String filename) {
		this.layouts.remove(filename);
	}

	public void createDefault() {
		createNewFile("default.ui");
		this.switchFile("default.ui");
	}
}