package classes.command;

import checker.SemanticException;
import checker.Visitor;
import classes.expression.Expression;

public class ReturnCommand extends Command {

	private Expression expression;
	
	public ReturnCommand(Expression expression){
		this.expression = expression;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitReturnCommand(this, obj);
	}

	
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

}
