# Execution Flow -  Contributing Guide

- [Problemas](#issues)
- [Pull Request - Guia](#pull-request-guide)
- [Configurando ambiente de desenvolvimento](#development-setup)
- [Padrão de documentação](#doc-standard)
- [Geração jar](#jar-generation)
- [Estrutura do projeto](#project-structure)
- [Apêndice](#appendix)


## <a name="issues"></a> Problemas

- Se ocorrer algum problema ou dúvida durante a edição do projeto, crie uma [issue](https://github.com/williamniemiec/ExecutionFlow/issues) detalhando o problema / dúvida.


## <a name="pull-request-guide"></a> Pull Request - Guia

### Branch
- Caso as alterações que foram feitas não alteram a estrutura da aplicação e nem o modo de usar alguma funcionalidade, use o branch atual. Caso contrário, [crie um novo branch](#new-branch) no seguinte formato:

> Se o branch atual for `N.x`, o novo branch deve se chamar `N+1.x`, onde N é um número

<b>OBS:</b> Não faça nenhuma alteração usando o branch `master`, pois ele será resultado do merge com a última versão lançada

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
* Gerar o JAR na localização correta
* Atualizar `pom.xml` com a nova versão
* Documentar as alterações de acordo com o [padrão de documentação citado acima](#doc-standard).


## <a name="development-setup"></a> Configurando ambiente de desenvolvimento

Para que seja possível executar algum arquivo do projeto é necessário importar algumas dependências para sua IDE. Até o momento, devido as dependências do projeto, só é possível executá-lo usando a IDE [Eclipse v2019-06](https://www.eclipse.org/downloads/packages/release/2019-06) ou inferior. Mais especificamente, a dependência AJDT só funciona corretamente até essa versão, o que impossibilita o uso de versões mais recentes.

* [Eclipse v2019-06](https://www.eclipse.org/downloads/packages/release/2019-06) ou superior
* [AJDT dev builds for Eclipse 4.8](http://download.eclipse.org/tools/ajdt/48/dev/update): Plugin da IDE Eclipse usado para habilitar [programação orientada a aspectos](https://en.wikipedia.org/wiki/Aspect-oriented_programming).
* [Java 12](https://www.oracle.com/java/technologies/javase/jdk12-archive-downloads.html) ou superior.
* JUnit 4 ou 5

### <a name="development-setup-run"></a> Rodando o projeto no Eclipse
Com o Eclipse, Java 12 ou sperior e AJDT (eclipse plugin) instalados, para executar o projeto na IDE faça o seguinte:
1) Importe o projeto no eclipse
2) Provavelmente irá aparecer vários erros. Ignore-os. Clique com o botão esquerdo no arquivo `pom.xml` e selecione `Run As` -> `Maven install`

![step1](https://raw.githubusercontent.com/williamniemiec/ExecutionFlow/master/docs/img/env-setup/step1.png?raw=true)

3) Após terminado a instalação, clique novamente com o botão esquerdo no arquivo `pom.xml` e selecione `Maven` -> `Update Project...`

![step2](https://raw.githubusercontent.com/williamniemiec/ExecutionFlow/master/docs/img/env-setup/step2.png?raw=true)

4) Selecione o projeto, marque as 3 últimas opções e clique em `Ok`

![step3](https://raw.githubusercontent.com/williamniemiec/ExecutionFlow/master/docs/img/env-setup/step3.png?raw=true)

## <a name="doc-standard"></a>Padrão de documentação
Todas as classes, métodos e algumas variáveis utilizam [javadoc](https://docs.oracle.com/javase/8/docs/technotes/tools/windows/javadoc.html) para explicar suas funcionalidades.

### <a name="doc-standard-class-enum"></a> Classes, classes internas e enumerações
As classes devem conter a seguinte padrão:

<pre>
/**
 * Descrição da classe.
 * 
 * @author		SeuNome &lt; seuemail@email.com &gt;
 * @version		X.Y.Z
 * @since		A.B.C
 */
</pre>
Onde X, Y e Z  são números relativos a versão da apllicação em que a classe foi modificada pela última vez e A, B e C identificam a versão da aplicação em que a classe foi criada. A anotação é separada do conteúdo com 2 tabs.
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
* Test hooks
* Serialization and deserialization methods
* Enumerations
* Inner classes


### <a name="doc-standard-methods"></a> Metodos
Os métodos devem ser documentados usando javadoc. É altamente recomendado adicionar comentários dentro do método para facilitar o entendimento (mas não é obrigatório).

<b>Atenção: </b> As documentações que utilizam javadoc devem ter o nome da tag seguido de duas tabulações seguido de seu valor, com exceção das tags `@implSpec`, `@apiNote` e `@implNote`, as quais contém apenas uma tabulação. Isso é feito a fim de manter a apresentação da documentação de forma uniforme

## <a name="jar-generation"></a>Geração jar
Para gerar arquivo jar:

1) No `pom.xml` atualize:

* project.version
* project.properties.version.major (se necessário)

2) Gere o JAR

* Console
> `mvn package`

* Eclipse
> Clique com o botão esquerdo no `pom.xml` -> Run As -> Maven build...
> Em `Goals:` digite: `package`
> Clique em `Run`

3) Certifique-se de que o arquivo JAR foi gerado no diretório `dist/V.X/<NOME_ARQUIVO>`, onde V = `project.properties.version.major` e o \<NOME_ARQUIVO\> é definido da seguinte maneira:
`executionflow-X.Y.Z.jar`
onde X, Y, Z são os números da versão da aplicação correspondente a `project.version`

## <a name="project-structure"></a> Estrutura do projeto
![global-schema](https://raw.githubusercontent.com/williamniemiec/ExecutionFlow/master/docs/img/schemas/global.png?raw=true)

### /
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|dist |`Diretório`|Versões lançadas da aplicação|
|docs |`Diretório`|Informações relativos a documentação|
|examples   |`Diretório`| Exemplos de testes JUnit para ver o funcionamento da aplicação   |
|lib   |`Diretório`|Bibliotecas que o projeto depende   |
|src     |`Diretório`| Arquivos fonte|

### /src
|        Nome        |Tipo|Descrição|
|----------------|-------------------------------|-----------------------------|
|assembly|`Diretório`|Arquivos de configuração relativos a geração do JAR|
|main|`Diretório`|Arquivos fonte da aplicação|
|test|`Diretório`|Testes dos arquivos fonte|


<hr />

## <a name="appendix"></a> Apendice

### Instalando maven
Veja [aqui](https://maven.apache.org/install.html) como instalar.

### <a name="new-branch"></a> Criando branch
De maneira resumida, para criar um novo branch:

<code>

	git checkout -b nome-branch
</code>

Para adicionar ao repositório remoto:
<code>
	
	git push -u origin nome-branch
</code>

#### Exemplo
<code>

	git checkout -b v1.x
	git push -u origin v1.x
</code>

Veja mais detalhes [aqui](https://git-scm.com/book/en/v2/Git-Branching-Basic-Branching-and-Merging).

### <a name="new-tag"></a> Criando tags
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
