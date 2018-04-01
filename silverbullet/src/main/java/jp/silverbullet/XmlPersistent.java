package jp.silverbullet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.io.FileUtils;

public class XmlPersistent<T> {

	public void save(T content, String filename, Class<T> cls) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(cls);
	    Marshaller m = context.createMarshaller();
	    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    
	    if (new File(filename).exists()) {
	    	new File(filename).delete();
	    }
	    // Write to File
	    m.marshal(content, new File(filename));
	}
	
	public T load(String filename, Class<T> cls) throws IOException {
		File file = FileUtils.getFile(filename);
		String input = FileUtils.readFileToString(file, "UTF-8");
		return loadFromXml(input, cls);

	}
	
	public T loadFromXml(String xml, Class<T> cls) throws FileNotFoundException {
		StringReader sr = new StringReader(xml);
		T content = JAXB.unmarshal(sr, cls);
		return content;
	}
}
