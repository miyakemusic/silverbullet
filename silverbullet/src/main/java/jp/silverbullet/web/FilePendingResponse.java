package jp.silverbullet.web;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.drive.model.File;

public class FilePendingResponse {
	public FilePendingResponse() {};
	public FilePendingResponse(List<File> files) {
		for (File f : files) {
			if (!f.getName().startsWith("downloaded.")) {
				list.add(f);
			}
		}
	}

	public List<com.google.api.services.drive.model.File> list = new ArrayList<>();
}
