package executionFlow.io.compiler.aspectj;

import java.nio.file.Path;
import java.util.List;

import executionFlow.io.compiler.Compiler;

/**
 * Compiler builder with support for AspectJ framework.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.3
 */
public interface AspectJCompilerBuilder {
	
	/**
	 * Accept as source bytecode any .class files in the .jar files or
	 * directories on Path. The output will include these classes, 
	 * possibly as woven with any applicable aspects.
	 * 
	 * @param		inpath Directory or .zip file
	 * 
	 * @return		Itself to allow chained calls
	 */
	public AspectJCompilerBuilder inpath (Path inpath);
	
	/**
	 * Specify where to find user class files.
	 * 
	 * @param		classpath List of paths to .zip files or directories
	 * 
	 * @return		Itself to allow chained calls
	 */
	public AspectJCompilerBuilder classpath(List<Path> classpath);
	
	/**
	 * Creates {@link Compiler} with provided information.
	 * 
	 * @return		Compiler with support for AspectJ framework
	 */
	public Compiler build();
}
