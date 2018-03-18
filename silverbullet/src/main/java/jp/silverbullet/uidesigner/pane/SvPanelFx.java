package jp.silverbullet.uidesigner.pane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.SvProperty;
import jp.silverbullet.SvPropertyListener;
import jp.silverbullet.dependency.analyzer.DependencyFrameFx;
import jp.silverbullet.dependency.engine.RequestRejectedException;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorModelImpl;
import jp.silverbullet.dependency.speceditor2.DependencySpecEditorPaneFx;
import jp.silverbullet.property.ListDetailElement;
import jp.silverbullet.uidesigner.EditableWidgetListenerFx;
import jp.silverbullet.uidesigner.pane.UiElement.LayoutType;
import jp.silverbullet.uidesigner.widgets.Description;
import jp.silverbullet.uidesigner.widgets.EditableWidgetFx;
import jp.silverbullet.uidesigner.widgets.SvPropertyWidgetFx;
import jp.silverbullet.uidesigner.widgets.WidgetFactoryFx;

public abstract class SvPanelFx extends VBox {

	abstract protected void onShowIdEditor(String id);
	abstract protected void onShowHandlerEditor(String id);
	abstract protected GuiPropertyFx getGuiPropFx();
	
	private SvPanelModel model;
	private WidgetFactoryFx factory;
	private ContextMenu contextMenu;
	private Pane basePane;
	public static String SELECTEDSTYLE = "-fx-border-width:4;";
	private Pane resizingBox = new Pane();

	private CommonWidgetListener commonListener = new CommonWidgetListener() {
		@Override
		public void onSelect(Object pointer) {
			model.setSelected(pointer);
			if (getGuiPropFx() != null) {
				getGuiPropFx().setElement(model.getSelectedElement());
			}
		}

		@Override
		public void onCut(Object pointer) {
			model.cut(pointer);
		}

		@Override
		public void onRemove(Object pointer) {
			model.removeElement(pointer);
		}

		@Override
		public void onStyleChange(Object pointer) {
			try {
				model.updateStyle(pointer, fetchText("Style", model.getStyle(pointer), pointer));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onPaste(Object myPane) {
			model.paste();
		}

		@Override
		public void onDescription(Object pointer) {
			try {
				model.updateDescription(pointer, fetchText("Description", model.getDescription(pointer), pointer));
			} catch (Exception e) {
		//		e.printStackTrace();
			}
		}

		@Override
		public void onLayout(LayoutType layout, Object pointer) {
			model.updateLayout(pointer, layout);
		}

		@Override
		public void onMoveUp(Object pointer) {
			model.moveUp(pointer);
		}

		@Override
		public void onMoveDown(Object pointer) {
			model.moveDown(pointer);
		}

		@Override
		public void onCopy(Object pointer) {
			model.copy(pointer);
		}

		@Override
		public void onAdd(Pane myPaneFx) {
			onShowIdEditor("");
		}

		@Override
		public void onHandlerProperty(String id) {
			onShowHandlerEditor(id);
		}

		@Override
		public void onGuiProperties(Object pointer) {
			//showGuiProperties();
		}

		@Override
		public void onMoved(double x, double y, Object pointer) {
			model.updatePosition(x, y, pointer);
		}

	};
//	private List<SvWidgetFx> allWidgets = new ArrayList<SvWidgetFx>();
	
	public SvPanelFx(final SvPanelModel model, WidgetFactoryFx factory) {
		this.model = model;
		this.factory = factory;
	
		basePane = new FlowPane();
		this.getChildren().add(basePane);
		
		updateLayout();
		this.setPrefWidth(1200);
		model.addListener(new SvPanelModelListener() {
			@Override
			public void dataChanged() {
				updateLayout() ;
			}
		});
		
		contextMenu = new ContextMenu();
		Menu layout = new Menu("Layout");
		
		Menu elementMenu = new Menu("Element");
		MenuItem labelMenu = new MenuItem("Label");
		
		elementMenu.getItems().add(labelMenu);
		labelMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addLabel("String");
			}
		});
		MenuItem paneMenu = new MenuItem("Pane");
		elementMenu.getItems().add(paneMenu);
		paneMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addPane("");
			}
		});
		
		MenuItem universalMenu = new MenuItem("Universal");
		elementMenu.getItems().add(universalMenu);
		universalMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addUniversal();
			}
		});
//		MenuItem tabMenu = new MenuItem("Tab");
//		elementMenu.getItems().add(tabMenu);
//		tabMenu.setOnAction(new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(ActionEvent arg0) {
//				addTab("");
//			}
//		});
		
		MenuItem pasteMenu = new MenuItem("Paste");
		pasteMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				model.paste();
			}
		});

		
		contextMenu.getItems().addAll(layout, elementMenu, pasteMenu);
		MenuItem flowLayout = new MenuItem("Flow");
		MenuItem verticalLayout = new MenuItem("Vertical");
		MenuItem horizontalLayout = new MenuItem("Horizontal");
		flowLayout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				changeLayout(new FlowPane());
			}
		});
		verticalLayout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				changeLayout(new VBox());
			}
		});
		horizontalLayout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				changeLayout(new HBox());
			}
		});
		
		layout.getItems().addAll(flowLayout, verticalLayout, horizontalLayout);
		
		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				model.setSelected(null);
				if (event.isSecondaryButtonDown()) {
					showPopup(contextMenu, event);
				}
				event.consume();
			}
		});
		
		resizingBox.setStyle("-fx-background-color:red;");
