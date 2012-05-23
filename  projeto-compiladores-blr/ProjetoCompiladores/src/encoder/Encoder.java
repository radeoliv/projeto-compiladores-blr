package encoder;

import java.util.ArrayList;

import compiler.Properties;

import util.Arquivo;
import util.Visitor;
import util.AST.AST;
import checker.SemanticException;
import classes.command.AssignmentCommand;
import classes.command.BreakCommand;
import classes.command.Command;
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
	private Arquivo file;
	private static final int DESLOCAMENTO = 4;
	// Necessário?
	//private int startSectionData = 0;

	//Contadores
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
		String output = (input.substring(0, input.lastIndexOf('.')+1)).concat("asm");

		file = new Arquivo(input,output);
		
		for(Instruction inst: instructions){
			file.println(inst.toString());
			
			//Para testes:
			System.out.println(inst.toString());
			//System.out.println(i.getOpCode() +" "+i.getOp1()+" "+i.getOp2()+" "+i.getOp3());
		}
		file.close();
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
	
	public void emit(int position, int opCode, String op1,String op2){
		instructions.add(position,new Instruction(opCode,op1,op2,null));
	}
	
	
	public Object visitRoot(Root root, Object obj) throws SemanticException{
		// Printf de C
		emit(InstructionType.EXTERN,InstructionType.PRINTF);
		// Section Data
		emit(InstructionType.SECTION, InstructionType.DATA);
		// Começo da Section Data
		// Acho que não precisamos disso pq não tem globais
		// startSectionData = instructions.size();
		
		for(Program p:root.getPrograms()){
			// Pq Nadile passa o primeiro argumento e não obj e todos os visit?
			// pra saber se é global ou não?
			p.visit(this, root);
		}
		// E ela retorna pq?
		return root;
	}
	

	public Object visitProgram(Program program, Object obj) throws SemanticException{
		Object cmd = program.getCommand();
		
		// Acho que podemos colocar isso aqui já
		emit(InstructionType.SECTION,InstructionType.TEXT);
		emit(InstructionType.GLOBAL,InstructionType.WINMAIN);
		// Temos que fazer algo para que os comandos sejam adicionados nessa área e funções na delas
		// entao acho que temos que ter um cont aqui
		// Como não temos main, vamos ter que criar um label pra main, aí poderia ser a primeira
		// TODO
		
		if (cmd instanceof Command){
			((Command) cmd).visit(this, program);
			
		}else{
			((FunctionDeclaration)cmd).visit(this, program);
		}
		
		return program;
	}
	
	@Override
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand, Object obj) throws SemanticException{
		assignmentCommand.getId().visit(this, obj);
		assignmentCommand.getExpression().visit(this, obj);		
		
		if (obj instanceof FunctionDeclaration){
			
		}else{
			// Se não for dentro de uma funcao... tem que add a instrucao no escopo da main
			// emit(...)
		}
		return assignmentCommand;
	}
	
	// Acho que só precisamos mandar aqui pra verificar se o comando vem de uma decla de funcao
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		Identifier id = functionDeclaration.getIdentifier();
		id.visit(this, obj);
		emit (InstructionType.FUNCTION_LABEL, id.getSpelling());
		
		for (int i=functionDeclaration.getParameters().size(); i>0;i--){
			//...
		}
		
		
		return functionDeclaration;
	}
	
	@Override
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj)
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
	public Object visitProcedureCall(ProcedureCall procedureCall, Object obj)
			throws SemanticException {
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
	public Object visitPrintCommand(PrintCommand printCommand, Object obj)
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
