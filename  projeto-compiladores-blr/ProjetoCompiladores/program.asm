;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

extern _printf
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

section .data
intFormat: db "%d", 10, 0
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

section .text
global _WinMain@16
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

_WinMain@16:

push ebp
mov ebp, esp
sub esp, 4
push dword 3
pop eax
push eax
pop dword [ebp-4]
_main_while_1_begin:

push ebp
mov ebp, esp
mov ecx, ebp
push dword [ebp]
pop ebp
mov edx, ebp
mov ebp, ecx
push dword [edx-4]
pop eax
push eax
push dword 0
pop eax
mov ebx, eax
pop eax
cmp eax, ebx
jle _main_while_1_end
mov ecx, ebp
push dword [ebp]
pop ebp
mov edx, ebp
mov ebp, ecx
push dword [edx-4]
pop eax
push eax
push dword intFormat
call _printf
add esp, 8
mov ecx, ebp
push dword [ebp]
pop ebp
mov edx, ebp
mov ebp, ecx
mov ecx, ebp
push dword [ebp]
pop ebp
mov edx, ebp
mov ebp, ecx
push dword [edx-4]
pop eax
push eax
push dword 1
pop eax
mov ebx, eax
pop eax
sub eax, ebx
push eax
pop dword [edx-4]
jmp _main_while_1_end
jmp _main_while_1_begin
mov esp, ebp
pop ebp
_main_while_1_end:

mov esp, ebp
pop ebp
mov eax, 0
ret
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

