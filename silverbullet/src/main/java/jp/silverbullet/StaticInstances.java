package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import jp.silverbullet.sourcegenerator.PropertySourceGenerator;
import jp.silverbullet.sourcegenerator.RegisterSourceGenerator;

public class StaticInstances {
	public static final String TMP_FOLDER = "./sv_tmp";
	
	private static StaticInstances instance;
	
	private List<BuilderModelImpl> builderModels = new ArrayList<>();
	private String currentFilename = "";

	public static StaticInstances getInstance() {
		if (instance == null) {
			instance = new StaticInstances();
		}
		return instance;
	}
		
	public BuilderModelImpl getBuilderModel(int index) {
		return builderModels.get(index);
	}

	public void save() {
		createTmpFolderIfNotExists();
		getBuilderModel(0).save(StaticInstances.TMP_FOLDER);
		Zip.zip(StaticInstances.TMP_FOLDER, currentFilename);
	}

	private static synchronized void createTmpFolderIfNotExists() {
		if (!Files.exists(Paths.get(StaticInstances.TMP_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(StaticInstances.TMP_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void load(String filename) {
		createTmpFolderIfNotExists();
		currentFilename = filename;
		if (Files.exists(Paths.get(filename))) {
			Zip.unzip(filename, StaticInstances.TMP_FOLDER);
			getBuilderModel().load(StaticInstances.TMP_FOLDER);
		}
	}

	public BuilderModelImpl getBuilderModel() {
		return this.getBuilderModel(0);
	}

	public void generateSource() {
		String info = getBuilderModel(0).getSourceInfo();
		String folder = info.split(";")[0];
		String packageName = info.split(";")[1];
		new PropertySourceGenerator(getBuilderModel().getPropertiesHolder2()).generate(folder, packageName);
		new RegisterSourceGenerator(getBuilderModel().getRegisterSpecHolder()).
			exportFile(folder, packageName);
	}

	public void createInstances(int instanceCount, long mainThreadId) {
		for (int i = 0; i < instanceCount; i++) {
			BuilderModelImpl builderModel = new BuilderModelImpl();
			this.builderModels.add(builderModel);
		}
	}

	public List<BuilderModelImpl> getBuilderModels() {
		return this.builderModels;
	}

}
