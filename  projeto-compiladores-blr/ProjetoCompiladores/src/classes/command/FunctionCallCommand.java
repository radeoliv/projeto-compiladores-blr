package classes.command;

import java.util.ArrayList;

import checker.Visitor;
import classes.expression.Expression;
import classes.terminal.Identifier;


public class FunctionCallCommand extends Command {

	Identifier identifier;
	ArrayList<Expression> arguments = new ArrayList<Expression>();
	
	public FunctionCallCommand(Identifier identifier, ArrayList<Expression> arguments){
		this.identifier = identifier;
		this.arguments = arguments;
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
