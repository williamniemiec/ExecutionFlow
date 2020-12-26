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
|methodCalledByTestedInvokeds|`Diretório`	| Exemplos de teste em que o método / construtor testado chama outros métodos |
|others|`Diretório`	| Contém testes de diversos tipos |
| overloadedMethod		|`Diretório`	|	Exemplos de códigos que utilizam chamadas para métodos sobrecarregados	|
| override 			|`Diretório`	|	Exemplos contendo sobrescrita	|
| polymorphism 			|`Diretório`	|	Exemplos de códigos que usam polimorfismo	|

<hr />

### Exemplo
- [others/OthersTest.java e others/MultipleTestPaths.java - output em arquivo](https://github.com/williamniemiec/ExecutionFlow/tree/master/examples/results)
- others/OthersTest.java - output no console
<code>
	
	[INFO] Pre-processing test method...
	[WARN] On the first run this process can be slow
	[INFO] Fetching dependencies...
	[INFO] Fetch completed
	[INFO] Pre-processing completed
	.[INFO] Processing source file of test method examples.others.OthersTest.testParamSignature_object()...
	[INFO] Fetching dependencies...
	[INFO] Fetch completed
	[INFO] Processing completed
	[INFO] Processing source file of invoked - examples.others.auxClasses.AuxClass.testObjParam(Object)...
	[INFO] Processing completed
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass.testObjParam(Object)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testParamSignature_object()
	examples.others.auxClasses.AuxClass.testObjParam(Object)
	[197]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testParamSignature_object()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.factorial(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass.fibonacci(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testingMultipleMethods()
	examples.others.auxClasses.AuxClass.factorial(int)
	[91, 93, 94, 93, 94, 93, 94, 93, 94, 93, 97]

	examples.others.OthersTest.testingMultipleMethods()
	examples.others.auxClasses.AuxClass.fibonacci(int)
	[108, 109, 110, 112, 113, 114, 115, 112, 113, 114, 115, 112, 118]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testingMultipleMethods()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.getNumber()...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.onlyOneMethod()
	examples.others.auxClasses.AuxClass.getNumber()
	[202]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.onlyOneMethod()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.fibonacci(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass.factorial(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testMethodWithAuxMethods()
	examples.others.auxClasses.AuxClass.fibonacci(int)
	[108, 109, 110, 112, 113, 114, 115, 112, 113, 114, 115, 112, 118]

	examples.others.OthersTest.testMethodWithAuxMethods()
	examples.others.auxClasses.AuxClass.factorial(int)
	[91, 93, 94, 93, 94, 93, 94, 93, 97]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testMethodWithAuxMethods()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.test2()...
	[WARN] Test path is empty
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testEmptyTest()
	examples.others.auxClasses.AuxClass.test2()
	[]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testEmptyTest()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.factorial(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testFactorial()
	examples.others.auxClasses.AuxClass.factorial(int)
	[91, 93, 94, 93, 94, 93, 94, 93, 94, 93, 97]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testFactorial()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.fibonacci(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testFibonacci()
	examples.others.auxClasses.AuxClass.fibonacci(int)
	[108, 109, 110, 112, 113, 114, 115, 112, 113, 114, 115, 112, 113, 114, 115, 112, 118]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testFibonacci()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.parseLetters_noInternalCall(CharSequence)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testStaticMethod_charSequence()
	examples.others.auxClasses.AuxClass.parseLetters_noInternalCall(CharSequence)
	[129, 130, 132, 133, 134, 132, 133, 134, 132, 133, 134, 132, 133, 134, 132, 133, 134, 132, 133, 135, 136, 132, 133, 135, 136, 132, 133, 135, 136, 132, 133, 135, 136, 132, 133, 135, 136, 132, 139]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.factorial(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testFactorial_zero()
	examples.others.auxClasses.AuxClass.factorial(int)
	[91, 93, 97]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testFactorial_zero()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	.[INFO] Computing test path of method examples.others.auxClasses.AuxClass.parseLetters_withInternalCall(char[])...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.OthersTest.testInternalCall()
	examples.others.auxClasses.AuxClass.parseLetters_withInternalCall(char[])
	[151, 153, 155, 153, 155, 153, 155, 153, 155, 153, 155, 153, 155, 153, 155, 153, 155, 153, 155, 153, 155, 153, 158]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv

	Time: 81,126

	OK (10 tests)
</code>

- others/MultipleTestPaths.java - output no console
<code>
	
	[INFO] Pre-processing test method...
	[WARN] On the first run this process can be slow
	[INFO] Fetching dependencies...
	[INFO] Fetch completed
	[INFO] Pre-processing completed
	.[INFO] Processing source file of test method examples.others.MultipleTestPaths.ThreeTestPathsTest()...
	[INFO] Fetching dependencies...
	[INFO] Fetch completed
	[INFO] Processing completed
	[INFO] Processing source file of invoked - examples.others.auxClasses.AuxClass.threePaths(int)...
	[INFO] Processing completed
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass.threePaths(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass.threePaths(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass.threePaths(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.MultipleTestPaths.ThreeTestPathsTest()
	examples.others.auxClasses.AuxClass.threePaths(int)
	[184, 185]
	[184, 188, 189]
	[184, 188, 192]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv
	[INFO] Computing test path of method examples.others.auxClasses.AuxClass(int)...
	[INFO] Test path has been successfully computed
	[WARN] There are no methods called by tested invoked
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.MultipleTestPaths.ThreeTestPathsTest()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]

	[INFO] Exporting invokers along with test methods that test them to CSV...
	[INFO] The export was successful
	[INFO] Location: C:\Users\William Niemiec\Documents\IC.local\workspace_ec12\ExecutionFlow\examples\results\Invokers_TestMethods.csv

	Time: 26,211

	OK (1 test)
</code>
