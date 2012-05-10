#-----------------------------------------------------------
# Teste 02
# Right
#-----------------------------------------------------------

	a   =    2 #Teste de tabulacao, espaco e atribuicao

# Testes de atribuicaoo com diversas formas de expressao
b = a+1
c = a+b
d = (3*a)
e = d/a
f = e
g = 1.5
h = soma (c,d)


# Teste de funcao sem parametro e sem retorno
function mais_um () print (1) end

# Teste de funcao com parametro e sem retorno
function mais_dois (a) print (a+2) end

# Chamada de funcao sem parametro e sem retorno
call mais_um ()

# Chamada de funcaoo com parametro e sem retorno
call mais_dois (3)


# Teste de funcao sem parametro e com retorno
function nada () x = 2 return x end

# Teste de funcao com parametros e com retorno
# Teste de: while, if, else, break e return dentro da funcao
function soma (a, b)
	while (b>0) do
		if (b==1) then
			soma = (a+b)
			break
		else soma = b
		end		
		b=b-1
	end
	return soma
end

# Chamada de funcaoo sem parametro e com retorno
i = nada ()

# Chamada de funcaoo com parametro e com retorno
j = soma (1, 2)


# Teste de: while, if, else, break e return
while (a>0) do
	if (a~=1) then
		print (a)
	else print (1000) print (g+a)
	end
	a=a-1
end


