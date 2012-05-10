package classes.expression;

import util.AST.AST;

public abstract class Expression extends AST {
	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
