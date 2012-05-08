package test;

import scanner.LexicalException;
import scanner.Scanner;
import scanner.Token;

public class ScannerTest {
	public static void main(String[] args) throws LexicalException {
		Scanner scanner = new Scanner();
		Token token = null;
		do{
			try {
				token = scanner.getNextToken();
				System.out.println(token.getKind() + " ->  " + token.getSpelling());
			} catch (LexicalException e) {
				e.printStackTrace();
				break;
			}
		}while(!scanner.isEOT());
	}
}
