# Execution flow
![](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/logo/logo.jpg?raw=true)
Aplicação que tem por objetivo exibir o caminho de teste (test path) de métodos de teste feitos com o JUnit.

<hr />

## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) (não testado com Java 13)
- [ASM 7 ou superior](https://github.com/williamniemiec/ExecutionFlow/tree/master/lib) (Incluído no projeto)
- JUnit 4 ou superior

## Restrições da aplicação
- Cada método de teste deve possuir uma das seguintes anotações: `@Test`.

<b>OBS:</b> Em breve será adicionado o suporte a testes com as seguintes anotações: `@RepeatedTest`, `@ParameterizedTest ` e `@TestFactory`.

<b>OBS:</b> Não está sendo gerado o test path completo das estruturas de controle de fluxo if-else e switch.

## Como usar?
### 1) No eclipse, selecione seu projeto, clique com o botão direito, vá em "configure" e selecione a opção "Convert to AspectJ Project"

![step1](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/howToUse/step1.png?raw=true)

### 2) Após isso selecione novamente seu projeto, clique com o botão direito, vá em "AspectJ Tools" e selecione "Configure AspectJ Build Path"

![step2](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/howToUse/step2.png?raw=true)

### 3) Vá na aba "Inpath" e selecione "Add External JARs...". Escolha o arquivo jar da aplicação

![step3](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/howToUse/step3.png?raw=true)

### 4) Clique em "Apply and Close"

![step4](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/howToUse/step4.png?raw=true)

### 5) Após isso, ao executar qualquer método de teste (que cumpra as restrições da aplicação) será gerado os test paths

![step5](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/howToUse/step5.png?raw=true)

<b>OBS:</b> O exemplo utilizado pode ser encontrado na pasta [examples](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/JUnitSimulation.java).

<b>OBS:</b> Mantenha a janela do eclipse ativa durante a computação do test path; caso contrário, a computação pode levar mais tempo do que o previsto.

## Como funciona?

### Etapa 1
Ao executar um teste JUnit, o aspecto TestMethodCollector irá pegar a assinatura do método de teste. Após isso, se o método que será testado não for estático, o aspecto ConstructorCollector irá pegar os dados do construtor desse método (informações sobre os parâmetros dele). Depois, o aspecto MethodCollector fará a coleta dos métodos que o método de teste testa e as informações de seus parâmetros. Por fim, é verificado se o método tem um construtor, isto é, se ele é não estático. Se ele tiver, será armazenado no [CollectorInfo](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/info/CollectorInfo.java) o método com seu construtor. Se ele não tiver construtor, será armazenado no CollectorInfo apenas esse método. Essas informações serão agrupadas de acordo com o número da linha em que o método é invocado. Por exemplo, se um método for invocado na linha x e estiver em um loop, ele será invocado x vezes. Supondo que ele seja invocado novamente fora do loop na linha y, será armazenado uma lista com todas as invocações desse método feitas a partir da linha x e suas invocações a partir da linha y. Isso permite que sejam coletados métodos que são invocados várias vezes em um método de teste. 
![]()













### Etapa 2
Após o término de um método de teste é executado, pelo aspecto TestMethodCollector, a classe ExecutionFlow, passando os dados coletados para ela. É essa classe que será a responsável por gerar o test path. Para isso, ela utilizará as classes do pacote `executionFlow.core`. Nessas classes será feita uma simulação da execução do método com base nos dados coletados na etapa 1, e, para cada linha executada, será salva em uma lista. No final da execução, será gerado o test path.

### Etapa 3
Após ser gerado o test path de todos os métodos coletados no método de teste, a classe ExecutionFlow irá exportar os dados salvos. Essa exportação será feita de acordo com o que estiver definido na variável `exporter` da classe ExecutionFlow (o valor dessa variável depende de qual versão você está utilizando - `ExecutionFlow_ConsoleExporter` ou `ExecutionFlow_FileExporter`). Caso vocÊ use a versão `ExecutionFlow_FileExporter`, os arquivos serão gerados no diretório "testPaths".

### Exemplo - [SimpleTest](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/SimpleTest.java)
Ex com um teste apenas para simplificar, sendo que exporter é consoleExporter:
<pre>
	<code>
		public class SimpleTest 
		{
			@Test
			public void testFactorial() 
			{
				int num = 4;
				long expectedResult = 24;
				
				TestClass tc = new TestClass(4);
				long res = tc.factorial(num);
				
				assertEquals(expectedResult, res);
			}
		}
	</code>
</pre>
#### Saída
<pre>
	<code>
		---------------------------------------------------------------------
                                EXPORT                               
		---------------------------------------------------------------------
		executionFlow.runtime.SimpleTest.testFactorial()
		executionFlow.runtime.TestClass.factorial(int)
		[68, 70, 71, 70, 71, 70, 71, 70, 71, 70, 74]
	</code>
</pre>

#### O que foi feito?
1) O aspecto TestMethodColletor pega a assinatura do metodo de teste (nesse caso, `executionFlow.runtime.SimpleTest.testFactorial()`)
2) Como o método a ser testado não é estático, o aspecto ConstructorCollector irá pegar as informações sobre o construtor do método que está sendo testado (ele irá armazenar informações dos parâmetros). Nesse caso, ele irá armazenar o tipo dos parâmetros (int) e o valor deles (4).
3) O aspecto MethodCollector fará a coleta das informações relevantes do método testado. Semelhante a coleta do construtor, será pego os tipos dos parâmetors (int), o valor deles (4) e o tipo de retorno do método (long). Além disso, para a futura utilização do [JDB](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/core/JDB.java) será armazenado o número da linha em que o método é invocado.
4) Terminado o método de teste `testFactorial()` o aspecto TestMethodColletor irá chamar o método `execute()` da classe ExecutionFlow e logo em seguida o método export da mesma.
5) A classe ExecutionFlow irá, para cada método coletado no método de teste, calcular o test path do método. Para isso, irá utilizar as classes do pacote `executionFlow.core`. A classe ExecutionFlow não utilizará diretamente as classes desse pacote, e sim apenas o gerenciador dessas classes ([TestPathManager](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/core/TestPathManager.java)), o qual computará os test paths.
6) As classes do pacote `executionFlow.core` irão pegar os parâmetros do método que forão passados e irá executar a simulação do método testado (factorial) com esses parâmetros. Caso o método não seja estático, é necessário invocar o construtor da classe ao qual esse método pertence. Para isso, será pego os dados do construtor que forão coletados no passo 2. Vale ressaltar que será usado duas abordagens diferentes para calcular o test path: via debugging (JDB) e via análise do bytecode da classe (CheapCoverage).
7) A classe [TestPathManager](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/core/TestPathManager.java) é então instanciada. A partir dela, será calculado o test path através da técnica de análise do bytecode da classe e através do debugging. Após obtido o test path por essas abordagens, será feito o merge deles e este será salvo como o test path do método.
8) A classe ExecutionFlow irá armazenar o test path com seu respectivo método, e irá verificar se há mais métodos a serem processados. Se houver, volta para o passo 5.
9) Calculado o test path de todos os métodos, será chamado o método `export()` (feita no passo 4). Esse método chamará o método `export()` da variável exporter e seu comportamento dependerá de qual classe que implementa a interface ExporterExecutionFlow estiver nessa variável. Como nesse caso é a [ConsoleExporter](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/exporter/ConsoleExporter.java), o resultado será exibido no console.

