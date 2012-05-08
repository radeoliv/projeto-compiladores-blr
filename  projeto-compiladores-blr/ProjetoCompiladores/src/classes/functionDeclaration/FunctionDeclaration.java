package classes.functionDeclaration;

import java.util.ArrayList;
import util.AST.AST;
import checker.Visitor;
import classes.command.Command;
import classes.terminal.Identifier;

public class FunctionDeclaration extends AST {

	Identifier identifier;
	ArrayList<Identifier> parameters;
	ArrayList<Command> commands;
	
	public FunctionDeclaration(Identifier identifier, ArrayList<Identifier> parameters, ArrayList<Command> commands){
		this.identifier = identifier;
		this.parameters = parameters;
		this.commands = commands;
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
