package scanner;

import compiler.Properties;
import compiler.Compiler;
import parser.GrammarSymbols;
import util.Arquivo;
import util.symbolsTable.IdentificationTable;

/**
 * Scanner class
 * 
 * @version 2010-september-04
 * @discipline Compiladores
 * @author Gustavo H P Carvalho
 * @email gustavohpcarvalho@ecomp.poli.br
 */
public class Scanner {

	// The file object that will be used to read the source code
	private Arquivo file;
	// The last char read from the source code
	private char currentChar;
	// The kind of the current token
	private int currentKind;
	// Buffer to append characters read from file
	private StringBuffer currentSpelling;
	// Current line and column in the source file
	private int line, column;
	// Created to correct bug from error at the last column of a line
	private int oldLine, oldColumn;
	private String errorDescription;
	private IdentificationTable reservedTable;

	/**
	 * Default constructor
	 */
	public Scanner() {
		this.file = new Arquivo(Properties.sourceCodeLocation);
		this.line = 0;
		this.column = 0;
		this.currentChar = this.file.readChar();
		this.reservedTable = new IdentificationTable();
		this.errorDescription = "";
	}

	/**
	 * Returns the next token
	 * 
	 * @return
	 */
	// TODO
	public Token getNextToken() throws LexicalException {
		Token token;
		// Initializes the string buffer
		currentSpelling = new StringBuffer();
		// Ignores separators
		if (isSeparator(this.currentChar)) {
			scanSeparator();
		}
		// Clears the string buffer
		this.currentSpelling = new StringBuffer();
		// Scans the next token
		int kind = scanToken();

		if (kind != GrammarSymbols.ERROR) {
			token = new Token(kind, this.currentSpelling.toString(), this.line,
					this.column);
		} else {
			/*throw new LexicalException(this.errorDescription, this.currentChar,
					this.line, this.column);*/
			throw new LexicalException(this.errorDescription, this.currentChar,
					this.oldLine, this.oldColumn);
			
		}
		// Creates and returns a token for the lexema identified
		return token;
	}

