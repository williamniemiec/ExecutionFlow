# ExecutionFlow

<hr />

## /tests/executionFlow/io/processor
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| files|`Diretório`|Arquivos gerados pelos processamentos realizados pelos métodos de teste [InvokedFileProcessorTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/io/processor/InvokedFileProcessor.java), [PreTestMethodFileProcessorTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/io/processor/PreTestMethodFileProcessor.java) e [TestMethodFileProcessorTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/io/processor/TestMethodFileProcessor.java). Os arquivos que resultantes do processamento contem o sufixo `_parsed`|
| PreTestMethodFileProcessorTest.java|`Class`|Testes que realizam o [pré-processamento de arquivos contendo métodos de teste](https://github.com/williamniemiec/ExecutionFlow/wiki/processamentos#pre-proc-test-method)  |
| InvokedFileProcessorTest.java|`Class`|Testes que realizam [processamento de arquivos contendo métodos ou construtores](https://github.com/williamniemiec/ExecutionFlow/wiki/processamentos#proc-method-constructor) |
| TestMethodFileProcessorTest.java|`Class`|Testes que realizam o [processamento de arquivos contendo métodos de teste](https://github.com/williamniemiec/ExecutionFlow/wiki/processamentos#proc-test-method)  |

<b>OBS:</b> Os arquivos não são executados; é feito apenas o pré-processamento deles