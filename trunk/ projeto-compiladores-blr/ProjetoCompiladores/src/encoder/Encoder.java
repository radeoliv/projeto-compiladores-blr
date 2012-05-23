package encoder;

import java.util.ArrayList;

import compiler.Properties;

import util.Arquivo;
import util.Visitor;
import util.AST.AST;
import checker.SemanticException;
import classes.command.AssignmentCommand;
import classes.command.BreakCommand;
import classes.command.IfCommand;
import classes.command.PrintCommand;
import classes.command.WhileCommand;
import classes.expression.BinaryExpression;
import classes.expression.UnaryExpressionFunction;
import classes.expression.UnaryExpressionId;
import classes.expression.UnaryExpressionNumber;
import classes.functionDeclaration.FunctionDeclaration;
import classes.functionDeclaration.Parameter;
import classes.procedureCall.ProcedureCall;
import classes.program.Program;
import classes.root.Root;
import classes.terminal.Identifier;
import classes.terminal.Number;
import classes.terminal.Operator;

public class Encoder implements Visitor{

	private ArrayList<Instruction> instructions;
	private int startSectionData = 0;
	private Arquivo arquivo;
	private int contIfElse;
	
	public Encoder(){}
	
	public void encode(AST a){
		instructions = new ArrayList<Instruction>();
		try {
			a.visit(this, null);
			createFile();
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}
	
	private void createFile(){
		String input = Properties.sourceCodeLocation;
		String output = input.substring(0, input.lastIndexOf('.')+1);
		output = output.concat("asm");
		arquivo = new Arquivo(input,output);
		
		for(Instruction i: instructions){
			arquivo.println(i.toString());
			
			//Para testes:
			System.out.println(i.toString());
			//System.out.println(i.getOpCode() +" "+i.getOp1()+" "+i.getOp2()+" "+i.getOp3());
		}
		
		arquivo.close();
	}
	
	public void emit(int opCode, String op1){
		instructions.add(new Instruction(opCode,op1,null,null));
	}
	
	public void emit(int opCode, String op1,String op2){
		instructions.add(new Instruction(opCode,op1,op2,null));
	}
	
	public void emit(int opCode, String op1, String op2, String op3){
		instructions.add(new Instruction(opCode,op1,op2,op3));
	}
	
	public void emit(int opCode, String op1,String op2,int posicao){
		instructions.add(posicao,new Instruction(opCode,op1,op2,null));
	}
	
	@Override
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand,
			Object obj) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBreakCommand(BreakCommand breakCommand, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitProcedureCall(ProcedureCall procedureCall, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIfCommand(IfCommand ifCommand, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitPrintCommand(PrintCommand printCommand, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,
			Object obj) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId,
			Object obj) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnaryExpressionNumber(
			UnaryExpressionNumber unaryExpressionNumber, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitUnaryExpressionFunction(
			UnaryExpressionFunction unaryExpressionFunction, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFunctionDeclaration(
			FunctionDeclaration functionDeclaration, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParameter(Parameter parameter, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRoot(Root root, Object obj) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier identifier, Object obj)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNumber(Number number, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOperator(Operator operator, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
