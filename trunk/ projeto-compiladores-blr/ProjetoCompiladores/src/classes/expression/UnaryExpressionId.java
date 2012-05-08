package classes.expression;

import checker.Visitor;
import classes.terminal.Identifier;

public class UnaryExpressionId extends UnaryExpression {

	Identifier identifier;
	String type;
	
	public UnaryExpressionId(Identifier identifier){
		this.identifier = identifier;
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
