package classes.program;

import util.AST.AST;
import checker.SemanticException;
import checker.Visitor;
import classes.command.Command;
import classes.functionDeclaration.FunctionDeclaration;

public class Program extends AST {
	
	private Command command;
	private FunctionDeclaration function;
	
	public Program(Command command){
		this.command = command;
	}
	
	public Program(FunctionDeclaration function){
		this.function = function;
	}

	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitProgram(this, obj);
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public FunctionDeclaration getFunction() {
		return function;
	}

	public void setFunction(FunctionDeclaration function) {
		this.function = function;
	}
}
