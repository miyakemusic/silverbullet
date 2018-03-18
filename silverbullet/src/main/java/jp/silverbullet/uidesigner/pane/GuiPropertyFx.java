package jp.silverbullet.uidesigner.pane;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jp.silverbullet.uidesigner.widgets.Description;

public abstract class GuiPropertyFx extends VBox {
	abstract protected UiElement onLeft();
	abstract protected UiElement onRight();
	abstract protected void onRequestShowDependency(String id);
	abstract protected void onRequestShowIdEditor(String id);
	abstract protected void onMoveDown();
	abstract protected void onMoveUp();
	
	private TableView<GuiPropElement> tableView;
	private static String[] descriptionOptions = {Description.UID, Description.RELATEDID, Description.WIDTH, Description.HEIGHT, Description.TITLE_WIDTH, 
		Description.TABLE_COLUMN_WIDTH, Description.TABS, Description.FUNCTIONKEY, Description.LAYOUT, Description.TITLEVISIBLE,
		Description.BUTTON_WIDTH, Description.ITEMS_PER_LINE, Description.LIST_ICONS, Description.USER_CODE, Description.X, Description.Y,
		Description.TABHEADERHIGHT, Description.BORDERTITLE, Description.TITLESTYLE};
	private static String[] styleCandOptions = {"-fx-font-size", "-fx-font-weight", "-fx-font-family", "-fx-background-color", "-fx-color", 
		"-fx-border-width", "-fx-border-color", "-fx-spacing", "-fx-padding", "-fx-hgap", "-fx-vgap", 
		"-fx-alignment", "-fx-background-image", "-fx-graphic"/*, "-fx-tab-max-height"*/};
	
	private Map<String, List<GuiPropElement>> dataMap = new HashMap<String, List<GuiPropElement>>();
	private UiElement uiElement;
	
	public GuiPropertyFx(UiElement uiElement) {
		initialize(uiElement);
	}

	protected void initialize(UiElement uiElement) {
		this.uiElement = uiElement;
		if (uiElement == null) {
			return;
		}
		
		this.getChildren().clear();
		this.getChildren().add(createTools());
		this.getChildren().add(new Label("Type: " + uiElement.getWidgetType()));
		this.getChildren().add(new Label("ID: " + uiElement.getId()));
		this.getChildren().add(createTable("Description", uiElement.getDescription(), descriptionOptions));
		this.getChildren().add(new Label("Style"));
		this.getChildren().add(createTable("Style", uiElement.getStyle(), styleCandOptions));
	}

