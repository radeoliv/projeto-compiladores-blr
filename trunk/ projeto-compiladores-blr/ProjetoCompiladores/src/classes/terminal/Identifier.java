package classes.terminal;

import checker.SemanticException;
import checker.Visitor;
import scanner.Token;
import util.AST.AST;

public class Identifier extends Terminal {

	private String spelling;
	private AST declaration;
	
	public Identifier(Token token){
		super.token = token;
		spelling = token.getSpelling();
	}

	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitIdentifier(this, obj);
	}
	
	// GET SET
	public String getSpelling() {
		return spelling;
	}

	public void setSpelling(String spelling) {
		this.spelling = spelling;
	}

	public AST getDeclaration() {
		return declaration;
	}

	public void setDeclaration(AST declaration) {
		this.declaration = declaration;
	}
}
