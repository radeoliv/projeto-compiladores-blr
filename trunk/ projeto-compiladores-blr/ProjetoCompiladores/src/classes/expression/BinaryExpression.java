package classes.expression;

import checker.Visitor;
import classes.terminal.Operator;

public class BinaryExpression extends Expression {

	Expression leftExpression;
	Operator operator;
	Expression rightExpression;
	String type;
	
	public BinaryExpression(Expression leftExpression, Operator operator, Expression rightExpression){
		this.leftExpression = leftExpression;
		this.operator = operator;
		this.rightExpression = rightExpression;
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
