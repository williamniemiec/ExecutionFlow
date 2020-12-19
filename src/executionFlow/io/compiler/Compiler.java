package executionFlow.io.compiler;

import java.io.IOException;
import java.nio.file.Path;

import executionFlow.io.FileEncoding;

public interface Compiler {
	public void compile(Path target, Path outputDir, FileEncoding encode) throws IOException;
}