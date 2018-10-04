package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import jp.silverbullet.register.RegisterMapModel;

public class StaticInstances {
	public static final String TMP_FOLDER = "sv_tmp";
	
	private static RegisterMapModel registerMapModel = null;
	private static String prevFilename = "";
	
	public static BuilderModel getBuilderModel() {
		return BuilderModelImpl.getInstance();
	}

	public static RegisterMapModel getRegisterMapModel() {
		if (registerMapModel == null) {
			registerMapModel = new RegisterMapModel(getBuilderModel());
			BuilderModelImpl.getInstance().setDeviceDriver(registerMapModel);
		}
		return registerMapModel;
	}

	public static void save() {
		if (!Files.exists(Paths.get(StaticInstances.TMP_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(StaticInstances.TMP_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		getBuilderModel().save(StaticInstances.TMP_FOLDER);
		Zip.zip(StaticInstances.TMP_FOLDER, prevFilename);
	}

	public static void load(String filename) {
		prevFilename = filename;
		if (Files.exists(Paths.get(filename))) {
			Zip.unzip(filename, StaticInstances.TMP_FOLDER);
			getBuilderModel().load(StaticInstances.TMP_FOLDER);
		}
		
	}

}
