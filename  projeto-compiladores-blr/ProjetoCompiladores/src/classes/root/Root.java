package classes.root;

import java.util.ArrayList;

import checker.SemanticException;
import checker.Visitor;
import classes.program.Program;
import util.AST.AST;

public class Root extends AST{
	
	private ArrayList<Program> programs;
	
	public Root(ArrayList<Program> programs){
		this.programs = programs;
	}
	
	@Override
	public String toString(int level) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Visitor visitor, Object obj) throws SemanticException {
		return visitor.visitRoot(this, obj);
	}

	public ArrayList<Program> getPrograms() {
		return programs;
	}

	public void setCommands(ArrayList<Program> programs) {
		this.programs = programs;
	}
}
