package classes.expression;

import checker.Visitor;
import classes.terminal.Number;

public class UnaryExpressionNumber extends UnaryExpression {

	Number number;
	String type;
	
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
		// TODO Auto-generated method stub
		return null;
	}
	

}
