package parser;


/**
 * This class contains codes for each grammar terminal
 * @version 2010-september-04
 * @discipline Compiladores
 * @author Gustavo H P Carvalho
 * @email gustavohpcarvalho@ecomp.poli.br
 */
public class GrammarSymbols {

	// Language terminals (starts from 0)
	public static final int ID = 0;
	public static final int INT = 1;
	public static final int FLOAT = 2;

	//ADDITIVE_OPERATOR
	public static final int PLUS = 3;
	public static final int MINUS = 4;
	
	//MULTIPLICATIVE_OPERATOR
	public static final int MULTIPLICATION = 5;
	public static final int DIVISION = 6;

	public static final int ASSIGNMENT = 7;
	
	//RELATIONAL_OPERATOR
	public static final int EQUALS = 8;
	public static final int DIFFERENT = 9;
	public static final int GREATER_THAN = 10;
	public static final int GREATER_THAN_OR_EQUAL_TO = 11;
	public static final int LESS_THAN = 12;
	public static final int LESS_THAN_OR_EQUAL_TO = 13;
	
	public static final int COMMA = 14;
	public static final int LEFT_PARENTHESIS = 15;
	public static final int RIGHT_PARENTHESIS = 16;
	
	public static final int SEMICOLON = 980;
	
	// Reserved
	public static final int IF = 985;
	public static final int THEN = 986;
	public static final int ELSE = 987;
	public static final int FUNCTION = 988;
	public static final int END = 989;
	public static final int RETURN = 990;
	public static final int WHILE = 991;
	public static final int DO = 992;
	public static final int BREAK = 993;
	public static final int PRINT = 994;
	
	// Token types
	public static final int MULTIPLICATIVE_OPERATOR = 995;
	public static final int ADDITIVE_OPERATOR = 996;
	public static final int RELATIONAL_OPERATOR = 997;
	
	public static final int ERROR = 999;
	public static final int EOT = 1000;
	
	
}

