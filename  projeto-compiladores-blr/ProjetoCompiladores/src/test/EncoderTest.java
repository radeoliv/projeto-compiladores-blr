package test;

import parser.Parser;
import parser.SyntacticException;
import scanner.LexicalException;
import checker.Checker;
import checker.SemanticException;
import encoder.Encoder;

public class EncoderTest {

	
	public static void main(String[] args) {
		Parser parser = new Parser();
		Checker checker = new Checker();
		Encoder encoder = new Encoder();
		try {
			encoder.encode(checker.check(parser.parse()));
		} catch (SyntacticException e) {
			e.printStackTrace();
		} catch (LexicalException e) {
			e.printStackTrace();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
