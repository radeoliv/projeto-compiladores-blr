package classes.expression;

import util.Visitor;
import classes.terminal.Number;

public class UnaryExpressionNumber extends UnaryExpression {

	private Number number;
	
	public UnaryExpressionNumber(Number number){
		this.number = number;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) {
		return visitor.visitUnaryExpressionNumber(this, obj);
	}

	public Number getNumber() {
		return number;
	}

	public void setNumber(Number number) {
		this.number = number;
	}
	
}
