package jp.silverbullet.web;

import java.util.List;

import com.google.api.services.drive.model.File;

public class FileUploadMessage  {

	public List<File> files;
	public FileUploadMessage() {}
	public FileUploadMessage(String fileName, List<com.google.api.services.drive.model.File> files) {
		this.files = files;
	}

}
