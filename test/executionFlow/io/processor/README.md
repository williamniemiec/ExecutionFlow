# ExecutionFlow

<hr />

## /tests/executionFlow/io/processor
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| files|`Diretório`|Arquivos gerados pelos processamentos realizados pelos métodos de teste [InvokedFileProcessorTest], [PreTestMethodFileProcessorTest] e [TestMethodFileProcessorTest]. Os arquivos que resultantes do processamento contem o sufixo `_parsed`|
| PreTestMethodFileParserTest.java|`Class`|Testes que realizam o [pré-processamento de arquivos contendo métodos de teste](LINK_WIKI)  |
| InvokedFileParserTest.java|`Class`|Testes que realizam [processamento de arquivos contendo métodos ou construtores](LINK_WIKI) |
| TestMethodFileParserTest.java|`Class`|Testes que realizam o [processamento de arquivos contendo métodos de teste](LINK_WIKI)  |

<b>OBS:</b> Os arquivos não são executados; é feito apenas o pré-processamento deles