package jp.silverbullet.uidesigner.widgets;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;

public abstract class SvAbstractTitledWidgetFx extends SvPropertyWidgetFx {
	
	private Label unit;
	private Label title;

	@Override
	public void onTitleChanged(String id, String title) {
		this.title.setText(title);
	}

	public SvAbstractTitledWidgetFx(SvProperty prop,
			DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener);
		
		HBox hbox = this;//new HBox();
		
		if (!description.getValue(Description.TITLEVISIBLE).equalsIgnoreCase("false")) {
			title = new Label(prop.getTitle() + ":");
			if (prop.getTitle().length() > 0) {
				title.setMinWidth(100);
			}
			hbox.getChildren().add(title);
			title.setStyle("-fx-alignment: center-left;");
		}

		if (description.isDefined(Description.TITLE_WIDTH)) {
			double titleWidth = Double.valueOf(description.getValue(Description.TITLE_WIDTH));
			title.setMinWidth(titleWidth);
		}
		
		hbox.getChildren().add(createContent(prop, widgetListener, description));
		unit = new Label(prop.getUnit());
		hbox.getChildren().add(unit);
	}

	abstract protected Node createContent(SvProperty prop,
			DependencyInterface m_svPanelHandler, Description description);
}


