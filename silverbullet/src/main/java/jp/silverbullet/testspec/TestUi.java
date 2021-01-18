package jp.silverbullet.testspec;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;
import javax.swing.JFrame;


class MyCanvas extends Canvas {

	private TsPresentationNodes presentation;

	public MyCanvas(TsPresentationNodes presentation) {
		this.presentation = presentation;
	}

	@Override
	public void paint(Graphics gg) {	
		Graphics2D g = (Graphics2D)gg;
		for (TsPresentationNode node : presentation.getAllNodes()) {
			g.drawRect(node.getLeft(), node.getTop(), node.getWidth(), node.getHeight());
			g.drawString(node.getId(), node.getLeft() + node.getWidth()/3, node.getTop() + node.getHeight()/2);
			
			node.getInput().forEach(o -> {
				g.drawRect(o.getLeft(), o.getTop(), o.getWidth(), o.getHeight());
				g.drawString(o.getId(), o.getLeft(), o.getTop() + 15);
			});
			
			node.getOutput().forEach(o -> {
				g.drawRect(o.getLeft(), o.getTop(), o.getWidth(), o.getHeight());
				g.drawString(o.getId(), o.getLeft(), o.getTop() + 15);
			});
		}
		
		for (TsLine line : presentation.getAllLines()) {
			g.drawLine(line.x1, line.y1, line.x2, line.y2);
		}
	}
}

public class TestUi extends JFrame {

	public static void main(String[] args) {
		new TestUi().setVisible(true);
	}

	private TsPresentationNodes presentation;

	public TestUi() {
		this.setSize(new Dimension(800, 600));
		presentation = new TsPresentationNodes(new NetworkConfiguration().createDemo());

		this.getContentPane().setLayout(new BorderLayout());

		MyCanvas canvas = new MyCanvas(presentation);
		
		this.getContentPane().add(canvas, BorderLayout.CENTER);
	}

	
}
