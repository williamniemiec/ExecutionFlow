![](https://github.com/williamniemiec/ExecutionFlow/blob/v7.x/docs/img/logo/logo.jpg?raw=true)

<h1 align='center'>Execution Flow</h1>
<p align='center'>Gera test path de métodos e construtores testados em testes JUnit</p>
<p align="center">
	<a href="https://github.com/williamniemiec/ExecutionFlow/actions?query=workflow%3AWindows"><img src="https://img.shields.io/github/workflow/status/williamniemiec/ExecutionFlow/Windows?label=Windows" alt=""></a>
	<a href="https://github.com/williamniemiec/ExecutionFlow/actions?query=workflow%3AMacOS"><img src="https://img.shields.io/github/workflow/status/williamniemiec/ExecutionFlow/MacOS?label=MacOS" alt=""></a>
	<a href="https://github.com/williamniemiec/ExecutionFlow/actions?query=workflow%3AUbuntu"><img src="https://img.shields.io/github/workflow/status/williamniemiec/ExecutionFlow/Ubuntu?label=Ubuntu" alt=""></a>
	<a href="https://codecov.io/gh/williamniemiec/ExecutionFlow"><img src="https://codecov.io/gh/williamniemiec/ExecutionFlow/branch/v7.x/graph/badge.svg?token=R2SFS4SP86" alt="Coverage status"></a>
	<a href="http://java.oracle.com"><img src="https://img.shields.io/badge/java-12+-D0008F.svg" alt="Java compatibility"></a>
	<a href="https://github.com/williamniemiec/ExecutionFlow/releases"><img src="https://img.shields.io/github/v/release/williamniemiec/ExecutionFlow" alt="Release"></a>
	<a href="https://github.com/williamniemiec/ExecutionFlow/blob/master/LICENCE"><img src="https://img.shields.io/github/license/williamniemiec/ExecutionFlow" alt="Licence"></a>
</p>
<hr />

## 🔵 Introdução
Computa test path de métodos e construtores testados em testes JUnit. [Veja como usar nos seus projetos](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar).

## 📖 Documentação
Veja a documentação na [wiki](https://github.com/williamniemiec/ExecutionFlow/wiki). Veja [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Limita%C3%A7%C3%B5es-e-pontos-importantes) as limitações da aplicação e outros pontos importantes.

## ❓ Como usar
A documentação sobre como usar a aplicação se encontra [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar). Não se esqueça de incluir a o arquivo [aspectjtools.jar](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar) no build path do projeto, pois caso contrário a aplicação não funcionará.

## ✔ Requisitos
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06) ou superior
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update)
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior
- JUnit 4 ou JUnit 5

## ⚠ Avisos
Caso você encontre algum problema na aplicação, certifique-se de verificar a solução de problemas na wiki. Caso seu problema não seja resolvido, verifique se já existe uma [issue](https://github.com/williamniemiec/ExecutionFlow/issues) com seu problema. Se não tiver, [crie uma issue](https://github.com/williamniemiec/ExecutionFlow/issues/new/choose) descrevendo detalhadamente o problema ocorrido.

### Incompatibilidade entre as versões
Na maioria das vezes as versões não são compatíveis entre si.  Caso você tenha usado uma versão anterior em seu projeto, é necessário eliminar a versão antiga antes de utilizar a nova. Para isso, certifique-se de excluir o diretório com o nome `executionFlow` dos arquivos de seu projeto. Além disso, use apenas uma versão no classpath / buildpath de seu projeto, pois caso contrário pode haver conflito entre as versões. Faça o procedimento abaixo antes de utilizar a nova versão.

![migration](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/gif/migration.gif)

### Parar a execução
Para parar a aplicação, utilize o botão `Stop` na janela `Execution Flow - Remote control`. Não use o botão stop do eclipse, pois senão os arquivos originais não serão restaurados. Isso ocorre porque o stop do eclipse emite um comando SIGKILL, enquanto que o botão stop da janela `Execution Flow - Remote control` emite um comando SIGTERM para finalizar a aplicação. Veja [aqui](https://major.io/2010/03/18/sigterm-vs-sigkill/) mais sobre a diferença desses dois comandos.

![stop](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/howToUse/stop.png)

### Warning durante a execução
Se for exibida essa mensagem durante a execução da aplicação, apenas clique em `continue` (informa que o código foi alterado durante a execução). Isso ocorre porque durante a execução da aplicação o código fonte é alterado, sendo que no final o código fonte original é restaurado.

![eclipse_msg](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/others/eclipse_msg.PNG?raw=true)

## 🚩 Changelog
Detalhes sobre cada versão estão documentadas na [seção releases](https://github.com/williamniemiec/ExecutionFlow/releases).

## 🤝 Contribua
Veja a documentação sobre como é possível contribuir com o projeto [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/CONTRIBUTING.md).

## 💻 Desenvolvedores
Veja a documentação específica para programadores que buscam alterar o código fonte [aqui](https://github.com/williamniemiec/ExecutionFlow/blob/master/DEVELOPERS.md).

## 🗺 Organização do projeto (desatualizado)
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/uml/uml.png?raw=true)

<b>OBS:</b> Não esta representada as classes do pacote [executionFlow.util](https://github.com/williamniemiec/ExecutionFlow/tree/master/src/executionFlow/util) para evitar que o diagrama fique poluído - com pouca legibilidade.

## Exemplo de saída - [SimpleTestPath](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/examples/others/SimpleTestPath.java)
<code>
	
	--------------------------------------------------------------------------------
	                                     EXPORT
	--------------------------------------------------------------------------------
	examples.others.SimpleTestPath.simpleTestPath()
	examples.others.auxClasses.AuxClass.factorial(int)
	[35, 36, 37, 36, 37, 36, 37, 36, 37, 36, 39]
	 
	[...]
	 
	--------------------------------------------------------------------------------
	    	                             EXPORT
	--------------------------------------------------------------------------------
	examples.others.SimpleTestPath.simpleTestPath()
	examples.others.auxClasses.AuxClass(int)
	[12]
</code>

Veja mais exemplos de test paths gerados [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

## ℹ Como funciona?
![](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/others/visao-geral.png?raw=true)

Veja mais detalhes [aqui](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-funciona).


## 📁 Arquivos
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
