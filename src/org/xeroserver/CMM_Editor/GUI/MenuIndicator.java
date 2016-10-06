package org.xeroserver.CMM_Editor.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JMenu;

public class MenuIndicator extends JMenu {

	private int errors = 0;

	private static final long serialVersionUID = 1L;

	public MenuIndicator() {
		this.setPreferredSize(new Dimension(20, 0));
		this.setEnabled(false);
	}

	public void updateErrorCount(int errors) {
		this.errors = errors;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.RED);

		if (errors == 0)
			g.setColor(Color.GREEN);

		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.BLACK);
		g.drawString(errors + "", 5, 15);
	}

}
