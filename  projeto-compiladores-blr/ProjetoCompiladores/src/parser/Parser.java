package parser;

import java.util.ArrayList;
import classes.command.*;
import classes.expression.*;
import classes.functionDeclaration.FunctionDeclaration;
import classes.functionDeclaration.Parameter;
import classes.procedureCall.ProcedureCall;
import classes.program.Program;
import classes.root.Root;
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
	 * Verifies if the current token kind is the expected one
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
		Root rootAST = parseRoot();
		accept(GrammarSymbols.EOT);
		return rootAST;
	}
	
	private Root parseRoot() throws LexicalException, SyntacticException{
		// root ::= (program)*
		ArrayList <Program> programs = new ArrayList <Program>();
		
		while (currentToken.getKind() != GrammarSymbols.EOT){
			programs.add(parseProgram());
		}
		return new Root(programs);
	}
	private Program parseProgram() throws LexicalException, SyntacticException{		
		
		// program ::= command | functionDeclaration
		Program program = null;
		
		if (currentToken.getKind() == GrammarSymbols.FUNCTION){
			program = new Program(parseFunctionDeclaration());
		}else{
			program = new Program(parseCommand());
		}
		
		return program;
	}
	
	private Command parseCommand () throws LexicalException, SyntacticException{
		
		Command commandAST = null;
		
		if (currentToken.getKind() == GrammarSymbols.ID){
			// command ::= identifier = expression
			
			Identifier id = new Identifier (currentToken);
			acceptIt();
			accept(GrammarSymbols.ASSIGNMENT);
			Expression e = parseExpression();
			commandAST = new AssignmentCommand(id,e);
			
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
	
	private FunctionDeclaration parseFunctionDeclaration() throws LexicalException, SyntacticException{
		//functionDeclaration ::= function identifier ( (parameters)? ) (command)* (return expression)? end

		Identifier id = null;
		accept(GrammarSymbols.FUNCTION);
		if(currentToken.getKind() == GrammarSymbols.ID){
			id = new Identifier(currentToken);
			acceptIt();
		}else{
			throw new SyntacticException("Identifier expected!", currentToken);
		}
		accept(GrammarSymbols.LEFT_PARENTHESIS);
		
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		ArrayList<Command> commands = new ArrayList<Command>();
		Expression returnExp = null;
		
		if(currentToken.getKind() != GrammarSymbols.RIGHT_PARENTHESIS){
			parameters = parseParameters(); 
		}
		accept(GrammarSymbols.RIGHT_PARENTHESIS);
		
		while (currentToken.getKind() != GrammarSymbols.END && currentToken.getKind() != GrammarSymbols.RETURN){
			commands.add(parseCommand());
		}
		
		if (currentToken.getKind() == GrammarSymbols.RETURN){
			acceptIt();
			returnExp = parseExpression();
		}
		accept(GrammarSymbols.END);
		
		FunctionDeclaration fd = new FunctionDeclaration (id, parameters, commands, returnExp);
		
		return fd;
	}
	
	private ArrayList<Parameter> parseParameters() throws LexicalException, SyntacticException{
		// parameters ::= parameter ( , parameter)*
		
		ArrayList<Parameter> parameters = new ArrayList<Parameter>();
		int parameterType = -1;

		if(currentToken.getKind() == GrammarSymbols.INT){
			parameterType = GrammarSymbols.INT;
			acceptIt();
		} else if(currentToken.getKind() == GrammarSymbols.FLOAT){
			parameterType = GrammarSymbols.FLOAT;
			acceptIt();
		} else {
			throw new SyntacticException("'int' or 'float' type definition expected!", currentToken);
		}
		
		if(currentToken.getKind() == GrammarSymbols.ID){
			parameters.add(new Parameter(new Identifier(currentToken), parameterType));
			acceptIt();

			while (currentToken.getKind() == GrammarSymbols.COMMA){
				acceptIt();
				
				if(currentToken.getKind() == GrammarSymbols.INT){
					parameterType = GrammarSymbols.INT;
					acceptIt();
				} else if(currentToken.getKind() == GrammarSymbols.FLOAT){
					parameterType = GrammarSymbols.FLOAT;
					acceptIt();
				} else {
					throw new SyntacticException("'int' or 'float' type definition expected!", currentToken);
				}
				
				if(currentToken.getKind() == GrammarSymbols.ID){
					parameters.add(new Parameter(new Identifier(currentToken), parameterType));
					acceptIt();
				} else {
					throw new SyntacticException("Not expected token!", currentToken);
				}
			}

		} else {
			throw new SyntacticException("Not expected token!", currentToken);
		}

		return parameters;
	}
	
	private ProcedureCall parseProcedureCall() throws LexicalException, SyntacticException{
		//procedureCall ::= call identifier ( (arguments)? )
		ProcedureCall fc = null;
		ArrayList<Expression> arguments = new ArrayList<Expression>();
		Identifier id = null;
		accept(GrammarSymbols.CALL);
		
		if(currentToken.getKind() == GrammarSymbols.ID){
			id = new Identifier(currentToken);
			acceptIt();
		} else {
			throw new SyntacticException("Identifier expected!", currentToken);
		}
		
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
		//BinaryExpression binaryE = new BinaryExpression(leftE, op, rightE);
		
		UnaryExpression unaryE = null;
		BinaryExpression binaryE = null;
		
		if(leftE instanceof UnaryExpression)
			unaryE = (UnaryExpression)leftE;
		else
			binaryE = (BinaryExpression)leftE;
		
		while (currentToken.getKind() == GrammarSymbols.RELATIONAL_OPERATOR){
			op = new Operator(currentToken);
			acceptIt();
			rightE = parseAdditiveExpression();
			binaryE = new BinaryExpression(leftE, op, rightE);
			leftE = binaryE;
		}
		
		Expression e;
		if(binaryE != null){
			e = binaryE;
		} else {
			e = unaryE;
		}
		
		return e;
	}
	
	private Expression parseAdditiveExpression () throws LexicalException, SyntacticException{
		// additiveExpression ::= multiplicativeExpression (additiveOperator multiplicativeExpression)*
		
		Expression leftE = parseMultiplicativeExpression ();
		Operator op = null;
		Expression rightE = null;
		
		//BinaryExpression binaryE = new BinaryExpression(leftE, op, rightE);
		UnaryExpression unaryE = null;
		BinaryExpression binaryE = null;
		
		if(leftE instanceof UnaryExpression)
			unaryE = (UnaryExpression)leftE;
		else
			binaryE = (BinaryExpression)leftE;
		
		while (currentToken.getKind() == GrammarSymbols.ADDITIVE_OPERATOR){
			op = new Operator(currentToken);
			acceptIt();
			rightE = parseMultiplicativeExpression();
			binaryE = new BinaryExpression(leftE, op, rightE);
			leftE = binaryE;
		}

		Expression e;
		if(binaryE != null){
			e = binaryE;
		} else {
			e = unaryE;
		}
		
		return e;
			
	}
	
	private Expression parseMultiplicativeExpression () throws LexicalException, SyntacticException{
		// multiplicativeExpression ::= baseExpression (multiplicativeOperator baseExpression)*
		
		Expression leftE = parseBaseExpression();
		Operator op = null;
		Expression rightE = null;

		//BinaryExpression binaryE = new BinaryExpression(leftE, op, rightE);
		UnaryExpression unaryE = null;
		BinaryExpression binaryE = null;
		
		if(leftE instanceof UnaryExpression)
			unaryE = (UnaryExpression)leftE;
		else
			binaryE = (BinaryExpression)leftE;
		
		while (currentToken.getKind() == GrammarSymbols.MULTIPLICATIVE_OPERATOR){
			op = new Operator(currentToken);
			acceptIt();
			rightE = parseBaseExpression();
			binaryE = new BinaryExpression(leftE, op, rightE);
			leftE = binaryE;
		}
		
		Expression e;
		if(binaryE != null){
			e = binaryE;
		} else {
			e = unaryE;
		}
		
		return e;
	}
	
	private Expression parseBaseExpression () throws LexicalException, SyntacticException{
		// baseExpression ::= ( expression ) | number | identifier ( 'vazio' | ( (arguments)? ) )
		
		Expression e = null;
		
		if (currentToken.getKind() == GrammarSymbols.LEFT_PARENTHESIS){
			acceptIt();
			e = parseExpression();
			accept(GrammarSymbols.RIGHT_PARENTHESIS);
			
		}else if (currentToken.getKind() == GrammarSymbols.INT || currentToken.getKind() == GrammarSymbols.FLOAT){
			Number n = new Number(currentToken);
			e = new UnaryExpressionNumber(n);
			e.setType(currentToken.getKind()+"");
			acceptIt();
			
		}else if(currentToken.getKind() == GrammarSymbols.ID){
			Identifier id = new Identifier(currentToken);
			acceptIt ();
			e = new UnaryExpressionId(id);
			
			ArrayList<Expression> arguments = new ArrayList<Expression>();
			
			if (currentToken.getKind() == GrammarSymbols.LEFT_PARENTHESIS){
				acceptIt();
				if (currentToken.getKind() != GrammarSymbols.RIGHT_PARENTHESIS){
					arguments = parseArguments();
					e = new UnaryExpressionFunction(id, arguments);
				}
				accept(GrammarSymbols.RIGHT_PARENTHESIS);
			}
		}else{
			throw new SyntacticException("Not expected token!", currentToken);
		}
		return e;
	}
}
