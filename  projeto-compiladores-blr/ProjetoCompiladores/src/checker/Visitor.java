package checker;

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

public interface Visitor {
	
	// Command
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand, Object obj);
	public Object visitBreakCommand(BreakCommand breakCommand, Object obj);
	public Object visitFunctionCallCommand(FunctionCallCommand functionCallCommand, Object obj);
	public Object visitIfCommand(IfCommand ifCommand, Object obj);
	public Object visitPrintCommand(PrintCommand printCommand, Object obj);
	public Object visitReturnCommand(ReturnCommand returnCommand, Object obj);
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj);
	
	// Expression
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object obj);
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId, Object obj);
	public Object visitUnaryExpressionNumber (UnaryExpressionNumber unaryExpressionNumber, Object obj);
	
	// Function Declaration
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj);
	
	// Program
	public Object visitProgram(Program program, Object obj);
	
	// Terminal
	public Object visitIdentifier(Identifier identifier, Object obj);
	public Object visitNumber(Number number, Object obj);
	public Object visitOperator(Operator operator, Object obj);
}
