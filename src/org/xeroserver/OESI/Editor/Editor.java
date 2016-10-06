package org.xeroserver.OESI.Editor;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.xeroserver.OESI.Interpreter;
import org.xeroserver.OESI.Node;
import org.xeroserver.OESI.Parser;
import org.xeroserver.OESI.Scanner;

public class Editor {

	private static GUI gui = null;

	// FILESYS
	private static File dir = new File(System.getProperty("user.home") + "\\OESI");
	private static File file = new File(dir, "tmp.oesi");

	// --------

	private static boolean hasErrors = false;
	private static Node mainNode = null;

	public static String version = "0.1";

	private static int ms_parseDely = 500;

	public static void run() {

		if (hasErrors)
			return;
		Interpreter i = new Interpreter();
		i.process(mainNode);

	}

	public static void setSave(File f) {
		file = f;
	}

	public static void main(String[] args) throws BadLocationException {

		// Creats folder in home folder:
		dir.mkdirs();

		gui = new GUI();

		gui.getEditorArea().setText("");

		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				save();

				checkErrors();

				// TypeHighlighter.colorTypes(gui.getEditorArea());

			}

		}, 0, ms_parseDely);

	}

	public static void save() {
		String content = gui.getEditorArea().getText();

		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(content);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void checkErrors() {

		Parser front = new Parser(new Scanner(file.getAbsolutePath()));

		try {
			front.Parse();

		} catch (Exception e) {
		}

		JTextPane textArea = gui.getEditorArea();

		Highlighter highlighter = textArea.getHighlighter();

		if (front.getErrorCount() != 0) {
			hasErrors = true;

			HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 90, 90));

			HashMap<Integer, String> errors = front.getErrorList();

			for (Integer line : errors.keySet()) {
				line--;

				try {
					int p0 = textArea.getDocument().getDefaultRootElement().getElement(line).getStartOffset();
					int p1 = textArea.getDocument().getDefaultRootElement().getElement(line).getEndOffset();

					highlighter.addHighlight(p0, p1, painter);
				} catch (Exception e) {

				}

			}

			String cosoleContent = "Found " + front.getErrorCount() + " errors:" + "\n";
			for (String err : errors.values()) {
				cosoleContent += err + "\n";
			}

			gui.getConsoleArea().setText(cosoleContent);
		}

		else {
			// setWorkingMainTree
			mainNode = front.main;

			// Update once
			if (hasErrors) {
				highlighter.removeAllHighlights();
				gui.getConsoleArea().setText("No errors found!");
				hasErrors = false;
			}

		}

		gui.indicator.updateErrorCount(front.getErrorCount());
	}

}
