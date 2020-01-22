package jp.silverbullet.dev.selfbuild;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jp.silverbullet.core.JsonPersistent;

public class SelfBuilder {
	
	private static final String SELF_BUILDER_JSON = "SelfBuilder.json";
	private BuildInfo info = new BuildInfo();

	public void save(String folder) {
		try {
			new JsonPersistent().saveJson(info, folder + "/" + SELF_BUILDER_JSON);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void load(String folder) {
		try {
			info = new JsonPersistent().loadJson(BuildInfo.class, folder + "/" + SELF_BUILDER_JSON);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setInfo(String path, String packageName) {
		this.info.path = path;
		this.info.packageName = packageName;
	}

	public String getPath() {
		return this.info.path;
	}

	public String getPackage() {
		return this.info.packageName;
	}

	public void build() {
		try {
			Runtime.getRuntime().exec("java -jar openti-jar-with-dependencies.jar 8081");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}
	
}
