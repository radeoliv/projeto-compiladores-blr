package classes.command;

import checker.Visitor;
import classes.expression.Expression;
import classes.terminal.Identifier;

public class AssignmentCommand extends Command {
	
	Identifier id;
	Expression e;
	
	public AssignmentCommand(Identifier id, Expression e){
		this.id = id;
		this.e = e;
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
