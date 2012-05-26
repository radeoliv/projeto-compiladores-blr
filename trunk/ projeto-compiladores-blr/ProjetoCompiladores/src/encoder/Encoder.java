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
	private int relativeOffset;
	
	private int parameterCounter; 
	
	private ArrayList<Instruction> instructions;
	private ArrayList<Instruction> functionInstructions;
	private Arquivo file;
	
	private HashMap<String, IdentifierLocation> idMap;
	private int level;
	private int contIfElse;
	private int contWhile;

	public Encoder(){
		this.instructions = new ArrayList<Instruction>();
		this.functionInstructions = new ArrayList<Instruction>();
		this.idMap = new HashMap<String, IdentifierLocation>();
		//Nível principal (da main) é 0
		this.level = 0;
		this.contIfElse = 0;
		this.contWhile = 0;
		this.relativeOffset = 0;
		this.parameterCounter = 0;
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
			
			//Para melhor visualização:
			System.out.println(inst.toString());
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
		openScope(instructions);
				
		ArrayList<Program> programList = root.getPrograms();
		
		for(Program program : programList){
			program.visit(this, null);
		}
		closeScope(instructions);
		emit(InstructionType.BLOCKS_SEPARATOR, instructions);
		
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
		
		Identifier id = assignmentCommand.getId();
		IdentifierLocation il = null; 
			
		if(!idMap.containsKey(id.getSpelling())) {
			//Primeira ocorrência do identificador
			id.visit(this, obj);
			il = idMap.get(id.getSpelling());
			
			//Resultado da expressão em EAX
			assignmentCommand.getExpression().visit(this, obj);
			
			//Guarda EAX na pilha
			emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EAX, destiny);
			
			//Guarda valor da expressão na posição do identificador -> pop dword [ebp+offset]
			emit(InstructionType.POP, InstructionType.POP_REG_OFFSET+"", InstructionType.EBP, il.getOffset()+"", destiny);
		}else{
			il = idMap.get(id.getSpelling());
			
			//EDX aponta para o EBP do frame do Identificador 
			reachIdentifier(il, destiny);
			
			// Resultado da expressão em EAX
			assignmentCommand.getExpression().visit(this, obj);
			
			//Guarda EAX na pilha
			emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EAX, destiny);
			
			// Atribui valor de EAX na posição do identificador encontrado em EDX
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
		
		parameterCounter = 0;
		ArrayList<Parameter> parameters = functionDeclaration.getParameters();
		for(Parameter p : parameters){
			p.visit(this, obj);
		}
		
		ArrayList<Command> commands = functionDeclaration.getCommands(); 
		for(Command c : commands){
			c.visit(this, obj);
		}
		
		//Se possuir um retorno, o valor da expressão é jogado em EAX
		Expression retExp = functionDeclaration.getReturnExp();
		if(retExp != null){
			retExp.visit(this, obj);
		}

		closeScope(functionInstructions);
		if (retExp != null)
			emit(InstructionType.RET, functionInstructions);
		
		emit(InstructionType.BLOCKS_SEPARATOR, functionInstructions);
		// Adiciona essa função no array que contém todas as instruções do arquivo
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
		emit(InstructionType.CONDITIONAL_JUMP, whatJump(op.getSpelling()),label+"_end" , destiny);
		
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
		int labelCounter = ++contIfElse;
		
		ArrayList <Command> elseCommands = ifCommand.getElseCommands();
		
		if (obj instanceof FunctionDeclaration){
			destiny = functionInstructions;
			label = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
		}else{
			destiny = instructions;
			label = "main";
		}
		
		// Início do if
		// TODO: Label para if e while não começa com _
		// TODO: Tentar não gerar parte do else caso não exista.
		emit(InstructionType.LABEL, label+"_if_"+labelCounter+"_begin", destiny);
		openScope(destiny);
		Operator op = ((BinaryExpression)ifCommand.getExpression()).getOperator();
		ifCommand.getExpression().visit(this, obj);
		
		// Jump para o else de acordo com a condição
		emit(InstructionType.CONDITIONAL_JUMP, whatJump(op.getSpelling()),label+"_else_"+labelCounter+"_begin" , destiny);
		
		// Avaliação de comandos do if
		for (Command cmdIf : ifCommand.getIfCommands()){
			cmdIf.visit(this, obj);
		}
		
		closeScope(destiny);
		// Jump para o fim do else
		emit(InstructionType.JMP, label+"_else_"+labelCounter+"_end" , destiny);
		// Fim do if
		emit(InstructionType.LABEL, label+"_if_"+labelCounter+"_end", destiny);
		
		// Começo do else
			emit(InstructionType.LABEL, label+"_else_"+labelCounter+"_begin" , destiny);
			openScope(destiny);
			
			// Avaliação de comandos do else
			for (Command cmdElse : elseCommands){
				cmdElse.visit(this, obj);
			}
				
			// Fim do else
			closeScope(destiny);
			emit(InstructionType.LABEL, label+"_else_"+labelCounter+"_end" , destiny);
			
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
			//EAX terá o resultado de cada expressão
			e.visit(this, obj);
			//O valor que EAX contém é jogado na pilha
			emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EAX, destiny);
		}
		
		emit(InstructionType.CALL_FUNCTION,"_"+functionName,destiny);
		
		// Contrai pilha deslocando esp devido os argumentos
		if (qntArguments > 0)
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
	public Object visitPrintCommand(PrintCommand printCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		//EAX tem o resultado da expressão
		printCommand.getExpression().visit(this, obj);
		emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EAX, destiny);
		
		//intFormat
		emit(InstructionType.PUSH, InstructionType.PUSH_CONSTANT+"", "intFormat", destiny);
		//_printf
		emit(InstructionType.CALL_FUNCTION, InstructionType.PRINTF, destiny);
		//Contrai pilha
		emit(InstructionType.ADD, InstructionType.ESP, 2*OFFSET+"", destiny);
		
		return null;
	}

	@Override
	public Object visitParameter(Parameter parameter, Object obj) throws SemanticException {
		
		parameterCounter++;
		
		Identifier id = parameter.getIdentifier();
		
		IdentifierLocation il = new IdentifierLocation(this.level, parameterCounter*OFFSET);
		idMap.put(id.getSpelling(), il);
		
		return null;
	}
	
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,Object obj) throws SemanticException {	
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		// Tem que colocar a let pra eax e a right para ebx e gravar o resultado em eax pra colocar na pilha
		// empilha resultado leftExpression (push eax)
		binaryExpression.getLeftExpression().visit(this, obj);
		//Salva resultado da leftExpression na pilha 
		emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EAX, destiny);

		// empilha resultado rightExpression
		binaryExpression.getRightExpression().visit(this, obj);
		//Salva resultado da rightExpression em EBX ou EDX dependendo da operação
		if(binaryExpression.getOperator().getKind() == GrammarSymbols.DIVISION){
			emit(InstructionType.MOV, InstructionType.EDX, InstructionType.EAX, destiny);
		} else {
			emit(InstructionType.MOV, InstructionType.EBX, InstructionType.EAX, destiny);
		}
		
		//Guarda em EAX o valor da leftExpression armazenado previamente na pilha
		emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EAX, destiny);
		
		// Realiza operação salvando o resultado em EAX
		binaryExpression.getOperator().visit(this, obj);
		
		return null;
	}

	@Override
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId,
			Object obj) throws SemanticException {
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		Identifier id = unaryExpressionId.getIdentifier();
		
		IdentifierLocation il = idMap.get(id.getSpelling());
		
		reachIdentifier(il, destiny);
		
		emit(InstructionType.PUSH, InstructionType.PUSH_REG_OFFSET+"", InstructionType.EDX, il.getOffset()+"", destiny);
		emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EAX, destiny);
		return null;
	}

	@Override
	public Object visitUnaryExpressionNumber(
			UnaryExpressionNumber unaryExpressionNumber, Object obj) {
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		Number number = unaryExpressionNumber.getNumber();
		
		emit(InstructionType.PUSH, InstructionType.PUSH_CONSTANT+"", number.getSpelling(), destiny);
		emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EAX, destiny);
		
		return null;
	}
	
	@Override
	public Object visitUnaryExpressionFunction(UnaryExpressionFunction unaryExpressionFunction, Object obj) throws SemanticException {
		
		ArrayList<Instruction> destiny;
		String functionName = unaryExpressionFunction.getIdentifier().getSpelling();
				
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma função
			destiny = functionInstructions;	
		}else{
			// Adicionar no final do array de instruções (main)
			destiny = instructions;
		}
		
		ArrayList<Expression> arguments = unaryExpressionFunction.getArguments(); 
		int qntArguments = arguments.size();
		
		Collections.reverse(arguments);
		for (Expression e : arguments){
			//EAX terá o resultado de cada expressão
			e.visit(this, obj);
			//O valor que EAX contém é jogado na pilha
			emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EAX, destiny);
		}
		
		emit(InstructionType.CALL_FUNCTION,"_"+functionName,destiny);
		
		// Contrai pilha deslocando esp devido os argumentos
		if (qntArguments > 0)
			emit(InstructionType.ADD, InstructionType.ESP, (qntArguments*OFFSET)+"", destiny);
		
		return null;
	}

		
	@Override
	public Object visitIdentifier(Identifier identifier, Object obj)
			throws SemanticException {
		
		relativeOffset--;
		
		IdentifierLocation il = new IdentifierLocation(this.level, relativeOffset*OFFSET);
		
		idMap.put(identifier.getSpelling(), il);
		
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
			//TODO: Cuidado com a divisão!
			case GrammarSymbols.DIVISION:
				emit(InstructionType.DIV, InstructionType.EAX, InstructionType.EDX, destiny);
				break;
			case GrammarSymbols.RELATIONAL_OPERATOR:
				emit(InstructionType.CMP, InstructionType.EAX, InstructionType.EBX, destiny);
				break;
		}

		return null;
	}
	
	//Métodos extras
	
	private String whatJump(String op){
		String jump = null;
	
		if (op.equals("==")){
			jump = InstructionType.JNE;
		}else if (op.equals("~=")){
			jump = InstructionType.JE;
		}else if (op.equals(">")){
			jump  = InstructionType.JLE;
		}else if (op.equals(">=")){
			jump = InstructionType.JL;
		}else if (op.equals("<")){
			jump = InstructionType.JGE;
		}else{
			jump = InstructionType.JG;
		}
				
		return jump;
	}
		
	
	private void openScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.PUSH, InstructionType.PUSH_REG_ADDRESS+"", InstructionType.EBP, instructionList);
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ESP, instructionList);
		this.level++;
		relativeOffset = 0;
	}
	
	private void closeScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.MOV, InstructionType.ESP, InstructionType.EBP, instructionList);
		emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EBP, instructionList);
		
		//Remove do hashmap os valores que não pertencerão ao escopo anterior
		removeLastScopeIds();
		
		this.level--;
		
		getLastScopeRelativeOffset();
		
	}
	
	private void getLastScopeRelativeOffset(){
		relativeOffset = 0;
		for(IdentifierLocation il : idMap.values()){
			if(il.getLevel() == this.level){
				relativeOffset--;
			}
		}
	}
	
	private void removeLastScopeIds(){
		for(IdentifierLocation il : idMap.values()){
			if(il.getLevel() == this.level){
				idMap.remove(il.getSpelling());
			}
		}
	}
	
	private void reachIdentifier(IdentifierLocation il, ArrayList<Instruction> destiny){
		
		int diff = this.level - il.getLevel();
		//Pegando o EBP antigo
		emit(InstructionType.MOV, InstructionType.ECX, InstructionType.EBP, destiny);
		
		for(int i=0; i<diff; i++){	
			emit(InstructionType.PUSH, InstructionType.PUSH_REG_VALUE+"", InstructionType.EBP, destiny);
			emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EBP, destiny);

			emit(InstructionType.MOV, InstructionType.EDX, InstructionType.EBP, destiny);
		}
		
		//Restaura o EBP
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ECX, destiny);
	}
}
