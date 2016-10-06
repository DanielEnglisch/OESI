package org.xeroserver.OESI;

public class Main {

	public static void main(String[] args) {	
		Scanner s = new Scanner(args[0]);
		Parser p = new Parser(s);
		p.Parse();
	}

}
