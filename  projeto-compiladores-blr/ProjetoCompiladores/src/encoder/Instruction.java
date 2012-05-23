package encoder;

public class Instruction {
	private int opCode; 
	private String op1; 
	private String op2; 
	private String op3;
	
	public Instruction(int opCode, String op1, String op2, String op3){
		this.opCode = opCode;
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
	}
	
	public int getOpCode() {
		return opCode;
	}

	public void setOpCode(int opCode) {
		this.opCode = opCode;
	}
	
	public String getOp1() {
		return op1;
	}

	public void setOp1(String op1) {
		this.op1 = op1;
	}

	public String getOp2() {
		return op2;
	}

	public void setOp2(String op2) {
		this.op2 = op2;
	}

	public String getOp3() {
		return op3;
	}

	public void setOp3(String op3) {
		this.op3 = op3;
	}

	public String toString(){
		switch(this.opCode){
		
			case InstructionType.EXTERN:
				return (InstructionType.NAME_EXTERN+" "+this.op1);
				
			case InstructionType.SECTION:
				return (InstructionType.NAME_SECTION+" "+this.op1);
				
			case InstructionType.RET:
				return (InstructionType.NAME_RET);
			
			//TODO: Talvez seja removido. Não temos variáveis globais
			case InstructionType.VARIAVEL_GLOBAL:
				return (this.op1+": "+ InstructionType.TIPO_INT+ " 0");
				
			case InstructionType.INT_FORMAT:
				return ("intFormat: db "+ "\"%d\""+" 10, 0");
			
			case InstructionType.GLOBAL:
				return ("global "+ this.op1);
			
			case InstructionType.FUNCTION_LABEL:
				if(!this.op1.equals(InstructionType.WINMAIN))
					return ("_"+this.op1+":");
				else 
					return(InstructionType.WINMAIN+":");
			
			case InstructionType.MOV:
				return (InstructionType.NAME_MOV+" "+this.op1+", "+this.op2);
				
			case InstructionType.PUSH:
				return putPush();
				
			case InstructionType.POP:
				return (putPop());
			
			case InstructionType.ADD:
				return (InstructionType.WORD_ADD+" "+this.op1+", "+this.op2);
			
			case InstructionType.SUB:
				return (InstructionType.WORD_SUB+" "+this.op1+", "+this.op2);
				
			case InstructionType.MULT:
				return (InstructionType.WORD_MULT+" "+this.op1+", "+this.op2);
				
			case InstructionType.DIV:
				return (InstructionType.WORD_DIV+" "+this.op1+", "+this.op2);
				
			case InstructionType.CALL_FUNCTION:
				return (InstructionType.CALL+" _"+this.op1);
				
			default:
				return (opCode+" "+op1+" "+op2+" "+op3);
		}
		
	}
	
	private String putPush(){
		//op1, caso exista, será usado sempre para passar o tipo int.
		//op2 é o registrador/valor que será inserido na pilha
		//op3, caso exista, é o deslocamento associado a um registrador
		
		if(this.op1 != null && this.op1.equals("int")){
			if(this.op3 == null || this.op3.equals(""))
			{
				//Usado para constantes 
				//Ex: push dword 0
				return (InstructionType.NAME_PUSH+" "+InstructionType.DWORD+" "+this.op2);
			} 
			else
			{
				//Usado para registradores + deslocamentos
				//Ex: push dword [ebp+4]
				return (InstructionType.NAME_PUSH+" "+InstructionType.DWORD+" ["+this.op2+this.op3+"]");
			}
		} else {
			//Usado para registradores
			//Ex: push ebp
			return (InstructionType.NAME_PUSH+" "+this.op2);
		}
	}
	
	private String putPop(){
		//op1, caso exista, será usado sempre para passar o tipo int.
		//op2 é o registrador/valor que será inserido na pilha
		//op3, caso exista, é o deslocamento associado a um registrador
		
		if(this.op1 != null && this.op1.equals("int"))
			//Usado para registradores + deslocamentos
			//Ex: pop dword [ebp+4]
			return (InstructionType.NAME_PUSH+" "+InstructionType.DWORD+" ["+this.op2+this.op3+"]");
		else
			//Usado para registradores
			//Ex: push ebp
			return (InstructionType.NAME_PUSH+" "+this.op2);
	}
}