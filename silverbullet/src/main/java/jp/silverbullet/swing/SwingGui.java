package jp.silverbullet.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import jp.silverbullet.core.BlobStore;
import jp.silverbullet.core.dependency2.CommitListener;
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
import jp.silverbullet.core.ui.part2.Layout;
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
				sequencer.requestChange(id, value, commitListener);
			} catch (RequestRejectedException e) {
				e.printStackTrace();
			}
		}		
	};
	private RuntimePropertyStore runtimePropertyStore;
	private BlobStore blobStore;
	private Sequencer sequencer;
	
	public SwingGui(UiBuilder uiBuilder, RuntimePropertyStore runtimePropertyStore, BlobStore blobStore, Sequencer sequencer) {
		this.runtimePropertyStore = runtimePropertyStore;
		this.blobStore = blobStore;
		this.sequencer = sequencer;
		
		JComboBox<String> rootPanes = new JComboBox<>();
		uiBuilder.getRootList().forEach(root -> rootPanes.addItem(root));
		this.getContentPane().setLayout(new BorderLayout());
		JPanel mainPane = new JPanel();
		
//		mainPane.setLayout(new VerticalLayout());
//		mainPane.setLayout(new FlowLayout());

		this.getContentPane().add(rootPanes, BorderLayout.SOUTH);
		this.getContentPane().add(new JScrollPane(mainPane), BorderLayout.CENTER);
		
		rootPanes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mainPane.removeAll();
				
				Pane pane = uiBuilder.getRootPane(rootPanes.getSelectedItem().toString(), true);
//				mainPane.setBorder(new TitledBorder("FRAME " + pane.layout.toString()));
				parsePane(pane, mainPane);
				getContentPane().repaint();
			}
		});
			
		this.setSize(new Dimension(1200, 800));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

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
		else if (pane.type.equals(WidgetType.Table)) {
			new SbTable(pane, uiModel, parent);
		}
		else if (pane.type.equals(WidgetType.Pane)) {
			JPanel panel = new JPanel();
			parent.add(panel);
			
			if (!pane.id.isEmpty()) {
				panel.setBorder(new TitledBorder(uiModel.getUiProperty(pane.id).getTitle()));
			}
			
//			try {
//				float width = Float.parseFloat(pane.css("width"));
//				float height = Float.parseFloat(pane.css("height"));
//				panel.setSize(new Dimension((int)width, (int)height));
//			}
//			catch(Exception e) {
//				
//			}
//			try {
//				float top = Float.valueOf(pane.css("top").replace("px", ""));
//				float left = Float.valueOf(pane.css("left").replace("px", ""));
//				panel.setLocation((int)left, (int)top);
//			}
//			catch(Exception e) {
//				panel.setLocation(0, 0);
//			}			
//			panel.setBorder(new TitledBorder(""));
			if (pane.layout.equals(Layout.VERTICAL)) {
				//panel.setLayout(new VerticalLayout());
				panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//				panel.setPreferredSize(new Dimension(-1, parent.getHeight()));
			}
			else if (pane.layout.equals(Layout.HORIZONTAL)) {
				try {
					int padding = Integer.valueOf(pane.css("padding").replace("px", ""));
					panel.setLayout(new FlowLayout(FlowLayout.LEFT, padding, padding));
				}
				catch (Exception e) {
					panel.setLayout(new FlowLayout(FlowLayout.LEFT));
				}
				//panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
				parent.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent arg0) {
//						int width = panel.getWidth();
//						panel.setSize(new Dimension(width, parent.getHeight()));
					}					
				});
				//panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
			}
			else if (pane.layout.equals(Layout.ABSOLUTE)){
				panel.setLayout(new VerticalLayout());
			}
			else {
				System.err.println("Invalid Layout");
			}
			
//			panel.setBorder(new EtchedBorder(1));
			
			for (Pane subPane : pane.widgets) {
				parsePane(subPane, panel);
			}
			
		}
//		else if (pane.type.equals(WidgetType.Pane)) {	
//			
//			if (!pane.id.isEmpty()) {
//				parent.setBorder(new TitledBorder(uiModel.getUiProperty(pane.id).getTitle()));
//			}
//			for (Pane subPane : pane.widgets) {
//				JPanel panel = new JPanel();
//
//				try {
//					float width = Float.parseFloat(subPane.css("width"));
//					float height = Float.parseFloat(subPane.css("height"));
//					panel.setSize(new Dimension((int)width, (int)height));
//				}
//				catch(Exception e) {
//					
//				}
//				try {
//					float top = Float.valueOf(subPane.css("top").replace("px", ""));
//					float left = Float.valueOf(subPane.css("left").replace("px", ""));
//				}
//				catch(Exception e) {
//					//e.printStackTrace();
//				}
//				
//				if (subPane.layout.equals(Layout.VERTICAL)) {
//				//	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
//					panel.setLayout(new VerticalLayout());
//				}
//				else if (subPane.layout.equals(Layout.HORIZONTAL)) {
//					panel.setLayout(new FlowLayout());
//				}
//				else if (subPane.layout.equals(Layout.ABSOLUTE)){
//					panel.setLayout(null);
//				}
//				else {
//					System.err.println("Invalid Layout");
//				}
//				parent.add(panel);
//				parsePane(subPane, panel);
//			}
//		}
	}
}