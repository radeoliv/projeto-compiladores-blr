package test;

import parser.Parser;
import parser.SyntacticException;
import scanner.LexicalException;
import util.AST.AST;

@SuppressWarnings("unused")
public class ParserTest {
	public static void main(String[] args) {
		Parser parser = new Parser();
		try {
			AST ast = parser.parse();
		} catch (SyntacticException e) {
			e.printStackTrace();
		} catch (LexicalException e) {
			e.printStackTrace();
		}
	}

}
