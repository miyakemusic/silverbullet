package jp.silverbullet.uidesigner.widgets;

import java.io.ByteArrayInputStream;
import com.sun.jersey.core.util.Base64;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;

public class SvImageFx extends SvPropertyWidgetFx {

	private ImageView imageView;

	public SvImageFx(SvProperty prop, DependencyInterface dependencyInterface, Description style, Description description) {
		super(prop, dependencyInterface);

		this.getChildren().add(imageView = new ImageView());
		
	}

	@Override
	public void onValueChanged(String id, String value) {
		ByteArrayInputStream rocketInputStream = new ByteArrayInputStream(Base64.decode(value));

		Image image = new Image(rocketInputStream);
//		imageView.setImage(null);
		imageView.setImage(image);
		
	}

	@Override
	public void onEnableChanged(String id, boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFlagChanged(String id, Flag flag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListMaskChanged(String id, String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTitleChanged(String id, String title) {
		// TODO Auto-generated method stub

	}

}
