# Execution flow
Aplicação que exibe o caminho de execução (caminho de teste) de métodos.

<b>OBS:</b> Esse projeto não está completo, e, portanto, pode apresentar falhas.

<hr />

## Como funciona?
...

## Como usar?
...

## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) (não testado com Java 13)
- [ASM 7 ou superior](https://github.com/williamniemiec/ExecutionFlow/tree/master/lib) (Incluído no projeto)

## Organização do projeto (desatualizado)
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/uml/UML.png)

##  Arquivos
### /
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|.settings|`Diretório`|Diretório gerado por IDE (Eclipse)|
| bin |`Diretório`      |Arquivos binários (.class)|
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|media |`Diretório`|Informações visuais / Diagrama UML|
|src     |`Diretório`| Arquivos fonte|
|tests|`Diretório`|Testes dos arquivos fonte|
|.class|`Arquivo`|Arquivo gerado por IDE (Eclipse)|
|.project|`Arquivo`|Arquivo gerado por IDE (Eclipse)|

### /src/executionFlow/
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| cheapCoverage|`Diretório`|Classes responsáveis por percorrer e registrar o caminho de execução de um método|
|info|`Diretório`| Classes que manipulam métodos e classes  |
|runtime|`Diretório`|Classe e aspecto que coletam os dados de um método em tempo de execução|
|ClassExecutionFlow.java|`Arquivo`| Classe usada para manipulação de classes|
|ExecutionFlow.java|`Arquivo`|Classe principal - responsável pela comunicação com todas as demais classes|
|MethodExecutionFlow.java|`Arquivo`|Classe usada para manipulação de métodos|

### /tests/
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| executionFlow|`Diretório`|Testes relacionados ao pacote `executionFlow`|
|math|`Diretório`| Classes usadas pelo teste [ExecutionFlowTest.java](https://github.com/williamniemiec/ExecutionFlow/blob/master/tests/executionFlow/ExecutionFlowTest.java) |

### /tests/executionFlow
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| runtime|`Diretório`|Classes responsáveis por percorrer e registrar o caminho de execução de um método|
|ExecutionFlowTest.java|`Arquivo`| Testes relacionados com a classe [ExecutionFlow](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/ExecutionFlow.java)|

### /tests/executionFlow/runtime
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|JUnitSimulation.java|`Arquivo`|Testes relacionados ao funcionamento da aplicação com testes feitos com o JUnit|
| TestClass.java |`Arquivo`      |Classe criada para executar os testes do arquivo `JUnitSimulation.java`|

## Exemplo de saída
[ExecutionFlowTest.java](https://github.com/williamniemiec/ExecutionFlow/blob/master/tests/executionFlow/ "ExecutionFlowTest.java")
