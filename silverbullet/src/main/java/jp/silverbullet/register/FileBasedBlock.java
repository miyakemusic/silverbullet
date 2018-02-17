package jp.silverbullet.register;

import java.io.Serializable;

public class FileBasedBlock implements Serializable {
	public String path;
	public byte[] data;
	
	public FileBasedBlock() {
		
	}
	
	public FileBasedBlock(String path, byte[] data) {
		this.path = path;
		this.data = data;
	}
}