	private Node createTools() {
		HBox hbox = new HBox();
		Button left = new Button("<<");
		//left.setStyle("-fx-graphic:url('http://icooon-mono.com/i/icon_12236/icon_122360_16.png')");
		left.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				UiElement e = onLeft();
				initialize(e);
			}
		});
		Button right = new Button(">>");
		//right.setStyle("-fx-graphic:url('http://icooon-mono.com/i/icon_12237/icon_122370_16.png')");
		right.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				UiElement e = onRight();
				initialize(e);
			}
		});	
		hbox.getChildren().add(left);
		hbox.getChildren().add(right);
		
		Button prop = new Button("ID");
		prop.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (uiElement.getId().isEmpty())return;
				onRequestShowIdEditor(uiElement.getId());
			}
		});	
		hbox.getChildren().add(prop);
		
		Button depedency = new Button("Dependency");
		depedency.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (uiElement.getId().isEmpty())return;
				onRequestShowDependency(uiElement.getId());
			}
		});	
		hbox.getChildren().add(depedency);
		
		VBox vbox = new VBox();
		hbox.getChildren().add(vbox);
		Button moveUp = new Button("", new ImageView(new Image(GuiPropertyFx.class.getResourceAsStream("up.png"))));
		//moveUp.setStyle("-fx-graphic:url('file:///c:/Projects/workspace/workspace-kepler/OtdrSimulator2/icons/up.png')");
		vbox.getChildren().add(moveUp);
		Button moveDown = new Button("", new ImageView(new Image(GuiPropertyFx.class.getResourceAsStream("down.png"))));
		//moveDown.setStyle("-fx-graphic:url('file:///c:/Projects/workspace/workspace-kepler/OtdrSimulator2/icons/down.png')");
		
		vbox.getChildren().add(moveDown);
		moveUp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onMoveUp();
			}
		});
		moveDown.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				onMoveDown();
			}
		});
		return hbox;
	}

	private HBox createId() {
		HBox hbox = new HBox();
		hbox.getChildren().add(new Label("ID:"));
		TextField idText = new TextField();
		hbox.getChildren().add(idText);
		return hbox;
	}

	private ComboBox createDescriptionCombo() {
		ComboBox<String> combo = new ComboBox<>();
		combo.getItems().add("-gui");
		combo.getItems().add("-width");
		combo.getItems().add("-height");
		return combo;
	}

	private Node createTable(String title, String style, String[] options) {		
		tableView = new TableView<>();
		tableView.setEditable(true);
		TableColumn<GuiPropElement, String> colProperty = new TableColumn<>("Title");
		colProperty.setCellValueFactory(new PropertyValueFactory<GuiPropElement, String>("title"));
//		colProperty.setCellFactory(TextFieldTableCell.<GuiProp>forTableColumn());
		
		TableColumn<GuiPropElement, String> colValue = new TableColumn<>("Value");
		colValue.setCellValueFactory(new PropertyValueFactory<GuiPropElement, String>("value"));	
		colValue.setCellFactory(TextFieldTableCell.<GuiPropElement>forTableColumn());
		colValue.setEditable(true);
		colValue.setOnEditCommit(
			    new EventHandler<CellEditEvent<GuiPropElement, String>>() {

					@Override
					public void handle(CellEditEvent<GuiPropElement, String> arg0) {
						arg0.getRowValue().setValue(arg0.getNewValue());
						commit();
					}
			    	
			    }
			    );
		tableView.getColumns().addAll(colProperty, colValue);
		
		ObservableList<GuiPropElement> data = createData(style, tableView, options);
		dataMap.put(title, data);
		
		return tableView;
	}

	protected void commit() {
		String description = "";
		for (GuiPropElement p : dataMap.get("Description")) {
			if (p.getValue().isEmpty())continue;
			description += p.getTitle() + ":" + p.getValue() + ";";
		}
		this.uiElement.setDescription(description);
		
		String style = "";
		for (GuiPropElement p : dataMap.get("Style")) {
			if (p.getValue().isEmpty())continue;
			style += p.getTitle() + ":" + p.getValue() + ";";
		}
		this.uiElement.setStyle(style);
		this.uiElement.fireUpdate();
	}

	private ChangeListener<String> listener = new ChangeListener<String>() {

		@Override
		public void changed(ObservableValue<? extends String> arg0,
				String arg1, String arg2) {
		}
		
	};
	
	protected ObservableList<GuiPropElement> createData(String style, TableView<GuiPropElement> tableView, String[] options) {
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		for (String line : style.split(";")) {
			if (line.isEmpty())continue;
			String[] ss = line.split(":");
			if (ss.length == 2) {
				map.put(ss[0], ss[1]);
			}
			else {
				String v = "";
				for (int i = 1; i < ss.length; i++) {
					v += ss[i] + ":";
				}
				map.put(ss[0], v.substring(0, v.length()-1));
			}
		}
		
		ObservableList<GuiPropElement> props = FXCollections.observableArrayList();
		for (String option : options) {
			String value = map.get(option);
			if (value == null) {
				value = "";
			}
			props.add(new GuiPropElement(option, value, listener));
		}
		tableView.getItems().addAll(props);
		return props;
	}

	public void setElement(UiElement selectedElement) {
		this.initialize(selectedElement);
	}

}
