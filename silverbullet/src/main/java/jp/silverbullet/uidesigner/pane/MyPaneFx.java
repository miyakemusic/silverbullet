package jp.silverbullet.uidesigner.pane;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import jp.silverbullet.uidesigner.pane.UiElement.LayoutType;
import jp.silverbullet.uidesigner.widgets.Description;
import javafx.scene.input.MouseEvent;

public abstract class MyPaneFx extends StackPane {
	
	abstract protected void onAddUniversal();

	abstract protected void onAddPane();
	
	private Pane base;// = new VBox();
	private Description desc;
	private CommonWidgetListener commonListener;
	private Description style;
	public MyPaneFx (LayoutType layoutType, String description, String style, final CommonWidgetListener commonListener) {
		desc = new Description(description);
		this.style = new Description(style);
		
		this.commonListener = commonListener;
		
		this.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> arg0,
					Parent arg1, Parent arg2) {
				if (arg2 == null) {
					getChildren().clear();
				}
			}
		});

		this.setLayout(layoutType);
		
		if (desc.isDefined(Description.BORDERTITLE)) {
			Label title = new Label(" " + desc.getValue(Description.BORDERTITLE) + " ");
			title.setStyle(title.getStyle() + ";" + "-fx-translate-y: -16;");
			if (this.style.isDefined("-fx-background-color")) {
				title.setStyle(title.getStyle() + ";-fx-background-color:" + this.style.getValue("-fx-background-color"));
			}
		    //title.getStyleClass().add("bordered-titled-title");
//		    StackPane.setAlignment(title, Pos.TOP_CENTER);
//
//		    StackPane contentPane = new StackPane();
//		    base.getStyleClass().add("bordered-titled-content");
//		    contentPane.getChildren().add(base);
//
//		    getStyleClass().add("bordered-titled-border");
//		    super.getChildren().addAll(title, contentPane);	
		    this.setAlignment(Pos.TOP_LEFT);
			super.getChildren().addAll(title, base);
		}
		else {
			super.getChildren().add(base);
		}
		
		this.setFocusTraversable(true);
		
		final ContextMenu contextMenu = new CommonPopupFx().create(commonListener, this);
		
		Menu elementMenu = new Menu("Element");
		MenuItem labelMenu = new MenuItem("Label");
		
		elementMenu.getItems().add(labelMenu);
		labelMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				
			}
		});
		MenuItem paneMenu = new MenuItem("Pane");
		elementMenu.getItems().add(paneMenu);
		paneMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onAddPane();
			}
		});
		
		MenuItem universalMenu = new MenuItem("Universal");
		elementMenu.getItems().add(universalMenu);
		universalMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onAddUniversal();
			}
		});

		contextMenu.getItems().addAll(elementMenu);
		
		if (desc.isDefined(Description.X)) {
			this.setLayoutX(Double.valueOf(desc.getValue(Description.X)));
		}
		if (desc.isDefined(Description.Y)) {
			this.setLayoutY(Double.valueOf(desc.getValue(Description.Y)));
		}

		focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0,
					Boolean arg1, Boolean arg2) {

				if (arg2.equals(true)) {
					setStyle(getStyle() + ";" + SvPanelFx.SELECTEDSTYLE);
				}
				else {
					setStyle(getStyle().replace(SvPanelFx.SELECTEDSTYLE, ""));
				}
			}
		});
		
		this.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				base.setPrefWidth(arg2.doubleValue() - calcMargin());
			}
		});
		this.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				base.setPrefHeight(arg2.doubleValue() - calcMargin());
			}
		});
	}

	public void add(Node node) {
//		if (this.base instanceof GridPane) {
//			GridPane layout = (GridPane)base;
//			int count = this.base.getChildren().size();
//			layout.add(node, 0, count+1);
//		}
//		else {
		this.base.getChildren().add(node);
		this.base.parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> arg0,
					Parent arg1, Parent arg2) {
				if (arg2 == null) {
					base.getChildren().clear();
				}
			}
		});
//		}
	}
