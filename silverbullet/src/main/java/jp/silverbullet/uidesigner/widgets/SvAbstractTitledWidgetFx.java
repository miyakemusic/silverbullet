package jp.silverbullet.uidesigner.widgets;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.silverbullet.SvProperty;
import jp.silverbullet.dependency.engine.DependencyInterface;

public abstract class SvAbstractTitledWidgetFx extends SvPropertyWidgetFx {
	
	private Label unit;
	private Label title;
	private Description description;

	@Override
	public void onTitleChanged(String id, String title) {
		this.title.setText(title);
	}

	public SvAbstractTitledWidgetFx(SvProperty prop,
			DependencyInterface widgetListener, Description description) {
		super(prop, widgetListener);
		this.description = description;
		Pane pane = null;// = new VBox();
		if (description.isDefined(Description.TITLESTYLE)) {
			String titleStyle = description.getValue(Description.TITLESTYLE);
			if (titleStyle.equals("Horizontal")) {
				pane = new HBox();
			}
			else if (titleStyle.equals("Vertical")) {
				pane = new VBox();
			}
			else if (titleStyle.equals("Border")) {
				
			}
			else {
				pane = new HBox();
			}
		}
		else {
			pane = new HBox();
		}
		this.getChildren().add(pane);
		
		if (!description.getValue(Description.TITLEVISIBLE).equalsIgnoreCase("false")) {
			title = new Label(prop.getTitle());
			if (!prop.getUnit().isEmpty()) {
				title.setText(title.getText() + " (" + prop.getUnit() + ")");
			}
			title.setText(title.getText() + ":");
			if (prop.getTitle().length() > 0) {
				title.setMinWidth(100);
			}
			pane.getChildren().add(title);
			title.setStyle("-fx-alignment: center-left;");
		}

		if (description.isDefined(Description.TITLE_WIDTH)) {
			double titleWidth = Double.valueOf(description.getValue(Description.TITLE_WIDTH));
			title.setMinWidth(titleWidth);
		}
		
		pane.getChildren().add(createContent(prop, widgetListener, description));
//		unit = new Label(prop.getUnit());
//		pane.getChildren().add(unit);
	}

	public Description getDescription() {
		return description;
	}

	abstract protected Node createContent(SvProperty prop,
			DependencyInterface m_svPanelHandler, Description description);
}


