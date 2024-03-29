package classes.procedureCall;

import java.util.ArrayList;

import util.Visitor;

import checker.SemanticException;
import classes.command.Command;
import classes.expression.Expression;
import classes.terminal.Identifier;


public class ProcedureCall extends Command {

	private Identifier identifier;
	private ArrayList<Expression> arguments;
	
	public ProcedureCall(Identifier identifier){
		this(identifier, new ArrayList<Expression>());
	}
	
	public ProcedureCall(Identifier identifier, ArrayList<Expression> arguments){
		this.identifier = identifier;
		this.arguments = arguments;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitProcedureCall(this, obj);
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Identifier identifier) {
		this.identifier = identifier;
	}

	public ArrayList<Expression> getArguments() {
		return arguments;
	}

	public void setArguments(ArrayList<Expression> arguments) {
		this.arguments = arguments;
	}
}
