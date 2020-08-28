![](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/logo/logo.jpg?raw=true)

<h1 align='center'> Execution Flow</h1>

<hr />

## Introdução
Computa test path de métodos e construtores testados em testes JUnit. [Veja como usar nos seus projetos](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar).

## Documentação
Veja a documentação na [wiki](https://github.com/williamniemiec/ExecutionFlow/wiki). Veja [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Limita%C3%A7%C3%B5es-e-pontos-importantes) as limitações da aplicação e outros pontos importantes.

## Como usar
A documentação sobre como usar a aplicação se encontra [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar). Não se esqueça de incluir a o arquivo [aspectjtools.jar](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar) no build path do projeto, pois caso contrário a aplicação não funcionará.

## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior
- JUnit 4 ou JUnit 5
- Sistema operacional: Windows

## Problemas
Caso você encontre algum problema na aplicação, certifique-se de verificar a solução de problemas na wiki. Caso seu problema não seja resolvido, verifique se já existe uma [issue](https://github.com/williamniemiec/ExecutionFlow/issues) com seu problema. Se não tiver, [crie uma issue](https://github.com/williamniemiec/ExecutionFlow/issues/new/choose) descrevendo detalhadamente o problema ocorrido.

### Incompatibilidade entre as versões
Na maioria das vezes as versões não são compatíveis entre si.  Caso você tenha usado uma versão anterior em seu projeto, é necessário eliminar a versão antiga antes de utilizar a nova. Para isso, certifique-se de excluir o diretório com o nome `executionFlow` dos arquivos de seu projeto. Além disso, use apenas uma versão no classpath / buildpath de seu projeto, pois caso contrário pode haver conflito entre as versões. Faça o procedimento abaixo antes de utilizar a nova versão.

![](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/gif/migration.gif)

### Warning durante a execução
Se for exibida essa mensagem durante a execução da aplicação, apenas clique em `continue` (informa que o código foi alterado durante a execução)

![eclipse_msg](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/others/eclipse_msg.PNG?raw=true)

## Changelog
Detalhes sobre cada versão estão documentadas na [seção releases](https://github.com/williamniemiec/ExecutionFlow/releases).

## Contribua
Veja a documentação sobre como é possível contribuir com o projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/CONTRIBUTING.md).

## Desenvolvedores
Veja a documentação específica para programadores que buscam alterar o código fonte [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/DEVELOPERS.md).

## Organização do projeto
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/uml/uml.png?raw=true)

Veja a descrição de cada classe do projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Classes,-Interfaces-e-Aspectos).

<b>OBS:</b> Não esta representada as classes do pacote [executionFlow.util](https://github.com/williamniemiec/ExecutionFlow/tree/master/src/executionFlow/util) para evitar que o diagrama fique poluído - com pouca legibilidade.

## Exemplo de saída - [SimpleTestPath](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/examples/others/SimpleTestPath.java)
<code>
	
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.SimpleTestPath.simpleTestPath()
	examples.others.auxClasses.AuxClass.factorial(int)
	[91, 93, 94, 93, 94, 93, 94, 93, 94, 93, 97]
	 
	[...]
	 
	--------------------------------------------------------------------------------
	    	                             EXPORT
	--------------------------------------------------------------------------------
	examples.others.SimpleTestPath.simpleTestPath()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]
</code>

Veja mais exemplos de test paths gerados [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

## Como funciona?
![](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/others/visao-geral.png?raw=true)

Veja mais detalhes [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-funciona).


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

Veja a descrição de todos os arquivos [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Arquivos).
