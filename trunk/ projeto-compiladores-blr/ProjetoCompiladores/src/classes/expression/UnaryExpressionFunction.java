package classes.expression;

import java.util.ArrayList;

import checker.SemanticException;
import checker.Visitor;
import classes.terminal.Identifier;

public class UnaryExpressionFunction extends UnaryExpression{
	private Identifier identifier;
	private ArrayList<Expression> arguments;
	
	public UnaryExpressionFunction(Identifier identifier, ArrayList<Expression> arguments){
		this.identifier = identifier;
		this.arguments = arguments;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitUnaryExpressionFunction(this, obj);
	}

	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}
}
