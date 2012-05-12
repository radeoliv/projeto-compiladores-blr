package test;

import parser.Parser;
import parser.SyntacticException;
import scanner.LexicalException;
import checker.Checker;
import checker.SemanticException;

public class CheckerTest {
	public static void main(String[] args) {
		Parser parser = new Parser();
		Checker checker = new Checker();
		try {
			checker.check(parser.parse());
			System.out.print("Checker Correto!");
		} catch (SyntacticException e) {
			e.printStackTrace();
		} catch (LexicalException e) {
			e.printStackTrace();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}
}
