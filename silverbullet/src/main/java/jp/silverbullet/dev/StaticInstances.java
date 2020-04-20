package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import jp.silverbullet.core.Zip;
import jp.silverbullet.dev.sourcegenerator.PropertySourceGenerator;
import jp.silverbullet.dev.sourcegenerator.RegisterSourceGenerator;

public class StaticInstances {
	public static final String PERSISTENT_FOLDER = "./persistent";
	public static final String TMP_FOLDER = PERSISTENT_FOLDER + "/sv_tmp";

	private Map<String, BuilderModelImpl> builderModels = new HashMap<>();// = new ArrayList<>();
//	private String currentFilename = "";
		
	public void save() {
		createTmpFolderIfNotExists();

		for (String name : builderModels.keySet()) {
			try {
				FileUtils.cleanDirectory(new File(TMP_FOLDER));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			builderModels.get(name).save(StaticInstances.TMP_FOLDER);
			Zip.zip(StaticInstances.TMP_FOLDER, PERSISTENT_FOLDER + "/" + name + ".zip");		
		}
	}

	private static synchronized void createTmpFolderIfNotExists() {
		if (!Files.exists(Paths.get(StaticInstances.PERSISTENT_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(StaticInstances.PERSISTENT_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!Files.exists(Paths.get(StaticInstances.TMP_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(StaticInstances.TMP_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void load() throws IOException {
		createTmpFolderIfNotExists();
		for (File file : new File(PERSISTENT_FOLDER).listFiles()) {
			if (file.getName().endsWith(".zip")) {
				FileUtils.cleanDirectory(new File(TMP_FOLDER));
				Zip.unzip(file.getAbsolutePath(), StaticInstances.TMP_FOLDER);
				BuilderModelImpl model = new BuilderModelImpl();
				model.load(StaticInstances.TMP_FOLDER);	
				builderModels.put(file.getName().replace(".zip", ""), model);
				
			}
		}
//		currentFilename = filename;

	}

	public BuilderModelImpl getBuilderModel() {
		return this.builderModels.values().iterator().next();
	}

	public void generateSource() {
		String info = getBuilderModel().getSourceInfo();
		String folder = info.split(";")[0];
		String packageName = info.split(";")[1];
		new PropertySourceGenerator(getBuilderModel().getPropertiesHolder2()).generate(folder, packageName);
		new RegisterSourceGenerator(getBuilderModel().getRegisterSpecHolder()).
			exportFile(folder, packageName);
	}

	public void newApplication() {
		BuilderModelImpl model = new BuilderModelImpl();
		builderModels.put(String.valueOf(System.currentTimeMillis()), model);
	}

	public List<String> getApplications() {
		return new ArrayList<String>(this.builderModels.keySet());
	}

}
