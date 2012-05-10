package classes.expression;

import checker.Visitor;
import classes.terminal.Number;

public class UnaryExpressionNumber extends UnaryExpression {

	private Number number;
	private String type;
	
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
