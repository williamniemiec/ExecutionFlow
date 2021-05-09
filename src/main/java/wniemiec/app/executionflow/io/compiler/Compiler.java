package wniemiec.app.executionflow.io.compiler;

import java.io.IOException;
import java.nio.file.Path;

import wniemiec.app.executionflow.io.FileEncoding;

/**
 * File compiler.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since		6.0.0
 */
public interface Compiler {

	/**
	 * Compiles a file and stores it in a specified directory.
	 * 
	 * @param		target Path of source file to be compiled
	 * @param		outputDir Path where compiled file will be saved
	 * @param		encode File encoding
	 * 
	 * @throws		IOException If an error occurs during compilation
	 */
	public void compile(Path target, Path outputDir, FileEncoding encode) 
			throws IOException;
}
