/**
 * Modified by Daniel Englisch
 */


package org.xeroserver.OESI.Editor;

import java.awt.Color;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class EditorDoc extends DefaultStyledDocument {

	private static final long serialVersionUID = 1L;

	final StyleContext cont = StyleContext.getDefaultStyleContext();

	private String match_r = "(\\W)*(es gibt|double|bool|float|string|char|void|return|break|const|while|for|if|else|true|false)";
	final AttributeSet attr = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, new Color(255, 0, 255));

	private String match_y = "(\\W)*(read|readln|print|println|length)";
	final AttributeSet atty = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.ORANGE);

	private String match_kurblue = "(\\W)*(\\d+|\\.((\\d)*)|\\d+(f)|\\]|\\[.)";
	final AttributeSet attrb = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
	final AttributeSet attkurblue = cont.addAttribute(attrb, StyleConstants.Italic, Boolean.TRUE);

	final AttributeSet attrBlack = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.BLACK);

	private int findLastNonWordChar(String text, int index) {
		while (--index >= 0) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
		}
		return index;
	}

	private int findFirstNonWordChar(String text, int index) {
		while (index < text.length()) {
			if (String.valueOf(text.charAt(index)).matches("\\W")) {
				break;
			}
			index++;
		}
		return index;
	}

	@Override
	public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offset, str, a);

		String text = getText(0, getLength());
		int before = findLastNonWordChar(text, offset);
		if (before < 0)
			before = 0;
		int after = findFirstNonWordChar(text, offset + str.length());
		int wordL = before;
		int wordR = before;

		while (wordR <= after) {
			if (wordR == after || String.valueOf(text.charAt(wordR)).matches("\\W")) {

				checkTypes(text, wordL, wordR);

				wordL = wordR;
			}
			wordR++;
		}
	}

	private void checkTypes(String text, int l, int r) {

		if (text.substring(l, r).matches(match_r))
			setCharacterAttributes(l, r - l, attr, false);
		else if (text.substring(l, r).matches(match_y))
			setCharacterAttributes(l, r - l, atty, false);
		else if (text.substring(l, r).matches(match_kurblue))
			setCharacterAttributes(l, r - l, attkurblue, false);
		else
			setCharacterAttributes(l, r - l, attrBlack, false);
	}

	@Override
	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);

		String text = getText(0, getLength());
		int before = findLastNonWordChar(text, offs);
		if (before < 0)
			before = 0;
		int after = findFirstNonWordChar(text, offs);

		checkTypes(text, before, after);

	}

}
