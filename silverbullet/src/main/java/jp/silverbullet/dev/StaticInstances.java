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
	private static final String NO_DEVICE = "NO_DEVICE";

	private Map<String, BuilderModelImpl> builderModels;// = new ArrayList<>();
	private Map<String, BuilderModelImpl> runtimeModels;
	
	public StaticInstances() {
		builderModels = new HashMap<>();// = new ArrayList<>();
		runtimeModels = new HashMap<>();		
	}
	
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

	public void load() {
		createTmpFolderIfNotExists();
		for (File file : new File(PERSISTENT_FOLDER).listFiles()) {
			String filename = file.getAbsolutePath();
			try {
				BuilderModelImpl model = loadAfile(filename, NO_DEVICE);
				builderModels.put(new File(filename).getName().replace(".zip", ""), model);
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
			}
			
		}
//		currentFilename = filename;

	}

	private synchronized BuilderModelImpl loadAfile(String filename, String device) throws IOException {
		if (filename.endsWith(".zip")) {
			FileUtils.cleanDirectory(new File(TMP_FOLDER));
			Zip.unzip(filename, StaticInstances.TMP_FOLDER);
			BuilderModelImpl model = new BuilderModelImpl();
			model.load(StaticInstances.TMP_FOLDER);			
			new SvClientHandler(device, model);
			return model;
		}
		throw new IOException();
	}

	public BuilderModelImpl getBuilderModel(String app) {
		return builderModels.get(app);
	}

	public BuilderModelImpl getBuilderModel(String app, String device) {
		BuilderModelImpl model = runtimeModels.get(device);
		return model;
	}

	private synchronized BuilderModelImpl generateModel(String app, String device) {
		try {
			BuilderModelImpl r = this.loadAfile(PERSISTENT_FOLDER + "/" + app + ".zip", device);
			runtimeModels.put(device, r);
			System.out.println("Runtime Model was generated. :" + 
					device + " @" + Thread.currentThread().getName() + ":" + runtimeModels.hashCode());
			return r;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void generateSource(String app) {
		String info = getBuilderModel(app).getSourceInfo();
		String folder = info.split(";")[0];
		String packageName = info.split(";")[1];
		new PropertySourceGenerator(getBuilderModel(app).getPropertiesHolder2()).generate(folder, packageName);
		new RegisterSourceGenerator(getBuilderModel(app).getRegisterSpecHolder()).
			exportFile(folder, packageName);
	}

	public void newApplication() {
		BuilderModelImpl model = new BuilderModelImpl();
		builderModels.put(String.valueOf(System.currentTimeMillis()), model);
		
//		new SvClientHandler(model);
		
	}


	public List<String> getApplications() {
		return new ArrayList<String>(this.builderModels.keySet());
	}

	public void createDevice(String app, String device) {
		generateModel(app, device);
	}

	public void deleteDevice(String app, String device) {
		this.runtimeModels.remove(device);
	}

}
