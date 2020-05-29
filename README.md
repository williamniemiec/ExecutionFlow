# Execution flow
![](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/logo/logo.jpg?raw=true)
Aplicação que tem por objetivo exibir o caminho de teste (test path) de métodos de teste feitos com o JUnit.

<hr />

## Avisos importantes
- Se o método de teste falhar (ou seja, se um assert não coincidir com o resultado esperado) o test path pode não ser gerado (será gerado até a exceção ocorrer)
- Se ao executar o método de teste não for gerado o test path, [execute `clean` no projeto](https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#clean)
- Não interrompa a execução do teste antes de ser exibida a mensagem '[INFO] Test path has been successfully computed'; caso contrário, o arquivo do código fonte pode ser comprometido. Caso isso ocorra, veja [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Solu%C3%A7%C3%A3o-de-problemas#arquivo-fonte-comprometido) como recuperar o arquivo original.
- Não será computado test path 2x de um mesmo método com mesmos parametros pertencente a uma mesma classe, já que resultado seria redundante
- É obrigatório ter diretório 'src' na raiz do projeto
- Metodo a ser testado deve estar externo ao arquivo do metodo de teste ([ver mais informações]())
- Não computa test path de metodos nativos do java (como size do lists)
- Se uma mesma linha for executada várias vezes seguidas ela só aparecerá uma vez [ver exemplo]()
- Chamadas de métodos realizadas na mesma linha são mais lentos de se computar test path (veja um exemplo [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/chainedCalls/ChainedMethods.java))
- Se houver algum erro de compilação no arquivo fonte o test path não será computado
- Não será computado test path de qualquer método que pertença a classes que contenham nome 'builder'; Além disso, não é computado test path de construtores ou blocos de inicialização
- Não computa test path de métodos nativos do java (como o método [size](https://docs.oracle.com/javase/8/docs/api/java/util/List.html#size--) do pacote [Lists](https://docs.oracle.com/javase/8/docs/api/java/util/List.html) ou o método [split](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#split(java.lang.String)) pertencente a classe [String](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html))
- Se a computação do test path for interrompida antes de seu fim, o arquivo da classe de teste ficará com código alterado (todos os locais que possuem `@Test` ficarão com `@Test @executionFlow.runtime._SkipMethod`). Ao executar o mesmo teste novamente, este não será executado, visto que essa anotação que foi adicionada desativa os coletores. Veja [aqui] como restaurar o arquivo original.
- Durante a execução do método de teste, o arquivo contendo a classe de teste sofrerá alterações. No final da execução, será restaurado o arquivo original. Se isso não acontecer, veja [aqui] como restaurar o arquivo original.

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
[89, 91, 92, 91, 92, 91, 92, 91, 92, 91, 95]
</code></pre>


Veja mais exemplos de test paths gerados [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

## Técnica utilizada para computar o test path

O test path é computado via debug do código. Além dessa técnica, há outras disponíveis (não geram o test path completo):

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
|.classpath|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.project|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|build.ajproperties|`Arquivo`|Arquivo gerado por IDE (Eclipse)|

Veja a descrição de todos os arquivos [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Arquivos).

## Mais informações
Veja mais informações sobre como a aplicação funciona, como é computado o test path, as limitações dela e muito mais na [wiki do projeto](https://github.com/williamniemiec/ExecutionFlow/wiki).
