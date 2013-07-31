package org.tzi.kodkod.helper;

import kodkod.ast.Node;
import kodkod.util.nodes.PrettyPrinter;

public class PrintHelper {

	public static String prettyKodkod(Node node) {
		return PrettyPrinter.print(node, 0);
	}
}
