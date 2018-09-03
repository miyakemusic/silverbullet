package jp.silverbullet;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import jp.silverbullet.dependency.analyzer.DependencyFrameFx;
import jp.silverbullet.dependency.engine.DependencyInterface;
import jp.silverbullet.dependency.engine.DependencyListener;
import jp.silverbullet.dependency.speceditor2.DependencySpec;
import jp.silverbullet.dependency.speceditor2.DependencySpecDetail;
import jp.silverbullet.property.PropertyDef;
import jp.silverbullet.property.PropertyHolder;
import jp.silverbullet.property.PropertyType;
import jp.silverbullet.property.editor.PropertyEditorPaneFx;
import jp.silverbullet.register.HardwarePane;
import jp.silverbullet.register.HardwarePaneModel;
import jp.silverbullet.register.RegisterMapModel;
import jp.silverbullet.register.RegisterMapUi;
import jp.silverbullet.register.RegisterProperty;
import jp.silverbullet.remote.RemoteEditorFx;
import jp.silverbullet.remote.RemoteEditorModel;
import jp.silverbullet.remote.SvTex;
import jp.silverbullet.remote.SvTexHolder;
import jp.silverbullet.remote.engine.RemoteServer;
import jp.silverbullet.remote.engine.RemoteServerModel;
import jp.silverbullet.spec.SpecPaneFx;
import jp.silverbullet.test.TestRecorder;
import jp.silverbullet.test.TestRecorderUi;
import jp.silverbullet.uidesigner.DesignerModel;
import jp.silverbullet.uidesigner.DesignerModelImpl;
import jp.silverbullet.uidesigner.MyCommonDialogFx;
import jp.silverbullet.uidesigner.pane.DependencyFrameFactory;
import jp.silverbullet.uidesigner.pane.GuiPropertyFx;
import jp.silverbullet.uidesigner.pane.SvCommonDialog;
import jp.silverbullet.uidesigner.pane.SvPanelFx;
import jp.silverbullet.uidesigner.pane.SvPanelModel;
import jp.silverbullet.uidesigner.pane.SvPanelModelImpl;
import jp.silverbullet.uidesigner.pane.UiElement;
import jp.silverbullet.uidesigner.widgets.WidgetFactoryFx;
import jp.silverbullet.web.BuilderServer;
import jp.silverbullet.web.BuilderServerListener;
import jp.silverbullet.web.WebClientManager;
import jp.silverbullet.handlers.HandlerProperty;
import jp.silverbullet.handlers.HandlerPropertyFx;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public abstract class BuilderFx extends Application {
	
//	private static final String SV_BACKUP_ZIP = "sv_backup.zip";
	private static final String DESIGNER_TMP = "sv_tmp";
	private BuilderModel builderModel;// = new BuilderModelImpl();
	private static DesignerModel designerModel;// = new DesignerModelImpl(builderModel);
	private TestRecorder testRecorder;// = new TestRecorder(builderModel);
	private static RegisterMapModel registerMapModel;// = new RegisterMapModel(builderModel);

	public static DesignerModel getModel() {
		return designerModel;
	}

	private VBox root;
	private TabPane tabPane;
	protected SvPanelModel currentPaneModel = null;
	private WidgetFactoryFx factory;

	private GuiPropertyFx guiPropFx;

	private TestRecorderUi testUi;
	private BuilderServer webServer;
		
	public BuilderFx() {
		builderModel = new BuilderModelImpl();
		designerModel = new DesignerModelImpl(builderModel);
		testRecorder = new TestRecorder(builderModel);
		registerMapModel = new RegisterMapModel(builderModel);		
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		builderModel.setUserPath(this.getClass().getName().replace("." + this.getClass().getSimpleName(), ""));
        primaryStage.setTitle("Designer");		
        root = new VBox();
               
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openMenu = new MenuItem("Open");
        openMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				openFile();
			}
        });
        MenuItem importMenu = new MenuItem("Import");
        importMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				importFile();
			}
        });
        MenuItem saveAsMenu = new MenuItem("Save as");
        saveAsMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				saveFile();
			}
        });
        MenuItem backupMenu = new MenuItem("Backup");
        backupMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				backup();
			}
        });
        MenuItem createJavaFileMenu = new MenuItem("Create Java File");
        createJavaFileMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				createJavaFile();
			}
        });
        MenuItem cleanMenu = new MenuItem("Clean");
        cleanMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				cleanProperties();
			}
        });
        MenuItem exportMenu = new MenuItem("Export");
        exportMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				exportPropInfo();
			}
        });
        fileMenu.getItems().addAll(openMenu, saveAsMenu, importMenu, backupMenu, createJavaFileMenu, cleanMenu, exportMenu);
                
        Menu specMenu = new Menu("Spec.");
        MenuItem storyMenu = new MenuItem("Story");
        storyMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showStoryEditor();
			}
        });
        MenuItem propertyMenu = new MenuItem("ID/Property");
        propertyMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showPropertyEditor("");
			}
        });
        MenuItem dependencyMenu = new MenuItem("Dependency");
        dependencyMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showDependencySpec();
			}
        });
        MenuItem handlerMenu = new MenuItem("Handler");
        handlerMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showHandlerPropertyEditor("");
			}
        });
        MenuItem remoteMenu = new MenuItem("Remote");
        remoteMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showRemoteEditor();
			}
        });
        
        MenuItem hardwareControlMenu = new MenuItem("Hardware Control Procedure");
        hardwareControlMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showHardControlEditor();
			}
        }); 
        MenuItem hardwareIoMenu = new MenuItem("Register");
        hardwareIoMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showRegisterEditor();
			}
        });
        MenuItem dependencyMenu2 = new MenuItem("Dependency2");
        dependencyMenu2.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showDependencySpec2();
			}
        });
        specMenu.getItems().addAll(storyMenu, propertyMenu, dependencyMenu, handlerMenu, remoteMenu, hardwareControlMenu, hardwareIoMenu, dependencyMenu2);
        
        Menu tabMenu = new Menu("Tab");
        MenuItem deleteMenu = new MenuItem("Delete");
        deleteMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				deleteTab();
			}
        });
        MenuItem addMenu = new MenuItem("Add");
        addMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				addTab();
			}
        });
        tabMenu.getItems().addAll(deleteMenu, addMenu);
        
        Menu toolsMenu = new Menu("Tools");
        MenuItem webClientMenu = new MenuItem("Web Client");
        webClientMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showWebBrowser();
			}
        });
        toolsMenu.getItems().add(webClientMenu);
        
        Menu debugMenu = new Menu("Debug");
        MenuItem debugRegisterMenu = new MenuItem("Register I/O");
        debugRegisterMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showRegisterIOForTest();
			}
        });
        MenuItem debugAutoTestMenu = new MenuItem("Auto Test");
        debugAutoTestMenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				showAutoTest();
			}
        });  
        
        debugMenu.getItems().addAll(debugRegisterMenu, debugAutoTestMenu);
        
        Menu viewMenu = new Menu("View");
        MenuItem guiProperties = new MenuItem("GUI Properties");
        guiProperties.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				showGuiProperty();
			}
        });
        viewMenu.getItems().addAll(guiProperties);
        menuBar.getMenus().addAll(fileMenu, specMenu, tabMenu, debugMenu, toolsMenu, viewMenu);       
        
        root.getChildren().add(menuBar);
            
        tabPane = new TabPane();
                
        tabPane.minHeightProperty().bind(primaryStage.heightProperty());
             
        
        factory = new WidgetFactoryFx(designerModel.getBuilderModel().getDependency(), new SvCommonDialog(primaryStage));
        
        root.getChildren().add(tabPane);       
        
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(BuilderFx.class.getResource("stylesheet.css").toExternalForm());
        primaryStage.setScene(scene);
        
        primaryStage.show();
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				saveFile(getBackupFilename());
				System.exit(0);
			}
        });
        loadFile(getBackupFilename());
        
        designerModel.getBuilderModel().getDependency().addDependencyListener(new DependencyListener() {
			@Override
			public boolean confirm(String history) {
				Alert alert = new Alert(AlertType.CONFIRMATION, history, ButtonType.OK, ButtonType.CANCEL);
				
				alert.setTitle("Do you continue?");
				alert.setHeaderText("Following value(s) will be changed.");
				alert.setContentText(history);
				alert.showAndWait();
				return alert.getResult().equals(ButtonType.OK);
			}

			@Override
			public void onResult(
					Map<String, List<ChangedItemValue>> changedHistory) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onCompleted(String wenmessage) {
				// TODO Auto-generated method stub
				
			}
        });
        
        //builderModel.setDeviceDriver(testRecorder);
        registerMapModel.setMonitor(testRecorder);
        builderModel.setDeviceDriver(registerMapModel);
        
        registerMapModel.update();
        
 //       showGuiProperty();
        
        startRemoteCommandServer();
        
        startWebServer();
       
        handleMessage();

	}

	abstract protected String getBackupFilename();

	protected void exportPropInfo() {
		XmlPersistent<PropertyType> per = new XmlPersistent<>();
		try {
			per.save(builderModel.getPropertyHolder().getTypes(), "defaultprottype.xml", PropertyType.class);

		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	protected void handleMessage() {
		SvPropertyListener messageListener = new SvPropertyListener() {
			@Override
			public void onValueChanged(String id, String value) {
				if (value.isEmpty()) {
					return;
				}
				SvProperty prop = builderModel.getProperty(id);
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Message");
				alert.setHeaderText(prop.getCurrentValue());
				alert.setContentText(prop.getSelectedListTitle());
				alert.showAndWait();
				prop.setCurrentValue("");
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
        	
        };
        for (SvProperty prop : this.builderModel.getPropertyStore().getAllProperties("MessageProperty")) {
        	prop.addListener(messageListener);
        }
	}

	protected void showAutoTest() {
		MyDialogFx dialog = new MyDialogFx("Auto Test", root);
		
//		TestRecorderMode model = new TestRecorderMode();
		designerModel.getBuilderModel().getDependency();
		if (testUi == null) {
			testUi = new TestRecorderUi(testRecorder);
		}
		dialog.showNoModal(testUi);
	}

	protected void startWebServer() {
		new WebClientManager();
		webServer = new BuilderServer(8081, new BuilderServerListener() {

			@Override
			public void onStarted() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
				        WebView browser = new WebView();
				        WebEngine webEngine = browser.getEngine();
				       //webEngine.load("http://localhost:8081/runtime.html");
				        webEngine.load("http://yahoo.co.jp");
				        browser.setPrefHeight(500);
				        browser.setPrefWidth(700);
				        root.getChildren().add(browser);
					}	
				});
			}
			
		});
	}

	protected void startRemoteCommandServer() {
		new RemoteServer(new RemoteServerModel() {

			@Override
			public SvTexHolder getTexHolder() {
				return designerModel.getBuilderModel().getTexHolder();
			}

			@Override
			public DependencyInterface getDependency() {
				return designerModel.getBuilderModel().getDependency();
			}

			@Override
			public SvProperty getProperty(String id) {
				return designerModel.getBuilderModel().getProperty(id);
			}
        });
	}

	protected void showWebBrowser() {
		try {
			Desktop.getDesktop().browse(new URI("http://localhost:8081/rest/test/html2"));
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	protected void showGuiProperty() {
		MyDialogFx dialog = new MyDialogFx("GUI Properties", root);
		if (guiPropFx == null) {
			guiPropFx = new GuiPropertyFx(this.currentPaneModel.getSelectedElement()) {
				@Override
				protected UiElement onLeft() {
					return currentPaneModel.selectPrevElement();
				}

				@Override
				protected UiElement onRight() {
					return currentPaneModel.selectNextElement();
				}

				@Override
				protected void onRequestShowDependency(String id) {
					showDependencyEditor(id);
				}

				@Override
				protected void onRequestShowIdEditor(String id) {
					showPropertyEditor(id);
				}

				@Override
				protected void onMoveDown() {
					currentPaneModel.moveDown(null);
				}

				@Override
				protected void onMoveUp() {
					currentPaneModel.moveUp(null);
				}
			};
		}
		dialog.setWidth(400);
		dialog.showNoModal(guiPropFx);
		
	}

	protected void showDependencyEditor(String id) {
		DependencyFrameFx pane = DependencyFrameFactory.create(id, this.currentPaneModel);
		MyDialogFx dialog = new MyDialogFx("Dependency", root);
		dialog.showModal(pane);
		pane.removeListeners();
	}

	protected void showStoryEditor() {
		MyDialogFx dialog = new MyDialogFx("User Story / Use Case", root);
		dialog.showNoModal(new SpecPaneFx(designerModel.getBuilderModel().getUserStory()));
	}

	protected void backup() {
		saveFile(getBackupFilename());
	}

	protected void showRegisterIOForTest() {
		MyDialogFx dialog = new MyDialogFx("Register", root);
		dialog.setHeight(400);
		final RegisterMapUi ui = new RegisterMapUi(registerMapModel);
		dialog.showNoModal(ui);
		dialog.setOnHidden(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent arg0) {
				ui.close();
			}
		});
		//dialog.showNoModal(new RegisterMapUi(registerMapModel));
	}

	protected void showRegisterEditor() {
		MyDialogFx dialog = new MyDialogFx("Register", root);
		dialog.showNoModal(new HardwarePane(new HardwarePaneModel() {
			@Override
			public PropertyHolder getPropertyHolder() {
				return designerModel.getBuilderModel().getPropertyHolder();
			}

			@Override
			public RegisterProperty getRegisterProperty() {
				return designerModel.getBuilderModel().getRegisterProperty();
			}
		}));
	}

	protected void showHardControlEditor() {
		MyDialogFx dialog = new MyDialogFx("Hardware Control", root);
		dialog.showNoModal(new SpecPaneFx(designerModel.getBuilderModel().getHardwareContorlProcedure()));
	}

	protected void showRemoteEditor() {
		MyDialogFx dialog = new MyDialogFx("Remote", root);
		Pane node = new RemoteEditorFx(new RemoteEditorModel() {
			@Override
			public List<SvProperty> getAllProperties() {
				return designerModel.getBuilderModel().getPropertyStore().getAllProperties();
			}

			@Override
			public PropertyHolder getPropertyHolder() {
				return designerModel.getBuilderModel().getPropertyHolder();
			}

			@Override
			public List<SvProperty> getProperties(List<String> ids) {
				return designerModel.getBuilderModel().getPropertyStore().getProperties(ids);
			}

			@Override
			public SvTexHolder getTexHolder() {
				return designerModel.getBuilderModel().getTexHolder();
			}

			@Override
			public List<HandlerProperty> getHandlers() {
				return designerModel.getBuilderModel().getHandlerPropertyHolder().getHandlers();
			}

			@Override
			public void remove(SvTex tex) {
				designerModel.getBuilderModel().getTexHolder().remove(tex);
			}
		}); 
		dialog.showNoModal(node);
	}

	protected void showDependencySpec() {
		MyDialogFx dialog = new MyDialogFx("Dependency", root);
		DependencySpecListFx node = new DependencySpecListFx(this.designerModel.getBuilderModel().getPropertyStore().getAllIds(), 
				this.designerModel.getBuilderModel().getDependencySpecHolder(), currentPaneModel);
		
		dialog.showNoModal(node);
		
	}

	protected void showDependencySpec2() {

	}
	
	protected void cleanProperties() {
		PropertyHolder holder = designerModel.getBuilderModel().getPropertyHolder();
		List<PropertyDef> shouldBeRemoved = new ArrayList<PropertyDef>();
		Set<String> exists = new HashSet<String>();
		for (PropertyDef prop : holder.getProperties()) {
			if (exists.contains(prop.getId())) {
				shouldBeRemoved.add(prop);
			}
			exists.add(prop.getId());
		}
		holder.removeAll(shouldBeRemoved);
		
		for (DependencySpec spec : designerModel.getBuilderModel().getDependencySpecHolder().getSpecs().values()) {
			List<DependencySpecDetail> removed = new ArrayList<>();
			for (DependencySpecDetail detail : spec.getSpecs()) {
				if (detail.getSpecification().getValueMatched().isEmpty()) {
					removed.add(detail);
				}
			}
			spec.getSpecs().removeAll(removed);
		}
	}

	protected void createJavaFile() {
		String path = builderModel.getUserApplicationPath();
		new JavaFileGenerator(designerModel.getBuilderModel().getAllProperties()).generate(path);
		new RegisterIoGenerator(designerModel.getBuilderModel().getRegisterProperty(), builderModel.getUserApplicationPath()).generate(path);;
	}

	protected void addTab() {
		String ret = MyCommonDialogFx.showInput("New Tab", root);
		if (ret != null) {
			designerModel.addNewTab(ret);
			updateTabs();
		}
	}

	protected void deleteTab() {
		String name = tabPane.getTabs().get(tabPane.getSelectionModel().getSelectedIndex()).getText();
		tabPane.getTabs().remove(tabPane.getSelectionModel().getSelectedIndex());
		designerModel.removeTab(name);
	}

	protected void updateTabs() {
		tabPane.getTabs().clear();
		for (int i = 0; i < designerModel.getTabCount(); i++) {
        	String tabName = designerModel.getTabName(i);
            Tab tab = new Tab(tabName);
            tabPane.getTabs().add(tab);    	
            final SvPanelModel paneModel = new SvPanelModelImpl(tabName, designerModel);
            SvPanelFx pane = new SvPanelFx(paneModel, factory) {
				@Override
				protected void onShowIdEditor(String id) {
					showPropertyEditor(id);
				}

				@Override
				protected void onShowHandlerEditor(String id) {
					showHandlerPropertyEditor(id);
				}

				@Override
				protected GuiPropertyFx getGuiPropFx() {
					return guiPropFx;
				}
            };
            
          //  pane.setStyle("-fx-background-color:blue");
            ScrollPane scPane = new ScrollPane();
      //      pane.setMinHeight(root.getHeight());
            pane.minHeightProperty().bind(tabPane.heightProperty());
            scPane.setContent(pane);
            tab.setContent(scPane);
            tab.setOnSelectionChanged(new EventHandler<Event>() {
				@Override
				public void handle(Event event) {
					currentPaneModel = paneModel;
				}
            });
            if (currentPaneModel == null) {
            	currentPaneModel =  paneModel;
            }
        }
	}

	protected void openFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("select file");
		//fc.setInitialDirectory(new File(System.getProperty("user.home")));
		fc.setInitialDirectory(new File("."));
		fc.getExtensionFilters().add(new ExtensionFilter("ZIP", "*.zip"));

		File f = fc.showOpenDialog(null);
		
		String filename = f.getAbsolutePath();
		
		if (f != null) {
			loadFile(filename);
		}
	}

	protected void loadFile(String filename) {
		if (!Files.exists(Paths.get(DESIGNER_TMP))) {
			try {
				Files.createDirectories(Paths.get(DESIGNER_TMP));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		for (File file : new File(DESIGNER_TMP).listFiles()) {
			try {
				Files.delete(Paths.get(file.getAbsolutePath()));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (Files.exists(Paths.get(filename))) {
			Zip.unzip(filename, DESIGNER_TMP);
			designerModel.load(DESIGNER_TMP);
		}
		builderModel.loadDefault();
		
		updateTabs();
	}

	protected void importFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("select file");
		//fc.setInitialDirectory(new File(System.getProperty("user.home")));
		fc.setInitialDirectory(new File("."));
		fc.getExtensionFilters().add(new ExtensionFilter("ZIP", "*.zip"));

		File f = fc.showOpenDialog(null);
		
		String filename = f.getAbsolutePath();
		
		if (f != null) {
			Zip.unzip(filename, DESIGNER_TMP);
			designerModel.importFile(DESIGNER_TMP);
			//this.builderModel.importFile(DESIGNER_TMP);
		}
		updateTabs();
	}
	
	protected void saveFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("select file");
		//fc.setInitialDirectory(new File(System.getProperty("user.home")));
		fc.getExtensionFilters().add(new ExtensionFilter("ZIP", "*.zip"));

		File f = fc.showSaveDialog(null);

		if (f != null) {
			//Path path = f.toPath();
			//String folder = f.getAbsolutePath().replace(f.getName(), "");
			String filename = f.getAbsolutePath();
			saveFile(filename);
		}
	}

	protected void saveFile(String filename) {
		if (!Files.exists(Paths.get(DESIGNER_TMP))) {
			try {
				Files.createDirectory(Paths.get(DESIGNER_TMP));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		designerModel.save(DESIGNER_TMP);
		Zip.zip(DESIGNER_TMP, filename);
	}

	protected void showPropertyEditor(String id) {		
		final MyDialogFx dialog = new MyDialogFx("Property", root);
		final PropertyEditorPaneFx node = new PropertyEditorPaneFx(designerModel.getBuilderModel().getPropertyHolder()) {

			@Override
			protected void onClose() {
				removeListener();
				dialog.close();
			}

			@Override
			protected void onSelect(List<String> selected, List<String> subs) {
				for (String id : selected) {
					currentPaneModel.addElement(id);
				}
				currentPaneModel.fireDataChanged();
			}
			
		};
		if (!id.isEmpty()) {
			node.setFilterText(id, designerModel.getBuilderModel().getProperty(id).getType());
		}
		dialog.showNoModal(node);
	}

	protected void showHandlerPropertyEditor(String id) {
		MyDialogFx dialog = new MyDialogFx("Handler", root);
		Pane pane = new HandlerPropertyFx(designerModel.getBuilderModel().getHandlerPropertyHolder(), id);
		dialog.showNoModal(pane);
	}
	
	protected Stage createDialog() {
		final Stage dialog = new Stage();
		dialog.setTitle("Property Editor");
		dialog.initOwner(root.getScene().getWindow());
		dialog.initStyle(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		return dialog;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static RegisterMapModel getRegisterMapModel() {
		return registerMapModel;
	}

}
