package classes.expression;

import util.Visitor;
import checker.SemanticException;
import classes.terminal.Operator;

public class BinaryExpression extends Expression {

	private Expression leftExpression;
	private Operator operator;
	private Expression rightExpression;
	
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
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitBinaryExpression(this, obj);
	}

	public Expression getLeftExpression() {
		return leftExpression;
	}

	public void setLeftExpression(Expression leftExpression) {
		this.leftExpression = leftExpression;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Expression getRightExpression() {
		return rightExpression;
	}

	public void setRightExpression(Expression rightExpression) {
		this.rightExpression = rightExpression;
	}
}
