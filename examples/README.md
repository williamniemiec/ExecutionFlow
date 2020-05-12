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
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testParamSignature_object()
	testClasses.TestClass.testObjParam(Object)
	[195]

	#####################################################################
	                             testEmptyTest                           
	#####################################################################
	Processing source file...
	Processing completed
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
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFactorial()
	testClasses.TestClass.factorial(int)
	[89, 91, 92, 91, 92, 91, 92, 91, 92, 91, 95]

	#####################################################################
	                            testFibonacci                            
	#####################################################################
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFibonacci()
	testClasses.TestClass.fibonacci(int)
	[106, 107, 108, 110, 111, 112, 113, 110, 111, 112, 113, 110, 111, 112, 113, 110, 116]

	#####################################################################
	                    testStaticMethod_charSequence                    
	#####################################################################
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testStaticMethod_charSequence()
	testClasses.TestClass.parseLetters_noInternalCall(CharSequence)
	[127, 128, 130, 131, 132, 130, 131, 132, 130, 131, 132, 130, 131, 132, 130, 131, 132, 130, 131, 133, 134, 130, 131, 133, 134, 130, 131, 133, 134, 130, 131, 133, 134, 130, 131, 133, 134, 130, 137]

	#####################################################################
	                           testFactorial_zero                       
	#####################################################################
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFactorial_zero()
	testClasses.TestClass.factorial(int)
	[89, 91, 95]

	#####################################################################
	                      testParamSignature_createdObject               
	#####################################################################
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testParamSignature_createdObject()
	testClasses.TestClass.testClassParam(ClassInterface)
	[200]

	#####################################################################
	                          testInternalCall                           
	#####################################################################
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testInternalCall()
	testClasses.TestClass.parseLetters_withInternalCall(char[])
	[149, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 156]

	#####################################################################
	                             testInterface                           
	#####################################################################
	Processing source file...
	Processing completed
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
	
	Processing source file...
	Processing completed
	Generating test path...
	Processing source file...
	Processing completed
	Generating test path...
	Processing source file...
	Processing completed
	Generating test path...
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	MultipleTestPaths.ThreeTestPathsTest()
	testClasses.TestClass.threePaths(int)
	[182, 183]
	[182, 186, 190]
	[182, 186, 187]
</code>
