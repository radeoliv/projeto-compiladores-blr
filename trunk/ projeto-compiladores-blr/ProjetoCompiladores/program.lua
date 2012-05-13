#-----------------------------------------------------------
# Teste 02
# Right
#-----------------------------------------------------------

#Teste de tabulacao, espaco e atribuicao
	a   =    2

# Testes de atribuicaoo com diversas formas de expressao
b = a+1
c = a+b
d = (3*a)
e = d/a
f = e
g = 1.5
h = 1

# Teste de funcao sem parametro e sem retorno
function maisUmFunction() 
	print(1) 
end

# Teste de funcao com parametro e sem retorno
function maisDoisFunction(int a1) 
	print (a1 + 2) 
end

# Chamada de funcao sem parametro e sem retorno
call maisUmFunction()

# Chamada de funcao com parametro e sem retorno
call maisDoisFunction(3)

# Teste de funcao com parametros e com retorno
# Teste de: while, if, else, break e return dentro da funcao
function soma (int a1, int b1)
	soma1 = 0
	while (b1 > 0) do
		if (b1 == 1) then
			soma1 = (a1 + b1)
			break
		else 
			soma1 = b1
		end		
	end
	return soma1
end


# Teste de funcao sem parametro e com retorno
function nada () 
	x = 2 
	return x 
end

# Chamada de funcao sem parametro e com retorno
i = nada()

# Chamada de funcao com parametro e com retorno
j = soma(1, 2)

# Teste de: while, if, else, break e return
while (a > 0) do
	if (a ~= 1) then
		print (a)
	else 
		print (1000) 
		print (h + a)
	end
end