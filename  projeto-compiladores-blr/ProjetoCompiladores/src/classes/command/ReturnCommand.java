package classes.command;

import checker.Visitor;
import classes.expression.Expression;

public class ReturnCommand extends Command {

	Expression e;
	
	public ReturnCommand(Expression e){
		this.e = e;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) {
		return visitor.visitReturnCommand(this, obj);
	}

}
