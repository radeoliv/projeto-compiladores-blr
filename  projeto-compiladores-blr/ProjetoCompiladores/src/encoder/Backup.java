package encoder;

import java.util.ArrayList;

import compiler.Properties;

import checker.SemanticException;

import util.Arquivo;
import util.Visitor;
import util.AST.AST;
import util.AST.Program;
import util.AST.Command.Command;
import util.AST.Command.FunctionBody;
import util.AST.Command.FunctionDeclaration;
import util.AST.Command.VariableDeclaration;
import util.AST.Expression.BinaryExpression;
import util.AST.Expression.BooleanUnaryExpression;
import util.AST.Expression.Expression;
import util.AST.Expression.IdentifierUnaryExpression;
import util.AST.Expression.NumberUnaryExpression;
import util.AST.Expression.UnaryExpression;
import util.AST.RHS.CallStatementRHS;
import util.AST.RHS.ExpressionRHS;
import util.AST.Statement.AssignStatement;
import util.AST.Statement.BreakStatement;
import util.AST.Statement.CallStatement;
import util.AST.Statement.ContinueStatement;
import util.AST.Statement.IfElseStatement;
import util.AST.Statement.PrintlnStatement;
import util.AST.Statement.ReturnStatement;
import util.AST.Statement.Statement;
import util.AST.Statement.WhileStatement;
import util.AST.Terminal.BooleanValue;
import util.AST.Terminal.Identifier;
import util.AST.Terminal.NumberValue;
import util.AST.Terminal.Operator;
import util.AST.Terminal.Terminal;
import util.AST.Terminal.Type;

public class Backup implements Visitor {

	private ArrayList<Instruction> instructions;
	private int startSectionData = 0;
	private Arquivo arquivo;
	private int contConstantesDouble = 1;

	private int contIfElse;
	
	public void encode(AST ast) throws SemanticException {
		instructions = new ArrayList<Instruction>();
		ast.visit(this, null);
		gerarArquivo();
	}
	
	private void gerarArquivo(){
		String entrada = Properties.sourceCodeLocation;
		String saida = entrada.substring(0, entrada.lastIndexOf('.')+1);
		saida = saida.concat("asm");
		arquivo = new Arquivo(entrada,saida);
		
		for(Instruction i: instructions){
			arquivo.println(i.toString());
			System.out.println(i.toString());
			//System.out.println(i.getOpCode() +" "+i.getOp1()+" "+i.getOp2()+" "+i.getOp3());
		}
		
		arquivo.close();
	}
	
	public void emit(int opCode, String op1, String op2, String op3){
		instructions.add(new Instruction(opCode,op1,op2,op3));
	}
	
	public void emit(int opCode, String op1){
		instructions.add(new Instruction(opCode,op1,null,null));
	}
	
	public void emit(int opCode, String op1,String op2){
		instructions.add(new Instruction(opCode,op1,op2,null));
	}
	
	public void emit(int opCode, String op1,String op2,int posicao){
		instructions.add(posicao,new Instruction(opCode,op1,op2,null));
	}

	public Object visitProgram(Program prog, Object arg)throws SemanticException {
		
		emit(InstructionType.EXTERN,InstructionType.PRINTF);
		emit(InstructionType.SECTION, InstructionType.DATA);
		startSectionData = instructions.size();
		
		for(Command c : prog.getCommands() ){
			if(c instanceof VariableDeclaration){
				VariableDeclaration vd = (VariableDeclaration)c;
				vd.setEscopo(Identifier.GLOBAL);
				c.visit(this, prog);
			}
		}
		emit(InstructionType.INT_FORMAT,"");
		emit(InstructionType.DOUBLE_FORMAT,"");
		
		emit(InstructionType.SECTION,InstructionType.TEXT);
		emit(InstructionType.GLOBAL,InstructionType.WINMAIN);
		
		for(Command c : prog.getCommands() ){
			if(c instanceof FunctionDeclaration)c.visit(this, prog);
		}
		
		return prog;
	}
	
