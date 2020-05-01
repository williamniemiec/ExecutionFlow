# ExecutionFlow

<hr />

## /examples
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| againstRequirements|`Diretório`| Testes que não funcionavam na primeira versão da aplicação|
| controlFlow |`Diretório`|Testes relacionados ao fluxo de controle (if, if-else, try-catch,...)|
| testClasses |`Diretório`|Classes criadas para a execução dos testes criados|
|AnnotationsTest.java|`Arquivo`      | Exemplos relacionados a anotações de teste (EM BREVE) |
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
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testParamSignature_object()
	testClasses.TestClass.testObjParam(Object)
	[194]

	#####################################################################
	                             testEmptyTest                           
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testEmptyTest()
	testClasses.TestClass.test2()
	[]

	#####################################################################
	                             testFactorial                           
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFactorial()
	testClasses.TestClass.factorial(int)
	[88, 90, 91, 90, 91, 90, 91, 90, 91, 90, 94]

	#####################################################################
	                            testFibonacci                            
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFibonacci()
	testClasses.TestClass.fibonacci(int)
	[105, 106, 107, 109, 110, 111, 112, 109, 110, 111, 112, 109, 110, 111, 112, 109, 115]

	#####################################################################
	                    testStaticMethod_charSequence                    
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testStaticMethod_charSequence()
	testClasses.TestClass.parseLetters_noInternalCall(CharSequence)
	[126, 127, 129, 130, 131, 129, 130, 131, 129, 130, 131, 129, 130, 131, 129, 130, 131, 129, 130, 133, 129, 130, 133, 129, 130, 133, 129, 130, 133, 129, 130, 133, 129, 136]

	#####################################################################
	                           testFactorial_zero                       
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFactorial_zero()
	testClasses.TestClass.factorial(int)
	[88, 90, 94]

	#####################################################################
	                      testParamSignature_createdObject               
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testParamSignature_createdObject()
	testClasses.TestClass.testClassParam(ClassInterface)
	[199]

	#####################################################################
	                          testInternalCall                           
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testInternalCall()
	testClasses.TestClass.parseLetters_withInternalCall(char[])
	[148, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 152, 150, 155]

	#####################################################################
	                             testInterface                           
	#####################################################################
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testInterface()
	testClasses.ClassInterface.test()
	[30, 31]
</code>

- MultipleTestPaths.java - output no console
<code>
	
	Generating test path...
	Generating test path...
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	MultipleTestPaths.ThreeTestPathsTest()
	testClasses.TestClass.threePaths(int)
	[181, 185, 186]
	[181, 185, 189]
	[181, 182]
</code>
