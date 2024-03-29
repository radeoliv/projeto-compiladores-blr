package checker;

import java.util.ArrayList;

import parser.GrammarSymbols;
import util.Visitor;
import util.AST.AST;
import util.symbolsTable.IdentificationTable;
import classes.command.*;
import classes.expression.*;
import classes.root.Root;
import classes.terminal.*;
import classes.terminal.Number;
import classes.procedureCall.ProcedureCall;
import classes.program.*;
import classes.functionDeclaration.*;


import classes.terminal.Operator;

public class Checker implements Visitor{

	IdentificationTable identificationTable;
	
	public Checker(){
		identificationTable = new IdentificationTable();
	}
	
	public AST check(AST a) throws SemanticException{
		a.visit(this, null);
		return a;
	}
	
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand,Object obj) throws SemanticException {
		Identifier id = assignmentCommand.getId();
		AST a = identificationTable.retrieve(id.getSpelling());
		String assignmentType = (String)assignmentCommand.getExpression().visit(this, obj);
		
		if(a == null){
			//Primeiro uso do identificador
			assignmentCommand.setType(assignmentType);
			identificationTable.enter(id.getSpelling(), assignmentCommand);
			id.visit(this, obj);
			a = identificationTable.retrieve(id.getSpelling());
		} else {
			//Atribui��o para vari�vel j� existente
			assignmentCommand.setType(assignmentType);
			id.visit(this, obj);
		}
		
		//Verifica��o se o identificador � uma fun��o
		if(a instanceof FunctionDeclaration){
			throw new SemanticException("Invalid assignment operation!");
		}
		
		String idType = ((AssignmentCommand)a).getType();
		String expType = assignmentType;
		
		if(!idType.equals(expType)){
			throw new SemanticException("Types do not match!");
		}
		return idType;
	}
	
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object obj) throws SemanticException {
		
		String leftExpType = (String)binaryExpression.getLeftExpression().visit(this, obj);
		String rightExpType = (String)binaryExpression.getRightExpression().visit(this, obj);
		
		if(!leftExpType.equals(rightExpType)){
			throw new SemanticException("Expressions types do not match!");
		}
		
		int operator = binaryExpression.getOperator().getKind();
		
		String binaryType = null;
		switch(operator){
			case GrammarSymbols.RELATIONAL_OPERATOR:
				binaryType = GrammarSymbols.INT + "";
				break;
			
			case GrammarSymbols.MULTIPLICATIVE_OPERATOR:
			case GrammarSymbols.ADDITIVE_OPERATOR:
				//Ou o rightExpType...
				binaryType = leftExpType;
				break;
			default:
				throw new SemanticException("Unknown expression type!");
		}
		
		binaryExpression.setType(binaryType);
		return binaryType;
	}

	public Object visitBreakCommand(BreakCommand breakCommand, Object obj) throws SemanticException {
		// � preciso verificar se o break est� contido em um escopo de while. O objeto obj vai servir para trazer a informa��o, mostrando se o visit foi chamado por meio do while.
		
		if(obj == null || !(obj instanceof WhileCommand))
			throw new SemanticException ("Break out of While!");
		return null;
	}

	public Object visitProcedureCall(ProcedureCall procedureCall, Object obj) throws SemanticException {
		visitIdentifier(procedureCall.getIdentifier(), obj);
		
		AST a = identificationTable.retrieve(procedureCall.getIdentifier().getSpelling());
		
		if(procedureCall.getArguments().size() == ((FunctionDeclaration)a).getParameters().size()){
			for(Expression exp : procedureCall.getArguments()){
				exp.visit(this, obj);
			}
		}else{
			throw new SemanticException("Invalid arguments!");
		}
		return null;
	}
	
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		
		Identifier fdId = functionDeclaration.getIdentifier();

		AST a = identificationTable.retrieve(fdId.getSpelling());
		
		if (a == null){
			identificationTable.enter(fdId.getSpelling(), functionDeclaration);
			fdId.visit(this, obj);
			
			identificationTable.openScope();
			
			//Visitando par�metros
			for(Parameter parameter : functionDeclaration.getParameters()){
				identificationTable.enter(parameter.getIdentifier().getSpelling(), functionDeclaration);
				parameter.visit(this, obj);
			}
			
			//Visitando comandos
			for(Command cmd : functionDeclaration.getCommands()){
				cmd.visit(this, obj);
			}
			
			Expression exp = functionDeclaration.getReturnExp();
			boolean saveReturn = false;
			if(exp != null){
				exp.visit(this, obj);
				saveReturn = true;
			}
			
			identificationTable.closeScope();
			
			/*
			 * Adiciona o indentificador presente no retorno para a,
			 * foi utilizado este artif�cio, pois o identificador era perdido
			 * no fechamento do escopo.
			 */
			if(saveReturn){
				if (functionDeclaration.getReturnExp() instanceof UnaryExpressionId){
					identificationTable.enter(
							((UnaryExpressionId) (functionDeclaration.getReturnExp())).getIdentifier().getSpelling(), 
								functionDeclaration);
				}
			}

		} else {
			throw new SemanticException ("Already existent identifier!");
		}
		return null;
	}

	public Object visitIdentifier(Identifier identifier, Object obj) throws SemanticException {
		AST a = identificationTable.retrieve(identifier.getSpelling());
		if (a!=null){
			 identifier.setDeclaration(a);
		}else{
			throw new SemanticException ("Nonexistent identifier!");
		}
		return null;
	}

	public Object visitIfCommand(IfCommand ifCommand, Object obj) throws SemanticException {
		Expression exp = ifCommand.getExpression();
		exp.visit(this, obj);
		
		/*
		 * Verifica express�o de condi��o no IF
		 * 
		 * Para ocorrer erro, vem:
		 * 	- A express�o passada n�o � uma express�o bin�ria com um operador relacional
		 * 	- A express�o n�o � Un�ria tendo 'inteiro' como tipo da mesma
		 * 		
		 */
		if ( !((exp instanceof BinaryExpression && ((BinaryExpression) exp).getOperator().getKind() == GrammarSymbols.RELATIONAL_OPERATOR))
				&& !((exp instanceof UnaryExpression && ((UnaryExpression) exp).getType().equals(GrammarSymbols.INT + "")))){
			
			throw new SemanticException("Invalid condition!");
		}
		
		identificationTable.openScope();
		for(Command ifCmd : ifCommand.getIfCommands()){
			ifCmd.visit(this, obj);
		}
		identificationTable.closeScope();
		
		identificationTable.openScope();
		for(Command elseCmd : ifCommand.getElseCommands()){
			elseCmd.visit(this, obj);
		}
		identificationTable.closeScope();
		
		return null;
	}
	
	public Object visitNumber(Number number, Object obj) {
		return number.getKind();
	}
	
	public Object visitOperator(Operator operator, Object obj) {
		return operator.getKind();
	}
	
	public Object visitPrintCommand(PrintCommand printCommand, Object obj) throws SemanticException {
		/*
		 * Verifica se a express�o � null pois o comando print 
		 * pode ou n�o possuir uma express�o associada 
		 */
		String expReturn = null;
		if(printCommand.getExpression() != null)
			expReturn = (String)printCommand.getExpression().visit(this, obj);
		return expReturn;
	}
	
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId, Object obj) throws SemanticException {
		unaryExpressionId.getIdentifier().visit(this, obj);
		
		// Verifica onde o identificador foi declarado
		AST a = unaryExpressionId.getIdentifier().getDeclaration();
		String idType = "";
		
		// Caso o declaration seja AssignmentCommand
		if(a instanceof AssignmentCommand){
			
			idType = ((AssignmentCommand)a).getType();
			unaryExpressionId.setType(idType);
			
		} else if(a instanceof FunctionDeclaration) { // caso seja FunctionDeclaration no getDeclaration()

			ArrayList<Parameter> parameters = ((FunctionDeclaration)a).getParameters();
			
			// verifica se o spelling do Identifier est� presente nos par�metros
			for(Parameter parameter : parameters){
				if(parameter.getIdentifier().getSpelling().equals( unaryExpressionId.getIdentifier().getSpelling() )){
					idType = parameter.getIdentifierType() + "";
					break;
				}
			}
			
			// caso o for n�o retorne nada, implica que o identifier da unary � o retorno
			if(idType.equals("")){
				idType = ((FunctionDeclaration)a).getReturnExp().getType();
			}
			
			// atribui o tipo 
			unaryExpressionId.setType(idType);
		}
		
		return idType;
	}
	
	public Object visitUnaryExpressionNumber(UnaryExpressionNumber unaryExpressionNumber, Object obj) {
		unaryExpressionNumber.getNumber().visit(this, obj);
		String numberType = unaryExpressionNumber.getNumber().getKind() + "";
		unaryExpressionNumber.setType(numberType);
		return numberType;
	}
	
	public Object visitUnaryExpressionFunction(UnaryExpressionFunction unaryExpressionFunction, Object obj) throws SemanticException {
		
		Identifier idU = unaryExpressionFunction.getIdentifier();
		idU.visit(this, obj);
		
		// Verifica se a fun��o j� existe
		AST a = identificationTable.retrieve(idU.getSpelling());
		
		if (a != null){
			// Argumentos da chamada
			ArrayList<Expression> arguments = unaryExpressionFunction.getArguments();
			
			/*
			 *  Caso a quantidade de argumentos da fun��o seja diferente do que foi
			 *  declarado, ocorre um erro
			 */
			if (((FunctionDeclaration)a).getParameters().size() == arguments.size()){
				for(Expression exp : arguments){
					exp.visit(this, obj);
				}
			}else{
				throw new SemanticException ("Invalid arguments!");	
			}
		}else{
			throw new SemanticException ("Nonexistent function!");	
		}
		
		return ((FunctionDeclaration)a).getReturnExp().visit(this, obj);
	}
	
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) throws SemanticException {
		
		Expression exp = whileCommand.getExpression();
		exp.visit(this, obj);
		
		/*
		 * Verifica express�o de condi��o no WHILE
		 * 
		 * Para ocorrer erro, vem:
		 * 	- A express�o passada n�o � uma express�o bin�ria com um operador relacional
		 * 	- A express�o n�o � Un�ria tendo 'inteiro' como tipo da mesma
		 * 		
		 */
		if ( !((exp instanceof BinaryExpression && ((BinaryExpression) exp).getOperator().getKind() == GrammarSymbols.RELATIONAL_OPERATOR))
				&& !((exp instanceof UnaryExpression && ((UnaryExpression) exp).getType().equals(GrammarSymbols.INT + "")))){
			
			throw new SemanticException("Invalid condition!");
		}
		
		identificationTable.openScope();
		
		for(Command cmd : whileCommand.getCommands() ){
			cmd.visit(this, whileCommand);
			
			if (cmd instanceof BreakCommand){
				break;
			}
		}
		
		identificationTable.closeScope();
		return null;
	}
	
	public Object visitProgram(Program program, Object obj) throws SemanticException {
		
		if (program.getCommand() != null){ 
			program.getCommand().visit(this, obj);
		}else{
			program.getFunction().visit(this, obj);
		}
		return null;
	}
	
	public Object visitRoot(Root root, Object obj) throws SemanticException {
		for(Program prg : root.getPrograms()){
			prg.visit(this, obj);
		}
		return null;
	}

	@Override
	public Object visitParameter(Parameter parameter, Object obj) throws SemanticException {
		parameter.getIdentifier().visit(this, obj);
		return parameter.getIdentifierType();
	}
}