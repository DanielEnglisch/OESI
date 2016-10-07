/**
 * @author Daniel Englisch
 */

package org.xeroserver.OESI;

import java.util.ArrayList;

import org.xeroserver.OESI.Node.Type;

public class Interpreter {

	private ArrayList<Variable> variables = new ArrayList<Variable>();

	public void process(Node main) {

		Node curr = main.next;
		while (curr != null) {

			switch (curr.type) {
			case DEFINE:
				DEFINE(curr.ident);
				break;
			case ASSIGN:
				ASSIGN(curr);
				break;
			default:
				return;
			}

			curr = curr.next;
		}

		// Dumps values of defined variables
		varState();

	}

	private void DEFINE(String ident) {
		if (getVar(ident) == null)
			variables.add(new Variable(ident));
		else
			System.err.println("Variable " + ident + " already defined!");
	}

	private void ASSIGN(Node curr) {

		if (!exists(curr.left.ident)) {
			System.err.println("Undefined variable " + curr.left.ident);
			return;
		}

		switch (curr.right.type) {
		case DEFINE: {
			if (!exists(curr.right.ident)) {
				System.err.println("Undefined variable " + curr.left.ident);
				return;
			} else {
				getVar(curr.left.ident).value = getVar(curr.right.ident).value;
			}
		}
			break;
		case LITERAL: {
			getVar(curr.left.ident).value = curr.right.value;
		}
			break;

		default: {
			getVar(curr.left.ident).value = getExpressionValue(curr.right);
		}
			break;
		}

	}

	private boolean isExpType(Type t) {
		return (t == Type.MUL || t == Type.DIV || t == Type.PLUS || t == Type.MINUS);
	}

	private double getExpressionValue(Node exp) {
		double l = 0, r = 0;
		boolean expl = false, explr = false;

		if (isExpType(exp.left.type)) {
			l = getExpressionValue(exp.left);
			expl = true;
		}
		if (isExpType(exp.right.type)) {
			r = getExpressionValue(exp.right);
			explr = true;
		}

		if (expl && explr) {
			switch (exp.type) {
			case MUL:
				return l * r;
			case DIV:
				return l / r;
			case PLUS:
				return l + r;
			case MINUS:
				return l - r;

			default:
				System.err.println("Error in expression calculation");
				break;
			}
		} else if (expl && !explr) {
			switch (exp.type) {
			case MUL:
				return l * getVarValueIfExists(exp.right);
			case DIV:
				return l / getVarValueIfExists(exp.right);
			case PLUS:
				return l + getVarValueIfExists(exp.right);
			case MINUS:
				return l - getVarValueIfExists(exp.right);

			default:
				System.err.println("Error in expression calculation");
				break;
			}
		} else if (!expl && explr) {
			switch (exp.type) {
			case MUL:
				return getVarValueIfExists(exp.left) * r;
			case DIV:
				return getVarValueIfExists(exp.left) / r;
			case PLUS:
				return getVarValueIfExists(exp.left) + r;
			case MINUS:
				return getVarValueIfExists(exp.left) - r;

			default:
				System.err.println("Error in expression calculation");
				break;
			}
		} else {
			switch (exp.type) {
			case MUL:
				return getVarValueIfExists(exp.left) * getVarValueIfExists(exp.right);
			case DIV:
				return getVarValueIfExists(exp.left) / getVarValueIfExists(exp.right);
			case PLUS:
				return getVarValueIfExists(exp.left) + getVarValueIfExists(exp.right);
			case MINUS:
				return getVarValueIfExists(exp.left) - getVarValueIfExists(exp.right);

			default:
				System.err.println("Error in expression calculation");
				break;
			}
		}

		return 0;
	}

	private double getVarValueIfExists(Node n) {

		if (n.type == Type.DEFINE) {

			if (exists(n.ident))
				return getVar(n.ident).value;
			else
				System.err.println("Undefined variable " + n.ident);
		}

		return n.value;
	}

	private boolean exists(String ident) {

		if (getVar(ident) == null)
			return false;
		return true;
	}

	private Variable getVar(String ident) {
		for (Variable v : variables) {
			if (v.ident.equals(ident))
				return v;
		}
		return null;
	}

	private void varState() {
		System.out.println("\n" + "===VARDUMP===");
		for (Variable v : variables) {
			System.out.println("Var " + v.ident + " = " + v.value);
		}
	}
}

class Variable {

	public double value;
	public String ident;

	public Variable(String ident) {
		this.ident = ident;
	}
}