	/**
	 * Returns if a character is a separator
	 * 
	 * @param c
	 * @return
	 */
	private boolean isSeparator(char c) {
		// A comment line is characterized by #
		if (c == '#' || c == ' ' || c == '\n' || c == '\t') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reads (and ignores) a separator
	 * 
	 * @throws LexicalException
	 */
	private void scanSeparator() throws LexicalException {
		do {
			if (this.currentChar == '#') {
				getNextChar();
				while (isGraphic(this.currentChar)) {
					if (this.currentChar == '\n') {
						break;
					}
					getNextChar();
				}
			}
			getNextChar();
		} while (isSeparator(currentChar));
		// If it is a comment line
		// Gets next char
		// Reads characters while they are graphics or '\t'
		// A command line should finish with a \n
	}

	/**
	 * Gets the next char
	 */
	private void getNextChar() {
		// Appends the current char to the string buffer
		this.currentSpelling.append(this.currentChar);
		// Reads the next one
		this.currentChar = this.file.readChar();
		// Increments the line and column
		this.incrementLineColumn();
	}

	/**
	 * Increments line and column
	 */
	private void incrementLineColumn() {
		// If the char read is a '\n', increments the line variable and assigns
		// 0 to the column
		oldLine = this.line;
		oldColumn = this.column;
		if (this.currentChar == '\n') {
			this.line++;
			this.column = 0;
			// If the char read is not a '\n'
		} else {
			// If it is a '\t', increments the column by 4
			if (this.currentChar == '\t') {
				this.column = this.column + 4;
				// If it is not a '\t', increments the column by 1
			} else {
				this.column++;
			}
		}
	}

	/**
	 * Returns if a char is a digit (between 0 and 9)
	 * @param c
	 * @return
	 */
	private boolean isDigit(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns if a char is a letter (between a and z or between A and Z)
	 * @param c
	 * @return
	 */
	private boolean isLetter(char c) {
		if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns if a char is a graphic (any ASCII visible character)
	 * @param c
	 * @return
	 */
	private boolean isGraphic(char c) {
		if (c >= ' ' && c <= '~') {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Scans the next token Simulates the DFA that recognizes the language
	 * described by the lexical grammar
	 * @return
	 * @throws LexicalException
	 */
	private int scanToken() throws LexicalException {
		
		// My automata initial state is -1
		// boolean error -> code enhancement
		// States 17 and 18 are not finals! So they don't have constants to
		// define them.
		
		int kind = -1;
		boolean jump = false;

		while (true) {
			switch (kind) {
			case -1:
				if (isLetter(this.currentChar) || this.currentChar == '_') {
					kind = GrammarSymbols.ID;
				} else if (isDigit(this.currentChar)) {
					kind = GrammarSymbols.INT;
				} else if (this.currentChar == '+') {
					kind = GrammarSymbols.PLUS;
				} else if (this.currentChar == '-') {
					kind = GrammarSymbols.MINUS;
				} else if (this.currentChar == '*') {
					kind = GrammarSymbols.MULTIPLICATION;
				} else if (this.currentChar == '/') {
					kind = GrammarSymbols.DIVISION;
				} else if (this.currentChar == '=') {
					kind = GrammarSymbols.ASSIGNMENT;
				} else if (this.currentChar == '>') {
					kind = GrammarSymbols.GREATER_THAN;
				} else if (this.currentChar == '<') {
					kind = GrammarSymbols.LESS_THAN;
				} else if (this.currentChar == ',') {
					kind = GrammarSymbols.COMMA;
				} else if (this.currentChar == '(') {
					kind = GrammarSymbols.LEFT_PARENTHESIS;
				} else if (this.currentChar == ')') {
					kind = GrammarSymbols.RIGHT_PARENTHESIS;
				} else if (this.currentChar == '~') {
					kind = 17;
				} else if(this.currentChar == ';'){
					kind = GrammarSymbols.SEMICOLON;
				} else if (this.currentChar == '\000') {
					kind = GrammarSymbols.EOT;
					jump = true;
				} else {
					kind = GrammarSymbols.ERROR;
					this.errorDescription = "Invalid Character!";
					jump = true;
				}
				if (!jump)
					getNextChar();
				break;

			case GrammarSymbols.ID:
				if (isDigit(this.currentChar) || isLetter(this.currentChar)
						|| this.currentChar == '_') {
					kind = GrammarSymbols.ID;
					getNextChar();
				} else {
					// Verifying reserved words
					return this.reservedTable.containsKey(this.currentSpelling
							.toString()) ? this.reservedTable
							.getReservedWordValue(this.currentSpelling.toString())
							: GrammarSymbols.ID;
				}
				break;

			case GrammarSymbols.INT:
				if (isDigit(this.currentChar)) {
					kind = GrammarSymbols.INT;
					getNextChar();
				} else if (this.currentChar == '.') {
					kind = 18;
					getNextChar();
				} else {
					return GrammarSymbols.INT;
				}
				break;

			case GrammarSymbols.FLOAT:
				if (isDigit(this.currentChar)) {
					kind = GrammarSymbols.FLOAT;
					getNextChar();
				} else {
					return GrammarSymbols.FLOAT;
				}
				break;

			case GrammarSymbols.PLUS:
				return GrammarSymbols.ADDITIVE_OPERATOR;

			case GrammarSymbols.MINUS:
				return GrammarSymbols.ADDITIVE_OPERATOR;

			case GrammarSymbols.MULTIPLICATION:
				return GrammarSymbols.MULTIPLICATIVE_OPERATOR;

			case GrammarSymbols.DIVISION:
				return GrammarSymbols.MULTIPLICATIVE_OPERATOR;

			case GrammarSymbols.ASSIGNMENT:
				if (this.currentChar == '=') {
					kind = GrammarSymbols.EQUALS;
					getNextChar();
				} else {
					return GrammarSymbols.ASSIGNMENT;
				}
				break;

			case GrammarSymbols.EQUALS:
				return GrammarSymbols.RELATIONAL_OPERATOR;

			case GrammarSymbols.DIFFERENT:
				return GrammarSymbols.RELATIONAL_OPERATOR;

			case GrammarSymbols.GREATER_THAN:
				if (this.currentChar == '=') {
					kind = GrammarSymbols.GREATER_THAN_OR_EQUAL_TO;
					getNextChar();
				} else {
					return GrammarSymbols.RELATIONAL_OPERATOR;
				}
				break;

			case GrammarSymbols.GREATER_THAN_OR_EQUAL_TO:
				return GrammarSymbols.RELATIONAL_OPERATOR;

			case GrammarSymbols.LESS_THAN:
				if (this.currentChar == '=') {
					kind = GrammarSymbols.LESS_THAN_OR_EQUAL_TO;
					getNextChar();
				} else {
					return GrammarSymbols.RELATIONAL_OPERATOR;
				}
				break;

			case GrammarSymbols.LESS_THAN_OR_EQUAL_TO:
				return GrammarSymbols.RELATIONAL_OPERATOR;

			case GrammarSymbols.COMMA:
				return GrammarSymbols.COMMA;

			case GrammarSymbols.LEFT_PARENTHESIS:
				return GrammarSymbols.LEFT_PARENTHESIS;

			case GrammarSymbols.RIGHT_PARENTHESIS:
				return GrammarSymbols.RIGHT_PARENTHESIS;

			case 17:
				if (this.currentChar == '=') {
					kind = GrammarSymbols.DIFFERENT;
					getNextChar();
				} else {
					kind = GrammarSymbols.ERROR;
					this.errorDescription = "Ops! '=' (ASSIGMENT) expected after " + "'"+ this.currentSpelling + "'";
				}
				break;

			case 18:
				if (isDigit(this.currentChar)) {
					kind = GrammarSymbols.FLOAT;
					getNextChar();
				} else {
					kind = GrammarSymbols.ERROR;
					this.errorDescription = "Ops! 'DIGIT' expected after " + "'"+ this.currentSpelling + "'";
				}
				break;
			
			case GrammarSymbols.SEMICOLON:
				return GrammarSymbols.SEMICOLON;
				
			case GrammarSymbols.ERROR:
				return GrammarSymbols.ERROR;

			case GrammarSymbols.EOT:
				return GrammarSymbols.EOT;
			}
		}
	}

	public boolean isEOT(){
		return this.currentChar =='\000';
	}
	
	/**
	 * Generated getters and setters
	 */
	public Arquivo getFile() {
		return file;
	}

	public void setFile(Arquivo file) {
		this.file = file;
	}

	public char getCurrentChar() {
		return currentChar;
	}

	public void setCurrentChar(char currentChar) {
		this.currentChar = currentChar;
	}

	public int getCurrentKind() {
		return currentKind;
	}

	public void setCurrentKind(int currentKind) {
		this.currentKind = currentKind;
	}

	public StringBuffer getCurrentSpelling() {
		return currentSpelling;
	}

	public void setCurrentSpelling(StringBuffer currentSpelling) {
		this.currentSpelling = currentSpelling;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

}
