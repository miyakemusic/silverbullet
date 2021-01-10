package jp.silverbullet.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import jp.silverbullet.dev.ControlElement;
import jp.silverbullet.dev.MessageObject;

public abstract class SbMessageBox extends JDialog {

	public SbMessageBox(MessageObject message2, int width, int height, Frame parent) {
		super(parent);
		this.setSize(new Dimension(width, height));
		this.setLocationRelativeTo(parent);
//		this.setLocation(40, 40);
		JPanel base = new JPanel();
		base.setBorder(new EtchedBorder());
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(base, BorderLayout.CENTER);
		
		base.setLayout(new BorderLayout());
		this.setUndecorated(true);
		
		JEditorPane editor = new JEditorPane("text/html", message2.html);
		base.add(editor, BorderLayout.CENTER);
		editor.setEditable(false);
		//editor.setText("<HTML>" + html + "</HTML>");
		
		JPanel controlBox = new JPanel();
		controlBox.setLayout(new FlowLayout());
		
		for (ControlElement element : message2.controls.controls) {
			JButton button = new JButton(element.title);
			controlBox.add(button);
			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					onClick(element.id);
				}
			});		
		}

		base.add(controlBox, BorderLayout.SOUTH);
	}

	abstract protected void onClick(String id);

}
