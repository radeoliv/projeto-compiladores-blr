package checker;

import java.util.ArrayList;

import parser.GrammarSymbols;
import util.AST.AST;
import util.symbolsTable.IdentificationTable;
import classes.command.*;
import classes.expression.*;
import classes.root.Root;
import classes.terminal.*;
import classes.terminal.Number;
import classes.procedureCall.ProcedureCall;
import classes.program.*;
import classes.functionDeclaration.*;


import classes.terminal.Operator;

public class Checker implements Visitor{

	IdentificationTable identificationTable;
	
	public Checker(){
		identificationTable = new IdentificationTable();
	}
	
	public void check(AST a) throws SemanticException{
		a.visit(this, null);
	}
	
	// OK! REVISAR
	public Object visitAssignmentCommand(AssignmentCommand assignmentCommand,Object obj) throws SemanticException {
		Identifier id = assignmentCommand.getId();
		AST a = identificationTable.retrieve(id.getSpelling());
		
		if(a == null){
			//Primeiro uso do identificador
			identificationTable.enter(id.getSpelling(), assignmentCommand);
			id.visit(this, obj);
			a = identificationTable.retrieve(id.getSpelling());
		} else {
			//Atribui��o para vari�vel j� existente
			id.visit(this, obj);
		}
		
		//Verifica��o se o identificador � uma fun��o
		if(a instanceof FunctionDeclaration){
			throw new SemanticException("N�o � poss�vel atribuir valor a uma fun��o!");
		}
		
		assignmentCommand.getExpression().visit(this, obj);
		
		// :TODO Isso que t� no coment�rio abaixo s� pode ocorrer se eu declarar como null.
		// Pq em Lua n�o d� pra declarar sem um tipo ao lado do = Ou seja, t� estranho
		
		//Caso o identificador nunca tenha sido usado -> "Declara��o"
		AssignmentCommand aux = ((AssignmentCommand)a);
		String idType = aux.getType();
		String expType = assignmentCommand.getExpression().getType();
		
		if(idType.equals(null)){
			aux.setType(expType);
		} else {
			if(!idType.equals(expType)){
				throw new SemanticException("Tipos incompat�veis!");
			}
		}
		return null;
	}
	
