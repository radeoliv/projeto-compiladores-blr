;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
extern _printf
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
section .data
intFormat: db "%d", 10, 0
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
section .text
global _WinMain@16
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
_funcaoTeste:
push ebp
mov ebp, esp
_funcaoTeste_if_1_begin:
push ebp
mov ebp, esp
je funcaoTeste_else_1_begin
_funcaoTeste_if_2_begin:
push ebp
mov ebp, esp
jne funcaoTeste_else_2_begin
_funcaoTeste_if_3_begin:
push ebp
mov ebp, esp
jne funcaoTeste_else_3_begin
mov esp, ebp
pop ebp
jmp funcaoTeste_else_3_end
_funcaoTeste_if_3_end:
_funcaoTeste_else_3_begin:
push ebp
mov ebp, esp
mov esp, ebp
pop ebp
_funcaoTeste_else_3_end:
mov esp, ebp
pop ebp
jmp funcaoTeste_else_2_end
_funcaoTeste_if_2_end:
_funcaoTeste_else_2_begin:
push ebp
mov ebp, esp
mov esp, ebp
pop ebp
_funcaoTeste_else_2_end:
mov esp, ebp
pop ebp
jmp funcaoTeste_else_1_end
_funcaoTeste_if_1_end:
_funcaoTeste_else_1_begin:
push ebp
mov ebp, esp
_funcaoTeste_if_4_begin:
push ebp
mov ebp, esp
jle funcaoTeste_else_4_begin
mov esp, ebp
pop ebp
jmp funcaoTeste_else_4_end
_funcaoTeste_if_4_end:
_funcaoTeste_else_4_begin:
push ebp
mov ebp, esp
mov esp, ebp
pop ebp
_funcaoTeste_else_4_end:
mov esp, ebp
pop ebp
_funcaoTeste_else_1_end:
mov esp, ebp
pop ebp
ret
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
_WinMain@16:
push ebp
mov ebp, esp
call _funcaoTeste
mov esp, ebp
pop ebp
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
