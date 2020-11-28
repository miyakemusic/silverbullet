package jp.silverbullet.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.dependency2.CommitListener;
import jp.silverbullet.core.dependency2.Id;
import jp.silverbullet.core.dependency2.IdValue;
import jp.silverbullet.core.dependency2.RequestRejectedException;
import jp.silverbullet.core.property2.RuntimeProperty;
import jp.silverbullet.core.property2.RuntimePropertyListener;
import jp.silverbullet.core.property2.RuntimePropertyStore;
import jp.silverbullet.core.sequncer.Sequencer;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiModelListener;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.UiPropertyConverter;
import jp.silverbullet.core.ui.part2.Pane;
import jp.silverbullet.core.ui.part2.UiBuilder;
import jp.silverbullet.core.ui.part2.WidgetType;

public class SwingGui extends JFrame {
	private CommitListener commitListener = new CommitListener() {

		@Override
		public Reply confirm(Set<IdValue> message) {
			int ret = JOptionPane.showConfirmDialog(SwingGui.this, createMessage(message), "Do you accept change?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			return JOptionPane.YES_OPTION == ret ? Reply.Accept : Reply.Reject;
		}

		private String createMessage(Set<IdValue> message) {
			String ret = "<html>";
			for (IdValue iv : message) {
				ret += uiModel.getUiProperty(iv.getId().toString()).getTitle() + " : " + iv.getValue() + "<br>";
			}
			ret += "</html>";
			return ret;
		}
		
	};
	
	private UiModel uiModel = new UiModel() {
		@Override
		public Object getBlob(String id) {
			return blobStore.get(id);
		}
		
		@Override
		public UiProperty getUiProperty(String id, String extention) {
			RuntimeProperty prop = runtimePropertyStore.get(id);
			return UiPropertyConverter.convert(prop, extention, blobStore);
		}

		@Override
		public UiProperty getUiProperty(String id) {
			RuntimeProperty prop = runtimePropertyStore.get(id);
			return UiPropertyConverter.convert(prop, null, blobStore);
		}
		
		@Override
		public void addListener(String id, UiModelListener uiModelListener) {
			RuntimeProperty prop = runtimePropertyStore.get(id);
			
			prop.addListener(new RuntimePropertyListener() {

				@Override
				public void onValueChange(String id, int index, String value) {
					uiModelListener.onUpdate(UiPropertyConverter.convert(prop, null, blobStore));
				}

				@Override
				public void onEnableChange(String id, int index, boolean b) {
					uiModelListener.onUpdate(UiPropertyConverter.convert(prop, null, blobStore));
				}

				@Override
				public void onFlagChange(String id, int index, Flag flag) {
					uiModelListener.onUpdate(UiPropertyConverter.convert(prop, null, blobStore));
				}

				@Override
				public void onListMaskChange(String id, int index, String optionId, boolean mask) {
					uiModelListener.onUpdate(UiPropertyConverter.convert(prop, null, blobStore));
				}

				@Override
				public void onTitleChange(String id, int index, String title) {
					uiModelListener.onUpdate(UiPropertyConverter.convert(prop, null, blobStore));
				}
				
			});
		}

		@Override
		public void setValue(String id, String value) {
			try {
				//sequencer.requestChange(id, value, commitListener);
				sequencer.requestChange(new Id(id), value, commitListener);
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}
		}		
	};
	
	private RuntimePropertyStore runtimePropertyStore;
	private BlobStore blobStore;
	private Sequencer sequencer;
	private JPanel mainPane;
	private UiBuilder uiBuilder;
	private String gui;
	
	public SwingGui(UiBuilder uiBuilder, RuntimePropertyStore runtimePropertyStore, 
			BlobStore blobStore, Sequencer sequencer, String gui, String title, Image bgImage) {
		this.runtimePropertyStore = runtimePropertyStore;
		this.blobStore = blobStore;
		this.sequencer = sequencer;
		this.uiBuilder = uiBuilder;
		this.gui = gui;
		
		this.setTitle(title);
		
		this.getContentPane().setLayout(new BorderLayout());
		mainPane = new JPanel();
		
		
		JPanel toolBar = new JPanel();
		this.getContentPane().add(toolBar, BorderLayout.NORTH);
		initToolbar(toolBar);
		
		JPanel backgroundPane = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(bgImage, 0, 0, null);
			}			
		};
		mainPane.setPreferredSize(new Dimension(550, 400));
		backgroundPane.setLayout(new BorderLayout());
		
		int topM = 93;
		int sizeM = 113;
		JLabel topMargin = new JLabel();
		JLabel bottomMargin = new JLabel();
		JLabel leftMargin = new JLabel();
		JLabel rightMargin = new JLabel();
		topMargin.setPreferredSize(new Dimension(120, topM));
		bottomMargin.setPreferredSize(new Dimension(120, topM));
		leftMargin.setPreferredSize(new Dimension(sizeM, 50));
		rightMargin.setPreferredSize(new Dimension(sizeM, 50));
		backgroundPane.add(topMargin, BorderLayout.NORTH);
		backgroundPane.add(mainPane, BorderLayout.CENTER);
		backgroundPane.add(bottomMargin, BorderLayout.SOUTH);
		backgroundPane.add(leftMargin, BorderLayout.EAST);
		backgroundPane.add(rightMargin, BorderLayout.WEST);
//		this.getContentPane().add(rootPanes, BorderLayout.SOUTH);
		this.getContentPane().add(backgroundPane/*new JScrollPane(mainPane)*/, BorderLayout.CENTER);
		
//		rootPanes.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				updateGUI(uiBuilder, rootPanes, mainPane);
//			}
//		});
//		
//		rootPanes.setSelectedItem(gui);
		this.setSize(new Dimension(985, 641));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				SwingGui.this.getContentPane().setVisible(true);
//			}	
//		});
		new Thread() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				backgroundPane.repaint();
			}
			
		}.start();
		
		updateGUI();

	}

	protected void initToolbar(JPanel toolBar) {
		// TODO Auto-generated method stub
		
	}

	protected String getBackgroundImage() {
		return null;
	}

	RetreiveDesignDialog retreiveDesignDialog = new RetreiveDesignDialog() {

		@Override
		public void build(JPanel panel, String name) {
			Pane pane = uiBuilder.getRootPane(name, true);
			parsePane(pane, panel);
		}
		
	};

	private Pane selectedRootPane;
	
	private void parsePane(Pane pane, JPanel parent) {
		if (pane.type.equals(WidgetType.Button)) {
			new SbButton(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.CheckBox)) {
			new SbCheckBox(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.ComboBox)) {
			new SbComboBox(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.Label)) {
			new SbLabel(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.StaticText)) {
			parent.add(new JLabel(pane.text));
		}
		else if (pane.type.equals(WidgetType.TextField)) {
			new SbTextField(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.ToggleButton)) {
			new SbToggleButtonSingle(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.Chart)) {
			new SbChart(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.Image)) {
			new SbImage(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.Table)) {
			new SbTable(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.Dialog)) {
			new SbDialog(pane, uiModel, parent, this, retreiveDesignDialog);
		}
		else if (pane.type.equals(WidgetType.Pane)) {
			new SbPanel(pane, uiModel, parent) {
				@Override
				void processSubPane(JPanel pane2, Pane subPane) {
					parsePane(subPane, pane2);
				}
			};
		}
	}

	public void updateGUI() {
		mainPane.removeAll();
		
		selectedRootPane = uiBuilder.getRootPane(gui, true);
		parsePane(selectedRootPane, mainPane);
		getContentPane().repaint();
	}

}
