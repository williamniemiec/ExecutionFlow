package wniemiec.executionflow.io.compiler.aspectj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wniemiec.executionflow.io.FileEncoding;
import wniemiec.executionflow.io.compiler.Compiler;
import wniemiec.executionflow.io.compiler.CompilerFactory;
import wniemiec.executionflow.lib.LibraryManager;

class StandardAspectJCompilerTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private final Path resourcesSrc;
	private final Path resourcesBin;
	private final Path outputDir;
	private Path base;
	private Path inpath;
	private List<Path> classpath;
	private String filename;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public StandardAspectJCompilerTest() {
		resourcesSrc = Path.of(".", "src", "test", "resources", "auxfiles", "aspectj");
		resourcesBin = Path.of(".", "target", "test-classes", "auxfiles", "aspectj");
		outputDir = Path.of(System.getProperty("java.io.tmpdir"));
	}
	
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@BeforeEach
	void prepare() {
		base = null;
		inpath = null;
		classpath = null;
		filename = null;
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	void testCompilation() throws IOException {
		withInpath(resourcesBin);
		withClasspath(LibraryManager.getJavaClassPath());
		withBase(Path.of("auxfiles", "aspectj"));
		withFilename("TestClass");
		
		doCompilation();
		
		assertFileWasCompiled();		
	}
	
	@Test
	void testCompilationWithNullInpath() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CompilerFactory.createStandardAspectJCompiler()
				.inpath(null)
				.classpath(LibraryManager.getJavaClassPath())
				.build();
		});
	}
	
	@Test
	void testCompilationWithNullClasspath() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> {
			CompilerFactory.createStandardAspectJCompiler()
				.inpath(resourcesBin)
				.classpath(null)
				.build();
		});
	}

	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private void withInpath(Path inpath) {
		this.inpath = inpath;
	}

	private void withClasspath(List<Path> classpath) {
		this.classpath = classpath;
	}

	private void withBase(Path base) {
		this.base = base;
	}

	private void withFilename(String filename) {
		this.filename = filename;
	}

	private void doCompilation() throws IOException {
		Compiler compiler = CompilerFactory.createStandardAspectJCompiler()
				.inpath(inpath)
				.classpath(classpath)
				.build();
		Path target = base.resolve(filename + ".java");

		compiler.compile(resourcesSrc.resolve(target), outputDir, FileEncoding.UTF_8);
	}

	private void assertFileWasCompiled() {
		assertFileExists(outputDir.resolve(base.resolve(filename + ".class")));
	}
	
	private void assertFileExists(Path file) {
		Assertions.assertTrue(Files.exists(file));
	}
}
