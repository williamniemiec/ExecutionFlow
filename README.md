# Execution flow
Aplicação que exibe o caminho de execução (caminho de teste) de métodos.

<b>OBS:</b> Esse projeto não está completo, e, portanto, pode apresentar falhas.

<hr />

## Como usar?
...


## Como funciona?

### Etapa 1
Ao executar um teste JUnit, o aspecto TestMethodCollector irá pegar a assinatura do método de teste. Após isso, se o método que será testado não for estático, o aspecto ConstructorCollector irá pegar os dados do construtor desse método (informações sobre os parâmetros dele). Por fim, o aspecto MethodCollector fará a coleta dos métodos que o método de teste testa e as informações de seus parâmetros.

### Etapa 2
Após o término de um método de teste é executado, pelo aspecto TestMethodCollector, a classe ExecutionFlow, passando os dados coletados para ela. É essa classe que será a responsável por gerar o test path. Para isso, ela utilizará as classes do pacote `executionFlow.cheapCoverage`. Nessas classes será feita uma simulação da execução do método com base nos dados coletados na etapa 1, e, para cada linha executada, será salva em uma lista. No final da execução, será gerado o test path.

### Etapa 3
Com o test path gerado, a classe ExecutionFlow irá exportar os dados salvos. Essa exportação será feita de acordo com o que estiver definido na variável `exporter` da classe ExecutionFlow (por padrão, será exportado para arquivos), no diretório "testPaths".

### [ex - SimpleTest]
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
				long res = tc.factorial(num);	COLOCAR LINK PARA O METODO
				
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
3) O aspecto MethodCollector fará a coleta das informações relevantes do método testado. Semelhante a coleta do construtor, será pego os tipos dos parâmetors (int), o valor deles (4) e o tipo de retorno do método (long).
4) Terminado o método de teste `testFactorial()` o aspecto TestMethodColletor irá chamar o método `execute()` da classe ExecutionFlow e logo em seguida o método export da mesma.
5) A classe ExecutionFlow irá, para cada método coletado no método de teste, calcular o test path do método. Para isso, irá utilizar as classes do pacote executionFlow.cheapCoverage.
6) As classes do pacote executionFlow.cheapCoverage irão pegar os parâmetros do método que forão passados e irá executar a simulação do método testado (factorial) com esses parâmetros. Caso o método não seja estático, é necessário invocar o construtor da classe ao qual esse método pertence. Para isso, será pego os dados do construtor que forão coletados no passo 2.
7) Durante a simulação, para cada linha executada, esta será colocada em uma lista. Ao final da simulação, o test path do método estará gerado e será retornado para a classe ExecutionFlow.
8) A classe ExecutionFlow irá armazenar o test path com seu respectivo método, e irá verificar se há mais métodos a serem processados. Se houver, volta para o passo 5.
9) Calculado o test path de todos os métodos, será chamado o método `export()` (feita no passo 4). Esse método chamará o método `export()` da variável exporter e seu comportamento dependerá de qual classe que implementa a interface ExporterExecutionFlow estiver nessa variável. Como nesse caso é a ConsoleExporter, o resultado será exibido no console.


## Como é a saída do programa?
Por padrão há dois tipos de saída: no console ou em arquivo. Ambas as opções exibirão, nesta ordem:
- Assinatura do método de teste
- Assinatura do método testado
- Test paths do método testado

### Console
A saída consiste em exibir um header com o texto "EXPORT" seguido do padrão citado acima. Um exemplo pode ser visto [aqui]().

### Arquivo
Em arquivos será criado um diretório (por padrão será "testPaths") e nele serão criados diretórios no seguinte formato:
> package_name/class_name.method_name(arguments)

Para exemplificar, suponha que será gerado o test path do método com essa assinatura:
> executionFlow.runtime.TestClass.factorial(int)

Os test paths desse método serão postos nesse diretório:
> testPaths/executionFlow/runtime/TestClass.factorial(int)

Um exemplo real desse método pode ser encontrado [aqui]().


## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) (não testado com Java 13)
- [ASM 7 ou superior](https://github.com/williamniemiec/ExecutionFlow/tree/master/lib) (Incluído no projeto)
- [JUnit]

## Organização do projeto (desatualizado)
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/media/uml/UML.png)

## Classes, Interfaces e Aspectos
- executionFlow
|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	ClassExecutionFlow	|	`Classe`	| 	Gerencia manipulação de classes (extrai os dados que a classe `ExecutionFlow` irá precisar)	|
|	ExecutionFlow		|	`Classe`	|	Dado um class path e um conjunto de métodos ela irá calcular os test paths para cada um desses métodos. Essa é a classe principal da aplicação	|
|	MethodExecutionFlow	|	`Classe`	|	Gerencia manipulação de métodos (extrai os dados que a classe `ExecutionFlow` irá precisar)	|


- executionFlow.cheapCoverage
|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	CheapCoverage	|	`Classe`	|	Classe que irá simular a execução de uma classe e gerar o test path dela	|
|	RT				|	`Classe`	|	Classe auxiliar da classe `CheapCoverage`	|


- executionFlow.exporter
|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	ConsoleExporter			|	`Classe`	|	Exporta os resultados para o console	|
|	ExporterExecutionFlow	|	`Interface`	|	Responsável por exportar os resultados obtidos na classe `ExecutionFlow` |
|	FileExporter			|	`Classe`	|	Exporta os resultados para um arquivo	|

<b>OBS:</b> Ambas as classes implementam a interface `ExporterExecutionFlow`

- executionFlow.info
|        Nome        | Tipo |	Descrição	|
|----------------|-------|--------------------------------------------------|
|	ClassConstructorInfo	|	`Classe`	|	Armazena informações sobre um construtor de uma classe	|
|	ClassMethodInfo			|	`Classe`	|	Armazena informações sobre um método de uma classe	|
|	SignaturesInfo			|	`Classe`	|	Armazena a assinatura do método de teste de um método bem como a sua assinatura	|

- executionFlow.runtime
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
| JUnitSimulation.java|`Arquivo`|Testes relacionados ao funcionamento da aplicação com testes feitos com o JUnit|
| MultipleTestPaths.java |`Arquivo`      |Testa a aplicação quando há vários test paths em um único método de teste|
| SimpleTest.java |`Arquivo`      |Teste simples com apenas 1 método de teste (usado para o exemplo da página inicial)|
| TestClass.java |`Arquivo`      |Classe criada para executar os testes do arquivo `JUnitSimulation.java`|

## Exemplo de saída
[ExecutionFlowTest.java](https://github.com/williamniemiec/ExecutionFlow/blob/master/tests/executionFlow/ "ExecutionFlowTest.java")
