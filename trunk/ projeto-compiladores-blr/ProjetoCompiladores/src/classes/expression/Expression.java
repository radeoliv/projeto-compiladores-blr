package classes.expression;

import util.AST.AST;

public abstract class Expression extends AST {
	
	private String type;
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
