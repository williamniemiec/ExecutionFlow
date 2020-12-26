# ExecutionFlow - Developers

* [Configurando ambiente de desenvolvimento](#setup)
* [Geração jar](#jar-generation)
* [Salvar modificações do projeto](#submit)
* [Testes](#tests)

## <a name="setup"> Configurando ambiente de desenvolvimento
### <a name="setup-dependencies"> Instalação de dependencias

Para que seja possível executar algum arquivo do projeto é necessário importar algumas dependências para sua IDE. Até o momento, devido as dependências do projeto, só é possível executá-lo usando a IDE [Eclipse v2019-06](https://www.eclipse.org/downloads/packages/release/2019-06) ou inferior. Mais especificamente, a dependência AJDT só funciona corretamente até essa versão, o que impossibilita o uso de versões mais recentes.

* [Eclipse v2019-06](https://www.eclipse.org/downloads/packages/release/2019-06): Como já citado anteriormente, até o momento não é possível usar uma versão mais nova da IDE devido a incompatibilidade com a dependência AJDT.
* [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update): Plugin da IDE Eclipse usado para habilitar [programação orientada a aspectos](https://en.wikipedia.org/wiki/Aspect-oriented_programming).
* [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior.
* [JUnit 4](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/junit-4.13.jar)
* [Hamcrest](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/hamcrest-all-1.3.jar): Necessário para a execução de métodos de teste dentro do JDB.
* [AspectJ Tools](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar): É utilizado para a compilação dos arquivos processados durante a execução da aplicação. Essa compilação é referente a execução da aplicação em outros projetos, e não a compilação do projeto da aplicação em si.
* [junit-jupiter-api-5.6.2.jar](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/junit-jupiter-api-5.6.2.jar) e [junit-jupiter-params-5.6.2.jar](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/junit-jupiter-params-5.6.2.jar): Necessário para a execução de testes JUnit 5 pela aplicação. Apesar dela converter esses testes em JUnit 4, elas são necessárias para não ocorrer erro caso haja elementos e anotações do JUnit 5 no código.


### <a name="setup-run"></a> Rodando o projeto no Eclipse
Com o Eclipse, Java e AJDT instalados, para executar o projeto na IDE é necessário incluir as dependências do projeto no build path do projeto. Para isso:
1) Clique com o botão direito no projeto
2) Selecione `Build Path` e `Configure Build path...` 
3) Clique em `Classpath` e clique em `Add External JARS...`
4) Selecione os arquivos .jar das seguintes dependências (as outras dependências a própria aplicação pegará do diretório `lib`):
* AspectJ Tools
* JUnit 4 ou 5
5) Por fim clique em `Apply and Close`.

### <a name="setup-environment"></a> Rodando aplicação como projeto e como plugin
Antes de executar a aplicação é necessário configurar seu ambiente. Existem 2 ambientes: Development e No-development. Se a aplicação for executada como projeto, isto é, ela não for executada como um plugin em outros projetos, é necessário informar a aplicação em qual ambiente ela será executada. Para isso, vá até o arquivo `src/executionFlow/ExecutionFlow.java` e procure pelo bloco de inicialização estático que define a variável `DEVELOPMENT`. Se a aplicação for executada como projeto, atribua `true` a essa variável; caso contrário, atribua `false`. Caso isso não seja feito, a aplicação pode ter mal funcionamento.

### <a name="setup-debug"></a> Debug
A aplicação fornece uma função de depuração para as seguintes classes:

* Analyzer
* ExecutionFlow
* FileCompiler
* InvokedFileProcessor
* PreTestMethodFileProcessor
* TestMethodFileProcessor

Essa funcionalidade pode ser ativada ou desativada através da variável `DEBUG`. Quando ativa, ela exibirá informações que podem ser uteis na detecção de bugs ou mesmo para facilitar o entendimento do funcionamento da aplicação.

### <a name="export"></a> Exportação
Existem 2 formas de exportas o test path: via console e via arquivo. A definição de qual método será usado é feita na variável `exporter`, na classe `ExecutionFlow`. 

## <a name="jar-generation"></a>Geração jar
Para gerar arquivo jar:
1) Certifique-se que todas as variáveis `DEBUG` são `false`. Lembre-se que as as classes em que ela se encontra são:

* Analyzer
* ExecutionFlow
* FileCompiler
* InvokerFileParser
* PreTestMethodFileParser
* TestMethodFileParser

2) Certifique-se que a variável `ENVIRONMENT` na classe `ExecutionFlow` seja `false`

3) Exporte o projeto

![jar-export-1](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/export/fig1.png?raw=true)

4) Na janela de exportação, selecione `Java` -> `JAR file with AspectJ support`.

![jar-export-2](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/export/fig2.png?raw=true)

5) Salve o jar no diretório `dist/X.Y/<NOME_ARQUIVO>`, onde X e Y são os números da versão atual e o \<NOME_ARQUIVO\> é definido da seguinte maneira:
`ExecutionFlow_<TYPE>Exporter_vX.Y.Z.jar`
onde:
* X, Y, Z: Números da versão da aplicação
* \<TYPE\>: Tipo de exportação dos test paths, podendo ser `Console` ou `File`

![jar-export-3](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/export/fig3.png?raw=true)

<b>OBS:</b> Ao gerar o jar será exibido uma mensagem avisando que o arquivo [MethodCallsCollector](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/runtime/collector/MethodCallsCollector.aj) contém warning. Ignore essa mensagem, pois este warning é devido ao fato de não ter nenhum arquivo corrente no projeto em que aspectos desse arquivo se aplicam.

## <a name="submit"> Salvar modificações do projeto
Para contribuir com o projeto, basta ter uma conta no GitHub, clonar o projeto (fork) na sua conta, realizar as alterações no código e abrir um [pull request](https://github.com/williamniemiec/ExecutionFlow/pulls). É recomendado olhar a documentação voltada para contribuições antes de realizar um pull request. Ela pode ser acessada [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/CONTRIBUTING.md).

## <a name="tests"> Testes

### <a name="unit-tests"></a> Testes unitários
Os testes foram feitos com base em códigos de exemplo (presentes no diretório [examples](https://github.com/williamniemiec/ExecutionFlow/tree/master/examples)). Eles testam se o test path destes códigos estão corretos ou não. Os testes pertencentes ao pacote `executionFlow.io.processor` realizam o processamento de códigos presentes no diretório [files](https://github.com/williamniemiec/ExecutionFlow/tree/master/test/executionFlow/io/processor/files) presente dentro do pacote. Os arquivos resultantes do processamento possuem o sufixo `_parsed`.
