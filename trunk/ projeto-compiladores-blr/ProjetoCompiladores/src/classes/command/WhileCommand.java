package classes.command;

import java.util.ArrayList;

import checker.Visitor;
import classes.expression.Expression;

public class WhileCommand extends Command {

	Expression e;
	ArrayList<Command> commands;
	
	public WhileCommand(Expression e, ArrayList<Command> commands){
		this.e = e;
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
