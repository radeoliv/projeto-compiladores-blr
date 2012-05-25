package encoder;

import java.util.ArrayList;
import java.util.HashMap;

import parser.GrammarSymbols;

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
	
	//A seção de função (declarações de funções) sempre vem após o cabeçalho. Como este não muda, o tamanho é constante.
	public static final int FUNCTION_SECTION = 9;
	//Deslocamento sempre é multiplo de 4 (tratamento apenas para inteiros) 
	private static final int OFFSET = 4;
	private ArrayList<Instruction> instructions;
	private Arquivo file;
	private int level;
	private int contIfElse;
	private HashMap<String, IdentifierLocation> idMap;
	private ArrayList<Instruction> functionInstructions;
	
	// Necessário?
	//private int startSectionData = 0;

	
	public Encoder(){
		this.instructions = new ArrayList<Instruction>();
		//Nível principal (da main) é 0
		this.level = 0;
		this.contIfElse = 0;
		this.idMap = new HashMap<String, IdentifierLocation>();
		this.functionInstructions = new ArrayList<Instruction>();
	}
	
	public void encode(AST a){
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
	
	public void emit(int operationCode, ArrayList<Instruction> instructionList){
		instructionList.add(new Instruction(operationCode, null, null, null));
	}
	
	public void emit(int operationCode, String op1, ArrayList<Instruction> instructionList){
		instructionList.add(new Instruction(operationCode,op1,null,null));
	}
	
	public void emit(int operationCode, String op1,String op2, ArrayList<Instruction> instructionList){
		instructionList.add(new Instruction(operationCode,op1,op2,null));
	}
	
	public void emit(int operationCode, String op1, String op2, String op3, ArrayList<Instruction> instructionList){
		instructionList.add(new Instruction(operationCode,op1,op2,op3));
	}
	
	public void emit(int position, int operationCode, String op1,String op2, ArrayList<Instruction> instructionList){
		instructionList.add(position,new Instruction(operationCode,op1,op2,null));
	}
	
	private void insertHeader(){
		emit(InstructionType.BLOCKS_SEPARATOR, instructions);
		
		// Importando Printf
		emit(InstructionType.EXTERN,InstructionType.PRINTF, instructions);
		emit(InstructionType.BLOCKS_SEPARATOR, instructions);
		
		// Section Data
		emit(InstructionType.SECTION, InstructionType.DATA, instructions);
		emit(InstructionType.INT_FORMAT, instructions);
		emit(InstructionType.BLOCKS_SEPARATOR, instructions);
		
		// Section Text
		emit(InstructionType.SECTION, InstructionType.TEXT, instructions);
		emit(InstructionType.GLOBAL, InstructionType.WINMAIN, instructions);
		emit(InstructionType.BLOCKS_SEPARATOR, instructions);
	}
	
	public Object visitRoot(Root root, Object obj) throws SemanticException{
		// startSectionData = instructions.size();
		
		//Gerando informações pré-código - 'cabeçalho'
		insertHeader();
		//Gerando label do main
		emit(InstructionType.FUNCTION_LABEL, InstructionType.WINMAIN, instructions);
		
		ArrayList<Program> programList = root.getPrograms();
		
		for(Program program : programList){
			
			//TODO: Por que raios o root está sendo passado como parametro?
			//program.visit(this, null);
			program.visit(this, root);
		}

		return null;
	}
	
	public Object visitProgram(Program program, Object obj) throws SemanticException{
		Command cmd = program.getCommand();
		FunctionDeclaration fd = program.getFunction();
		
		if (cmd != null){
			cmd.visit(this, null);
		}else{
			fd.visit(this, fd);
		}
		
		return null;
	}
	
	@Override
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand, Object obj) throws SemanticException{
		
		// Resultado de expressão em EAX
		assignmentCommand.getExpression().visit(this, obj);
		
		assignmentCommand.getId().visit(this, obj);
		if (obj instanceof FunctionDeclaration){
			
		}else{
			// Se não for dentro de uma funcao... tem que add a instrucao no escopo da main
			// emit(...)
		}
		return assignmentCommand;
	}
	
	// Acho que só precisamos mandar aqui pra verificar se o comando vem de uma decla de funcao
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		functionInstructions = new ArrayList<Instruction>();
		
		Identifier id = functionDeclaration.getIdentifier();
		id.visit(this, obj);
		
		emit (InstructionType.FUNCTION_LABEL, id.getSpelling(), functionInstructions);
		
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
		
		printCommand.getExpression().visit(this, obj);
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		emit(InstructionType.CALL_FUNCTION, InstructionType.PRINTF, destiny);
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
		
		//Nó esquerda = eax
		// Nó direita = ebx
		//visit operator
		// empilha resultado leftExpression (push eax)
		binaryExpression.getLeftExpression().visit(this, obj);
		
		// empilha resultado rightExpression
		binaryExpression.getRightExpression().visit(this, obj);
		
		// realiza operação empilhando o resultado
		binaryExpression.getOperator().visit(this, obj);
		
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
		return number.getSpelling();
	}

	@Override
	public Object visitOperator(Operator operator, Object obj) {

		// pega o tipo da operação
		int operatorKind = operator.getKind();
				
		/*
		 *  considerando que os resultados das expressões já foram empilhados
		 *  só precisamos verificar qual o tipo da operação que deverá ser feita
		 */
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		switch (operatorKind) {
			case GrammarSymbols.MINUS:
				emit(InstructionType.SUB, InstructionType.EAX, InstructionType.EBX, destiny);
				break;
			case GrammarSymbols.PLUS:
				emit(InstructionType.ADD, InstructionType.EAX, InstructionType.EBX, destiny);
				break;
			case GrammarSymbols.MULTIPLICATION:
				emit(InstructionType.MULT, InstructionType.EAX, InstructionType.EBX, destiny);
				break;
			case GrammarSymbols.DIVISION:
				emit(InstructionType.DIV, InstructionType.EAX, InstructionType.EBX, destiny);
				break;
		}

		return null;
	}
	
	private void openScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.PUSH, InstructionType.EBP, instructionList);
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ESP, instructionList);
	}
	
	private void closeScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.MOV, InstructionType.ESP, InstructionType.EBP, instructionList);
		emit(InstructionType.POP, InstructionType.EBP, instructionList);
	}
	
	private void increaseLevel(){
		this.level++;
	}
	
	private void decreaseLevel(){
		this.level--;
	}
}
