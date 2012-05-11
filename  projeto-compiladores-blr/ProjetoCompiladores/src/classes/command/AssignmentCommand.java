package classes.command;

import checker.SemanticException;
import checker.Visitor;
import classes.expression.Expression;
import classes.terminal.Identifier;

public class AssignmentCommand extends Command {
	
	private Identifier id;
	private Expression expression;
	private String type;
	
	public AssignmentCommand(Identifier id, Expression e){
		this.id = id;
		this.expression = expression;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitAssignmentCommand(this, obj);
	}
	
	// GET SET
	public Identifier getId() {
		return id;
	}

	public void setId(Identifier id) {
		this.id = id;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
