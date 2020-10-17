package jp.silverbullet.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;

public class MyFileUtils {
	public static final String ID_DEF_JSON = "id_def.json";
	public static final String DEPENDENCYSPEC3_XML = "dependencyspec3.xml";
	public static final String UIBUILDER2 = "uibuilder2.json";
	
	private static synchronized void createTmpFolderIfNotExists(String folder) {		
		if (!Files.exists(Paths.get(folder))) {
			try {
				Files.createDirectory(Paths.get(folder));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public static void unzip(String filename, String folder) {
		createTmpFolderIfNotExists(folder);
		try {
			FileUtils.cleanDirectory(new File(folder));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Zip.unzip(filename, folder);
	}
}
