![](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/logo/logo.jpg?raw=true)

<h1 align='center'> Execution Flow</h1>

<hr />

## Introdução
Computa test path de métodos e construtores testados em testes JUnit. [Veja como usar nos seus projetos](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar).

## Documentação
Veja a documentação na [wiki](https://github.com/williamniemiec/ExecutionFlow/wiki). Veja [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Limita%C3%A7%C3%B5es-e-pontos-importantes) as limitações da aplicação e outros pontos importantes.

## Como usar
A documentação sobre como usar a aplicação se encontra [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar). Não se esqueça de incluir a o arquivo [aspectjtools.jar](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar) no build path do projeto, pois caso contrário a aplicação não funcionará.

## Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06)
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior
- JUnit 4 ou JUnit 5
- Sistema operacional: Windows

## Problemas
Caso você encontre algum problema na aplicação, certifique-se de verificar a solução de problemas na wiki. Caso seu problema não seja resolvido, verifique se já existe uma [issue](https://github.com/williamniemiec/ExecutionFlow/issues) com seu problema. Se não tiver, [crie uma issue](https://github.com/williamniemiec/ExecutionFlow/issues/new/choose) descrevendo detalhadamente o problema ocorrido.

### Migração da versão 3.x para 4.x
A versão `4.x` não é compatível com a versão `3.x` e anteriores. Caso você tenha usado uma versão anterior em seu projeto e quer usar a versão `4.x`, siga os <a href="#migration-1x-2x">mesmos passos</a> da migração da versão `1.x` para `2.x`.

### Migração da versão 2.x para 3.x
A versão `3.x` não é compatível com a versão `2.x`; logo, se você usou ela em seu projeto e quer usar a versão `3.x`, siga os mesmos passos da migração da versão `1.x` para `2.x`. Caso contrário, ocorrerá o seguinte erro:

<code>
	
	org.aspectj.lang.NoAspectBoundException: Exception while initializing executionFlow_runtime_collector_TestMethodCollector: java.lang.NoSuchMethodError: executionFlow.util.Checkpoint.<init>(Ljava/lang/String;)V
	at executionFlow.runtime.collector.TestMethodCollector.aspectOf(TestMethodCollector.aj:1)
	at io.socket.parser.ByteArrayTest.encodeByteArray(ByteArrayTest.java:28)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:567)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:89)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:41)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:541)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:763)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:463)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:209)
	Caused by: java.lang.NoSuchMethodError: executionFlow.util.Checkpoint.<init>(Ljava/lang/String;)V
		at executionFlow.runtime.collector.TestMethodCollector.<clinit>(TestMethodCollector.aj:44)
		at io.socket.parser.ByteArrayTest.encodeByteArray(ByteArrayTest.java:23)
		... 23 more
</code>


### <a name="migration-1x-2x"></a> Migração da versão 1.x para 2.x
Caso você tenha usado a versão `1.x` em algum momento em seu projeto, certifique-se de excluir o diretório com o nome `executionFlow` dos arquivos de seu projeto. Além disso, use apenas uma versão no classpath / buildpath de seu projeto, pois caso contrário pode haver conflito entre as versões. Faça o procedimento abaixo antes de utilizar a nova versão.

![](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/gif/migration.gif)

### Warning durante a execução
Se for exibida essa mensagem durante a execução da aplicação, apenas clique em `continue` (informa que o código foi alterado durante a execução)

![eclipse_msg](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/others/eclipse_msg.PNG?raw=true)

## Changelog
Detalhes sobre cada versão estão documentadas na [seção releases](https://github.com/williamniemiec/ExecutionFlow/releases).

## Contribua
Veja a documentação sobre como é possível contribuir com o projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/CONTRIBUTING.md).

## Desenvolvedores
Veja a documentação específica para programadores que buscam alterar o código fonte [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/DEVELOPERS.md).

## Organização do projeto
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/uml/uml.png?raw=true)

Veja a descrição de cada classe do projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Classes,-Interfaces-e-Aspectos).

<b>OBS:</b> Não esta representada as classes do pacote [executionFlow.util](https://github.com/williamniemiec/ExecutionFlow/tree/master/src/executionFlow/util) para evitar que o diagrama fique poluído - com pouca legibilidade.

## Exemplo de saída - [SimpleTestPath](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/examples/others/SimpleTestPath.java)
<code>
	
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.SimpleTestPath.simpleTestPath()
	examples.others.auxClasses.AuxClass.factorial(int)
	[91, 93, 94, 93, 94, 93, 94, 93, 94, 93, 97]
	 
	[...]
	 
	--------------------------------------------------------------------------------
	    	                             EXPORT
	--------------------------------------------------------------------------------
	examples.others.SimpleTestPath.simpleTestPath()
	examples.others.auxClasses.AuxClass(int)
	[29, 31]
</code>

Veja mais exemplos de test paths gerados [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

##  Arquivos
### /
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|dist |`Diretório`|Versões lançadas da aplicação|
|docs |`Diretório`|Informações relativos a documentação|
|examples   |`Diretório`|	Exemplos de testes JUnit para ver o funcionamento da aplicação   |
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|src     |`Diretório`| Arquivos fonte|
|test|`Diretório`|Testes dos arquivos fonte|

Veja a descrição de todos os arquivos [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Arquivos).
