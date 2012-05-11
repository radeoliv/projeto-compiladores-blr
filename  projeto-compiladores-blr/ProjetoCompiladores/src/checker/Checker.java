package checker;

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
		
		if(a == null){
			//Primeiro uso
			identificationTable.enter(id.getSpelling(), assignmentCommand);
			id.visit(this, obj);
			a = identificationTable.retrieve(id.getSpelling());
		} else {
			//Atribuição para variável já existente
			id.visit(this, obj);
		}
		
		//Verificação se o identificador é uma função
		if(a instanceof FunctionDeclaration){
			throw new SemanticException("Não é possível atribuir valor a uma função!");
		}
		
		assignmentCommand.getE().visit(this, obj);
		
		AssignmentCommand aux = ((AssignmentCommand)a);
		String idType = aux.getType();
		String expType = assignmentCommand.getE().getType();
		
		//Caso o identificador nunca tenha sido usado -> "Declaração"
		if(idType.equals(null)){
			aux.setType(expType);
		} else {
			if(!idType.equals(expType)){
				throw new SemanticException("Tipos incompatíveis!");
			}
		}
		return null;
	}

	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object obj) throws SemanticException {
		String leftExpType = (String)binaryExpression.getLeftExpression().visit(this, obj);
		String rightExpType = (String)binaryExpression.getRightExpression().visit(this, obj);
		
		if(!leftExpType.equals(rightExpType)){
			throw new SemanticException("Expressões incompatíveis!");
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
				throw new SemanticException("Tipo desconhecido!");
		}
		
		binaryExpression.setType(binaryType);
		
		return binaryType;
	}

	public Object visitBreakCommand(BreakCommand breakCommand, Object obj) {
		// TODO
		return null;
	}

	public Object visitFunctionCallCommand(ProcedureCall functionCallCommand, Object obj) throws SemanticException {
		visitIdentifier(functionCallCommand.getIdentifier(), obj);
		
		AST a = identificationTable.retrieve(functionCallCommand.getIdentifier().getSpelling());
		
		if(functionCallCommand.getArguments().size() != ((FunctionDeclaration)a).getParameters().size()){
			throw new SemanticException("Argumentos inválidos!");
		}
		
		return null;
	}

	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		Identifier fdId = functionDeclaration.getIdentifier();

		AST a = identificationTable.retrieve(fdId.getSpelling());
		if (a == null){
			identificationTable.enter(fdId.getSpelling(), functionDeclaration);
			fdId.visit(this, obj);
			//TODO: Revisar a necessidade do scope
			identificationTable.openScope();
			//TODO: Parametros? + Object para tratar return
			for(Command cmd : functionDeclaration.getCommands()){
				cmd.visit(this, obj);
				
				//TODO: Como trata o return?
				if(cmd instanceof ReturnCommand){
					((ReturnCommand)cmd).visit(this, obj);
				}
			}
			
			identificationTable.closeScope();
		}else{
			throw new SemanticException ("Função já existente!");
		}
		return null;
	}

	//OK!
	public Object visitIdentifier(Identifier identifier, Object obj) throws SemanticException {
		AST a = identificationTable.retrieve(identifier.getSpelling());
		if (a!=null){
			 identifier.setDeclaration(a);
		}else{
			throw new SemanticException ("Identificador não declarado!");
		}
		return null;
	}

	public Object visitIfCommand(IfCommand ifCommand, Object obj) {
		// TODO
		return null;
	}

	public Object visitNumber(Number number, Object obj) {
		return number.getKind();
	}

	public Object visitOperator(Operator operator, Object obj) {
		return operator.getKind();
	}

	public Object visitPrintCommand(PrintCommand printCommand, Object obj) throws SemanticException {
		if(printCommand.getE() != null)
			printCommand.getE().visit(this, obj);
		return null;
	}

	public Object visitReturnCommand(ReturnCommand returnCommand, Object obj) {
		// TODO
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

	@Override
	public Object visitUnaryExpressionFunction(UnaryExpressionFunction unaryExpressionFunction, Object obj) {
		//TODO
		return null;
	}

	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) {
		// TODO
		return null;
	}
	
	
	public Object visitProgram(Program program, Object obj) throws SemanticException {
		// TODO
		return null;
	}
	
	public Object visitRoot(Root root, Object obj) throws SemanticException {
		// :TODO
		return null;
	}

	
	
}
