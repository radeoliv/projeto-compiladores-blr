package classes.command;

import checker.Visitor;
import classes.expression.Expression;

public class PrintCommand extends Command {

	Expression e;
	
	public PrintCommand(Expression e){
		this.e = e;
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
