package classes.program;

import java.util.ArrayList;

import util.AST.AST;
import checker.Visitor;
import classes.command.Command;
import classes.functionDeclaration.FunctionDeclaration;

public class Program extends AST {
	
	ArrayList<Command> commands;
	ArrayList<FunctionDeclaration> functions;
	
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
	public Object visit(Visitor visitor, Object obj) {
		// TODO Auto-generated method stub
		return null;
	}

}
