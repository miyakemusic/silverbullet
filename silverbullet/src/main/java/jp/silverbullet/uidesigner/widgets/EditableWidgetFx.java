package jp.silverbullet.uidesigner.widgets;

import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.uidesigner.EditableWidgetListenerFx;
import jp.silverbullet.uidesigner.pane.CommonWidgetListener;

public class EditableWidgetFx extends VBox {

	private EditableWidgetListenerFx listener;
	private SvPropertyWidgetFx widget;

	private CommonWidgetListener commonListener;

	public EditableWidgetFx(final SvPropertyWidgetFx widget) {
		this.widget = widget;

		final ContextMenu contextMenu = new ContextMenu();
		MenuItem edit = new MenuItem("Edit Property");
		MenuItem dependency = new MenuItem("Edit Dependency");
		MenuItem handler = new MenuItem("Edit Handlers");
		MenuItem cut = new MenuItem("Cut");
		MenuItem remove = new MenuItem("Remove");
		final Menu change = new Menu("Change");
		MenuItem style = new MenuItem("Style");
		MenuItem property = new MenuItem("Description");
		 			
		Menu move = new Menu("Move");
		MenuItem up = new MenuItem("Up");
		up.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onMoveUp(EditableWidgetFx.this);
			}
		});
		MenuItem down = new MenuItem("Down");
		down.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				commonListener.onMoveDown(EditableWidgetFx.this);
			}
		});
		move.getItems().addAll(up, down);
			
		 contextMenu.getItems().addAll(edit, dependency, handler, cut, remove, change, style, property, move);

		 edit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				listener.onEdit(widget.getProperty());
			}
		 });
		 cut.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				commonListener.onCut(EditableWidgetFx.this);
			}
		 });
		 remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onRemove(EditableWidgetFx.this);
			}
		 });
		 
		 change.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				//listener.onChangeSelected(option, editableWidget);
			}
		 });
		 
		 dependency.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				listener.onDependency(widget.getProperty());
			}
		 });
		 handler.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onHandlerProperty(widget.getProperty().getId());
			}
		 });
		 
		 this.setOnMousePressed(new EventHandler<MouseEvent>() {
		    @Override
		    public void handle(MouseEvent event) {
		    	commonListener.onSelect(EditableWidgetFx.this);
		        if (event.isSecondaryButtonDown()) {
		            showPopup(widget, change, contextMenu, event);
		        }
		        event.consume();
		    }
		});

		style.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onStyleChange(EditableWidgetFx.this);
			}
		});
		property.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				commonListener.onDescription(EditableWidgetFx.this);
			}
		});
		
//		widget.prefHeightProperty().bind(this.heightProperty());
		this.getChildren().add(widget);
//		
//		this.widthProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable,
//					Number oldValue, Number newValue) {
//
//			}
//		});
		this.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> arg0,
					Parent arg1, Parent arg2) {

				if (arg2 == null) {
					//System.out.println("EditableWidgetFx Removed");
					getChildren().clear();
				}
			}
		});
		
	}
	
	@Override
	public String toString() {
		return this.widget.getProperty().getId();
	}


	public void setOnEditableWidgetListener(EditableWidgetListenerFx listener) {
		this.listener = listener;
	}

	protected void showPopup(SvPropertyWidgetFx widget, Menu change, final ContextMenu contextMenu, MouseEvent event) {
		contextMenu.setStyle("-fx-font-size:16");
		List<String> options = listener.onRequestWidgetChange(widget);
		if (options != null) {
			change.getItems().clear();
			for (final String option : options) {
				MenuItem item = new MenuItem(option);
				change.getItems().add(item);
				item.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						updateWidget(listener, option);
					}
					
				});
			}
		}
		
		contextMenu.show(EditableWidgetFx.this, event.getScreenX(), event.getScreenY());
	}

	protected void updateWidget(EditableWidgetListenerFx listener2,
			String option) {
		
//		this.getChildren().remove(widget);
		widget = listener2.onChangeSelected(option, this);
//		this.getChildren().add(widget);
	}

	public SvPropertyWidgetFx getWidget() {
		return this.widget;
	}

	public void setOnCommonListener(CommonWidgetListener commonListener) {
		this.commonListener = commonListener;
	}
}
