# ExecutionFlow -  Contributing Guide

- [Problemas](#issues)
- [Pull Request - Guia](#pull-request-guide)
- [Configurando ambiente de desenvolvimento](#development-setup)
- [Padrão de documentação](#doc-standard)
-  [Geração jar ](#jar-generation)
-  [Alteração no diagrama UML](#uml)
- [Estrutura do projeto](#project-structure)


## <a name="issues"></a> Problemas

- Se ocorrer algum problema ou dúvida durante a edição do projeto, crie uma [issue](https://github.com/williamniemiec/ExecutionFlow/issues) detalhando o problema / dúvida.


## <a name="pull-request-guide"></a> Pull Request - Guia

### Branch
- Caso as alterações que foram feitas não alteram a estrutura da aplicação e nem o modo de usar alguma funcionalidade, use o branch atual. Caso contrário, [crie um novo branch](#new-branch) no seguinte formato:

> Se o branch atual for `N.x`, o novo branch deve se chamar `N+1.x`, onde N é um número

### Tag
- Sempre, antes de criar um pull request, crie uma tag
- Só crie a tag ao final de suas alterações - deve ser criada apenas uma tag por pull request
- Escolha uma tag diferente da tag atual. Se a tag atual for X.Y.Z, onde X, Y e Z são números, [crie uma nova tag](#new-tag) usando o seguinte critério:
<ul>
	<li>Se as alterações feitas são pequenas, isto é, pequenas modificações que não modificam a forma de utilização de uma funcionalidade ou mesmo para correções de bugs, crie a tag `X.Y.Z+1`</li>
	<li>Se for adicionado novas funcionalidades, crie a tag `X.Y+1.0`</li>
	<li>Se for alterada a forma de usar uma ou mais funcionalidades, ou mesmo se uma funcionalidade for excluída, crie um novo branch como o nome `X+1.x` e crie uma nova tag com o nome `X+1.0.0`</li>
</ul>

<b>OBS:</b> A criação de tags deve ser do tipo `Annotated Tags`.


- As versões lançadas devem ser colocadas no diretório `dist/X.Y`, onde X e Y são os números da versão lançada

- Procure sempre que possível adicionar testes em cada funcionalidade adicionada. Se uma funcionalidade for editada, certifique-se que os testes relacionados a ela continuam funcionando.

- Antes de adicionar uma nova funcionalidade, é recomendado criar uma issue descrevendo a nova funcionalidade e uma justificativa do porquê ela seria util à aplicação.

- Se a contribuição for corrigindo algum bug, o commit deve ser: `bug fix #xyzw`, onde #xyzw é o id da issue que cita o bug. Se não existir, o commit deve ser `bug fix <DESCRIPTION>`, onde \<DESCRIPTION\> é uma breve descrição do bug que foi corrigido.


### <a name="pull-request-submit"></a> Submetendo alterações

Após realizada as modificações no projeto, crie um pull request com o projeto que você modificou. Procure adicionar uma descrição detalhada do que você alterou com relação ao projeto original. Evite ao máximo alterar a estrutura do projeto, a fim de evitar quebra de código.
<b>ATENÇÃO:</b> Antes de realizar o pull request, certifique-se de:
* Deixar as variáveis `DEBUG` como `false`;
* Deixar a variável `ExecutionFlow.ENVIRONMENT` como `true`;
* Gerar jar da versão `ConsoleExporter` e da versão `FileExporter`;
* Documentar as alterações de acordo com o [padrão de documentação citado acima](#doc-standard).
* [Atualizar diagrama UML](#uml), se necessário.


## <a name="development-setup"></a> Configurando ambiente de desenvolvimento

Para que seja possível executar algum arquivo do projeto é necessário importar algumas dependências para sua IDE. Até o momento, devido as dependências do projeto, só é possível executá-lo usando a IDE [Eclipse v2019-06](https://www.eclipse.org/downloads/packages/release/2019-06) ou inferior. Mais especificamente, a dependência AJDT só funciona corretamente até essa versão, o que impossibilita o uso de versões mais recentes.

* [Eclipse v2019-06](https://www.eclipse.org/downloads/packages/release/2019-06): Como já citado anteriormente, até o momento não é possível usar uma versão mais nova da IDE devido a incompatibilidade com a dependência AJDT.
* [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update): Plugin da IDE Eclipse usado para habilitar [programação orientada a aspectos](https://en.wikipedia.org/wiki/Aspect-oriented_programming).
* [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior.
* [JUnit 4](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/junit-4.13.jar)
* [Hamcrest](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar): Necessário para a execução de métodos de teste dentro do JDB.
* [AspectJ Tools](https://github.com/williamniemiec/ExecutionFlow/blob/master/lib/aspectjtools.jar): É utilizado para a compilação dos arquivos processados durante a execução da aplicação. Essa compilação é referente a execução da aplicação em outros projetos, e não a compilação do projeto da aplicação em si.


### <a name="development-setup-run"></a> Rodando o projeto no Eclipse
Com o Eclipse, Java e AJDT instalados, para executar o projeto na IDE é necessário incluir as dependências do projeto no build path do projeto. Para isso:
1) Clique com o botão direito no projeto
2) Selecione `Build Path` e `Configure Build path...` 
3) Clique em `Classpath` e clique em `Add External JARS...`
4) Selecione os arquivos .jar das seguintes dependências (as outras dependências a própria aplicação pegará do diretório `lib`):
* AspectJ Tools
* JUnit 4
5) Por fim clique em `Apply and Close`.

### <a name="development-setup-environment"></a> Rodando aplicação como projeto e como plugin
Antes de executar a aplicação é necessário configurar seu ambiente. Existem 2 ambientes: Development e No-development. Se a aplicação for executada como projeto, isto é, ela não for executada como um plugin em outros projetos, é necessário informar a aplicação em qual ambiente ela será executada. Para isso, vá até o arquivo `src/executionFlow/ExecutionFlow.java` e procure pelo bloco de inicialização estático que define a variável `DEVELOPMENT`. Se a aplicação for executada como projeto, atribua `true` a essa variável; caso contrário, atribua `false`. Caso isso não seja feito, a aplicação pode ter mal funcionamento.

### <a name="output-dir"></a>Output directory
O diretório de saída do projeto, isto é, o diretório onde serão colocados os arquivos compilados deve ser `bin`, sendo que este deve ficar na raiz do projeto. Vale ressaltar que esse diretório não deve ser submetido ao repositório (`.gitignore` ignorará esse diretório).

## <a name="doc-standard"></a>Padrão de documentação
Todas as classes, métodos e algumas variáveis utilizam [javadoc](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html) para explicar suas funcionalidades.


### <a name="doc-standard-class-enum"></a> Classes, classes internas e enumerações
As classes devem conter a seguinte padrão:

<pre>
/**
 * Descrição da classe.
 * 
 * @author    SeuNome &lt; seuemail@email.com &gt;
 * @version   X.Y.Z
 * @since   A.B.C
 */
</pre>
Onde X, Y e Z  são números relativos a versão da apllicação em que a classe foi modificada pela última vez e A, B e C identificam a versão da aplicação em que a classe foi criada.
Além disso, internamente, a classe deve ser dividida em seções, sendo estas identificadas com o seguinte padrão:

<pre>
//-------------------------------------------------------------------------
//    [Nome_seção]
//-------------------------------------------------------------------------
</pre>

Onde [Nome_seção] pode ser:
* Attributes
* Constructor(s)
* Methods
* Getters
* Getters & Setters
* Setters
* Initialization block
* Tests
* Test preparers
* Serialization and deserialization methods
* Enumerations
* Inner classes


### <a name="doc-standard-methods"></a> Metodos
Os métodos devem ser documentados usando javadoc. É altamente recomendado adicionar comentários dentro do método para facilitar o entendimento (mas não é obrigatório).

<b>Atenção: </b> As documentações que utilizam javadoc devem ter o nome da tag seguido de duas tabulações seguido de seu valor, com exceção das tags `@implSpec`, `@apiNote` e `@implNote`, as quais contém apenas uma tabulação. Isso é feito a fim de manter a apresentação da documentação de forma uniforme

## <a name="jar-generation"></a>Geração jar
Para gerar arquivo jar:
1) Certifique-se que todas as variáveis `DEBUG` são `false`. Lembre-se que as as classes em que ela se encontra são:

* Analyzer
* ExecutionFlow
* FileCompiler
* InvokedFileProcessor
* PreTestMethodFileProcessor
* TestMethodFileProcessor

2) Certifique-se que a variável `ENVIRONMENT` na classe `ExecutionFlow` seja `false`

3) Exporte o projeto

![jar-export-1](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/export/fig1.png?raw=true)

4) Na janela de exportação, selecione `Java` -> `JAR file with AspectJ support`.

![jar-export-2](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/export/fig2.png?raw=true)

5) Salve o jar no diretório `dist/X.Y/<NOME_ARQUIVO>`, onde X e Y são os números da versão atual e o \<NOME_ARQUIVO\> é definido da seguinte maneira:
`ExecutionFlow_<TYPE>Exporter_vX.Y.Z.jar`
onde:
* X, Y, Z: Números da versão da aplicação
* \<TYPE\>: Tipo de exportação dos test paths, podendo ser `Console` ou `File`

![jar-export-3](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/img/export/fig3.png?raw=true)

### <a name="jar-generation-console"></a> Geração da versão ConsoleExporter
1) Na classe `ExecutionFlow` comente a linha `EXPORT = Export.FILE;` e retire o comentário da linha `EXPORT = Export.CONSOLE;`
2) Siga os [passos da geração do jar acima](#jar-generation).


### <a name="jar-generation-file"></a> Geração da versão FileExporter
1) Na classe `ExecutionFlow` comente a linha `EXPORT = Export.CONSOLE;` e retire o comentário da linha `EXPORT = Export.FILE;`
2) Siga os [passos da geração do jar acima](#jar-generation).


## <a name="uml"></a> Alteração no diagrama UML
Para alterar o diagrama UML, presente no diretório `docs/uml`, é necessário baixar o programa [Dia Diagram Editor](http://dia-installer.de/index.html). Ao editar o diagrama, salve o arquivo .dia no diretório e também exporte o diagrama como png, salvando também no mesmo diretório. O nome dos arquivos deve ser `uml`. Não se esqueça de salvar o arquivo do projeto uml alterado (`.dia`) junto com exportações do uml no formato `.png` e `.svg`.


## <a name="project-structure"></a> Estrutura do projeto
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|dist |`Diretório`|Versões lançadas da aplicação|
|docs |`Diretório`|Informações relativos a documentação|
|examples   |`Diretório`| Exemplos de testes JUnit para ver o funcionamento da aplicação   |
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|src     |`Diretório`| Arquivos fonte|
|test|`Diretório`|Testes dos arquivos fonte|


### <a name="project-structure-uml"></a> UML
![UML diagram](https://github.com/williamniemiec/ExecutionFlow/blob/master/docs/uml/uml.png?raw=true)

<b>OBS:</b> Não esta representada as classes do pacote [executionFlow.util](https://github.com/williamniemiec/ExecutionFlow/tree/master/src/executionFlow/util) para evitar que o diagrama fique poluido - com pouco legibilidade.

<hr />

## Apendice

### <a name="new-branch"></a> Criação de branchs
De maneira resumida, para criar um novo branch:

<code>
	
	git branch -a nome-branch
	git checkout nome-branch
</code>

Para adicionar ao repositório remoto:
<code>
	
	git push -u origin nome-branch
</code>

#### Exemplo
<code>
	
	git branch -a v1.x
	git checkout v1.x
	git push -u origin v1.x
</code>

Veja mais detalhes [aqui](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging).

### <a name="new-tag"></a> Criação de tags
<code>
	
	git tag -a nome-tag -m descricao
</code>

Para adicionar ao repositório remoto:
<code>
	git push -u origin nome-tag
</code>

#### Exemplo
<code>
	
	git tag -a v1.0.1 -m "Melhoria de performance"
	git push -u origin v1.0.1
</code>

Veja mais detalhes [aqui](https://git-scm.com/book/en/v2/Git-Basics-Tagging).
