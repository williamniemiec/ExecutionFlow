# ExecutionFlow

<hr />

## /examples/examples
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
| againstRequirements	|`Diretório`	|	Exemplos de códigos que não respeitam as limitações da aplicação	|
| builderPattern		|`Diretório`	|	Exemplos de códigos que utilizam [builder pattern](https://www.geeksforgeeks.org/builder-design-pattern/)	|
| chainedCalls			|`Diretório`	|	Exemplos de códigos que utilizam métodos encadeados	|
| complexTests			|`Diretório`	|	Exemplos de códigos mais sofisticados, como o uso de construtores em um loop, além de conter códigos que não funcionavam nas versões anteriores da aplicação	|
| controlFlow 			|`Diretório`	|	Exemplos relacionados ao fluxo de controle (if, if-else, try-catch,...)	|
| junit5 			|`Diretório`	|	Exemplos de testes JUnit 5	|
| overloadedMethod		|`Diretório`	|	Exemplos de códigos que utilizam chamadas para métodos sobrecarregados	|
| override 			|`Diretório`	|	Exemplos contendo sobrescrita	|
| polymorphism 			|`Diretório`	|	Exemplos de códigos que usam polimorfismo	|


<hr />

### Exemplo
- [JUnitSimulation.java e MultipleTestPaths.java - output em arquivo](https://github.com/williamniemiec/ExecutionFlow/tree/master/testPaths/testClasses)
- JUnitSimulation.java - output no console
<code>
	
	#####################################################################
	                      testParamSignature_object                      
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.testObjParam(Object)...
	[INFO] Processing source file of test method JUnitSimulation.testParamSignature_object()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.testObjParam(Object)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testParamSignature_object()
	testClasses.TestClass.testObjParam(Object)
	[195]

	#####################################################################
	                             testEmptyTest                           
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.test2()...
	[INFO] Processing source file of test method JUnitSimulation.testEmptyTest()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.test2()...
	[INFO] Test path is empty
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testEmptyTest()
	testClasses.TestClass.test2()
	[]

	#####################################################################
	                             testFactorial                           
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.factorial(int)...
	[INFO] Processing source file of test method JUnitSimulation.testFactorial()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.factorial(int)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFactorial()
	testClasses.TestClass.factorial(int)
	[89, 91, 92, 91, 92, 91, 92, 91, 92, 91, 95]

	#####################################################################
	                            testFibonacci                            
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.fibonacci(int)...
	[INFO] Processing source file of test method JUnitSimulation.testFibonacci()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.fibonacci(int)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFibonacci()
	testClasses.TestClass.fibonacci(int)
	[106, 107, 108, 110, 111, 112, 113, 110, 111, 112, 113, 110, 111, 112, 113, 110, 116]

	#####################################################################
	                    testStaticMethod_charSequence                    
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.parseLetters_noInternalCall(CharSequence)...
	[INFO] Processing source file of test method JUnitSimulation.testStaticMethod_charSequence()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.parseLetters_noInternalCall(CharSequence)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testStaticMethod_charSequence()
	testClasses.TestClass.parseLetters_noInternalCall(CharSequence)
	[127, 128, 130, 131, 132, 130, 131, 132, 130, 131, 132, 130, 131, 132, 130, 131, 132, 130, 131, 133, 134, 130, 131, 133, 134, 130, 131, 133, 134, 130, 131, 133, 134, 130, 131, 133, 134, 130, 137]

	#####################################################################
	                           testFactorial_zero                       
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.factorial(int)...
	[INFO] Processing source file of test method JUnitSimulation.testFactorial_zero()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.factorial(int)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testFactorial_zero()
	testClasses.TestClass.factorial(int)
	[89, 91, 95]

	#####################################################################
	                      testParamSignature_createdObject               
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.testClassParam(ClassInterface)...
	[INFO] Processing source file of test method JUnitSimulation.testParamSignature_createdObject()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.testClassParam(ClassInterface)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testParamSignature_createdObject()
	testClasses.TestClass.testClassParam(ClassInterface)
	[200]

	#####################################################################
	                          testInternalCall                           
	#####################################################################
	[INFO] Processing source file of method testClasses.TestClass.parseLetters_withInternalCall(char[])...
	[INFO] Processing source file of test method JUnitSimulation.testInternalCall()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.parseLetters_withInternalCall(char[])...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testInternalCall()
	testClasses.TestClass.parseLetters_withInternalCall(char[])
	[149, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 153, 151, 156]

	#####################################################################
	                             testInterface                           
	#####################################################################
	[INFO] Processing source file of method testClasses.Interface.test()...
	[INFO] Processing source file of test method JUnitSimulation.testInterface()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.Interface.test()...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	JUnitSimulation.testInterface()
	testClasses.Interface.test()
	[30, 31]
</code>

- MultipleTestPaths.java - output no console
<code>
	
	[INFO] Processing source file of method testClasses.TestClass.threePaths(int)...
	[INFO] Processing source file of test method MultipleTestPaths.ThreeTestPathsTest()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.threePaths(int)...
	[INFO] Test path has been successfully computed
	[INFO] Processing source file of method testClasses.TestClass.threePaths(int)...
	[INFO] Processing source file of test method MultipleTestPaths.ThreeTestPathsTest()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.threePaths(int)...
	[INFO] Test path has been successfully computed
	[INFO] Processing source file of method testClasses.TestClass.threePaths(int)...
	[INFO] Processing source file of test method MultipleTestPaths.ThreeTestPathsTest()...
	[INFO] Processing completed
	[INFO] Computing test path of method testClasses.TestClass.threePaths(int)...
	[INFO] Test path has been successfully computed
	---------------------------------------------------------------------
	                                EXPORT                               
	---------------------------------------------------------------------
	MultipleTestPaths.ThreeTestPathsTest()
	testClasses.TestClass.threePaths(int)
	[182, 186, 190]
	[182, 183]
	[182, 186, 187]
</code>