	public Object visitVariableDeclaration(VariableDeclaration com, Object arg)	throws SemanticException {
		com.getIdentifier().visit(this, arg);
		Identifier id = com.getIdentifier();
		String s = id.getTipo();
		if(arg instanceof Program){
			emit(InstructionType.VARIAVEL_GLOBAL,id.getSpelling(),s);
			((VariableDeclaration)id.getNodeDeclaration()).setEscopo(Identifier.GLOBAL);
		}else{
			((VariableDeclaration)id.getNodeDeclaration()).setEscopo(Identifier.LOCAL);
		}
		
		return null;
	}
	
	public Object visitFunctionDeclaration(FunctionDeclaration com, Object arg)throws SemanticException {
		
		com.getFunctionName().visit(this, com);
		Identifier id = com.getFunctionName();
		emit(InstructionType.FUNCTION_LABEL,id.getSpelling());
		
		int deslocamento=4;
		for(int i=(com.getParameters().size()-1);i>=0;i--){
			com.getParameters().get(i).setDeslocamento(deslocamento);
			if(com.getParameters().get(i).getType().getSpelling().equals("double")){
				deslocamento+=8;
			}else
				deslocamento+=4;	
		}
		
		com.getFunctionBody().visit(this, com);
		
		emit(InstructionType.MOV,InstructionType.ESP,InstructionType.EBP);
		emit(InstructionType.POP,InstructionType.EBP);
		if(com.getFunctionName().getSpelling().equals("main")) 
			emit(InstructionType.MOV,InstructionType.EAX,"0");
		emit(InstructionType.RET,"");
		
		return null;
	}
	
	public Object visitFunctionBody(FunctionBody funB, Object arg)throws SemanticException {
		contIfElse = 1;
		emit(InstructionType.PUSH, InstructionType.EBP);
		emit(InstructionType.MOV, InstructionType.EBP, InstructionType.ESP);
		
		Integer deslocamento = 0;
		String tipo;
		//Conta o deslocamento
		for(VariableDeclaration vd : funB.getVariables()){
			tipo = vd.getType().getSpelling();
			if(tipo.equals("int") || tipo.equals("boolean"))
				deslocamento += 4;
			else
				deslocamento += 8;
		}
		//Desloca o ESP
		if(deslocamento != 0)
			emit(InstructionType.SUB,InstructionType.ESP,deslocamento.toString());
		
		//Atribui o deslocamento
		for(VariableDeclaration vd : funB.getVariables()){
			vd.setEscopo(Identifier.LOCAL);
			tipo = vd.getType().getSpelling();
			if(tipo.equals("int") || tipo.equals("boolean")){
				vd.setDeslocamento(deslocamento);
				deslocamento -= 4;
			}
			else{
				vd.setDeslocamento(deslocamento);
				deslocamento -= 8;
			}
		}
		for(Statement s : funB.getStatements()){
			s.visit(this, arg);
		}
		return null;
	}
	
	public Object visitAssignStatement(AssignStatement statement, Object arg)throws SemanticException {
		statement.getVariableName().visit(this, arg);
		//Empilha
		statement.getRightHandStatement().visit(this, statement);
		
		Identifier id = statement.getVariableName();
		VariableDeclaration vd = ((VariableDeclaration)id.getNodeDeclaration());
		
		Integer deslocamento = vd.getDeslocamento();
		String tipo = vd.getType().getSpelling();
		
		//System.out.println("Indentifier:             "+id.getSpelling());
		//Desempilha e salva
		if(tipo.equals("double")) 
			if(vd.getEscopo() == Identifier.GLOBAL)
				emit(InstructionType.POP_FLOAT,id.getSpelling());
			else{
				emit(InstructionType.POP_FLOAT,InstructionType.EBP,'-'+deslocamento.toString());
			}
		else{
			if(vd.getEscopo() == Identifier.GLOBAL)
				emit(InstructionType.POP,id.getTipo(),id.getSpelling());
			else
				emit(InstructionType.POP,id.getTipo(),InstructionType.EBP,'-'+deslocamento.toString());
		}

		return null;
	}

