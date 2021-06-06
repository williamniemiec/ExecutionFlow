![](https://github.com/williamniemiec/ExecutionFlow/blob/v7.x/docs/img/logo/logo.jpg?raw=true)

<h1 align='center'>Execution Flow</h1>
<p align='center'>Generates test path for tested methods and constructors from JUnit tests</p>
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

## ❇ Introduction
Computes test path for tested methods and constructors from JUnit tests using a debugger and aspect-oriented programming.

#### Demonstration (click to play)
[![Video demonstration](http://img.youtube.com/vi/bpfoJe1xv5g/0.jpg)](http://www.youtube.com/watch?v=bpfoJe1xv5g "ExecutionFlow - Introduction and Demonstration")

## ❓ How to use
The step by step on how to use the application can be found [here](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-usar). Don't forget to include [aspectjtools.jar](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar) in the build path of the project (otherwise the application will not work).

## 📖 Documentation
See the full documentation on [wiki](https://github.com/williamniemiec/ExecutionFlow/wiki). See [here](https://github.com/williamniemiec/ExecutionFlow/wiki/Limita%C3%A7%C3%B5es-e-pontos-importantes) the application restrictions and other important points .

## ✔ Requiremens
- [Eclipse 2019-06](https://www.eclipse.org/downloads/packages/release/2019-06) ou higher;
- [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update);
- [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) or higher;
- JUnit 4 or 5.

## 🖨 Output
![global-schema](https://raw.githubusercontent.com/williamniemiec/ExecutionFlow/master/docs/img/schemas/export.png?raw=true)

### Example - [SimpleTestPath](https://github.com/williamniemiec/ExecutionFlow/blob/master/examples/examples/others/SimpleTestPath.java)
```	
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
```

See more [here](https://github.com/williamniemiec/ExecutionFlow/wiki/Exemplos).

## ℹ How it works?
![app-schema](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/schemas/app.png?raw=true)

See more [here](https://github.com/williamniemiec/ExecutionFlow/wiki/Como-funciona).

## ⚠ Warnings
If you encounter any problems with the application, be sure to check the wiki for troubleshooting. If your problem is not resolved, check to see if there is already an [issue](https://github.com/williamniemiec/ExecutionFlow/issues) with your problem. If not, [create an issue](https://github.com/williamniemiec/ExecutionFlow/issues/new/choose) describing the problem in detail. 

### Incompatibility between versions 
Most of the time the versions are not compatible with each other. If you have used a previous version in your project, you must delete the old version before using the new one. To do this, be sure to delete the directory named `executionflow` from your project files. Also, use only one version in your project's classpath / buildpath, otherwise the versions may conflict. Do the procedure below before using the new version. 

![migration](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/gif/migration.gif)

### ⛔ Stop
In order to stop the application, use the `Stop` button in the` Execution Flow` window. Do not use the eclipse stop button, otherwise the original files will not be restored. This is because the eclipse stop issues a SIGKILL command, while the stop button in the `Execution Flow` window issues a SIGTERM command to end the application. See [here](https://major.io/2010/03/18/sigterm-vs-sigkill/) more about the difference between these two commands. 

![stop](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/howToUse/stop.png)

### Runtime warnings
If this message is displayed during the execution of the application, just click on `continue` (informs that the code was changed during the execution). This is because during the execution of the application the source code is changed, and in the end the original source code is restored. 

![eclipse_msg](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/others/eclipse_msg.PNG?raw=true)

## 🚩 Changelog
Details about each version are documented in the [releases section](https://github.com/williamniemiec/ExecutionFlow/releases).

## 🤝 Contribute!
See the documentation on how you can contribute to the project [here](https://github.com/williamniemiec/ExecutionFlow/blob/master/CONTRIBUTING.md).

## 🗺 Project structure
![global-schema](https://raw.githubusercontent.com/williamniemiec/ExecutionFlow/master/docs/img/schemas/global.png?raw=true)

## 📁 Files

### /
|        Name        |Type|Description|
|----------------|-------------------------------|-----------------------------|
|dist |`Directory`|Released versions|
|docs |`Directory`|Documentation files|
|examples   |`Directory`| Examples of JUnit tests to see how the application works    |
|lib   |`Directory`|Libraries the project uses   |
|src     |`Directory`| Source files|

### /src
|        Name        |Type|Description|
|----------------|-------------------------------|-----------------------------|
|assembly|`Directory`|Configuration files related to the generation of the JAR file |
|main|`Directory`|Application source files |
|test|`Directory`|Application test files  |
