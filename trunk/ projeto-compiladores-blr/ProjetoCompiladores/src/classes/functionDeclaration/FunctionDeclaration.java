package classes.functionDeclaration;

import java.util.ArrayList;
import util.AST.AST;
import checker.SemanticException;
import checker.Visitor;
import classes.command.Command;
import classes.expression.Expression;
import classes.terminal.Identifier;

public class FunctionDeclaration extends AST {

	private Identifier identifier;
	private ArrayList<Identifier> parameters;
	private ArrayList<Command> commands;
	private Expression e;
	
	public FunctionDeclaration(Identifier identifier, ArrayList<Identifier> parameters, ArrayList<Command> commands, Expression e){
		this.identifier = identifier;
		this.parameters = parameters;
		this.commands = commands;
		this.e = e;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitFunctionDeclaration(this, obj);
	}
	
	// GET SET
	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}
	
	public ArrayList<Identifier> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Identifier> parameters) {
		this.parameters = parameters;
	}

	public ArrayList<Command> getCommands() {
		return commands;
	}

	public void setCommands(ArrayList<Command> commands) {
		this.commands = commands;
	}
}