	public Object visitBinaryExpression(BinaryExpression expr, Object arg)throws SemanticException {
		Expression exp = (Expression) expr;
		if(exp instanceof UnaryExpression){
			if(exp instanceof BooleanUnaryExpression){
				BooleanUnaryExpression ex = (BooleanUnaryExpression) exp;
			}else if(exp instanceof IdentifierUnaryExpression){
				IdentifierUnaryExpression ex = (IdentifierUnaryExpression) exp;
			}else {
				NumberValue ex = ((NumberUnaryExpression)exp).getNumberValue();
			}
			UnaryExpression ex = (UnaryExpression)exp;
			emit(InstructionType.PUSH,null );
		}
		expr.getLeftExpression().visit(this, arg); 
		expr.getRightExpression().visit(this, arg); 
		expr.getOperator().visit(this, expr); 
		return null;
	}

	public Object visitBooleanUnaryExpression(BooleanUnaryExpression expr,Object arg) throws SemanticException {
		expr.getBooleanValue().visit(this, arg);
		return null;
	}

	public Object visitBooleanValue(BooleanValue value, Object arg)	throws SemanticException {
		
		if(value.getSpelling().equals("true")){
				emit(InstructionType.PUSH,"boolean","true");
		}else{
				emit(InstructionType.PUSH,"boolean","false");
		}
		return null;
	}

	public Object visitBreakStatement(BreakStatement statement, Object arg)throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitCallStatement(CallStatement statement, Object arg)throws SemanticException {
		if(arg instanceof AssignStatement){
			//Empilhando os parametros da função
			for(Identifier id : statement.getArguments() ){
				id.visit(this, statement);
			}
			emit(InstructionType.CALL_FUNTION, statement.getFunctionName().getSpelling());
			
			//limpar valor alocado para os paremetros
			int deslocamento=0;
			Identifier id = statement.getFunctionName();
			FunctionDeclaration fun = ((FunctionDeclaration)id.getNodeDeclaration());
			for( VariableDeclaration vd : fun.getParameters() ){
				deslocamento += vd.getDeslocamento();
			}
			Integer desloc=new Integer(deslocamento);
			emit(InstructionType.ADD, InstructionType.ESP, desloc.toString());
			//Empilhando valor de retorno
			emit(InstructionType.PUSH, InstructionType.EAX);
		}
		return null;
	}

	public Object visitCallStatementRHS(CallStatementRHS rhs, Object arg)throws SemanticException {
		rhs.getFunctionCall().visit(this, arg);
		return null;
	}

	public Object visitContinueStatement(ContinueStatement statement, Object arg)throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitExpressionRHS(ExpressionRHS expRHS, Object arg)throws SemanticException {
		expRHS.getExpression().visit(this, arg);
		return null;
	}

	public Object visitIdentifier(Identifier id, Object arg)throws SemanticException {
		if(arg instanceof CallStatement){
			VariableDeclaration vd = ((VariableDeclaration)id.getNodeDeclaration());
			Integer deslocamento = vd.getDeslocamento();
			String tipo = vd.getType().getSpelling();
			
			if(tipo.equals("double")){
				if(vd.getEscopo() == Identifier.GLOBAL)
					emit(InstructionType.PUSH_FLOAT,id.getSpelling());
				else
					emit(InstructionType.PUSH_FLOAT,InstructionType.EBP,'-'+deslocamento.toString());
			}else {
				if(vd.getEscopo() == Identifier.GLOBAL)
					emit(InstructionType.PUSH,id.getTipo(),id.getSpelling());
				else
					emit(InstructionType.PUSH,id.getTipo(),InstructionType.EBP,'-'+deslocamento.toString());
			}
		}
		return null;
	}

