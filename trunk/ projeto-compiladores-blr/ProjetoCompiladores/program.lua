function soma (a, b)
	soma1 = 1
	while (b>0) do
		if (b==1) then
			soma1 = (a+b)
			break
		else soma1 = b
		end		
		b=b-1
	end
	return soma1
end

# Chamada de funcao sem parametro e com retorno
i = nada ()

# Chamada de funcao com parametro e com retorno
j = soma (1, 2)

a = 1
g = 3
# Teste de: while, if, else, break e return
while (a>0) do
	if (a~=1) then
		print (a)
	else print (1000) print (g+a)
	end
	a=a-1
end