//		this.getChildren().add(resizingBox);
	}

	protected void addUniversal() {
		model.addStaticWidget(UiElement.Universal, "", "");
	}
	
//	protected void addTab(String string) {
//		model.addStaticWidget("Tab", string, "-fx-padding:5;-fx-width:100; -fx-height:100;");
//	}

	protected void addPane(String string) {
		model.addStaticWidget(UiElement.Pane, string, "-fx-padding:5;-fx-border-width:1; -fx-border-color:lightgray;");
	}

	protected void addLabel(String string) {
		model.addStaticWidget(UiElement.Label, string, "");
	}

	protected void showPopup(ContextMenu contextMenu2,
			MouseEvent event) {
		contextMenu.show(this, event.getScreenX(), event.getScreenY());
	}

	private void createLayout(Pane basePane2, List<UiElement> elements, TabPane tabPane, List<String> tabNames) {
		for (UiElement e: elements) {
			if (!e.getId().isEmpty()) {
				createDynamicWidget(basePane2, e, tabPane);
			}
			else {
				createStaticWidget(basePane2, e, tabPane, tabNames);
			}
		}
	}
	protected void createDynamicWidget(Pane basePane2, UiElement e, TabPane tabPane) {
		SvPropertyWidgetFx widget = createWidget(e);
	
		final EditableWidgetFx editable = new EditableWidgetFx(widget);		
		e.setListener(createUiElementListener(editable));
		editable.setStyle(e.getStyle());
		e.setPointer(editable);
		applyDescription(e, editable);
		addWidget(basePane2, editable, e.isSelected());

		
		editable.setOnCommonListener(commonListener);
		
		editable.setOnEditableWidgetListener(new EditableWidgetListenerFx() {

			@Override
			public void onEdit(SvProperty property) {	
				onShowIdEditor(property.getId());
			}

			@Override
			public List<String> onRequestWidgetChange(SvPropertyWidgetFx widget) {
				return model.getAlternativeWidgets(widget.getProperty());
			}

			@Override
			public SvPropertyWidgetFx onChangeSelected(String option,
					EditableWidgetFx editableWidget) {
				model.replaceType(editableWidget, option);
				return null;
			}

			@Override
			public void onDependency(SvProperty property) {
				showDependencyDiagramUi(property.getId());
			}

		});
	}
	protected SvPropertyWidgetFx createWidget(UiElement e) {
		SvPropertyWidgetFx widget = factory.create(model.getProperty(e.getId()), e);
		return widget;
	}
	
	private UiElementListener createUiElementListener(final Node node) {
		return new UiElementListener() {
			@Override
			public void onSelectChanged(boolean selected) {
				setNodeSelected(node, selected);
			}

			@Override
			public void onPropertyUpdated() {
				updateLayout() ;
			}
		};
	}

	protected void showDependencyDiagramUi(final String id) {
		MyDialogFx dialog = new MyDialogFx("Dependency Diagram", this);
		
		DependencyFrameFx node = createDependencySpecPane(id, this.model);
		dialog.showModal(node);
		node.removeListeners();
	}
	
	private boolean doNotRequestDependency = false;
	
	private void createStaticWidget(Pane basePane2, UiElement e, TabPane tabPane2, List<String> tabNames) {
		if (e.getWidgetType().equals(UiElement.Label)) {
			Label label = new Label(e.getDescription());
			e.setPointer(label);
			e.setListener(this.createUiElementListener(label));
			addWidget(basePane2, label, e.isSelected());
		}
		else if (e.getWidgetType().equals(UiElement.Universal)) {
			SvUniversalPane node = new SvUniversalPane(model.getDi(), new Description(e.getStyle()), new Description(e.getDescription()), commonListener);
			e.setPointer(node);
			e.setListener(this.createUiElementListener(node));
			node.setStyle(e.getStyle());
			addWidget(basePane2, node, e.isSelected());
		}
		else if (e.getWidgetType().equals(UiElement.Pane)) {
			MyPaneFx pane = new MyPaneFx(e.getLayoutType(), e.getDescription(), e.getStyle(), commonListener) {
				@Override
				protected void onAddUniversal() {
					addUniversal();
				}

				@Override
				protected void onAddPane() {
					addPane("");
				}
			};
			e.setListener(this.createUiElementListener(pane));
			e.setPointer(pane);
			pane.setMinHeight(30);
			pane.setMinWidth(30);
			pane.setStyle(e.getStyle());
			
			Description desc = new Description(e.getDescription());
			String tabs = desc.getValue(Description.TABS);
			TabPane tabPane = null;
			List<String> newTabList = new ArrayList<String>();
			if (tabPane2 != null) {
				Tab tab = new Tab(tabNames.get(0));
				tabPane2.getTabs().add(tab);
				tab.setContent(pane);
				tab.setClosable(false);
	//			pane.setMouseTransparent(true);
				tabNames.remove(0);

			}
			else if (!tabs.isEmpty()){
				tabPane = new TabPane();
				
//				tabPane.setMouseTransparent(true);
				String tabHeaderHeight = desc.getValue(Description.TABHEADERHIGHT);
				if (!tabHeaderHeight.isEmpty()) {
					tabPane.setTabMaxHeight(Double.valueOf(tabHeaderHeight));
				}
				if (desc.isDefined(Description.HEIGHT)) {
					tabPane.setMinHeight(Double.valueOf(desc.getValue(Description.HEIGHT)));
				}
				String tabId = desc.getValue(Description.RELATEDID);
				
				if (!tabId.isEmpty()) {
					SvProperty prop = model.getProperty(tabId);
					for (ListDetailElement ee : prop.getAvailableListDetail()) {
						newTabList.add(ee.getTitle());
					}
					TabPane tabPaneTmp = tabPane;
					tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

						@Override
						public void changed(ObservableValue<? extends Tab> arg0, Tab arg1, Tab arg2) {
							if (doNotRequestDependency) {
								return;
							}
							int index = tabPaneTmp.getTabs().indexOf(arg2);
							try {
								model.getDi().getDependency().requestChange(tabId, prop.getAvailableListDetail().get(index).getId());
							} catch (RequestRejectedException e) {
								e.printStackTrace();
							}
						}
						
					});

					prop.addListener(new SvPropertyListener() {
						@Override
						public void onValueChanged(String id, String value) {
							doNotRequestDependency = true;
							int index = 0;
							for (ListDetailElement ee : prop.getAvailableListDetail()) {
								if (ee.getId().equals(value)) {
									tabPaneTmp.getSelectionModel().select(index);
									break;
								}
								index++;
							}
							doNotRequestDependency = false;
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
						public void onVisibleChanged(String id, Boolean b) {
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
						
					});
				}
				else {
					newTabList = new ArrayList<String>(Arrays.asList(tabs.split(",")));
				}
				
				basePane2.getChildren().add(pane);
				pane.getChildren().add(tabPane);
			}
			else {
				addWidget(basePane2, pane, e.isSelected());
			}
			applyDescription(e, pane);

			createLayout(pane, e.getLayout().getElements(), tabPane, newTabList);

		}
	}

	private void setTabHandler(TabPane tabPane, SvProperty property) {

	}
	protected void applyDescription(UiElement e, Pane pane) {
		for (String description : e.getDescription().split(";")) {
			String[] tmp = description.split(":");
			if (tmp[0].equals("-width")) {
				pane.setMinWidth(Double.valueOf(tmp[1]));
			}
			else if (tmp[0].equals("-height")) {
				pane.setMinHeight(Double.valueOf(tmp[1]));
			}
		}
	}

	protected void addWidget(Pane basePane2, Node node, boolean b) {
		
		if (basePane2 instanceof MyPaneFx) {
			((MyPaneFx) basePane2).add(node);
		}
		else { 
			basePane2.getChildren().add(node);
		}
		
		
		if (b) {
			//setNodeSelected(widget, true);
			model.setSelected(node);
		}
	}


	protected DependencyFrameFx createDependencySpecPane(final String id, final SvPanelModel svPanelModel) {
		return DependencyFrameFactory.create(id, svPanelModel);

	}

	protected void showDependencySpecUi(String id) {
		DependencySpecEditorPaneFx node = new DependencySpecEditorPaneFx(new DependencySpecEditorModelImpl(id, model));
		MyDialogFx dialog = new MyDialogFx("Dependency Editor", this);
		dialog.showModal(node);
	}

	protected void changeLayout(Pane newPane) {
		getChildren().clear();
		basePane = newPane;
		getChildren().add(basePane);
		updateLayout();
	}

	protected void updateLayout() {
		this.basePane.getChildren().clear();
		createLayout(basePane, model.getElements(), null, null);
	}

	protected String fetchText(String title, String defaultValue, Object pointer) throws Exception {
		String style = defaultValue;
		TextField text = new TextField(style);
		MyDialogFx dlg = new MyDialogFx(title, SvPanelFx.this);
		VBox vbox = new VBox();
		vbox.getChildren().add(text);
		dlg.showModal(vbox);
		if (!dlg.isOkClicked()) {
			throw new Exception();
		}
		return text.getText();
	}
	
	protected void setNodeSelected(final Node node, boolean selected) {
		if (selected) {
			node.setStyle(node.getStyle() + ";-fx-background-color:lightblue;");
		}
		else {
			node.setStyle(node.getStyle().replace("-fx-background-color:lightblue;", ""));
		}
	}

}
