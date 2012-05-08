package classes.command;

import java.util.ArrayList;

import scanner.Token;

import checker.Visitor;
import classes.expression.Expression;
import classes.terminal.Identifier;


public class FunctionCall extends Command {

	ArrayList<Expression> arguments = new ArrayList<Expression>();
	Identifier identifier;
	
	public FunctionCall(Identifier identifier, ArrayList<Expression> arguments){
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