	public Object visitIdentifierUnaryExpression(IdentifierUnaryExpression expr, Object arg)throws SemanticException {
		if(arg instanceof AssignStatement){
			Identifier id = expr.getVariableName();
			VariableDeclaration vd = ((VariableDeclaration)id.getNodeDeclaration());
			Integer deslocamento = vd.getDeslocamento();
			
			if(expr.getTipo().equals("double")){
				if(vd.getEscopo() == Identifier.GLOBAL)
					emit(InstructionType.PUSH_FLOAT,id.getSpelling());
				else{
					emit(InstructionType.PUSH_FLOAT,InstructionType.EBP,'-'+deslocamento.toString());
				}
				
			}else {
				if(vd.getEscopo() == Identifier.GLOBAL)
					emit(InstructionType.PUSH,id.getTipo(),id.getSpelling());
				else
					emit(InstructionType.PUSH,id.getTipo(),InstructionType.EBP,'-'+deslocamento.toString());
			}
		}
		return null;
	}

	public Object visitIfElseStatement(IfElseStatement statement, Object arg)throws SemanticException {
		if(arg instanceof FunctionDeclaration){
			FunctionDeclaration fd = (FunctionDeclaration)arg;
			for(Statement s: statement.getIfStatements()){
				s.visit(this, arg);
			}
			String labelElse = fd.getFunctionName().getSpelling()+'_'+"else_"+contIfElse+"_block";
			emit(InstructionType.FUNCTION_LABEL, labelElse);
			
			for(Statement s: statement.getElseStatements()){
				s.visit(this, arg);
			}
			String label = fd.getFunctionName().getSpelling()+'_'+"endIf_"+contIfElse;
			emit(InstructionType.FUNCTION_LABEL, label);
			contIfElse++;
		}
		return null;
	}

	public Object visitNumberUnaryExpression(NumberUnaryExpression expr,Object arg) throws SemanticException {
		expr.getNumberValue().visit(this, arg);
		return null;
	}

	public Object visitNumberValue(NumberValue value, Object arg)throws SemanticException {
		if(arg instanceof AssignStatement){
			if(value.getTipo().equals("double")){
				String constante = "const"+contConstantesDouble;
				emit(InstructionType.VARIAVEL_GLOBAL,constante,value.getTipo(),startSectionData);
				contConstantesDouble++;
				emit(InstructionType.PUSH_FLOAT,constante);
			}else{
				emit(InstructionType.PUSH,value.getTipo(),value.getSpelling());
			}
		}
		return null;
	}

	public Object visitOperator(Operator operator, Object arg)throws SemanticException {
		String op=operator.getSpelling();
		BinaryExpression be = (BinaryExpression)arg;
		if(be.getTipo().equals("double")){
			if(op.equals("+")){
				emit(InstructionType.ADD_FLOAT,InstructionType.ST1);
			}
			if(op.equals("-")){
				emit(InstructionType.SUB_FLOAT,InstructionType.ST1);
			}
			if(op.equals("/")){
				emit(InstructionType.MULT_FLOAT,InstructionType.ST1);
			}
			if(op.equals("*")){
				emit(InstructionType.DIV_FLOAT,InstructionType.ST1);
			}
		}else{
			if(op.equals("+")){
				
			}
			if(op.equals("-")){
				
			}
			if(op.equals("/")){
				
			}
			if(op.equals("*")){
				
			}
		}
		return null;
	}

	public Object visitPrintlnStatement(PrintlnStatement statement, Object arg)	throws SemanticException {
		Identifier id = statement.getVariableName();
		VariableDeclaration vd = (VariableDeclaration)id.getNodeDeclaration();
		
		if(vd.getEscopo() == Identifier.GLOBAL){
		//	if(!id.getTipo().equals("double"))emit(InstructionType.PUSH,id.getTipo(),"["+id.getSpelling()+"]");
		}
		
		return null;
	}

	public Object visitReturnStatement(ReturnStatement statement, Object arg)throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitType(Type type, Object arg) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object visitUnaryExpression(UnaryExpression unExp, Object arg)throws SemanticException {
		unExp.visit(this, arg);
		return null;
	}


	public Object visitWhileStatement(WhileStatement statement, Object arg)	throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
