# ExecutionFlow

<hr />

## /tests/executionFlow/runtime

|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| JUnitSimulation.java|`Arquivo`|Testes relacionados ao funcionamento da aplicação com testes feitos com o JUnit|
| MultipleTestPaths.java |`Arquivo`      |Testa a aplicação quando há vários test paths em um único método de teste|
| SimpleTest.java |`Arquivo`      |Teste simples com apenas 1 método de teste (usado para o exemplo da página inicial)|
| TestClass.java |`Arquivo`      |Classe criada para executar os testes do arquivo `JUnitSimulation.java`|

<hr />

### Exemplo
- [JUnitSimulation.java e MultipleTestPaths.java - output em arquivo]()
- JUnitSimulation.java - output no console
<code>
	#####################################################################
                          	testStaticMethod                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testStaticMethod()
	executionFlow.runtime.TestClass.parseLetters_noInternalCall(char[])
	[106, 108, 109, 110, 108, 109, 110, 108, 109, 110, 108, 109, 110, 108, 109, 110, 108, 109, 112, 108, 109, 112, 108, 109, 112, 108, 109, 112, 108, 109, 112, 108, 115]

	#####################################################################
	                             testEmptyTest                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------

	#####################################################################
	                             testFactorial                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testFactorial()
	executionFlow.runtime.TestClass.factorial(int)
	[68, 70, 71, 70, 71, 70, 71, 70, 71, 70, 74]

	#####################################################################
	                            testFibonacci                            
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testFibonacci()
	executionFlow.runtime.TestClass.fibonacci(int)
	[85, 86, 87, 89, 90, 91, 92, 89, 90, 91, 92, 89, 90, 91, 92, 89, 95]

	#####################################################################
	                           testFactorial_zero                       
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testFactorial_zero()
	executionFlow.runtime.TestClass.factorial(int)
	[68, 70, 74]

	#####################################################################
	                          testInternalCall                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testInternalCall()
	executionFlow.runtime.TestClass.parseLetters_withInternalCall(char[])
	[127, 129, 131, 129, 131, 129, 131, 129, 131, 129, 131, 129, 131, 129, 131, 129, 131, 129, 131, 129, 131, 129, 134]
</code>
- MultipleTestPaths.java - output no console
<code>
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.MultipleTestPaths.ThreeTestPathsTest()
	executionFlow.runtime.TestClass.threePaths(int)
	[161, 165, 166]
	[161, 162]
	[161, 165, 169]
</code>