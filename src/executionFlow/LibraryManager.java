package executionFlow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import executionFlow.dependency.DependencyManager;
import executionFlow.util.FileUtil;


/**
 * Responsible for gathering all libraries that the application needs.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.1
 * @since		5.2.0
 */
public class LibraryManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path ARGUMENT_FILE = 
			Path.of(System.getProperty("user.home"), ".ef_dependencies.txt");
	private static List<Path> libraries = new ArrayList<>();
	private static boolean modified = false;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		libraries.add(ExecutionFlow.getLibPath());
		libraries.add(ExecutionFlow.getAppRootPath());
		libraries.add(ExecutionFlow.getLibPath().resolve("aspectjrt.jar"));
		libraries.add(ExecutionFlow.getLibPath().resolve("aspectjtools.jar"));
		libraries.add(ExecutionFlow.getLibPath().resolve("junit-4.13.jar"));
		libraries.add(ExecutionFlow.getLibPath().resolve("hamcrest-all-1.3.jar"));
		
		if (!DependencyManager.hasDependencies()) {
			try {
				DependencyManager.fetch();
			} 
			catch (IOException e) {}
		}
		
		libraries.addAll(DependencyManager.getDependencies());
		modified = true;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Adds a new library, which will be part of the argument file.
	 * 
	 * @param		library Library path (.jar file or directory path)
	 * 
	 * @throws		IllegalArgumentException If library is null
	 */
	public static void addLibrary(Path library) 
	{
		if (library == null)
			throw new IllegalArgumentException("Path cannot be null");
		
		if (libraries.contains(library))
			return;
		
		modified = true;
		libraries.add(library);
	}

	/**
	 * Gets argument file with all dependencies that the application needs. If
	 * any library has been added recently, the argument file will be updated.
	 * 
	 * @return		Argument file
	 * 
	 * @throws 		IOException If an error occurs while creating the file
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm#GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	public static Path getArgumentFile() throws IOException
	{
		if (modified) {
			modified = false;
			generateArgumentFile();
		}
		
		return ARGUMENT_FILE;
	}
	
	/**
	 * Creates / updates argument file with all dependencies that the 
	 * application needs.
	 * 
	 * @throws		IOException If an error occurs while writing the file
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm#GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	private static void generateArgumentFile() throws IOException
	{
		FileUtil.createArgumentFile(
				ARGUMENT_FILE.getParent(), 
				ARGUMENT_FILE.getFileName().toString(), 
				libraries
		);
	}
}
