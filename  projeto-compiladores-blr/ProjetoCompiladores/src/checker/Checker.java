package checker;

import util.symbolsTable.IdentificationTable;
import classes.command.AssignmentCommand;
import classes.command.BreakCommand;
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
	
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand,
			Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitBinaryExpression(BinaryExpression binaryExpression,
			Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitBreakCommand(BreakCommand breakCommand, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitFunctionCallCommand(
			FunctionCallCommand functionCallCommand, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitFunctionDeclaration(
			FunctionDeclaration functionDeclaration, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitIdentifier(Identifier identifier, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitIfCommand(IfCommand ifCommand, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitNumber(Number number, Object obj) {
		// TODO Auto-generated method stub
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

	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId,
			Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitUnaryExpressionNumber(
			UnaryExpressionNumber unaryExpressionNumber, Object obj) {
		// TODO Auto-generated method stub
		return null;
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
