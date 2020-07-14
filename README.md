![](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/logo/logo.jpg?raw=true)

<h1 align='center'> Execution flow</h1>

<hr />

## Introdução
Computa test path de métodos testados em testes JUnit.

## Documentação
Veja a documentação na [wiki](https://github.com/williamniemiec/ExecutionFlow/wiki).

## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior
- JUnit 4 ou JUnit 5

## Problemas
Caso você encontre algum problema na aplicação, certifique-se de verificar a solução de problemas na wiki. Caso seu problema não seja resolvido, verifique se já existe uma [issue](https://github.com/williamniemiec/ExecutionFlow/issues) com seu problema. Se não tiver, [crie uma issue](https://github.com/williamniemiec/ExecutionFlow/issues/new/choose) descrevendo detalhadamente o problema ocorrido.

## Changelog
Detalhes sobre cada versão estão documentadas na [seção releases](https://github.com/williamniemiec/ExecutionFlow/releases).

## Contribua
Veja a documentação sobre como é possível contribuir com o projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/CONTRIBUTING.md).

## Desenvolvedores
Veja a documentação específica para programadores que buscam alterar o código fonte [aqui](https://github.com/williamniemiec/ExecutionFlow/DEVELOPERS.md).

## Organização do projeto
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/uml/UML.png?raw=true)

Veja a descrição de cada classe do projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Classes,-Interfaces-e-Aspectos).

## Exemplo de saída - [SimpleTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/SimpleTest.java)
<code>
	
	[INFO] Processing source file of method testClasses.TestClass.factorial(int)...
	[INFO] Processing source file of test method SimpleTest.testFactorial()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.factorial(int)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	SimpleTest.testFactorial()
	testClasses.TestClass.factorial(int)
	[89, 91, 92, 91, 92, 91, 92, 91, 92, 91, 95]
</code>

Veja mais exemplos de test paths gerados [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

##  Arquivos
### /
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|dist |`Diretório`|Versões lançadas da aplicação|
|docs |`Diretório`|Informações relativos a documentação|
|examples   |`Diretório`|	Exemplos de testes JUnit para ver o funcionamento da aplicação   |
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|src     |`Diretório`| Arquivos fonte|
|test|`Diretório`|Testes dos arquivos fonte|
|.classpath|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.project|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|build.ajproperties|`Arquivo`|Arquivo gerado por IDE (Eclipse)|

Veja a descrição de todos os arquivos [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Arquivos).