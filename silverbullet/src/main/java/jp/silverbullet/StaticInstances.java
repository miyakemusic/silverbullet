package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;

import jp.silverbullet.property.editor.PropertyListModel;
import jp.silverbullet.register.RegisterMapModel;
import jp.silverbullet.register.SvSimulator;

public class StaticInstances {
	public static final String TMP_FOLDER = "./sv_tmp";
	
	private static RegisterMapModel registerMapModel = null;
	private static PropertyListModel propertyListModel = null;
	
	private static String prevFilename = "";

	public static SvSimulator getSimulator() {
		return simulator;
	}

	private static SvSimulator simulator;
	
	public static BuilderModel getBuilderModel() {
		return BuilderModelImpl.getInstance();
	}

	public static RegisterMapModel getRegisterMapModel() {
		if (registerMapModel == null) {
			registerMapModel = new RegisterMapModel(getBuilderModel());
			BuilderModelImpl.getInstance().setDeviceDriver(registerMapModel);
			
			simulator = new SvSimulator() {
				@Override
				protected void writeIo(long address, BitSet data, BitSet mask) {

				}

				@Override
				protected void writeBlock(long address, byte[] data) {

				}
			};
			registerMapModel.addSimulator(simulator);
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
		registerMapModel.update();
		
	}

	public static PropertyListModel getPropertyListModel() {
		if (propertyListModel == null) {
			propertyListModel = new PropertyListModel(getBuilderModel().getPropertyHolder());
		}
		return propertyListModel;
	}

	public static void generateSource() {
		String path = getBuilderModel().getUserApplicationPath();
		new JavaFileGenerator(getBuilderModel().getAllProperties()).generate(path);
		new RegisterIoGenerator(getBuilderModel().getRegisterProperty(), path).generate(path);

	}

}
