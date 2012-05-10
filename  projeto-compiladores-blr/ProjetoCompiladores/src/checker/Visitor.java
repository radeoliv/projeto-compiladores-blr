package checker;

import classes.command.AssignmentCommand;
import classes.command.BreakCommand;
import classes.command.IfCommand;
import classes.command.PrintCommand;
import classes.command.ReturnCommand;
import classes.command.WhileCommand;
import classes.expression.BinaryExpression;
import classes.expression.UnaryExpressionId;
import classes.expression.UnaryExpressionNumber;
import classes.functionDeclaration.FunctionDeclaration;
import classes.procedureCall.ProcedureCall;
import classes.program.Program;
import classes.root.Root;
import classes.terminal.Identifier;
import classes.terminal.Operator;
import classes.terminal.Number;

public interface Visitor {
	
	// Command
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand, Object obj) throws SemanticException;
	public Object visitBreakCommand(BreakCommand breakCommand, Object obj);
	public Object visitFunctionCallCommand(ProcedureCall functionCallCommand, Object obj) throws SemanticException;
	public Object visitIfCommand(IfCommand ifCommand, Object obj);
	public Object visitPrintCommand(PrintCommand printCommand, Object obj) throws SemanticException;
	public Object visitReturnCommand(ReturnCommand returnCommand, Object obj);
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj);
	
	// Expression
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object obj) throws SemanticException;
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId, Object obj) throws SemanticException;
	public Object visitUnaryExpressionNumber (UnaryExpressionNumber unaryExpressionNumber, Object obj);
	
	// Function Declaration
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException;
	
	// Program
	public Object visitProgram(Program program, Object obj) throws SemanticException;
	
	// Root
	public Object visitRoot(Root root, Object obj) throws SemanticException;
	
	// Terminal
	public Object visitIdentifier(Identifier identifier, Object obj) throws SemanticException;
	public Object visitNumber(Number number, Object obj);
	public Object visitOperator(Operator operator, Object obj);
}
