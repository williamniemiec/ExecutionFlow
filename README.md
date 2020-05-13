# Execution flow
![](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/logo/logo.jpg?raw=true)
Aplicação que tem por objetivo exibir o caminho de teste (test path) de métodos de teste feitos com o JUnit.

<hr />

## Avisos importantes
- Se o método de teste falhar (ou seja, se um assert não coincidir com o resultado esperado) o test path pode não ser gerado
- Se ao executar o método de teste não for gerado o test path, [execute `clean` no projeto](https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#clean)
- Não interrompa a execução do teste antes de ser exibida a mensagem 'Processing completed', caso contrário o arquivo fonte do código pode ser comprometido. Caso isso ocorra, veja [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#arquivo-fonte-comprometido) como recuperar o arquivo original.

Veja todas as limitações da aplicação [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Limita%C3%A7%C3%B5es-e-pontos-importantes).

## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior
- JUnit 4 (não compatível com JUnit 5)

## Exemplo de saída - [SimpleTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/SimpleTest.java)
<pre><code>
---------------------------------------------------------------------
                        EXPORT                               
---------------------------------------------------------------------
SimpleTest.testFactorial()
testClasses.TestClass.factorial(int)
[88, 90, 91, 90, 91, 90, 91, 90, 91, 90, 94]
</code></pre>


Veja mais exemplos de test paths gerados [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

## Técnica utilizada para computar o test path

O test path é computado via debug do código. Além dessa técnica, há outras disponíveis:

- Via análise do bytecode da classe ([branch core_asm](https://github.com/williamniemiec/ExecutionFlow/tree/core_asm))
- Via debug & análise do bytecode da classe ([branch core_hybrid](https://github.com/williamniemiec/ExecutionFlow/tree/core_hybrid))

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
|examples   |`Diretório`|	Exemplos de testes JUnit para ver o funcionamento da aplicação   |
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|media |`Diretório`|Informações visuais / Diagrama UML|
|releases |`Diretório`|Versões lançadas da aplicação|
|src     |`Diretório`| Arquivos fonte|
|testPaths|`Diretório`|Test paths gerados sobre alguns testes presentes na pasta [examples](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples)|
|tests|`Diretório`|Testes dos arquivos fonte|
|.class|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.project|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.build.ajproperties|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.manifest.mf|`Arquivo`|Arquivo responsável por gerar .jar (útil para releases)|

Veja a descrição de todos os arquivos [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Arquivos).

## Mais informações
Veja mais informações sobre como a aplicação funciona, como é computado o test path, as limitações dela e muito mais na [wiki do projeto](https://github.com/williamniemiec/ExecutionFlow/wiki).
