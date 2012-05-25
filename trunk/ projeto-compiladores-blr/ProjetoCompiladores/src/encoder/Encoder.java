package encoder;

import java.util.ArrayList;
import java.util.Collections;
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
import classes.expression.Expression;
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
	
	//TODO: Verificar pushes na pilha
	private int relativeOffset;
	
	private ArrayList<Instruction> instructions;
	private Arquivo file;
	private int level;
	private int contIfElse;
	private int contWhile;
	private HashMap<String, IdentifierLocation> idMap;
	private ArrayList<Instruction> functionInstructions;

	public Encoder(){
		this.instructions = new ArrayList<Instruction>();
		//Nível principal (da main) é 0
		this.level = 0;
		this.contIfElse = 0;
		this.contWhile = 0;
		this.idMap = new HashMap<String, IdentifierLocation>();
		this.functionInstructions = new ArrayList<Instruction>();
		this.relativeOffset = 0;
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
	
	//TODO: Provavelmente sem uso!
	public void emit(int position, int operationCode, String op1,String op2, ArrayList<Instruction> instructionList){
		instructionList.add(position,new Instruction(operationCode,op1,op2,null));
	}
	
	public void emit(ArrayList<Instruction> source, ArrayList<Instruction> destiny){
		destiny.addAll(FUNCTION_SECTION, source);
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
	
	public Object visitRoot(Root root, Object obj) throws SemanticException {
		
		//Gerando informações pré-código - 'cabeçalho'
		insertHeader();
		//Gerando label do main
		emit(InstructionType.LABEL, InstructionType.WINMAIN, instructions);
		
		ArrayList<Program> programList = root.getPrograms();
		
		for(Program program : programList){
			program.visit(this, null);
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
		ArrayList<Instruction> destiny = null;		
		
		if (obj instanceof FunctionDeclaration){
			destiny = functionInstructions;
		}else{
			destiny = instructions;
		}
		if(!idMap.containsKey(assignmentCommand.getId().getSpelling()))
		{
			//Primeira vez que o identificador aparece
			assignmentCommand.getId().visit(this, obj);
		} 
		else
		{			
			// Resultado de expressão em EAX
			assignmentCommand.getExpression().visit(this, obj);
			
			IdentifierLocation il = idMap.get(assignmentCommand.getId().getSpelling());
			
			//EDX aponta para o EBP do frame do Identificador 
			reachIdentifier(il, destiny);
			
			// Joga valor de EAX na posição do identificador encontrado em EDX
			emit(InstructionType.POP, InstructionType.POP_REG_OFFSET+"", InstructionType.EDX, il.getOffset()+"", destiny);
		}
		
		return null;
	}
	
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		//Limpando lista de instruções de função
		functionInstructions = new ArrayList<Instruction>();		
		
		Identifier id = functionDeclaration.getIdentifier();
		
		//Armazena informações do identificador da FunctionDeclaration
		id.visit(this, obj);
		
		emit (InstructionType.LABEL, id.getSpelling(), functionInstructions);
		openScope(functionInstructions);		
		
		ArrayList<Command> commands = functionDeclaration.getCommands(); 
		for(Command c : commands){
			c.visit(this, obj);
		}
		
		//Se possuir um retorno, o valor da expressão é jogado em EAX
		Expression ret = functionDeclaration.getReturnExp(); 
		if(ret != null){
			ret.visit(this, obj);
		}
		
		closeScope(functionInstructions);
		emit(InstructionType.RET, functionInstructions);
		emit(InstructionType.BLOCKS_SEPARATOR, functionInstructions);
		emit(functionInstructions, instructions);
		
		return null;
	}
	
	@Override
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String label;
		contWhile++;
		
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma função
			destiny = functionInstructions;
			String functionName = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
			label = functionName+"_while_"+contWhile;
			
		}else{
			// Adicionar no final do array de instruções (main)
			destiny = instructions;
			label = "main_while_"+contWhile;
		}
		
		// Label de ínicio do While
		emit(InstructionType.LABEL, label+"_begin", destiny);
		openScope(destiny);
		
		Operator op = ((BinaryExpression)whileCommand.getExpression()).getOperator();

		// Avaliação da Expressão - EAX
		whileCommand.getExpression().visit(this, obj);
		
		// Jump de acordo com a condição
		emit(InstructionType.CONDITIONAL_JUMP, whatJump(op),label+"_end" , destiny);
		
		// Avaliação de comandos
		for (Command cmd : whileCommand.getCommands()){
			cmd.visit(this, obj);
		}
		
		// Jump para o início do While
		emit (InstructionType.JMP, label+"_begin", destiny);
		
		closeScope(destiny);
		emit(InstructionType.LABEL, label+"_end", destiny);
		
		return null;
	}

	@Override
	public Object visitIfCommand(IfCommand ifCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String label;
		contIfElse++;
		
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma função
			destiny = functionInstructions;
			label = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
			
		}else{
			// Adicionar no final do array de instruções
			destiny = instructions;
			label = "main";
		}
		// Label início do if
		emit(InstructionType.LABEL, label+"_if_"+contIfElse+"_begin", destiny);
		openScope(destiny);
		
		Operator op = ((BinaryExpression)ifCommand.getExpression()).getOperator();
		// Avaliação da Expressão
		ifCommand.getExpression().visit(this, obj);
		// Jump para o else de acordo com a condição
		emit(InstructionType.CONDITIONAL_JUMP, whatJump(op),label+"_else_"+contIfElse+"_begin" , destiny);
		
		// Avaliação de comandos do if
		for (Command cmdIf : ifCommand.getIfCommands()){
			cmdIf.visit(this, obj);
		}
		
		closeScope(destiny);
		
		// Jump para o fim do else caso o if tenha sido executado
		emit(InstructionType.JMP, label+"_else_"+contIfElse+"_end" , destiny);
		// Fim do if
		emit(InstructionType.LABEL, label+"_if_"+contIfElse+"_end", destiny);
		
		// Começo do else
		emit(InstructionType.LABEL, label+"_else_"+contIfElse+"_begin" , destiny);
		openScope(destiny);
		
		// Avaliação de comandos do else
		for (Command cmdElse : ifCommand.getElseCommands()){
			cmdElse.visit(this, obj);
		}
		
		// Fim do else
		closeScope(destiny);
		emit(InstructionType.LABEL, label+"_else_"+contIfElse+"_end" , destiny);
		
		return null;
	}
	
	@Override
	public Object visitProcedureCall(ProcedureCall procedureCall, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String functionName = procedureCall.getIdentifier().getSpelling();
				
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma função
			destiny = functionInstructions;			
		}else{
			// Adicionar no final do array de instruções (main)
			destiny = instructions;
		}
		
		ArrayList<Expression> arguments = procedureCall.getArguments(); 
		int qntArguments = arguments.size();
		
		Collections.reverse(arguments);
		for (Expression e : arguments){
			e.visit(this, obj);
		}
		emit(InstructionType.CALL_FUNCTION,"_"+functionName,destiny);
		// Desloca esp devido os argumentos
		emit(InstructionType.ADD, InstructionType.ESP, (qntArguments*OFFSET)+"", destiny);
		
		return null;
	}
	
	@Override
	public Object visitBreakCommand(BreakCommand breakCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String label;
						
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma função
			destiny = functionInstructions;
			label = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
			
		}else{
			// Adicionar no final do array de instruções
			destiny = instructions;
			label = "main";
		}
		
		emit(InstructionType.JMP, label+"_while_"+contWhile+"_end", destiny);
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
		//TODO: unimplemented
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

		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		//TODO: visita identificador???????
		unaryExpressionFunction.getIdentifier().visit(this, obj);
		
		ArrayList<Expression> arguments = unaryExpressionFunction.getArguments();
		Collections.reverse(arguments);
		for (Expression e : arguments){
			// empilha os parametros
			e.visit(this, null);
		}
		
		String idFunction = unaryExpressionFunction.getIdentifier().getSpelling();
		
		emit(InstructionType.LABEL, "_"+idFunction, destiny);
		
		// Desloca esp devido os argumentos
		emit(InstructionType.ADD, InstructionType.ESP, (arguments.size()*OFFSET)+"", destiny);
		
		return null;
	}

		
	@Override
	public Object visitIdentifier(Identifier identifier, Object obj)
			throws SemanticException {
		
		//idMap.put(identifier.getSpelling(), new IdentifierLocation(level, offset));
		//TODO: unimplemented
		
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
	
	//Métodos extras
	private String whatJump(Operator op){
		String jump = null;
		
		switch (op.getKind()){
		case GrammarSymbols.EQUALS:
			jump = InstructionType.JNE;
			break;
		case GrammarSymbols.DIFFERENT:
			jump = InstructionType.JE;
			break;
		case GrammarSymbols.GREATER_THAN:
			jump  = InstructionType.JLE;
			break;
		case GrammarSymbols.GREATER_THAN_OR_EQUAL_TO:
			jump = InstructionType.JL;
			break;
		case GrammarSymbols.LESS_THAN:
			jump = InstructionType.JGE;
			break;
		case GrammarSymbols.LESS_THAN_OR_EQUAL_TO:
			jump = InstructionType.JG;
			break;
		
		}
		return jump;
	}
	
	private void openScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EBP, instructionList);
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ESP, instructionList);
		increaseLevel();
	}
	
	private void closeScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.MOV, InstructionType.ESP, InstructionType.EBP, instructionList);
		emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EBP, instructionList);
		decreaseLevel();
	}
	
	private void increaseLevel(){
		this.level++;
	}
	
	private void decreaseLevel(){
		this.level--;
	}
	
	private void reachIdentifier(IdentifierLocation il, ArrayList<Instruction> destiny){
		
		int diff = this.level - il.getLevel();
		
		emit(InstructionType.MOV, InstructionType.ECX, InstructionType.EBP, destiny);
		for(int i=0; i<diff; i++){	
			emit(InstructionType.PUSH, InstructionType.PUSH_REG_VALUE+"", InstructionType.EBP, destiny);
			emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EBP, destiny);
			emit(InstructionType.MOV, InstructionType.EDX, InstructionType.EBP, destiny);
		}
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ECX, destiny);
	}
}
