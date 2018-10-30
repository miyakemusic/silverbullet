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
	
	private static StaticInstances instance;
	private RegisterMapModel registerMapModel = null;
	private PropertyListModel propertyListModel = null;
	
	private BuilderModel builderModel;
	private String currentFilename = "";

	public static StaticInstances getInstance() {
		if (instance == null) {
			instance = new StaticInstances();
		}
		return instance;
	}
	
	public SvSimulator getSimulator() {
		return simulator;
	}

	private SvSimulator simulator;
	
	public BuilderModel getBuilderModel() {
		return builderModel;
	}

	private StaticInstances() {
		builderModel = new BuilderModelImpl();
		createOtherInstances();
	}

	private void createOtherInstances() {
		registerMapModel = new RegisterMapModel(builderModel);
		propertyListModel = new PropertyListModel(getBuilderModel().getPropertyHolder());
		
		getBuilderModel().setDeviceDriver(registerMapModel);
		
		simulator = new SvSimulator() {
			@Override
			protected void writeIo(long address, BitSet data, BitSet mask) {
			}

			@Override
			protected void writeBlock(long address, byte[] data) {
			}
		};
		registerMapModel.addSimulator(simulator);
		registerMapModel.addListener(builderModel.getTestRecorder());
		
	}
	
	public RegisterMapModel getRegisterMapModel() {
		return registerMapModel;
	}

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
			createOtherInstances();
		}
		else {
			builderModel.loadDefault();
		}
		registerMapModel.update();
	}

	public PropertyListModel getPropertyListModel() {
		return propertyListModel;
	}

	public void generateSource() {
		String path = getBuilderModel().getUserApplicationPath();
		new JavaFileGenerator(getBuilderModel().getAllProperties()).generate(path);
		new RegisterIoGenerator(getBuilderModel().getRegisterProperty(), path).generate(path);

	}

}
