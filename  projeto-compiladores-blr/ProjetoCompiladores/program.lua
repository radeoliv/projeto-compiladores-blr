function soma (int a, int b)
	soma1 = 1
	c = 2
	while (soma1>0) do
		if (soma1==1) then
			soma1 = (2+3)
			break
		else soma1 = c
		end		
		c=c-1
	end
	return soma1
end

# Chamada de funcao com parametro e com retorno
j = soma (1,2)
x = 4.5
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



