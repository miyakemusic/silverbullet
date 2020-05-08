package jp.silverbullet.web;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

public class GoogleDrivePost {

	private String type;
	private File file;
	private String base64;
	private String filename;

	public String post(String access_token, String pathWithFilename) {
		String targetFilename = new File(pathWithFilename).getName();
		
		try {
			File file;
			if (base64 != null) {
				DataOutputStream dataOutStream = 
				        new DataOutputStream(
				          new BufferedOutputStream(
				            new FileOutputStream(targetFilename)));
				dataOutStream.write(Base64.decodeBase64(base64.getBytes()));
				dataOutStream.flush();
				dataOutStream.close();
				file = new File(targetFilename);
			}
			else {
				file = new File(this.filename); 
			}
			
			String fileID = SystemResource.googleHandler.postFile(access_token, type, pathWithFilename, file);
			file.delete();
			return fileID;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	public GoogleDrivePost type(String type) {
		this.type = type;
		return this;
	}
	
	public GoogleDrivePost filename(String filename) {
		this.filename = filename;
		return this;
	}
	
	public GoogleDrivePost base64(String base64withHeader) {
		String[] tmp = base64withHeader.split(",");
		
		type = tmp[0].split("[:;]+")[1];
		base64 = tmp[1];	
		return this;
	}

}
