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
	
	//A se��o de fun��o (declara��es de fun��es) sempre vem ap�s o cabe�alho. Como este n�o muda, o tamanho � constante.
	public static final int FUNCTION_SECTION = 9;
	
	//Deslocamento sempre � multiplo de 4 (tratamento apenas para inteiros) 
	private static final int OFFSET = 4;
	
	//TODO: Verificar pushes na pilha
	private int relativeOffset;
	
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
		//N�vel principal (da main) � 0
		this.level = 0;
		this.contIfElse = 0;
		this.contWhile = 0;
		
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
	//public void emit(int position, int operationCode, String op1,String op2, ArrayList<Instruction> instructionList){
	//	instructionList.add(position,new Instruction(operationCode,op1,op2,null));
	//}
	
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
		
		//Gerando informa��es pr�-c�digo - 'cabe�alho'
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
		// TODO No c�digo dele, depois do label da main ele aloca espa�o pra vari�veis que aparecem dentro da main
		//sub esp, 16 ; Aloca espa�o para vari�veis locais: 1 double e 2 int = 16 bytes
		
		ArrayList<Instruction> destiny = null;		
		
		if (obj instanceof FunctionDeclaration){
			destiny = functionInstructions;
		}else{
			destiny = instructions;
		}
		
		if(!idMap.containsKey(assignmentCommand.getId().getSpelling())) {
			//Primeira vez que o identificador aparece
			assignmentCommand.getId().visit(this, obj);
			//TODO Mas a primeira vez que ele aparece tb tem uma express�o � direita
		}else{			
			// Resultado de express�o em EAX
			assignmentCommand.getExpression().visit(this, obj);
			
			IdentifierLocation il = idMap.get(assignmentCommand.getId().getSpelling());
			
			//EDX aponta para o EBP do frame do Identificador 
			reachIdentifier(il, destiny);
			
			// Joga valor de EAX na posi��o do identificador encontrado em EDX
			emit(InstructionType.POP, InstructionType.POP_REG_OFFSET+"", InstructionType.EDX, il.getOffset()+"", destiny);
		}
		
		return null;
	}
	
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		//Limpando lista de instru��es de fun��o
		functionInstructions = new ArrayList<Instruction>();		
		
		Identifier id = functionDeclaration.getIdentifier();
		
		//Armazena informa��es do identificador da FunctionDeclaration
		id.visit(this, obj);
		
		emit (InstructionType.LABEL, id.getSpelling(), functionInstructions);
		openScope(functionInstructions);		
		
		ArrayList<Command> commands = functionDeclaration.getCommands(); 
		for(Command c : commands){
			c.visit(this, obj);
		}
		
		//Se possuir um retorno, o valor da express�o � jogado em EAX
		Expression ret = functionDeclaration.getReturnExp(); 
		if(ret != null){
			ret.visit(this, obj);
		}
		// TODO Ele tava emitindo a instru��o de retorno toda vez... E se for procedimento?
		closeScope(functionInstructions);
		if (ret!=null)
			emit(InstructionType.RET, functionInstructions);
		emit(InstructionType.BLOCKS_SEPARATOR, functionInstructions);
		// Adiciona essa fun��o no array que cont�m todas as instru��es do arquivo
		emit(functionInstructions, instructions);
		
		return null;
	}
	
	@Override
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String label;
		contWhile++;
		
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma fun��o
			destiny = functionInstructions;
			String functionName = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
			label = functionName+"_while_"+contWhile;
			
		}else{
			// Adicionar no final do array de instru��es (main)
			destiny = instructions;
			label = "main_while_"+contWhile;
		}
		
		// Label de �nicio do While
		emit(InstructionType.LABEL, label+"_begin", destiny);
		openScope(destiny);
		
		Operator op = ((BinaryExpression)whileCommand.getExpression()).getOperator();

		// Avalia��o da Express�o - EAX
		whileCommand.getExpression().visit(this, obj);
		
		// Jump de acordo com a condi��o
		emit(InstructionType.CONDITIONAL_JUMP, whatJump(op.getSpelling()),label+"_end" , destiny);
		
		// Avalia��o de comandos
		for (Command cmd : whileCommand.getCommands()){
			cmd.visit(this, obj);
		}
		
		// Jump para o in�cio do While
		emit (InstructionType.JMP, label+"_begin", destiny);
		
		closeScope(destiny);
		emit(InstructionType.LABEL, label+"_end", destiny);
		
		return null;
	}

	@Override
	public Object visitIfCommand(IfCommand ifCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String label;
		// TODO Ele t� se confundindo todooo
		contIfElse++;
		ArrayList <Command> elseCommands = ifCommand.getElseCommands();
		
		if (obj instanceof FunctionDeclaration){
			destiny = functionInstructions;
			label = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
		}else{
			destiny = instructions;
			label = "main";
		}
		
		// In�cio do if
		// TODO label para if e while n�o come�a com _
		emit(InstructionType.LABEL, label+"_if_"+contIfElse+"_begin", destiny);
		openScope(destiny);
		Operator op = ((BinaryExpression)ifCommand.getExpression()).getOperator();
		ifCommand.getExpression().visit(this, obj);
		
		// Jump para o else de acordo com a condi��o
		emit(InstructionType.CONDITIONAL_JUMP, whatJump(op.getSpelling()),label+"_else_"+contIfElse+"_begin" , destiny);
		
		// Avalia��o de comandos do if
		for (Command cmdIf : ifCommand.getIfCommands()){
			cmdIf.visit(this, obj);
		}
		
		closeScope(destiny);
		// Jump para o fim do else
		emit(InstructionType.JMP, label+"_else_"+contIfElse+"_end" , destiny);
		// Fim do if
		emit(InstructionType.LABEL, label+"_if_"+contIfElse+"_end", destiny);
		
		// Come�o do else
			emit(InstructionType.LABEL, label+"_else_"+contIfElse+"_begin" , destiny);
			openScope(destiny);
			
			// Avalia��o de comandos do else
			for (Command cmdElse : elseCommands){
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
			// Adicionar para uma fun��o
			destiny = functionInstructions;			
		}else{
			// Adicionar no final do array de instru��es (main)
			destiny = instructions;
		}
		
		ArrayList<Expression> arguments = procedureCall.getArguments(); 
		int qntArguments = arguments.size();
		
		// TODO Rever isso aqui
		Collections.reverse(arguments);
		for (Expression e : arguments){
			e.visit(this, obj);
		}
		emit(InstructionType.CALL_FUNCTION,"_"+functionName,destiny);
		// Desloca esp devido os argumentos
		// TODO Ele tava gerando a instru��o add esp, 0
		if (qntArguments != 0)
			emit(InstructionType.ADD, InstructionType.ESP, (qntArguments*OFFSET)+"", destiny);
		
		return null;
	}
	
	@Override
	public Object visitBreakCommand(BreakCommand breakCommand, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String label;
						
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma fun��o
			destiny = functionInstructions;
			label = ((FunctionDeclaration)obj).getIdentifier().getSpelling();
			
		}else{
			// Adicionar no final do array de instru��es
			destiny = instructions;
			label = "main";
		}
		
		emit(InstructionType.JMP, label+"_while_"+contWhile+"_end", destiny);
		return null;
	}

	@Override
	public Object visitPrintCommand(PrintCommand printCommand, Object obj) throws SemanticException {
		
		/* TODO No c�digo dele tem assim sempre que aparece um printf
		push dword [a] ; Empilha o valor de a
	    push dword intFormat ; Empilha o formato do printf para int
	    call _printf ; Chama a fun��o externa printf (C)
	    add esp, 8 ; Libera o espa�o dos dois par�metros*/
		
		printCommand.getExpression().visit(this, obj);
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		
		emit(InstructionType.CALL_FUNCTION, InstructionType.PRINTF, destiny);
		return null;
	}

	// Larissa
	@Override
	public Object visitParameter(Parameter parameter, Object obj) throws SemanticException {
		parameter.getIdentifier().visit(this, obj);
		return null;
	}
	
	// Larissa
	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression,Object obj) throws SemanticException {
		//TODO: n�o sei se ser� assim	
		
		ArrayList<Instruction> destiny = instructions;
		if(obj instanceof FunctionDeclaration)
			destiny = functionInstructions;
		// Tem que colocar a let pra eax e a right para ebx e gravar o resultado em eax pra colocar na pilha
		// empilha resultado leftExpression (push eax)
		binaryExpression.getLeftExpression().visit(this, obj);
		// empilha resultado rightExpression
		binaryExpression.getRightExpression().visit(this, obj);
		// realiza opera��o empilhando o resultado
		binaryExpression.getOperator().visit(this, obj);
		
		return null;
	}

	@Override
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId,
			Object obj) throws SemanticException {
		// TODO Auto-generated method stub
		// TODO Aqui vai ficar todo aquele esquema do reach
		return null;
	}

	@Override
	public Object visitUnaryExpressionNumber(
			UnaryExpressionNumber unaryExpressionNumber, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	// Larissa
	@Override
	public Object visitUnaryExpressionFunction(UnaryExpressionFunction unaryExpressionFunction, Object obj) throws SemanticException {
		ArrayList<Instruction> destiny;
		String functionName = unaryExpressionFunction.getIdentifier().getSpelling();
				
		if (obj instanceof FunctionDeclaration){
			// Adicionar para uma fun��o
			destiny = functionInstructions;			
		}else{
			// Adicionar no final do array de instru��es (main)
			destiny = instructions;
		}
		
		ArrayList<Expression> arguments = unaryExpressionFunction.getArguments(); 
		int qntArguments = arguments.size();
		
		// TODO Rever isso aqui
		Collections.reverse(arguments);
		for (Expression e : arguments){
			e.visit(this, obj);
		}
		
		// TODO (n�o � toDo) -- Algu�m tinha esquecido que para chamar fun��o ou procedimento tem que colocar
		// esse call, pq � de assembly. A pessoa tinha colocado pra criar um label... acho que ela confundiu
		// com o call que nossa gram�tica exige
		emit(InstructionType.CALL_FUNCTION,"_"+functionName,destiny);
		// Desloca esp devido os argumentos
		// TODO Ele tava gerando a instru��o add esp, 0
		if (qntArguments != 0)
			emit(InstructionType.ADD, InstructionType.ESP, (qntArguments*OFFSET)+"", destiny);
		
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

		// pega o tipo da opera��o
		int operatorKind = operator.getKind();
				
		/*
		 *  considerando que os resultados das express�es j� foram empilhados
		 *  s� precisamos verificar qual o tipo da opera��o que dever� ser feita
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
	
	//M�todos extras
	/*private String whatJump(Operator op){
		String jump = null;
		
		// TODO Ele n�o t� conseguindo entrar nos cases e t� retornando null
		// Verifiquei e � por causa da passagem Qnd passo l� op.kind()) n�o pega
		// Ent�o esse kind n�o � oq a gente quer
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
	}*/
	
	// Pensei numa solu��o
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
		increaseLevel();
	}
	
	private void closeScope(ArrayList<Instruction> instructionList){
		emit(InstructionType.MOV, InstructionType.ESP, InstructionType.EBP, instructionList);
		emit(InstructionType.POP, InstructionType.POP_REG+"", InstructionType.EBP, instructionList);
		decreaseLevel();
	}
	
	// TODO D� pra colocar esses m�todos como parte dos anteriores, afinal � uma linha
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
			// TODO Para que essa linha?
			emit(InstructionType.MOV, InstructionType.EDX, InstructionType.EBP, destiny);
		}
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ECX, destiny);
	}
}
