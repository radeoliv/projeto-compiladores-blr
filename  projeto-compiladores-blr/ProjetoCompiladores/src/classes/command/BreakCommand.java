package classes.command;

import util.Visitor;
import checker.SemanticException;

public class BreakCommand extends Command {

	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitBreakCommand(this, obj);
	}

}
