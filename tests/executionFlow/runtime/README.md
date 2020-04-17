# ExecutionFlow

<hr />

## /tests/executionFlow/runtime
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| againstRequirements|`Diretório`| Testes que não funcionavam na primeira versão da aplicação|
| controlFlow |`Diretório`|Testes relacionados ao fluxo de controle (if, if-else, try-catch,...)|
| testClasses |`Diretório`|Classes criadas para a execução dos testes criados|
| ComplexTests.java |`Arquivo`      |Testes mais sofisticados, como testes realizados dentro de laços |
| JUnitSimulation.java|`Arquivo`|Testes relacionados ao funcionamento da aplicação com testes feitos com o JUnit|
| MultipleTestPaths.java |`Arquivo`      |Testa a aplicação quando há vários test paths em um único método de teste|
| SimpleTest.java |`Arquivo`      |Teste simples com apenas 1 método de teste (usado para o exemplo da página inicial)|

<hr />

### Exemplo
- [JUnitSimulation.java e MultipleTestPaths.java - output em arquivo]()
- JUnitSimulation.java - output no console
<code>
	#####################################################################
	                      testParamSignature_object                      
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testParamSignature_object()
	executionFlow.runtime.testClasses.TestClass.testObjParam(Object)
	[194]

	#####################################################################
	                             testEmptyTest                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testEmptyTest()
	executionFlow.runtime.testClasses.TestClass.test2()
	[]

	#####################################################################
	                             testFactorial                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testFactorial()
	executionFlow.runtime.testClasses.TestClass.factorial(int)
	[88, 90, 91, 90, 91, 90, 91, 90, 91, 90, 94]

	#####################################################################
	                            testFibonacci                            
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testFibonacci()
	executionFlow.runtime.testClasses.TestClass.fibonacci(int)
	[105, 106, 107, 109, 110, 111, 112, 109, 110, 111, 112, 109, 110, 111, 112, 109, 115]

	#####################################################################
	                    testStaticMethod_charSequence                    
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testStaticMethod_charSequence()
	executionFlow.runtime.testClasses.TestClass.parseLetters_noInternalCall(CharSequence)
	[126, 127, 129, 130, 131, 129, 130, 131, 129, 130, 131, 129, 130, 131, 129, 130, 131, 129, 130, 133, 129, 130, 133, 129, 130, 133, 129, 130, 133, 129, 130, 133, 129, 136]

	#####################################################################
	                           testFactorial_zero                       
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testFactorial_zero()
	executionFlow.runtime.testClasses.TestClass.factorial(int)
	[88, 90, 94]

	#####################################################################
	                      testParamSignature_createdObject               
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testParamSignature_createdObject()
	executionFlow.runtime.testClasses.TestClass.testClassParam(ClassInterface)
	[199]

	#####################################################################
	                          testInternalCall                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testInternalCall()
	executionFlow.runtime.testClasses.TestClass.parseLetters_withInternalCall(char[])
	[148, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 155]

	#####################################################################
	                             testInterface                           
	#####################################################################
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.JUnitSimulation.testInterface()
	executionFlow.runtime.testClasses.ClassInterface.test()
	[30, 31]
</code>
- MultipleTestPaths.java - output no console
<code>
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	executionFlow.runtime.MultipleTestPaths.ThreeTestPathsTest()
	executionFlow.runtime.testClasses.TestClass.threePaths(int)
	[181, 185, 189]
	[181, 185, 186]
	[181, 182]
</code>