	// OK! REVISAR
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object obj) throws SemanticException {
		String leftExpType = (String)binaryExpression.getLeftExpression().visit(this, obj);
		String rightExpType = (String)binaryExpression.getRightExpression().visit(this, obj);
		
		if(!leftExpType.equals(rightExpType)){
			throw new SemanticException("Express�es incompat�veis!");
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

	// OK! REVISAR
	public Object visitBreakCommand(BreakCommand breakCommand, Object obj) throws SemanticException {
		// � assim que verifica a posi��o 1 do arrayList? Tem como definir que esse array � de int?
		// 1 na posi��o 1 do arrayList significa que passou por um while
		try {
			if ( ((ArrayList<Integer>)obj).get(1) != 1){
				throw new SemanticException ("Break fora de While!");	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// OK! REVISAR
	public Object visitProcedureCall(ProcedureCall procedureCall, Object obj) throws SemanticException {
		visitIdentifier(procedureCall.getIdentifier(), obj);
		
		AST a = identificationTable.retrieve(procedureCall.getIdentifier().getSpelling());
		
		if (a!=null){
			// Verifica se a lista de argumentos � null
			if (procedureCall.getArguments() != null){
				if(procedureCall.getArguments().size() == ((FunctionDeclaration)a).getParameters().size()){
					for(Expression exp : procedureCall.getArguments()){
						exp.visit(this, obj);
					}
				}else{
					throw new SemanticException("Argumentos inv�lidos!");
				}
			}
		
		}else{
			throw new SemanticException ("Procedimento n�o declarado!");	
		}
		
		return null;
	}
	
	// OK! REVISAR
	//: TODO As vari�veis dentro do escopo da fun��o s�o locais, n�?
	//Tem que verificar ainda que se eu declarar com o mesmo nome de uma global funciona, � isso?
	
	public Object visitFunctionDeclaration(FunctionDeclaration functionDeclaration, Object obj) throws SemanticException {
		Identifier fdId = functionDeclaration.getIdentifier();

		AST a = identificationTable.retrieve(fdId.getSpelling());
		if (a == null){
			identificationTable.enter(fdId.getSpelling(), functionDeclaration);
			fdId.visit(this, obj);
			
			identificationTable.openScope();
			
			// Verifica se a lista de parametros � null
			ArrayList<Identifier> parameters = functionDeclaration.getParameters();
			if (parameters != null){
				for(Identifier prm : functionDeclaration.getParameters() ){
					prm.visit(this, obj);
				}
			}
			
			// Verifica se a lista de comandos � null
			ArrayList<Command> commands = functionDeclaration.getCommands();
			if (commands != null){
				int cont = 1;
				for(Command cmd : commands ){
					cmd.visit(this, obj);
					cont++;
					// Verifica se o return � o �ltimo comando
					if (cmd instanceof ReturnCommand && cont!=commands.size() ){
						throw new SemanticException ("Return n�o � o �ltimo comando da fun��o!");
					}else{
						// Return passou por uma fun��o, logo a posi��o 0 do array torna-se 1
						((ArrayList)(obj)).set(0, 1);
						((ReturnCommand)cmd).visit(this, obj);
					}
				}
			}			
								
			identificationTable.closeScope();
		}else{
			throw new SemanticException ("Fun��o j� existente!");
		}
		return null;
	}

	//OK! REVISAR
	public Object visitIdentifier(Identifier identifier, Object obj) throws SemanticException {
		AST a = identificationTable.retrieve(identifier.getSpelling());
		if (a!=null){
			 identifier.setDeclaration(a);
		}else{
			throw new SemanticException ("Identificador n�o existe!");
		}
		return null;
	}

	// OK! REVISAR
	public Object visitIfCommand(IfCommand ifCommand, Object obj) throws SemanticException {
		Expression exp = ifCommand.getExpression();
		
		exp.visit(this, obj);
		if (exp.getType() != GrammarSymbols.INT + "") {
			throw new SemanticException ("Express�o inv�lida!");	
		}
		identificationTable.openScope();
		
		// Verifica se a lista de comandos if � null
		ArrayList<Command> ifCommands = ifCommand.getIfCommands();
		if (ifCommands != null){
			for(Command ifCmd : ifCommand.getIfCommands()){
			ifCmd.visit(this, obj);
			}
		}
		
		// Verifica se a lista de comandos else � null
		ArrayList<Command> elseCommands = ifCommand.getElseCommands();
		if (elseCommands != null){
			for(Command elseCmd : ifCommand.getElseCommands()){
			elseCmd.visit(this, obj);
			}
		}
		identificationTable.closeScope();
		
		return null;
	}
	
	// OK! REVISAR
	public Object visitNumber(Number number, Object obj) {
		return number.getKind();
	}
	
	// OK! REVISAR
	public Object visitOperator(Operator operator, Object obj) {
		return operator.getKind();
	}
	
	// OK! REVISAR
	public Object visitPrintCommand(PrintCommand printCommand, Object obj) throws SemanticException {
		// Verifica se a express�o � null
		if(printCommand.getE() != null)
			printCommand.getE().visit(this, obj);
		return null;
	}
	
	// OK! REVISAR
	public Object visitReturnCommand(ReturnCommand returnCommand, Object obj) throws SemanticException {
		// � assim que verifica a posi��o 0 do arrayList? Tem como definir que esse array � de int?
		// 1 na posi��o 0 do arrayList significa que passou por uma fun��o
		try {
			if ( ((ArrayList<Integer>)obj).get(0) != 1){
				throw new SemanticException ("Return fora de fun��o!");	
			}else{
				// Verifica se a express�o � null
				if(returnCommand.getExpression() != null){
					returnCommand.getExpression().visit(this, obj);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// OK! REVISAR
	public Object visitUnaryExpressionId(UnaryExpressionId unaryExpressionId, Object obj) throws SemanticException {
		unaryExpressionId.getIdentifier().visit(this, obj);
		String idType = ((AssignmentCommand)unaryExpressionId.getIdentifier().getDeclaration()).getType();
		unaryExpressionId.setType(idType);
		return idType;
	}
	
	// OK! REVISAR
	public Object visitUnaryExpressionNumber(UnaryExpressionNumber unaryExpressionNumber, Object obj) {
		unaryExpressionNumber.getNumber().visit(this, obj);
		String numberType = unaryExpressionNumber.getNumber().getKind() + "";
		unaryExpressionNumber.setType(numberType);
		return numberType;
	}
	
	// OK! REVISAR
	public Object visitUnaryExpressionFunction(UnaryExpressionFunction unaryExpressionFunction, Object obj) throws SemanticException {
		Identifier idU = unaryExpressionFunction.getIdentifier();
		AST a = identificationTable.retrieve(idU.getSpelling());
		
		if (a!=null){
			idU.visit(this, obj);
			ArrayList<Expression> arguments = unaryExpressionFunction.getArguments();
			
			if (arguments!= null){
				if ( ((FunctionDeclaration)a).getParameters().size()== arguments.size() ){
					for(Expression exp : arguments){
						exp.visit(this, obj);
					}
				}else{
					throw new SemanticException ("Argumentos inv�lidos!");	
				}
			}
			
		}else{
			throw new SemanticException ("Fun��o n�o declarada!");	
		}
		return null;
	}
	
	// OK! REVISAR
	public Object visitWhileCommand(WhileCommand whileCommand, Object obj) throws SemanticException {
		Expression exp = whileCommand.getExpression();
		
		exp.visit(this, obj);
		if (exp.getType() != GrammarSymbols.INT + "") {
			throw new SemanticException ("Express�o inv�lida!");	
		}
		identificationTable.openScope();
		
		ArrayList<Command> commands = whileCommand.getCommands();
		if (commands != null){
			for(Command cmd : commands ){
				cmd.visit(this, obj);
				if (cmd instanceof BreakCommand){
					// Break passou por um while, logo a posi��o 1 do array torna-se 1
					((ArrayList)(obj)).set(1, 1);
					((BreakCommand)cmd).visit(this, obj);
				}
			}
		}
		identificationTable.closeScope();
		return null;
	}
	
	// OK! REVISAR
	public Object visitProgram(Program program, Object obj) throws SemanticException {
		// Se n�o for um comando ser� uma fun��o
		// Tem maneira de fazer isso melhor?
		// Revisar classe Program
		
		if (program.getCommand() != null){ 
			program.getCommand().visit(this, obj);
		}else{
			program.getFunction().visit(this, obj);
		}
		return null;
	}
	
	// OK! REVISAR
	public Object visitRoot(Root root, Object obj) throws SemanticException {
		if (root!=null){
			for(Program prg : root.getPrograms()){
				prg.visit(this, obj);
			}
		}
		return null;
	}

	
	
}
