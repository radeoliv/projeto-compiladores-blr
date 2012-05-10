package classes.command;

import java.util.ArrayList;

import checker.Visitor;
import classes.expression.Expression;

public class IfCommand extends Command {

	Expression condition;
	ArrayList<Command> ifCommands;
	ArrayList<Command> elseCommands;
	
	public IfCommand(Expression condition, ArrayList<Command> ifCommands, ArrayList<Command> elseCommands){
		this.condition = condition;
		this.ifCommands = ifCommands;
		this.elseCommands = elseCommands;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) {
		return visitor.visitIfCommand(this, obj);
	}
	

}
