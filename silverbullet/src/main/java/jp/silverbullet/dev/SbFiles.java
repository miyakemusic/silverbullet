package jp.silverbullet.dev;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jp.silverbullet.core.Zip;

public class SbFiles {
	public static final String PERSISTENT_FOLDER = "./persistent";
	public static final String TMP_FOLDER = PERSISTENT_FOLDER + "/sv_tmp";
	
	public void createTmpFolderIfNotExists() {
		if (!Files.exists(Paths.get(PERSISTENT_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(PERSISTENT_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (!Files.exists(Paths.get(TMP_FOLDER))) {
			try {
				Files.createDirectory(Paths.get(TMP_FOLDER));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	public BuilderModelImpl loadAfile(String userid, String appName, String device) throws IOException {
		String filename = SbFiles.PERSISTENT_FOLDER + "/" + userid + "/" + appName + ".zip";
		if (filename.endsWith(".zip")) {
			createTmpFolderIfNotExists();
			FileUtils.cleanDirectory(new File(TMP_FOLDER));
			Zip.unzip(filename, TMP_FOLDER);
			BuilderModelImpl model = new BuilderModelImpl() {
				@Override
				protected String getAccessToken() {
					//return BuilderModelHolder.this.getAccessToken(userid);
					return null;
				}	
			};
			model.load(TMP_FOLDER);			
			model.setDevice(device);
			new SvClientHandler(userid, device, model);
			return model;
		}
		throw new IOException();
	}

	public void createFolderIfNotExists(String folder) {
		folder = SbFiles.PERSISTENT_FOLDER + "/" + folder;
		if (!Files.exists(Paths.get(folder))) {
			try {
				Files.createDirectory(Paths.get(folder));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static abstract class WalkThrough {
		public WalkThrough() {
			for (File file : new File(PERSISTENT_FOLDER).listFiles()) {
				if (file.isDirectory()) {
					if (file.getName().equals(new File(TMP_FOLDER).getName())) {
						continue;
					}
					String userid = file.getName();
					List<String> list = new ArrayList<>();
					for (File file2 : file.listFiles()) {
						list.add(file2.getAbsolutePath());
					}
					handleUserId(userid, list);
				}			
			}
		}
		protected abstract void handleUserId(String userid, List<String> list);
	}
	public String getStorePath(String userid) {
		return PERSISTENT_FOLDER + "/" + userid + "/store/";
	}
	
	public List<String> getStorePaths(String userid) {
		List<String> ret = new ArrayList<>();
		for (File file : new File(PERSISTENT_FOLDER + "/" + userid + "/store/").listFiles()) {
			ret.add(file.getAbsolutePath());
		}
		return ret;
	}
};