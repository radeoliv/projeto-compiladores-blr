package classes.program;

import java.util.ArrayList;

import util.AST.AST;
import checker.SemanticException;
import checker.Visitor;
import classes.command.Command;
import classes.functionDeclaration.FunctionDeclaration;

public class Program extends AST {
	
	private ArrayList<Command> commands;
	private ArrayList<FunctionDeclaration> functions;
	
	public Program(ArrayList<Command> commands, ArrayList<FunctionDeclaration> functions){
		this.commands = commands;
		this.functions = functions;
	}

	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitProgram(this, obj);
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<Command> commands) {
		this.commands = commands;
	}

	public ArrayList<FunctionDeclaration> getFunctions() {
		return functions;
	}

	public void setFunctions(ArrayList<FunctionDeclaration> functions) {
		this.functions = functions;
	}
}
