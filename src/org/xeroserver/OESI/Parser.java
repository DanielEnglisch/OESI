package org.xeroserver.OESI;

import org.xeroserver.OESI.Node.Type;

import java.util.HashMap;

public class Parser {
	public static final int _EOF = 0;
	public static final int _ident = 1;
	public static final int _intCon = 2;
	public static final int _realCon = 3;
	public static final int maxT = 12;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t; // last recognized token
	public Token la; // lookahead token
	int errDist = minErrDist;

	public Scanner scanner;
	public Errors errors;

	public Node main = new Node();

	// Editor Stuff
	public int getErrorCount() {
		return errors.count;
	}

	public HashMap<Integer, String> getErrorList() {
		return errors.errorList;
	}
	// --End Editor

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
		ass = null;
		Expect(1);
		name = t.val;
		Expect(7);
		Node exp = EXPRESSION();
		ass = new Node(Type.ASSIGN, new Node(name), exp);
		Expect(6);
		return ass;
	}

	Node EXPRESSION() {
		Node exp;
		Node trm1 = TERM();
		exp = trm1;
		while (la.kind == 8 || la.kind == 9) {
			Type operator = ADDOP();
			Node trm2 = TERM();
			exp = new Node(operator, trm1, trm2);
			trm1 = exp;
		}
		return exp;
	}

	Node TERM() {
		Node trm;
		Node fac1 = FACTOR();
		trm = fac1;
		while (la.kind == 10 || la.kind == 11) {
			Type operator = MULOP();
			Node fac2 = FACTOR();
			trm = new Node(operator, fac1, fac2);
			fac1 = trm;
		}
		return trm;
	}

	Type ADDOP() {
		Type operator;
		operator = null;
		if (la.kind == 8) {
			Get();
			operator = Type.PLUS;
		} else if (la.kind == 9) {
			Get();
			operator = Type.MINUS;
		} else
			SynErr(13);
		return operator;
	}

	Node FACTOR() {
		Node fac;
		fac = null;
		if (la.kind == 2) {
			Get();
			fac = new Node(Double.parseDouble(t.val));
		} else if (la.kind == 3) {
			Get();
			fac = new Node(Double.parseDouble(t.val));
		} else if (la.kind == 1) {
			Get();
			fac = new Node(t.val);
		} else
			SynErr(14);
		return fac;
	}

	Type MULOP() {
		Type operator;
		operator = null;
		if (la.kind == 10) {
			Get();
			operator = Type.MUL;
		} else if (la.kind == 11) {
			Get();
			operator = Type.DIV;
		} else
			SynErr(15);
		return operator;
	}

	public void Parse() {
		la = new Token();
		la.val = "";
		Get();
		OESI();
		Expect(0);

	}

	private static final boolean[][] set = { { _T, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x, _x }

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
			s = "\"blus\" expected";
			break;
		case 9:
			s = "\"minus\" expected";
			break;
		case 10:
			s = "\"moi\" expected";
			break;
		case 11:
			s = "\"duach\" expected";
			break;
		case 12:
			s = "??? expected";
			break;
		case 13:
			s = "invalid ADDOP";
			break;
		case 14:
			s = "invalid FACTOR";
			break;
		case 15:
			s = "invalid MULOP";
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
