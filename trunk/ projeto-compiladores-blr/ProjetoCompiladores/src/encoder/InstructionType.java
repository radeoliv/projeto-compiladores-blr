package encoder;

public interface InstructionType {
	public static final int EXTERN = 0;
	public static final int SECTION = 1;
	public static final int RET = 2;
	public static final int PUSH = 3;
	public static final int VARIAVEL_GLOBAL = 4;
	public static final int FUNCAO_LABEL = 6;
	
	public static final int POP = 7;
	public static final int MOV = 8;
	public static final int CMP = 9;
	
	public static final int ADD = 10;
	public static final int SUB = 11;
	public static final int MULT = 12;
	public static final int DIV = 13;
	
	public static final int INT_FORMAT = 14;
	public static final int GLOBAL = 16;
	public static final int CALL_FUNCTION = 23;
	
	public static final String JUMP = "jmp";
	public static final String JNE = "jne"; //Jump if Not Equal
	public static final String JE = "je"; 	//Jump if Equal
	public static final String JZ = "jz";	//Jump if Zero
	public static final String JGE = "jge";	//Jump if Greater or Equal
	public static final String JLE = "jle";	//Jump if Less or Equal
	public static final String JG = "jg";	//Jump if Greater
	public static final String JL = "jl";	//Jump if Less
	public static final String NAME_RET = "ret";
	public static final String CALL = "call";
	
	public static final String PRINTF = "_printf";
	public static final String DATA = ".data";
	public static final String TEXT = ".text";
	public static final String NAME_EXTERN = "extern";
	public static final String NAME_SECTION = "SECTION";
	public static final String NAME_PUSH = "push";
	public static final String WORD_POP = "pop";
	public static final String FLD = "fld";
	public static final String NAME_MOV = "mov";
	public static final String WORD_SUB = "sub";
	public static final String WORD_ADD = "add";
	public static final String WORD_MULT = "imul";
	public static final String WORD_DIV = "idiv";
	public static final String WINMAIN = "_WinMain@16";
	public static final String TIPO_INT = "dd";
	public static final String CONSTANTE = "const";
	public static final String DWORD = "dword";
	public static final String QWORD = "qword";
	public static final String ST1 = "st1";
	
	public static final String EBP = "ebp";
	public static final String ESP = "esp";
	public static final String EAX = "eax";
	public static final String EBX = "ebx";

}
