package executionFlow.io.compiler;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;

import executionFlow.util.Logger;


/**
 * Responsible for compiling Java files.
 * 
 * @apiNote		Compatible with AspectJ
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		1.3
 */
public class StandardAspectJCompiler implements Compiler
{
	public static class Builder implements AspectJCompilerBuilder {
		private List<Path> classpaths;
		private Path inpath;
		
		public Builder inpath (Path inpath) {
			this.inpath = inpath;
			return this;
		}
		
		public Builder classpath(List<Path> classpath) {
			this.classpaths = classpath;
			return this;
		}
		
		public Compiler build() {
			return new StandardAspectJCompiler(inpath, classpaths);
		}
	}

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private Main compiler;
	private MessageHandler messageHandler;
	private List<Path> classpaths;
	private Path inpath;
	private List<String> commands;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public StandardAspectJCompiler() 
	{
		compiler = new Main();
		messageHandler = new MessageHandler();
	}
	
	public StandardAspectJCompiler(Path inpath, List<Path> classpaths) 
	{
		this();
		this.inpath = inpath;
		this.classpaths = classpaths;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Compiles .java file.
	 * 
	 * @param		target Path of source file to be compiled
	 * @param		outputDir Path where generated .class will be saved
	 * @param		encode File encoding
	 * 
	 * @throws		IOException If an error occurs during compilation
	 */
	public void compile(Path target, Path outputDir, FileEncoding encode) throws IOException
	{
		compiler.run(
			buildCommands(target, outputDir, encode),
			messageHandler
		);
		compiler.quit();
		
		dump(outputDir);
		
		if (checkError())
			throw new IOException("Compilation error. Check the log for more information");
	}

	private String[] buildCommands(Path target, Path outputDir, FileEncoding encode) {
		commands = new ArrayList<>();
		
		ignoreUncheckedWarnings();
		initializeInpath();
		initializeCompilerVersion();
		initializeEncoding(encode);
		initializeClassPaths();
		initializeOutput(outputDir);
		initializeTarget(target);
		
		return commands.toArray(new String[] {});
	}

	private void initializeTarget(Path target) {
		commands.add(target.toAbsolutePath().toString());
	}

	private void initializeOutput(Path outputDir) {
		commands.add("-d");
		initializeTarget(outputDir);
	}

	private void initializeClassPaths() {
		if (classpaths != null) {
			commands.add("-classpath");
			StringBuilder cps = new StringBuilder();
			
			for (Path classpath : classpaths) {
				cps.append(classpath.toAbsolutePath().toString());
				cps.append(";");
			}
			
			commands.add(cps.toString());
		}
	}

	private void initializeEncoding(FileEncoding encode) {
		commands.add("-encoding");
		commands.add(encode.getName());
	}

	private void initializeCompilerVersion() {
		commands.add("-9.0");
	}

	private void initializeInpath() {
		if (inpath != null) {
			commands.add("-inpath");
			commands.add(inpath.toAbsolutePath().toString());
		}
	}

	private void ignoreUncheckedWarnings() {
		commands.add("-Xlint:ignore");
	}

	private void dump(Path outputDir) {
		Logger.debug(this.getClass().getSimpleName(), "start");
		
		for (IMessage message : messageHandler.getMessages(null, true)) {
			Logger.debug(message.toString());
		}
		
		Logger.debug(
				this.getClass().getSimpleName(),
				"Output dir: " + outputDir.toAbsolutePath().toString()
		);
	}

	private boolean checkError() {
		for (IMessage message : messageHandler.getMessages(null, true)) {
			if (message.toString().contains("error at"))
				return true;
		}
		
		return false;
	}
	
	public void setClassPath(List<Path> classpath) {
		this.classpaths = classpath;
	}
	
	public void setClassPath(Path... classpath) {
		this.classpaths = new ArrayList<>();
		
		for (Path path : classpath) {
			this.classpaths.add(path);			
		}
	}
	
	public void setInpath(Path inpath) {
		this.inpath = inpath;
	}
}
