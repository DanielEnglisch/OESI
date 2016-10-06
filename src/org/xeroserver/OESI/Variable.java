package org.xeroserver.OESI;

public class Variable<T> {

	public Variable(String ident, T value) {

		System.out.println("Defined " + value.getClass().getSimpleName() + " " + ident + " " + value);

	}

	

}
