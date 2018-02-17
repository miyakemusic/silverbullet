package jp.silverbullet.uidesigner.pane;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.silverbullet.BuilderModel;
import jp.silverbullet.uidesigner.widgets.Description;

public class SvUniversalPane extends VBox {
	public SvUniversalPane(BuilderModel model, Description style, Description description, final CommonWidgetListener commonListener) {
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				commonListener.onSelect(SvUniversalPane.this);
			}
		});
		
		this.setMinHeight(100);
		this.setMinWidth(100);
		if (!description.isDefined(Description.USER_CODE)) {
			Label label  = new Label("NO SOURCE CODE DEFINED!!!");
			label.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					commonListener.onSelect(SvUniversalPane.this);
				}
			});
			this.getChildren().add(label);
			new CommonPopupFx().create(commonListener, this);
			return;
		}
		String className = model.getUserApplicationPath() + "." + description.getValue(Description.USER_CODE);
		try {
			Class[] types = new Class[] { BuilderModel.class, Description.class, Description.class, CommonWidgetListener.class };
			//Class[] types = new Class[] { String.class, String.class };
			Class cls = Class.forName(className);
			Constructor cons = cls.getConstructor(types);
			Object[] args = new Object[] { model, style, description, commonListener };
			Pane node = (Pane)cons.newInstance(args);

			this.getChildren().add(node);
			
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (NoSuchMethodException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> arg0,
					Parent arg1, Parent arg2) {

				if (arg2 == null) {
					removeListener();
				}
			}
		});
		
		new CommonPopupFx().create(commonListener, this);
	}

	protected void removeListener() {
		// TODO Auto-generated method stub
		
	}

}
