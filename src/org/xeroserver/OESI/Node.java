/**
 * @author Daniel Englisch
 */

package org.xeroserver.OESI;

public class Node {

	public enum Type {
		ASSIGN, LITERAL, DEFINE, MAIN, TERM, FACTOR, PLUS, MINUS, MUL, DIV
	}

	public Node left, right, next;
	public Type type;
	public double value;
	public String ident;

	public Node() {
		this.type = Type.MAIN;
	}

	public Node(Type type, Node left, Node right) {
		this.type = type;
		this.left = left;
		this.right = right;
	}

	public Node(double value) {
		this.type = Type.LITERAL;
		this.value = value;
	}

	public Node(String ident) {
		this.type = Type.DEFINE;
		this.ident = ident;
	}

	public void append(Node n) {

		if (next != null) {
			next.append(n);
		} else
			next = n;

	}

	@SuppressWarnings("incomplete-switch")
	public void dump() {

		System.out.print(type);

		switch (type) {
		case LITERAL:
			System.out.print(" = " + value);
			break;
		case DEFINE:
			System.out.print(" " + ident);
			break;
		}

		System.out.print("\n");

		if (left != null) {
			System.out.print("\t");
			left.dump();
		}
		if (right != null) {
			System.out.print("\t");
			right.dump();
		}
		if (next != null) {
			next.dump();
		}
	}

}
