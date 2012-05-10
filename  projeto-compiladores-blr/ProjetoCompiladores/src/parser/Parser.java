package parser;

import java.util.ArrayList;
import classes.command.*;
import classes.expression.*;
import classes.functionDeclaration.FunctionDeclaration;
import classes.procedureCall.ProcedureCall;
import classes.program.Program;
import classes.terminal.*;
import classes.terminal.Number;
import scanner.*;
import util.AST.AST;

/**
 * Parser class
 * @version 2010-august-29
 * @discipline Projeto de Compiladores
 * @author Gustavo H P Carvalho
 * @email gustavohpcarvalho@ecomp.poli.br
 */
public class Parser {

	// The current token
	private Token currentToken = null;
	// The scanner
	private Scanner scanner = null;
	
	/**
	 * Parser constructor
	 */
	public Parser() {
		// Initializes the scanner object
		this.scanner = new Scanner();
		
		try {
			this.currentToken = scanner.getNextToken();
		} catch (LexicalException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Veririfes if the current token kind is the expected one
	 * @param kind
	 * @throws SyntacticException
	 * @throws LexicalException 
	 */
	private void accept(int kind) throws SyntacticException, LexicalException{
		if (currentToken.getKind() == kind)
			currentToken = scanner.getNextToken();
		else
			throw new SyntacticException ("Not expected token", currentToken);
	}
	
	/**
	 * Gets next token
	 * @throws LexicalException 
	 */
	private void acceptIt() throws LexicalException {
		currentToken = scanner.getNextToken();
	}

	/**
	 * Verifies if the source program is syntactically correct
	 * @throws SyntacticException
	 * @throws LexicalException 
	 */
	public AST parse() throws SyntacticException, LexicalException {
		Program programAST = parseProgram();
		accept (GrammarSymbols.EOT);
		return programAST;
	}
	
	private Program parseProgram () throws LexicalException, SyntacticException{		
		// 1ª MUDANÇA
		// program ::= command* functionDeclaration*
		
		ArrayList <FunctionDeclaration> functions = new ArrayList <FunctionDeclaration>();
		ArrayList <Command> commands = new ArrayList <Command>();
		
		while (currentToken.getKind() != GrammarSymbols.EOT){
			if (currentToken.getKind() == GrammarSymbols.FUNCTION){
				functions.add(parseFunctionDeclaration());
			}else{
				commands.add(parseCommand());
			}
		}
		Program programAST = new Program (commands, functions);
		return programAST;
	}
	
	private Command parseCommand () throws LexicalException, SyntacticException{
		
		Command commandAST = null;
		
		// 2ª MUDANÇA
		if (currentToken.getKind() == GrammarSymbols.ID){
			// command ::= identifier = expression
			
			Identifier id = new Identifier (currentToken);
			acceptIt();
			accept(GrammarSymbols.ASSIGNMENT);
			Expression e = parseExpression();
			commandAST = new AssignmentCommand(id,e);
			
		// 2ª MUDANÇA
		// command ::=  procedureCall	
		}else if(currentToken.getKind() == GrammarSymbols.CALL){
			commandAST = parseProcedureCall();
		
			
		// command ::= while expression do (command)* end
		}else if(currentToken.getKind() == GrammarSymbols.WHILE){
			acceptIt();
			Expression e = parseExpression();
			accept (GrammarSymbols.DO);
			
			ArrayList <Command> commands = new ArrayList <Command>();
			while (currentToken.getKind() != GrammarSymbols.END){
				commands.add(parseCommand());
			}
			commandAST = new WhileCommand(e,commands);
			acceptIt();
			
		// command ::= if expression then (command)* (else (command)*)? end
		}else if (currentToken.getKind() == GrammarSymbols.IF){
			acceptIt();
			Expression condition = parseExpression();
			accept(GrammarSymbols.THEN);
			
			ArrayList <Command> ifCommands = new ArrayList <Command>();
			ArrayList <Command> elseCommands = new ArrayList <Command>();
			while (currentToken.getKind() != GrammarSymbols.ELSE && currentToken.getKind() != GrammarSymbols.END){
				ifCommands.add(parseCommand());
			}
			if (currentToken.getKind() == GrammarSymbols.ELSE){
				acceptIt();

				while (currentToken.getKind() != GrammarSymbols.END){
					elseCommands.add(parseCommand());
				}
			}
			
			commandAST = new IfCommand (condition, ifCommands, elseCommands);
			acceptIt();
			
		// command ::= return (expression)? ;
		}else if (currentToken.getKind() == GrammarSymbols.RETURN){
			acceptIt();
			Expression e = null;
			if(currentToken.getKind() != GrammarSymbols.SEMICOLON){
				e = parseExpression();
			}
			
			commandAST = new ReturnCommand(e);
			accept(GrammarSymbols.SEMICOLON);
			
		// command ::= break
		}else if (currentToken.getKind() == GrammarSymbols.BREAK){
			acceptIt();
			commandAST = new BreakCommand();
			
		// command ::= print ( (expression)? )
		}else if (currentToken.getKind() == GrammarSymbols.PRINT){
			acceptIt();
			accept (GrammarSymbols.LEFT_PARENTHESIS);
			Expression e = null;
			
			if (currentToken.getKind() != GrammarSymbols.RIGHT_PARENTHESIS){
				e = parseExpression();
			}
			
			commandAST = new PrintCommand (e);
			accept(GrammarSymbols.RIGHT_PARENTHESIS);
			
		}else{
			throw new SyntacticException("Not expected token!", currentToken);
		}
		return commandAST;
	}
	
	// 3ª MUDANÇA
	private FunctionDeclaration parseFunctionDeclaration () throws LexicalException, SyntacticException{
		//functionDeclaration ::= function identifier ( (parameters)? ) (command)* (return (expression)?)? end
		
		accept(GrammarSymbols.FUNCTION);
		Identifier id = new Identifier(currentToken);
		accept(GrammarSymbols.ID);
		accept(GrammarSymbols.LEFT_PARENTHESIS);
		
		ArrayList<Identifier> parameters = new ArrayList<Identifier>();
		ArrayList<Command> commands = new ArrayList<Command>();
		Expression e = null;
		
		if(currentToken.getKind() != GrammarSymbols.RIGHT_PARENTHESIS){
			parameters = parseParameters();
		}
		accept(GrammarSymbols.RIGHT_PARENTHESIS);
		
		while (currentToken.getKind() != GrammarSymbols.END && currentToken.getKind() != GrammarSymbols.RETURN){
			commands.add(parseCommand());
		}
		
		if (currentToken.getKind() == GrammarSymbols.RETURN){
			acceptIt();
			if (currentToken.getKind() != GrammarSymbols.END){
				e = parseExpression (); 
			}
		}
		accept (GrammarSymbols.END);
		
		FunctionDeclaration fd = new FunctionDeclaration (id, parameters, commands, e);
		//acceptIt(); ???? TAVA FAZENDO OQ AQUI?
		return fd;
	}
	
	private ArrayList <Identifier> parseParameters () throws LexicalException, SyntacticException{
		// parameters ::= identifier ( , identifier)*
		
		ArrayList<Identifier> identifiers = new ArrayList<Identifier>();
		
		if(currentToken.getKind() == GrammarSymbols.ID){
			identifiers.add(new Identifier(currentToken));
			acceptIt();
			
			while (currentToken.getKind() == GrammarSymbols.COMMA){
				acceptIt();
				if(currentToken.getKind() == GrammarSymbols.ID){
					identifiers.add(new Identifier(currentToken));
					acceptIt();
				} else {
					throw new SyntacticException("Not expected token!", currentToken);
				}
			}

		} else {
			throw new SyntacticException("Not expected token!", currentToken);
		}

		return identifiers;
	}
	
	// 2ª MUDANÇA
	private ProcedureCall parseProcedureCall() throws LexicalException, SyntacticException{
		//procedureCall ::= call identifier ( (arguments)? )
		ProcedureCall fc;
		ArrayList<Expression> arguments = new ArrayList<Expression>();
	
		accept(GrammarSymbols.CALL);
		Identifier id = new Identifier(currentToken);
		accept(GrammarSymbols.ID);
		accept(GrammarSymbols.LEFT_PARENTHESIS);
			
		if(currentToken.getKind() != GrammarSymbols.RIGHT_PARENTHESIS){
			arguments = parseArguments();
		}
		accept(GrammarSymbols.RIGHT_PARENTHESIS);
		fc = new ProcedureCall (id, arguments);
		
		return fc;
	}
	
	private ArrayList<Expression> parseArguments() throws LexicalException, SyntacticException{
		// arguments ::=  expression ( , expression)*
		
		ArrayList<Expression> expressions = new ArrayList<Expression>();
		
		expressions.add(parseExpression());
		
		while (currentToken.getKind() == GrammarSymbols.COMMA){
			acceptIt();
			expressions.add(parseExpression());
		}
		return expressions;
	}
	
	private Expression parseExpression() throws LexicalException, SyntacticException{
		// expression ::= relationalExpression
		return parseRelationalExpression();
	}
	
	private Expression parseRelationalExpression() throws LexicalException, SyntacticException{
		// relationalExpression ::= additiveExpression (relationalOperator additiveExpression)*
		
		Expression leftE = parseAdditiveExpression();
		Operator op = null;
		Expression rightE = null;
		BinaryExpression binaryE = new BinaryExpression(leftE, op, rightE);
		
		
		while (currentToken.getKind() == GrammarSymbols.RELATIONAL_OPERATOR){
			op = new Operator(currentToken);
			acceptIt();
			rightE = parseAdditiveExpression();
			binaryE = new BinaryExpression(leftE, op, rightE);
			leftE = binaryE;
		}

		return binaryE;
	}
	
	private Expression parseAdditiveExpression () throws LexicalException, SyntacticException{
		// additiveExpression ::= multiplicativeExpression (additiveOperator multiplicativeExpression)*
		
		Expression leftE = parseMultiplicativeExpression ();
		Operator op = null;
		Expression rightE = null;
		BinaryExpression binaryE = new BinaryExpression(leftE, op, rightE);
		
		while (currentToken.getKind() == GrammarSymbols.ADDITIVE_OPERATOR){
			op = new Operator(currentToken);
			acceptIt();
			rightE = parseMultiplicativeExpression();
			binaryE = new BinaryExpression(leftE, op, rightE);
			leftE = binaryE;
		}

		return binaryE;
	}
	
	private Expression parseMultiplicativeExpression () throws LexicalException, SyntacticException{
		// multiplicativeExpression ::= baseExpression (multiplicativeOperator baseExpression)*
		
		Expression leftE = parseBaseExpression();
		Operator op = null;
		Expression rightE = null;
		BinaryExpression binaryE = new BinaryExpression(leftE, op, rightE);
		
		while (currentToken.getKind() == GrammarSymbols.MULTIPLICATIVE_OPERATOR){
			op = new Operator(currentToken);
			acceptIt();
			rightE = parseBaseExpression();
			binaryE = new BinaryExpression(leftE, op, rightE);
			leftE = binaryE;
		}
		
		return binaryE;
	}
	
	// 4ª MUDANÇA
	private Expression parseBaseExpression () throws LexicalException, SyntacticException{
		// baseExpression ::= ( expression ) | number | identifier
		// baseExpression ::= ( expression ) | number | identifier (nil | ( (arguments)? ) )
		
		Expression e = null;
		
		if (currentToken.getKind() == GrammarSymbols.LEFT_PARENTHESIS){
			acceptIt();
			e = parseExpression();
			accept(GrammarSymbols.RIGHT_PARENTHESIS);
			
		}else if (currentToken.getKind() == GrammarSymbols.INT || currentToken.getKind() == GrammarSymbols.FLOAT){
			Number n = new Number (currentToken);
			e = new UnaryExpressionNumber(n);
			acceptIt();
			
		}else if(currentToken.getKind() == GrammarSymbols.ID){
			Identifier id = new Identifier(currentToken);
			ArrayList<Expression> arguments;
			
			acceptIt ();
			if (currentToken.getKind() == GrammarSymbols.LEFT_PARENTHESIS){
				acceptIt();
				if (currentToken.getKind() != GrammarSymbols.RIGHT_PARENTHESIS){
					arguments = parseArguments();
				}
				accept (GrammarSymbols.RIGHT_PARENTHESIS);
			}
						
			// :TODO MUDARRRRRRRRRRRR A CRIAÇÃOOOOOOOOOOOOOOO
			e = new UnaryExpressionId(id);
			//acceptIt(); ???? TAVA FAZENDO OQ AQUI?
			
		}else{
			throw new SyntacticException("Not expected token!", currentToken);
		}
		
		return e;
	}
}
