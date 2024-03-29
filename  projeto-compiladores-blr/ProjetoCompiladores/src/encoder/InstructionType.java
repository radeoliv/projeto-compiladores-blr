package encoder;

public interface InstructionType {
	public static final int EXTERN = 0;
	public static final int SECTION = 1;
	public static final int GLOBAL = 2;
	public static final int LABEL = 3;
	public static final int CALL_FUNCTION = 4;
	
	public static final int PUSH = 5;
	public static final int POP = 6;
	public static final int MOV = 7;
	public static final int CMP = 8;
	public static final int RET = 9;
	public static final int JMP = 10;
	public static final int CONDITIONAL_JUMP = 11;
		
	public static final int ADD = 12;
	public static final int SUB = 13;
	public static final int MULT = 14;
	public static final int DIV = 15;
	public static final int INT_FORMAT = 16;
	public static final int BLOCKS_SEPARATOR = 17;	
	
	public static final String PRINTF = "_printf";
	public static final String DATA = ".data";
	public static final String TEXT = ".text";
	public static final String WINMAIN = "_WinMain@16";
		
	public static final int PUSH_REG_ADDRESS = 99;	//push eax
	public static final int PUSH_REG_VALUE = 98;	//push [eax]
	public static final int PUSH_CONSTANT = 97;		//push dword 1
	public static final int PUSH_REG_OFFSET = 96;	//push dword [eax+8]
	
	public static final int POP_REG = 95;			//pop eax
	public static final int POP_REG_OFFSET = 94;	//pop dword [ebp-8]
	
	public static final String JNE = "jne"; //Jump if Not Equal
	public static final String JE = "je"; 	//Jump if Equal
	public static final String JZ = "jz";	//Jump if Zero
	public static final String JGE = "jge";	//Jump if Greater or Equal
	public static final String JLE = "jle";	//Jump if Less or Equal
	public static final String JG = "jg";	//Jump if Greater
	public static final String JL = "jl";	//Jump if Less
	
	public static final String EBP = "ebp";
	public static final String ESP = "esp";
	public static final String EAX = "eax";
	public static final String EBX = "ebx";
	public static final String ECX = "ecx";
	public static final String EDX = "edx";
	
	public static final String BLOCK_SEPARATOR_STRING = ";;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;\n";
}
