package jp.silverbullet.property2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListDetailElement implements Cloneable {
	
	@Override
	public ListDetailElement clone() {
		try {
			ListDetailElement ret = (ListDetailElement) super.clone();
			return ret;
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ListDetailElement() {
		
	}
	public ListDetailElement(String id) {
		this.id = id;
//		this.value = value;
	}
	
	public ListDetailElement(String id, String title, String comment) {
		this.id = id;
		this.title = title;
		this.comment = comment;
	}
	
	@TableColumn(targetType={PropertyType2.List}, Presentation="ID", Width=200)
	private String id = "ID_";
	
	@TableColumn(targetType={PropertyType2.List}, Presentation="Title", Width=200)
	private String title = "";
	
	@TableColumn(targetType={PropertyType2.List}, Presentation="Comment", Width=200)
	private String comment = "";
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setFieldValue(String paramName, String value) {

	}
};