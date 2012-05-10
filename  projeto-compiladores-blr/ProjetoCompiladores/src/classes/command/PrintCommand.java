package classes.command;

import checker.SemanticException;
import checker.Visitor;
import classes.expression.Expression;

public class PrintCommand extends Command {

	private Expression e;
	
	public PrintCommand(Expression e){
		this.e = e;
	}
	
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitPrintCommand(this, obj);
	}


	public Expression getE() {
		return e;
	}

	public void setE(Expression e) {
		this.e = e;
	}	

}
