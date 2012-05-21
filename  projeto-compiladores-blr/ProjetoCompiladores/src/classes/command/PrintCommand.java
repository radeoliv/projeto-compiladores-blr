package classes.command;

import util.Visitor;
import checker.SemanticException;
import classes.expression.Expression;

public class PrintCommand extends Command {

	private Expression expression;
	
	public PrintCommand(Expression expression){
		this.expression = expression;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitPrintCommand(this, obj);
	}

	public Expression getExpression() {
		return expression;
	}

	public void setE(Expression expression) {
		this.expression = expression;
	}	

}
