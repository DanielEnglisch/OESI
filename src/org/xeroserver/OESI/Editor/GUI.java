/**
 * @author Daniel Englisch
 */



package org.xeroserver.OESI.Editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextPane editor_area;
	private JTextArea console_area;

	private TextLineNumber lineView = null;
	private boolean showLineNumbers = true;
	private boolean showConsole = true;

	public MenuIndicator indicator = null;

	public GUI() {

		setTitle("OESI Editor v. " + Editor.version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 560);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu_window = new JMenu("Window");
		menuBar.add(menu_window);

		JMenuItem mi_toggleline = new JMenuItem("Hide line numbers");
		mi_toggleline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				toggleLineView();

				if (showLineNumbers) {
					mi_toggleline.setText("Hide line numbers");
				} else {
					mi_toggleline.setText("Show line numbers");
				}

			}
		});
		menu_window.add(mi_toggleline);

		JMenuItem mi_toggleconsole = new JMenuItem("Hide console");
		mi_toggleconsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				toggleConsole();

				if (showConsole) {
					mi_toggleconsole.setText("Hide console");
				} else {
					mi_toggleconsole.setText("Show console");
				}

			}
		});
		menu_window.add(mi_toggleconsole);

		indicator = new MenuIndicator();
		// menuBar.add(indicator);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JScrollPane editor_scroll = new JScrollPane();
		contentPane.add(editor_scroll, BorderLayout.CENTER);

		editor_area = new JTextPane(new EditorDoc());

		editor_area.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.isControlDown()) {
					if (e.getKeyCode() == KeyEvent.VK_R) {
						Editor.run();
					}

				}

				if (e.getKeyCode() == KeyEvent.VK_TAB) {
					e.consume();

					try {

						editor_area.getDocument().insertString(editor_area.getCaretPosition(), "      ", null);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}

			}

		});
		editor_area.setFont(new Font("Arial", Font.PLAIN, 20));

		lineView = new TextLineNumber(editor_area);
		editor_scroll.setRowHeaderView(lineView);

		editor_scroll.setViewportView(editor_area);

		JScrollPane console_scroll = new JScrollPane();

		console_area = new JTextArea();
		console_area.setTabSize(2);
		console_area.setEditable(false);
		console_area.setFont(new Font("Arial", Font.PLAIN, 20));

		console_area.setPreferredSize(
				new Dimension((int) console_area.getSize().getWidth(), (int) console_area.getSize().getHeight() + 100));
		console_scroll.setViewportView(console_area);
		contentPane.add(console_scroll, BorderLayout.SOUTH);

		setVisible(true);
	}

	private void toggleLineView() {
		if (showLineNumbers) {
			lineView.hide();
		} else {
			lineView.show();

		}

		showLineNumbers = !showLineNumbers;
	}

	private void toggleConsole() {
		if (showConsole) {
			console_area.setPreferredSize(new Dimension(0, 0));
			console_area.setSize(0, 0);
			console_area.repaint();
			setSize(getWidth(), getHeight() + 1);
			setSize(getWidth(), getHeight() - 1);

		} else {
			console_area.setPreferredSize(new Dimension((int) console_area.getSize().getWidth(),
					(int) console_area.getSize().getHeight() + 100));
			setSize(getWidth(), getHeight() + 1);
			setSize(getWidth(), getHeight() - 1);

		}
		showConsole = !showConsole;
	}

	public JTextPane getEditorArea() {
		return editor_area;
	}

	public JTextArea getConsoleArea() {
		return console_area;
	}

}
