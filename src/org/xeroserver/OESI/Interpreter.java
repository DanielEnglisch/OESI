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

			if (curr.type == Type.DEFINE) {
				variables.add(new Variable(curr.ident));
			} else if (curr.type == Type.ASSIGN) {
				Variable v = getVar(curr.left.ident);
				if (v == null)
					System.err.println("Undifined variable " + curr.left.ident);
				else
					v.value = curr.right.value;
			}

			curr = curr.next;

		}

		varState();

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
