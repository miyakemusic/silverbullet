package jp.silverbullet.register;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import jp.silverbullet.MyDialogFx;
import jp.silverbullet.register.RegisterBit.ReadWriteType;

public class RegisterMapUi extends VBox implements RegisterMapListener {

	private static final int REGBUTTONWIDTH = 40;
	private Map<String, Control> buttons = new HashMap<>();
	private RegisterMapModel model;
	private Button interrupt;
	
	public RegisterMapUi(final RegisterMapModel model) {
		this.model = model;
//		this.model.setSimulatorClass("SvOsaSimulator");
		model.setOnChange(this);
		model.update();
		ScrollPane pane = new ScrollPane();
		pane.setMinHeight(600);
		this.getChildren().add(pane);
		
		VBox vbox = new VBox();
		pane.setContent(vbox);
				
		vbox.getChildren().add(createToolbar(model));
		
		HBox titleRow = new HBox();
		Label title1 = new Label("Register");
		title1.setPrefWidth(200);
		titleRow.getChildren().add(title1);
		for (int i = 31; i >= 0; i--) {
			Button bit = new Button(String.valueOf(i));
			bit.setPrefWidth(REGBUTTONWIDTH);
			titleRow.getChildren().add(bit);
		}
		vbox.getChildren().add(titleRow);
		
		for (int i = 0; i < model.getCount(); i++) {
			if (model.exists(i)) {
				VBox v = new VBox();
				vbox.getChildren().add(v);
				{
					HBox hbox = new HBox();
					v.getChildren().add(hbox);
					Label title = new Label(model.getTitle(i));
					title.setTooltip(new Tooltip(model.getDescription(i)));
					title.setPrefWidth(200);
					hbox.getChildren().add(title);
					for (RegisterBit bit : model.getBits(i)) {
						Label button =  new Label(bit.getName());
						button.setTooltip(new Tooltip(bit.getName() + "\n" + bit.getDescription())); 
	
						button.setStyle("-fx-border-width:1;-fx-border-color:black;-fx-alignment:center;");
						button.setMinWidth(model.getBitWidth(bit.getBit()) * REGBUTTONWIDTH);
						button.setMaxWidth(model.getBitWidth(bit.getBit()) * REGBUTTONWIDTH);
						hbox.getChildren().add(button);
					}
				}
				{
					final HBox hbox = new HBox();
					v.getChildren().add(hbox);
					Label title = new Label("(" + model.getAddress(i) + ")");
					title.setPrefWidth(200);
					hbox.getChildren().add(title);
			
					int j = 0;
					for (RegisterBit bit : model.getBits(i)) {
						final int width = model.getBitWidth(bit.getBit());
						String value = model.getValue(i, j);
						Control control;
						if (bit.getType().equals(ReadWriteType.WO)) {
							control = new Label(value);
						}
						else {
							final int ii = i;
							final int jj = j;
							if (width == 1) {
								final ToggleButton button  =  new ToggleButton(value);
	
								button.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent arg0) {
										model.setValue(ii, jj, button.isSelected() ? 1 : 0);
									}
								});
								control = button;
							}
							else {
								final Button button =  new Button(value);
								button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
									@Override
									public void handle(MouseEvent arg0) {
										if (arg0.getButton().equals(MouseButton.PRIMARY)) {
											HBox h = new HBox();
											TextField textField = new TextField(button.getText());
											h.getChildren().add(textField);
											MyDialogFx dlg = new MyDialogFx("", hbox);
											//dlg.setControl(textField);
											dlg.setSize(200, 90);
											//dlg.setX(button.getLocalToSceneTransform().getMxx());
											dlg.showModal(h);
											if (dlg.isOkClicked()) {
												model.setValue(ii, jj, Integer.valueOf(textField.getText()));
											}		
										}
										else if (arg0.getButton().equals(MouseButton.SECONDARY)) {
											if (width != 32) {
												return;
											}
											FileChooser fileChooser = new FileChooser();
											File ret = fileChooser.showOpenDialog(null);
											if (ret != null) {
												model.setBlock(ii, jj, ret);
											}
											
										}
									}
								});
			
								button.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent arg0) {
			
	
									}									
								});
								control = button;
							}
						}
						
						if (bit.getName().equals("Reserve")) {
							control = new Label();
							control.setStyle("-fx-background-color:darkgray");
						}
						control.setTooltip(new Tooltip(bit.getName() + "\n" + bit.getDescription())); 
		
						control.setMinWidth(width * REGBUTTONWIDTH);
						control.setMaxWidth(width * REGBUTTONWIDTH);
						
						String key = i + "x" + j;
						this.buttons.put(key, control);
						hbox.getChildren().add(control);
						
						j++;
					}
				}
			}
			else {
				//Label label = new Label("None");
				//label.setPrefWidth(20*32);
				//this.getChildren().add(label);
			}
		}
	}

	protected HBox createToolbar(final RegisterMapModel model) {
		HBox hbox = new HBox();
		
		interrupt = new Button("Trigger Interrupt");
		interrupt.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.triggerInterrupt();
			}
		});
		hbox.getChildren().add(interrupt);
		
		final ToggleButton simulator = new ToggleButton("Enable Simulator");
		simulator.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.setSimulatorEnabled(simulator.isSelected());
			}
		});
		hbox.getChildren().add(simulator);
		
		final ComboBox<String> combo = new ComboBox<>();
		combo.getItems().addAll(model.getSimulatorClasses());
		hbox.getChildren().add(combo);
		combo.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				model.setSimulatorClass(combo.getSelectionModel().getSelectedItem());
			}
		});
		return hbox;
	}

	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDataUpdate(int regIndex, int blockNumber, int value3) {
		for (Control control : buttons.values()) {
			if (control.getStyle().contains("-fx-base:red;")) {
				control.setStyle(control.getStyle().replace("-fx-base:red;", ""));
			}
		}
		Control control = this.buttons.get(regIndex + "x" +  blockNumber);
		
		String text = model.getValue(regIndex, blockNumber);
		if (text.startsWith("File:")) {
			((Button)control).setText(String.valueOf(text));
		}
		else {
			int value = Integer.valueOf(text);
				
			if (control == null) {
				return;
			}
			if (control instanceof Button) {
				((Button)control).setText(String.valueOf(value));
			}
			else if (control instanceof ToggleButton) {
				((ToggleButton)control).setText(String.valueOf(value));
			}
			else if (control instanceof Label) {
				((Label)control).setText(String.valueOf(value));
			}
		}
		control.setStyle(control.getStyle() + "-fx-base:red;");
	}

	@Override
	public void onInterrupt() {
		this.interrupt.setStyle("-fx-background-color:red;");
		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						interrupt.setStyle("");
					}
					
				});
			}
			
		}.start();
	}


}
