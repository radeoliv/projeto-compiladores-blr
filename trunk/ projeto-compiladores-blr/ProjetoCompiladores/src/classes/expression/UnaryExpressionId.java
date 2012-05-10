package classes.expression;

import checker.SemanticException;
import checker.Visitor;
import classes.terminal.Identifier;

public class UnaryExpressionId extends UnaryExpression {

	private Identifier identifier;
	private String type;
	
	public UnaryExpressionId(Identifier identifier){
		this.identifier = identifier;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitUnaryExpressionId(this, obj);
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
