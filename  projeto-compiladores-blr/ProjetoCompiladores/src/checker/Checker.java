package checker;

import parser.GrammarSymbols;
import util.AST.AST;
import util.symbolsTable.IdentificationTable;
import classes.command.AssignmentCommand;
import classes.command.BreakCommand;
import classes.command.Command;
import classes.command.FunctionCallCommand;
import classes.command.IfCommand;
import classes.command.PrintCommand;
import classes.command.ReturnCommand;
import classes.command.WhileCommand;
import classes.expression.BinaryExpression;
import classes.expression.UnaryExpressionId;
import classes.expression.UnaryExpressionNumber;
import classes.functionDeclaration.FunctionDeclaration;
import classes.program.Program;
import classes.terminal.Identifier;
import classes.terminal.Operator;

public class Checker implements Visitor{

	IdentificationTable identificationTable;
	
	public Checker(){
		identificationTable = new IdentificationTable();
	}
	
	//'main'
	public void check(AST a){
		a.visit(this, null);
	}
	
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand,Object obj) throws SemanticException {
		Identifier id = assignmentCommand.getId();
		AST a = identificationTable.retrieve(id.getSpelling());
		if(a == null){
			identificationTable.enter(id.getSpelling(), assignmentCommand);
			id.visit(this, obj);
			a = identificationTable.retrieve(id.getSpelling());
		} else {
			id.visit(this, obj);
		}
		
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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitFunctionCallCommand(
			FunctionCallCommand functionCallCommand, Object obj) {
		
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
			for(Command cmd : functionDeclaration.getCommands())
				cmd.visit(this, obj);
			identificationTable.closeScope();
		}else{
			throw new SemanticException ("Função já existente!");
		}
		return null;
	}

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
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitNumber(Number number, Object obj) {
		//TODO: O que fazer?!
		return null;
	}

	public Object visitOperator(Operator operator, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitPrintCommand(PrintCommand printCommand, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitReturnCommand(ReturnCommand returnCommand, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId, Object obj) {
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

	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitProgram(Program program, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
