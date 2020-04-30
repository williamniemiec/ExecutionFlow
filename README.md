# Execution flow
![](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/logo/logo.jpg?raw=true)
Aplicação que tem por objetivo exibir o caminho de teste (test path) de métodos de teste feitos com o JUnit.

<hr />


## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior
- [ASM 7 ou superior](https://github.com/williamniemiec/ExecutionFlow/tree/master/lib) (Incluído no projeto)
- JUnit 4

## Exemplo de saída - [SimpleTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/SimpleTest.java)
<pre><code>
---------------------------------------------------------------------
                        EXPORT                               
---------------------------------------------------------------------
SimpleTest.testFactorial()
testClasses.TestClass.factorial(int)
[88, 90, 91, 90, 91, 90, 91, 90, 91, 90, 94]
</code></pre>


## Técnica utilizada para computar o test path
O test path é computado via debugging do código. Além dessa técnica, há outras disponíveis:
- Via análise do bytecode da classe ([branch core_asm](https://github.com/williamniemiec/ExecutionFlow/tree/core_asm))
- Via debugging & análise do bytecode da classe ([branch_hybrid](https://github.com/williamniemiec/ExecutionFlow/tree/core_hybrid))

[Veja mais detalhes](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-%C3%A9-computado-o-test-path).


## Organização do projeto
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/uml/UML.png?raw=true)

Veja a descrição de cada classe do projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Classes,-Interfaces-e-Aspectos).

##  Arquivos
### /
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|.settings|`Diretório`|Diretório gerado por IDE (Eclipse)|
| bin |`Diretório`      |Arquivos binários (.class)|
|example   |`Diretório`|	Projeto que foi utilizado na exemplificação de como usar a aplicação   |
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|media |`Diretório`|Informações visuais / Diagrama UML|
|releases |`Diretório`|Versões lançadas da aplicação|
|src     |`Diretório`| Arquivos fonte|
|testPaths|`Diretório`|Test paths gerados sobre os testes presentes na pasta [runtime](https://github.com/williamniemiec/ExecutionFlow/blob/master/tests/executionFlow/runtime)|
|tests|`Diretório`|Testes dos arquivos fonte|
|.class|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.project|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.build.ajproperties|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.manifest.mf|`Arquivo`|Arquivo responsável por gerar .jar (útil para releases)|

Veja a descrição de todos os arquivos [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Arquivos).

## Mais informações
Veja mais informações sobre como a aplicação funciona, como é computado o test path, as limitações dela e muito mais na [wiki do projeto](https://github.com/williamniemiec/ExecutionFlow/wiki).