//	
//	protected void showPopup(ContextMenu contextMenu,
//			MouseEvent event) {
//		contextMenu.show(this, event.getScreenX(), event.getScreenY());
//	}

	boolean dragging = false;
	double dragDeltaX = 0.0;
	double dragDeltaY = 0.0;
	protected double initialX;
	protected double initialY;
	
	private void setLayout(LayoutType layout) {
		if (layout == null) {
			this.base = new VBox();//new TilePane(Orientation.VERTICAL);
		}
		else if (layout.equals(LayoutType.Flow)) {
			this.base = new FlowPane();
		}
		else if (layout.equals(LayoutType.Horizontal)) {
			this.base = new HBox();//new TilePane(Orientation.HORIZONTAL);
		}
		else if (layout.equals(LayoutType.Vertical)) {
			this.base = new VBox();//new TilePane(Orientation.VERTICAL);
		}
		else if (layout.equals(LayoutType.Absolute)) {
			this.base = new AnchorPane();
		}
		else {
			this.base = new VBox();
		}
	//	this.styleProperty().bind(this.base.styleProperty());
		this.styleProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Description desc = new Description(newValue);
				desc.removeElement("-fx-padding");
				desc.removeElement("-fx-border-width");
				base.setStyle(desc.get() + "-fx-padding:0;-fx-border-width:0;");
			}
		});
		
		this.base.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				dragging = true;
				initialX = MyPaneFx.this.getLayoutX();
				initialY = MyPaneFx.this.getLayoutY();
				
				dragDeltaX = MyPaneFx.this.getLayoutX() - arg0.getSceneX();
				dragDeltaY = MyPaneFx.this.getLayoutY() - arg0.getSceneY();
				
			}
		});
		
		this.base.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent arg0) {
				if (!dragging) {
					return;
				}
				double sceneX = arg0.getSceneX();
				double sceneY = arg0.getSceneY();
		
				double sceneW = MyPaneFx.this.getScene().getWidth();
				double sceneH = MyPaneFx.this.getScene().getHeight();
				
				if (sceneX < 0) {
					MyPaneFx.this.setLayoutX(dragDeltaX);

                }
                else if (sceneX > sceneW) {
                	MyPaneFx.this.setLayoutX(sceneW + dragDeltaX);
                }

                else {

                	MyPaneFx.this.setLayoutX(sceneX + dragDeltaX);

                }
				
				if (sceneY < 0) {
					MyPaneFx.this.setLayoutY(dragDeltaX);
                }

                else if (sceneY > sceneH) {
                	MyPaneFx.this.setLayoutY(sceneH + dragDeltaY);
                }

                else {
                	MyPaneFx.this.setLayoutY(sceneY + dragDeltaY);

                }
	//			commonListener.onMoving(arg0.getSceneX() - dragDeltaX, arg0.getSceneY() - dragDeltaY, 
	//					MyPaneFx.this.getWidth(), MyPaneFx.this.getHeight(),						
	//					MyPaneFx.this);
			}
			
		});


		this.base.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				if (dragging) {
					dragging = false;
					double diffX = Math.abs(initialX - MyPaneFx.this.getLayoutX());
					double diffY = Math.abs(initialY - MyPaneFx.this.getLayoutY());
					if (diffX > 1 || diffY > 1) {
					
						commonListener.onMoved(MyPaneFx.this.getLayoutX(), MyPaneFx.this.getLayoutY(), MyPaneFx.this);
					}
				}
			}
		});
	//	this.base.styleProperty().bind(this.styleProperty());
	}

private int calcMargin() {
//	Description desc = new Description(MyPaneFx.this.getStyle());
	String pad = style.getValue("-fx-padding");
	String bw = style.getValue("-fx-border-width");
	int margin = 0;
	if (!pad.isEmpty()) {
		if (pad.contains(",")) {
			pad = pad.split(",")[0];
		}
		margin += Integer.valueOf(pad);
	}
	if (!bw.isEmpty()) {
		margin += Integer.valueOf(bw);
	}
	return margin * 2;
}
	
}