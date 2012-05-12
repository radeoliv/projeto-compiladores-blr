package classes.functionDeclaration;

import classes.terminal.Identifier;
import checker.SemanticException;
import checker.Visitor;
import util.AST.AST;

public class Parameter extends AST{
	
	private Identifier identifier;
	private int identifierType;
	
	public Parameter(Identifier identifier, int identifierType){
		this.identifier = identifier;
		this.identifierType = identifierType;
	}
	
	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitParameter(this, obj);
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

	public int getIdentifierType() {
		return identifierType;
	}

	public void setIdentifierType(int identifierType) {
		this.identifierType = identifierType;
	}

}
