package executionFlow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import executionFlow.dependency.DependencyManager;
import executionFlow.util.FileUtil;

public class LibraryManager 
{
	private static final Path ARGUMENT_FILE = 
			Path.of(System.getProperty("user.home"), ".ef_dependencies.txt");
	private static List<Path> classPath = new ArrayList<>();
	private static boolean modified = false;
	
	static {
		classPath.add(ExecutionFlow.getLibPath());
		classPath.add(ExecutionFlow.getAppRootPath());
		classPath.add(ExecutionFlow.getLibPath().resolve("aspectjrt-1.9.2.jar"));
		classPath.add(ExecutionFlow.getLibPath().resolve("aspectjtools.jar"));
		classPath.add(ExecutionFlow.getLibPath().resolve("junit-4.13.jar"));
		classPath.add(ExecutionFlow.getLibPath().resolve("hamcrest-all-1.3.jar"));
		
		if (!DependencyManager.hasDependencies()) {
			try {
				DependencyManager.fetch();
			} 
			catch (IOException e) {}
		}
		
		classPath.addAll(DependencyManager.getDependencies());
		modified = true;
	}
	
	
	public static Path generateArgumentFile() throws IOException
	{
		FileUtil.createArgumentFile(
				ARGUMENT_FILE.getParent(), 
				ARGUMENT_FILE.getFileName().toString(), 
				classPath
		);
		
		return ARGUMENT_FILE;
	}
	
	public static void addClassPath(Path p) 
	{
		if (p == null)
			throw new IllegalArgumentException("Path cannot be null");
		
		if (classPath.contains(p))
			return;
		
		modified = true;
		classPath.add(p);
	}

	public static Path getArgumentFile() throws IOException
	{
		if (modified) {
			modified = false;
			generateArgumentFile();
		}
		
		return ARGUMENT_FILE;
	}
}
