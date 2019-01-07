package jp.silverbullet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.BitSet;

import jp.silverbullet.property.editor.PropertyListModel;
import jp.silverbullet.register2.RegisterSourceGenerator;
import obsolute.register.SvSimulator;

public class StaticInstances {
	public static final String TMP_FOLDER = "./sv_tmp";
	
	private static StaticInstances instance;
	private PropertyListModel propertyListModel = null;
	
	private BuilderModelImpl builderModel;
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
	
	public BuilderModelImpl getBuilderModel() {
		return builderModel;
	}

	private StaticInstances() {
		builderModel = new BuilderModelImpl();
		createOtherInstances();
	}

	private void createOtherInstances() {
		propertyListModel = new PropertyListModel(getBuilderModel().getPropertyHolder());
		
		simulator = new SvSimulator() {
			@Override
			protected void writeIo(long address, BitSet data, BitSet mask) {
			}

			@Override
			protected void writeBlock(long address, byte[] data) {
			}
		};

//		builderModel.getRegisterMapModel().addSimulator(simulator);
//		simulator.setDevice(builderModel.getRegisterMapModel());
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
//		builderModel.getRegisterMapModel().update();
	}

	public PropertyListModel getPropertyListModel() {
		return propertyListModel;
	}

	public void generateSource() {
		String path = getBuilderModel().getUserApplicationPath();
		new JavaFileGenerator(getBuilderModel().getAllProperties()).generate(path);
		new RegisterIoGenerator(getBuilderModel().getRegisterProperty(), path).generate(path);
		new RegisterSourceGenerator(getBuilderModel().getRegisterSpecHolder(), getBuilderModel().getUserApplicationPath(), "UserRegister").
			exportFile("src/main/java/" + path);
	}

}
