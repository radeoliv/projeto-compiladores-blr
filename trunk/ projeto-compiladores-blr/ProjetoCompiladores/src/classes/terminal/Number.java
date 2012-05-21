package classes.terminal;

import scanner.Token;
import util.Visitor;

public class Number extends Terminal {
	
	public Number(Token token){
		super.token = token;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) {
		return visitor.visitNumber(this, obj);
	}
	
	public int getKind(){
		return super.token.getKind();
	}

}
