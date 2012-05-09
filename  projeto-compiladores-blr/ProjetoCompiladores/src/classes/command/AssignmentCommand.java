package classes.command;

import checker.Visitor;
import classes.expression.Expression;
import classes.terminal.Identifier;

public class AssignmentCommand extends Command {
	
	private Identifier id;
	private Expression e;
	
	
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
	
	// GET SET
	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public Expression getE() {
		return e;
	}

	public void setE(Expression e) {
		this.e = e;
	}

	public AssignmentCommand(Identifier id, Expression e){
		this.id = id;
		this.e = e;
	}
}
