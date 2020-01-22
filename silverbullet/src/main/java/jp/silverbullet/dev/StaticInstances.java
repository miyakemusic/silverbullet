package jp.silverbullet.dev;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jp.silverbullet.core.Zip;
import jp.silverbullet.dev.sourcegenerator.PropertySourceGenerator;
import jp.silverbullet.dev.sourcegenerator.RegisterSourceGenerator;

public class StaticInstances {
	public static final String TMP_FOLDER = "./sv_tmp";

	private BuilderModelImpl builderModel = new BuilderModelImpl();// = new ArrayList<>();
	private String currentFilename = "";
		
	public void save() {
		createTmpFolderIfNotExists();
		getBuilderModel().save(StaticInstances.TMP_FOLDER);
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
		return this.builderModel;
	}

	public void generateSource() {
		String info = getBuilderModel().getSourceInfo();
		String folder = info.split(";")[0];
		String packageName = info.split(";")[1];
		new PropertySourceGenerator(getBuilderModel().getPropertiesHolder2()).generate(folder, packageName);
		new RegisterSourceGenerator(getBuilderModel().getRegisterSpecHolder()).
			exportFile(folder, packageName);
	}

}
