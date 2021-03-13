package wniemiec.app.executionflow.io.compiler.aspectj;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;

import wniemiec.app.executionflow.io.FileEncoding;
import wniemiec.app.executionflow.io.compiler.Compiler;
import wniemiec.util.logger.Logger;

/**
 * Responsible for compiling Java files.
 * 
 * @apiNote		Compatible with AspectJ
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		1.3
 */
public class StandardAspectJCompiler implements Compiler {
	
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
	private StandardAspectJCompiler(Path inpath, List<Path> classpaths) {
		if (inpath == null)
			throw new IllegalArgumentException("Inpath cannot be null");
		
		if (classpaths == null)
			throw new IllegalArgumentException("Classpaths cannot be null");
		
		this.compiler = new Main();
		this.messageHandler = new MessageHandler();
		this.inpath = inpath.normalize().toAbsolutePath();
		this.classpaths = classpaths;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	public static class Builder implements AspectJCompilerBuilder {
		private List<Path> classpaths;
		private Path inpath;
		
		
		@Override
		public Builder inpath (Path inpath) {
			this.inpath = inpath;
			
			return this;
		}
		
		@Override
		public Builder classpath(List<Path> classpath) {
			this.classpaths = classpath;
			
			return this;
		}
		
		@Override
		public Compiler build() {
			return new StandardAspectJCompiler(inpath, classpaths);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public void compile(Path target, Path outputDir, FileEncoding encode) 
			throws IOException {
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
		initializeOutput(outputDir);
		initializeEncoding(encode);
		initializeClassPaths();
		initializeTarget(target);
		
		return commands.toArray(new String[] {});
	}

	private void ignoreUncheckedWarnings() {
		commands.add("-Xlint:ignore");
	}
	
	private void initializeInpath() {
		if (inpath != null) {
			commands.add("-inpath");
			commands.add(inpath.toAbsolutePath().toString());
		}
	}
	
	private void initializeCompilerVersion() {
		commands.add("-9.0");
	}

	private void initializeEncoding(FileEncoding encode) {
		commands.add("-encoding");
		commands.add(encode.getName());
	}
	
	private void initializeClassPaths() {
		if (classpaths == null)
			return;

		StringBuilder cps = new StringBuilder();
		
		commands.add("-classpath");
		
		for (Path classpath : classpaths) {
			cps.append(classpath.toAbsolutePath().toString());
			cps.append(File.pathSeparator);
		}
		
		commands.add(cps.toString());
	}
	
	private void initializeOutput(Path outputDir) {
		commands.add("-d");
		initializeTarget(outputDir);
	}
	
	private void initializeTarget(Path target) {
		commands.add(target.normalize().toAbsolutePath().toString());
	}
	
	private void dump(Path outputDir) {
		Logger.debug(this.getClass(), "start");
		
		for (IMessage message : messageHandler.getMessages(null, true)) {
			Logger.debug(message.toString());
		}
		
		Logger.debug(
				this.getClass(),
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
}
