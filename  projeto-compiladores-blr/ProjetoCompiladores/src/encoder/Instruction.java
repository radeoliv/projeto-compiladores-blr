package encoder;

public class Instruction {
	// Mudar esse nomes seria cool
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
	
	public String toString(){
		switch(this.opCode){
		
			case InstructionType.EXTERN:
				return ("extern "+this.op1);
				
			case InstructionType.SECTION:
				return ("section "+this.op1);
				
			case InstructionType.GLOBAL:
				return ("global "+ this.op1);
				
			case InstructionType.FUNCTION_LABEL:
				if(this.op1.equals(InstructionType.WINMAIN)){
					return(InstructionType.WINMAIN+":");
				}else{
					return ("_"+this.op1+":");
				}
				
			case InstructionType.CALL_FUNCTION:
				return ("call "+this.op1);
				
			case InstructionType.PUSH:
				return iPush();
				
			case InstructionType.POP:
				return iPop();
				
			case InstructionType.MOV:
				return ("mov "+this.op1+", "+this.op2);
				
			// Faltando o CMP, né?
			case InstructionType.CMP:
				return ("cmp "+this.op1+", "+this.op2);
				
			case InstructionType.RET:
				return ("ret");
				
			case InstructionType.ADD:
				return ("add "+this.op1+", "+this.op2);
			
			case InstructionType.SUB:
				return ("sub "+this.op1+", "+this.op2);
				
			case InstructionType.MULT:
				return ("imul "+this.op1+", "+this.op2);
				
			case InstructionType.DIV:
				return ("idiv "+this.op1+", "+this.op2);
				
			// E se for um jump?
				
			default:
				return (opCode+" "+op1+" "+op2+" "+op3);
		}
	}
	

	private String iPush(){
		//op1, caso exista, será usado sempre para passar o tipo int.
		//op2 é o registrador/valor que será inserido na pilha
		//op3, caso exista, é o deslocamento associado a um registrador
		
		if(this.op1 != null && this.op1.equals("int")){
			if(this.op3 == null || this.op3.equals("")){
				//Usado para constantes 
				//Ex: push dword 0
				return ("push dword "+this.op2);
				//return ("push "+InstructionType.DWORD+" "+this.op2);
			}else{
				//Usado para registradores + deslocamentos
				//Ex: push dword [ebp+4]
				return ("push dword ["+this.op2+"+"+this.op3+"]");
				//return ("push "+InstructionType.DWORD+" ["+this.op2+this.op3+"]");
			}
		} else {
			//Usado para registradores
			//Ex: push ebp
			return ("push "+this.op2);
		}
	}
	
	private String iPop(){
		//op1, caso exista, será usado sempre para passar o tipo int.
		//op2 é o registrador/valor que será inserido na pilha
		//op3, caso exista, é o deslocamento associado a um registrador	
		
		if(this.op1 != null && this.op1.equals("int"))
			//Usado para registradores + deslocamentos
			//Ex: pop dword [ebp+4]
			return ("pop dword ["+this.op2+"+"+this.op3+"]");
			//return (InstructionType.NAME_PUSH+" "+InstructionType.DWORD+" ["+this.op2+this.op3+"]");
		else
			//Usado para registradores
			//Ex: pop ebp
			return ("pop "+this.op2);
			//return (InstructionType.NAME_PUSH+" "+this.op2);
		
		
				
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
}