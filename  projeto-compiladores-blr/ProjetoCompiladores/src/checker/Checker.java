package checker;

import java.util.ArrayList;

import parser.GrammarSymbols;
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
	
	public void check(AST a) throws SemanticException{
		a.visit(this, null);
	}
	
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand,Object obj) throws SemanticException {
		Identifier id = assignmentCommand.getId();
		AST a = identificationTable.retrieve(id.getSpelling());
		assignmentCommand.getExpression().visit(this, obj);
		
		//TODO: Como gerenciar referencias de declara��es para mesmo identificador?
		
		if(a == null){
			//Primeiro uso do identificador
			assignmentCommand.setType(assignmentCommand.getExpression().getType());
			identificationTable.enter(id.getSpelling(), assignmentCommand);
			id.visit(this, obj);
			a = identificationTable.retrieve(id.getSpelling());
		} else {
			//Atribui��o para vari�vel j� existente
			id.visit(this, obj);
		}
		
		//Verifica��o se o identificador � uma fun��o
		
		if(a instanceof FunctionDeclaration){
			throw new SemanticException("Is not possible to assign a value to a function!");
		}
		
		String idType = ((AssignmentCommand)a).getType();
		String expType = assignmentCommand.getExpression().getType();
		
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
			throw new SemanticException ("Break fora de While!");
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
			for(Identifier parameter : functionDeclaration.getParameters()){
				identificationTable.enter(parameter.getSpelling(), functionDeclaration);
				parameter.visit(this, obj);
			}
			
			//Visitando comandos
			for(Command cmd : functionDeclaration.getCommands()){
				cmd.visit(this, obj);
			}
			
			Expression exp = functionDeclaration.getReturnExp();
			if(exp != null){
				exp.visit(this, obj);
			}

			identificationTable.closeScope();
		}else{
			throw new SemanticException ("Already existent function!");
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
		
		//Verificando se a condi��o do if � v�lida
		if ( !((exp instanceof BinaryExpression && ((BinaryExpression) exp).getOperator().getKind() == GrammarSymbols.RELATIONAL_OPERATOR))
				&& !((exp instanceof UnaryExpression && ((UnaryExpression) exp).getType().equals(GrammarSymbols.INT + "")))){
			
			throw new SemanticException("Invalid condition!");
		}
		
		identificationTable.openScope();
		for(Command ifCmd : ifCommand.getIfCommands()){
			ifCmd.visit(this, obj);
		}
		
		ArrayList<Command> elseCmds = ifCommand.getElseCommands();
		if(elseCmds.size() > 0){
			identificationTable.openScope();
			for(Command elseCmd : elseCmds){
				elseCmd.visit(this, obj);
			}
			identificationTable.closeScope();
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
		// Verifica se a express�o � null pois o comando print pode ou n�o possuir uma express�o associada
		if(printCommand.getExpression() != null)
			printCommand.getExpression().visit(this, obj);
		return null;
	}
	
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId, Object obj) throws SemanticException {
		unaryExpressionId.getIdentifier().visit(this, obj);
		String idType = ((AssignmentCommand)unaryExpressionId.getIdentifier().getDeclaration()).getType();
		unaryExpressionId.setType(idType);
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
		AST a = identificationTable.retrieve(idU.getSpelling());
		
		if (a != null){
			ArrayList<Expression> arguments = unaryExpressionFunction.getArguments();
			
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
		
		return ((FunctionDeclaration)a).getReturnExp().getType();
	}
	
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) throws SemanticException {
		Expression exp = whileCommand.getExpression();
		exp.visit(this, obj);
		
		if ( !((exp instanceof BinaryExpression && ((BinaryExpression) exp).getOperator().getKind() == GrammarSymbols.RELATIONAL_OPERATOR))
				&& !((exp instanceof UnaryExpression && ((UnaryExpression) exp).getType().equals(GrammarSymbols.INT + "")))){
			
			throw new SemanticException("Invalid condition!");
		}
		
		identificationTable.openScope();
		
		for(Command cmd : whileCommand.getCommands() ){
			cmd.visit(this, whileCommand);
			
			if (cmd instanceof BreakCommand){
				//TODO: Verificar uso
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

	
	
}
