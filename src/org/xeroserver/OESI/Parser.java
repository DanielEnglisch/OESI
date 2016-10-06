package org.xeroserver.OESI;

import org.xeroserver.OESI.Node.Type;

import java.util.HashMap;

public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _intCon = 2;
	public static final int _realCon = 3;
	public static final int maxT = 8;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t; // last recognized token
	public Token la; // lookahead token
	int errDist = minErrDist;

	public Scanner scanner;
	public Errors errors;

	public Node main = new Node();

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr(int n) {
		if (errDist >= minErrDist)
			errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr(String msg) {
		if (errDist >= minErrDist)
			errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}

	void Get() {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}

	void Expect(int n) {
		if (la.kind == n)
			Get();
		else {
			SynErr(n);
		}
	}

	boolean StartOf(int s) {
		return set[s][la.kind];
	}

	void ExpectWeak(int n, int follow) {
		if (la.kind == n)
			Get();
		else {
			SynErr(n);
			while (!StartOf(follow))
				Get();
		}
	}

	boolean WeakSeparator(int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) {
			Get();
			return true;
		} else if (StartOf(repFol))
			return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}

	public int Errors() {
		return errors.count;
	}

	void OESI() {
		while (la.kind == 1 || la.kind == 4) {
			if (la.kind == 4) {
				Node def = DEFINE();
				main.append(def);
			} else {
				Node ass = ASSIGN();
				main.append(ass);
			}
		}
	}

	Node DEFINE() {
		Node def;
		Expect(4);
		Expect(5);
		Expect(1);
		def = new Node(t.val);
		Expect(6);
		return def;
	}

	Node ASSIGN() {
		Node ass;
		String name = "";
		Expect(1);
		name = t.val;
		Expect(7);
		if (la.kind == 2) {
			Get();
		} else if (la.kind == 3) {
			Get();
		} else
			SynErr(9);
		ass = new Node(Type.ASSIGN, new Node(name), new Node(Double.parseDouble(t.val)));
		Expect(6);
		return ass;
	}

	public void Parse() {
		la = new Token();
		la.val = "";
		Get();
		OESI();
		Expect(0);

	}

	private static final boolean[][] set = { { _T, _x, _x, _x, _x, _x, _x, _x, _x, _x }

	};
} // end Parser

class Errors {
	public int count = 0; // number of errors detected
	public java.io.PrintStream errorStream = System.out; // error messages go to
															// this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line,
																// 1=column,
																// 2=text
	public HashMap<Integer, String> errorList = new HashMap<Integer, String>();

	public HashMap<Integer, String> getErrorList() {
		return errorList;
	}

	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) {
			b.delete(pos, pos + 3);
			b.insert(pos, line);
		}
		pos = b.indexOf("{1}");
		if (pos >= 0) {
			b.delete(pos, pos + 3);
			b.insert(pos, column);
		}
		pos = b.indexOf("{2}");
		if (pos >= 0)
			b.replace(pos, pos + 3, msg);
		errorStream.println(b.toString());
		errorList.put(line, b.toString()); // x0
	}

	public void SynErr(int line, int col, int n) {
		String s;
		switch (n) {
		case 0:
			s = "EOF expected";
			break;
		case 1:
			s = "ident expected";
			break;
		case 2:
			s = "intCon expected";
			break;
		case 3:
			s = "realCon expected";
			break;
		case 4:
			s = "\"es\" expected";
			break;
		case 5:
			s = "\"gibt\" expected";
			break;
		case 6:
			s = "\";\" expected";
			break;
		case 7:
			s = "\"is\" expected";
			break;
		case 8:
			s = "??? expected";
			break;
		case 9:
			s = "invalid ASSIGN";
			break;
		default:
			s = "error " + n;
			break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr(int line, int col, String s) {
		printMsg(line, col, s);
		count++;
	}

	public void SemErr(String s) {
		errorStream.println(s);
		count++;
	}

	public void Warning(int line, int col, String s) {
		printMsg(line, col, s);
	}

	public void Warning(String s) {
		errorStream.println(s);
	}
} // Errors

class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;

	public FatalError(String s) {
		super(s);
	}
}
