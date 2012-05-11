package classes.command;

import java.util.ArrayList;

import checker.SemanticException;
import checker.Visitor;
import classes.expression.Expression;

public class IfCommand extends Command {

	private Expression expression;
	private ArrayList<Command> ifCommands;
	private ArrayList<Command> elseCommands;
	
	public IfCommand(Expression expression, ArrayList<Command> ifCommands, ArrayList<Command> elseCommands){
		this.expression = expression;
		this.ifCommands = ifCommands;
		this.elseCommands = elseCommands;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitIfCommand(this, obj);
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public ArrayList<Command> getIfCommands() {
		return ifCommands;
	}

	public void setIfCommands(ArrayList<Command> ifCommands) {
		this.ifCommands = ifCommands;
	}

	public ArrayList<Command> getElseCommands() {
		return elseCommands;
	}

	public void setElseCommands(ArrayList<Command> elseCommands) {
		this.elseCommands = elseCommands;
	}	

}
