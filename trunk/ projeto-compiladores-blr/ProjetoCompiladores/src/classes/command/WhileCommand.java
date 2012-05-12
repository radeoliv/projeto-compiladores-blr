package classes.command;

import java.util.ArrayList;

import checker.SemanticException;
import checker.Visitor;
import classes.expression.Expression;

public class WhileCommand extends Command {

	private Expression expression;
	private ArrayList<Command> commands;
	
	public WhileCommand(Expression expression, ArrayList<Command> commands){
		this.expression = expression;
		this.commands = commands;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitWhileCommand(this, obj);
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<Command> commands) {
		this.commands = commands;
	}
	

}
