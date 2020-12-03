package jp.silverbullet.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import jp.silverbullet.core.dependency2.DependencySpec;
import jp.silverbullet.core.ui.UiModel;
import jp.silverbullet.core.ui.UiProperty;
import jp.silverbullet.core.ui.part2.Pane;

public class SbDialog extends SbWidget {

	private JButton button;

	public SbDialog(Pane pane, UiModel uiModel, JPanel parent, JFrame gui, RetreiveDesignDialog retreiveDesignDialog) {
		super(pane, uiModel, parent);
		button = new JButton(uiModel.getUiProperty(pane.id).getTitle());
		parent.add(button);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showDialog();
			}

			private void showDialog() {
				JDialog dialog = new JDialog(gui, true);
//				dialog.setUndecorated (true);
				dialog.setSize(new Dimension(500, 200));
				dialog.setLocationRelativeTo(gui);
				JPanel basePanel = new JPanel();
				basePanel.setLayout(new BorderLayout());
				dialog.getContentPane().add(basePanel);
				
				JPanel panel = new JPanel();
				basePanel.add(panel, BorderLayout.CENTER);
				
				JPanel tool = new JPanel();
				basePanel.add(tool, BorderLayout.SOUTH);
				JButton ok = new JButton("OK");
				JButton cancel = new JButton("Cancel");
				tool.setLayout(new FlowLayout());
				tool.add(ok);
				tool.add(cancel);
				
				ActionListener closeLisetenr = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						dialog.setVisible(false);
					}
				};
				ActionListener okLisetenr = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						//SbDialog.this.setValue(SbDialog.this.getUiProperty().getCurrentValue().equals(DependencySpec.True) ? DependencySpec.False : DependencySpec.True);
						SbDialog.this.setValue(DependencySpec.True);
						
					}
				};				
				ok.addActionListener(closeLisetenr);
				ok.addActionListener(okLisetenr);
				cancel.addActionListener(closeLisetenr);
				
				String[] tmp = pane.optional.split("=");
				if (tmp.length > 1 && tmp[0].equals("$CONTENT")) {
					retreiveDesignDialog.build(panel, tmp[1]);
				}
				dialog.setVisible(true);
				
			}
		});
	}

	@Override
	protected void onSize(int width, int height) {
		this.button.setPreferredSize(new Dimension(width, height));
	}

	@Override
	protected void onPosition(int left, int top) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void onInit(Pane pane, UiProperty uiProp, Container parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onUpdate(UiProperty uiProp) {

	}

}