## Como é computado o test path?
Infelizmente, não consegui desenvolver um método que compute com 100% de precisão o test path. A abordagem utilizada não computa test paths errados, mas omite algumas linhas que deveriam aparecer. Essas omissões ocorrem com maior frequência em switches, if-else's e try-catches. Além disso, a aplicação não considera linhas em que são declarados variáveis sem inicializá-las.
Para a computação do test path são utilizadas duas abordagens:

- Via debugging ([JDB](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/core/JDB.java))

Essa técnica basicamente consiste em adicionar um breakpoint na linha de invocação do método em que se deseja obter o test path, realizar um 'step into' e, enquanto estiver dentro do método, realizar 'step over', coletando a linha executada após cada 'step over'. Quando sair do método, é executado 'cont' para abranger métodos que são invocados dentro de um laço de repetição.

- Via análise do bytecode da classe ([CheapCoverage](https://github.com/williamniemiec/ExecutionFlow/blob/master/src/executionFlow/core/CheapCoverage.java))

Essa técnica analisa o bytecode da classe e irá simular a execução da classe invocando os métodos coletados pelos aspectos e, para cada linha desses métodos executada, esta será salva em uma lista. Após o término da execução, estará gerado o test path do método, bastando salvá-lo e executar os mesmos procedimentos para o próximo método (se houver).

Foram utilizadas duas técnicas porque elas se complementam. A técnica da análise do bytecode da classe é muito rápida, mas omite diversas linhas na computação do test path. Já a técnica do debugging abrange bem mais linhas, mas ela sempre considera a última linha do método como executada, mesmo que seja executado um 'return' antes do final do método (imagem abaixo), enquando que a abordagem anterior não. Além disso, ela é mais lenta para o cálculo do test path. 
![jdb_endMethod](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/example/jdb_endMethod.png?raw=true)


Por fim, ao utilizar as duas abordagens, computa-se com maior precisão o test path. Porém, ainda há linhas que são omitidas, principalmente em estruturas if-else e switch. Acredito que não há uma maneira direta de obter essas linhas, pois como o código é convertido em assembler antes de ser executado, essa conversão não é fiél ao formato do código. Em outras palavras, um switch possui uma conversão em assembler diferente da implementação do código. A mesma coisa ocorre com a estrutura if-else (na verdade não existirá no código assembler um 'if-else'). Para ficar mais claro, a imagem abaixo exibe um código com if-else e ao lado seu código em assembler (na verdade em bytecode - que é próximo ao assembler - obtido utilizando `javap -verbose`). Percebe-se que o if-else foi convertido para if's. Essa conversão não mudará o comportamento do programa, isto é, o resultado final que ele gera; porém, ela influência diretamente o cálculo do test path, pois este é sensível as linhas de execução do programa.

### Código fonte
![sourceFile](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/examples/controlFlow/if-else_noAsm.png?raw=true)

### Código assembler - bytecode
![asmFile](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/examples/controlFlow/if-else_asm.png?raw=true)

## Como é a saída do programa?
Por padrão há dois tipos de saída: no console ou em arquivo. No console será exibido nesta ordem:
- Assinatura do método de teste
- Assinatura do método testado
- Test paths do método testado

Já no arquivo a saída será:
- Assinatura do método de teste
- Test paths do método testado

<b>OBS:</b> A assinatura do método testado estará no caminho até os test paths. Ela é explicada logo abaixo.

### Console
A saída consiste em exibir um header com o texto "EXPORT" seguido do padrão citado acima. Um exemplo pode ser visto [aqui](https://github.com/williamniemiec/ExecutionFlow/tree/master/examples#Exemplo).

### Arquivo
Em arquivos será criado um diretório (por padrão será "testPaths") e nele serão criados diretórios no seguinte formato:
> package_name/class_name.method_name(arguments)

Para exemplificar, suponha que será gerado o test path do método com essa assinatura:
> executionFlow.runtime.TestClass.factorial(int)

Os test paths desse método serão postos nesse diretório:
> testPaths/executionFlow/runtime/TestClass.factorial(int)

Um exemplo real desse método pode ser encontrado [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/testPaths/executionFlow/runtime/testClasses/TestClass.factorial(int)).

## Pontos importantes
Como ja dito anteriormente, as estruturas if-else, switch e try-catch possuem algumas linhas omitidas no test path. Segue abaixo um exemplo claro da omissão de cada uma dessas estruturas.

### If-else
Considere o código abaixo:
![if-else](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/example/controlFlow/if-else.png?raw=true)

| Test path esperado | Test path gerado |
|--------------------|------------------|
|	[11, 15, 17, 19, <b>21</b>, 22, 25]	|	[11, 15, 17, 19, 22, 25]	|


### Switch
Considere o código abaixo:
![switch](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/example/controlFlow/switch.png?raw=true)

| Test path esperado | Test path gerado |
|--------------------|------------------|
|	[64, 66, <b>67</b>, <b>68</b>, <b>69</b>, <b>70</b>, <b>73</b>, 76, 77, 96]	|	[64, 66, 76, 77, 96]	|

### Try-catch - try
![try-catch_try](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/example/controlFlow/try-catch_try.png?raw=true)

| Test path esperado | Test path gerado |
|--------------------|------------------|
|	[30, <b>32</b>, <b>33</b>, 34, 35, 36, 37, 38, 42]	|	[30, 34, 35, 36, 37, 38, 42]	|

### Try-catch - catch
![try-catch_catch](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/example/controlFlow/try-catch_catch.png?raw=true)

| Test path esperado | Test path gerado |
|--------------------|------------------|
|	[47, <b>49</b>, <b>50</b>, 51, 52, 53]	|	[47, 51, 52, 53]	|

Além disso, ambas as abordagens não consideram linhas em que só são declaradas variáveis, sem inicializá-las.
### Declaração de variaveis sem inicialização
![var_noInit](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/examples/variables/var_noInit.png?raw=true)

### Declaração de variaveis com inicialização
![var_withInit](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/examples/variables/var_withInit.png?raw=true)

## Organização do projeto
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/uml/UML.png?raw=true)

## Classes, Interfaces e Aspectos

### executionFlow

|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	ExecutionFlow		|	`Classe`	|	Dado um class path e um conjunto de métodos ela irá calcular os test paths para cada um desses métodos. Essa é a classe principal da aplicação	|


### executionFlow.core

|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	CheapCoverage	|	`Classe`	|	Computa test path via análise de bytecode |
|	JDB				|	`Classe`	|	Computa test path via debugging	|
|	RT				|	`Classe`	|	Classe auxiliar da classe `CheapCoverage`	|
|	TestPathManager	|	`Classe`	|	Computa test path a partir das técnicas disponíveis (CheapCoverage ou JDB)	|


### executionFlow.exporter

|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	ConsoleExporter			|	`Classe`	|	Exporta os resultados para o console	|
|	ExporterExecutionFlow	|	`Interface`	|	Responsável por exportar os resultados obtidos na classe `ExecutionFlow` |
|	FileExporter			|	`Classe`	|	Exporta os resultados para um arquivo	|

<b>OBS:</b> Ambas as classes implementam a interface `ExporterExecutionFlow`

### executionFlow.info

|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	ClassConstructorInfo	|	`Classe`	|	Armazena informações sobre um construtor de uma classe	|
|	ClassMethodInfo			|	`Classe`	|	Armazena informações sobre um método de uma classe	|
|	CollectorInfo			|	`Classe`	|	Armazena informações sobre o método de uma classe junto com seu respectivo construtor (se for um método não estático)	|
|	SignaturesInfo			|	`Classe`	|	Armazena a assinatura do método de teste de um método bem como a sua assinatura	|

### executionFlow.runtime

|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	CollectorExecutionFlow	|	`Classe`			|	Classe auxiliar usada para extrair os dados coletados que serão relevantes para a classe `ExecutionFlow`	|
|	ConstructorCollector	|	`Aspecto`			|	Captura instanciação de classes	|
|	MethodCollector			|	`Aspecto`			|	Captura todos os métodos com a anotação @Test, incluindo chamadas a outros métodos dentro deles. Ela não considera métodos nativos do java	|
|	RuntimeCollector		|	`Aspecto abstrato`	|	Responsável pela coleta de dados e construtores de classes usadas nos testes	|
|	SkipCollection			|	`Interface`			|	Anotação usada para indicar que uma classe não deve ter seus métodos coletados (será ignorada pelos coletores)	|
|	TestMethodCollector		|	`Aspecto`			|	Captura todos os métodos com a anotação @Test, ignorando chamadas a outros métodos dentro deles. Ela não considera métodos nativos do java	|


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

### /src/executionFlow
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|core|`Diretório`|Classes responsáveis por percorrer e registrar o caminho de execução de um método|
|exporter|`Diretório`|Classes responsáveis pela exportação dos test paths gerados|
|info|`Diretório`| Classes que manipulam métodos e classes  |
|runtime|`Diretório`|Classe e aspecto que coletam os dados de um método em tempo de execução|
|ExecutionFlow.java|`Arquivo`|Classe principal - responsável pela comunicação com todas as demais classes|

### /tests
Contém alguns testes relativos ao pacote `executionFlow.core` sem simular a coleta de dados pelos aspectos. Exemplos de testes mais complexos podem ser encontrados na pasta [examples](https://github.com/williamniemiec/ExecutionFlow/tree/examples), onde a coleta será feita pelos aspectos.

|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| executionFlow|`Diretório`|Testes relacionados ao pacote `executionFlow.core`|
|math|`Diretório`| Classes criadas para executar os [testes relativos ao pacote `executionFlow.core`](https://github.com/williamniemiec/ExecutionFlow/tree/master/tests/executionFlow/core). Essas classes só foram criadas para testar as classes da aplicação, e não fazem parte da aplicação|

### /tests/executionFlow/core
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| CheapCoverageTest.java|`Arquivo`|Testes relativo a classe [CheapCoverage](https://github.com/williamniemiec/ExecutionFlow/blog/master/src/executionFlow/core/CheapCoverage.java)|
|ExecutionFlowTest.java|`Arquivo`| Contém testes JUnit que utilizam as classes do diretório [math](). Utilizado para testar se as classes do pacote `executionFlow.core` estão computando o test path corretamente caso os dados dos métodos sejam coletados corretamente.|
| JDBTest.java|`Arquivo`|Testes relativo a classe [CheapCoverage](https://github.com/williamniemiec/ExecutionFlow/blog/master/src/executionFlow/core/CheapCoverage.java)|


### /examples
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| againstRequirements	|`Diretório`	| Testes que não funcionavam na primeira versão da aplicação|
| controlFlow 			|`Diretório`	|Testes relacionados ao fluxo de controle (if, if-else, try-catch,...)|
| testClasses 			|`Diretório`	|Classes criadas para a execução dos testes criados|
| ComplexTests.java 	|`Arquivo`      |Testes mais sofisticados, como testes realizados dentro de laços |
| JUnitSimulation.java 	|`Arquivo`		|Testes relacionados ao funcionamento da aplicação com testes feitos com o JUnit|
| MultipleTestPaths.java |`Arquivo`     |Testa a aplicação quando há vários test paths em um único método de teste|
| SimpleTest.java 		|`Arquivo`      |Teste simples com apenas 1 método de teste (usado para o exemplo da página inicial)|
