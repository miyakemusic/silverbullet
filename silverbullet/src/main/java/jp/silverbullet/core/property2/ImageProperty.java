package jp.silverbullet.core.property2;

import org.apache.commons.codec.binary.Base64;


public class ImageProperty {

	private byte[] image;
	public ImageProperty() {}
	public ImageProperty(byte[] img) {
		this.image = img;
	}
	public byte[] getImage() {
		return image;
	}
	@Override
	public String toString() {
		return "data:image/png;base64," + new String(Base64.encodeBase64(image));
	}
	
	
}
