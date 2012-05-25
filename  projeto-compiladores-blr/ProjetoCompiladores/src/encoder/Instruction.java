package encoder;

public class Instruction {
	private int operationCode; 
	private String op1; 
	private String op2; 
	private String op3;
	
	public Instruction(int opCode, String op1, String op2, String op3){
		this.operationCode = opCode;
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
	}
	
	public String toString(){
		switch(this.operationCode){
		
			case InstructionType.EXTERN:
				return ("extern "+this.op1);
				
			case InstructionType.SECTION:
				return ("section "+this.op1);
				
			case InstructionType.GLOBAL:
				return ("global "+ this.op1);
				
			case InstructionType.LABEL:
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
				
			case InstructionType.CMP:
				return ("cmp "+this.op1+", "+this.op2);
				
			case InstructionType.RET:
				return ("ret");
				
			case InstructionType.JMP:
				return ("jmp "+this.op1);
				
			case InstructionType.CONDITIONAL_JUMP:
				return (this.op1+" "+this.op2);
				
			case InstructionType.ADD:
				return ("add "+this.op1+", "+this.op2);
			
			case InstructionType.SUB:
				return ("sub "+this.op1+", "+this.op2);
				
			case InstructionType.MULT:
				return ("imul "+this.op1+", "+this.op2);
				
			case InstructionType.DIV:
				return ("idiv "+this.op1+", "+this.op2);				
			
			case InstructionType.INT_FORMAT:
				return ("intFormat: db \"%d\", 10, 0");
				
			case InstructionType.BLOCKS_SEPARATOR:
				return (InstructionType.BLOCK_SEPARATOR_STRING);
				
			default:
				return (operationCode+" "+op1+" "+op2+" "+op3);
		}
	}
	

	private String iPush(){
		// TODO Tava retornando null no arquivo
		// Adicionei os "" nas comparações, se não tivesse... retornava errado
		if(op1.equals(InstructionType.PUSH_REG_OFFSET+"")){
			return ("push dword ["+op2+op3+"]");
		} else if(op1.equals(InstructionType.PUSH_CONSTANT+"")){
			return ("push dword "+op2);
		} else if(op1.equals(InstructionType.PUSH_REG_ADDRESS+"")){
			return ("push "+op2);
		/*} else if(op1.equals(InstructionType.PUSH_REG_VALUE+"")){
			return ("push ["+op2+"]");
		}*/
		}else{
			return ("push ["+op2+"]");
		}
		//return null;

	}
	
	private String iPop(){
		// TODO Tava retornando null no arquivo
		// Adicionei os "" nas comparações, se não tivesse... retornava errado
		if(this.op1.equals(InstructionType.POP_REG+"")){
			return ("pop "+op2);
		/*} else if(this.op1.equals(InstructionType.POP_REG_OFFSET+"")){
			return ("pop dword ["+op2+op3+"]");
		}*/
		}else{
			return ("pop dword ["+op2+op3+"]");
		}
		
		//return null;
	}
	
	
	public int getOpCode() {
		return operationCode;
	}

	public void setOpCode(int opCode) {
		this.operationCode = opCode;
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