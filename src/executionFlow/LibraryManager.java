package executionFlow;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import executionFlow.util.FileUtil;


/**
 * Responsible for gathering all libraries that the application needs.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.3
 * @since		5.2.0
 */
public class LibraryManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static final Path ARGUMENT_FILE = 
			Path.of(System.getProperty("user.home"), ".ef_dependencies.txt");
	private static Map<String, Path> libraries = new HashMap<>();
	private static Path libPath;
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		libraries.put("HAMCREST", getLibPath().resolve("hamcrest-all-1.3.jar"));
		libraries.put("JUNIT_4", getLibPath().resolve("junit-4.13.jar"));
		libraries.put("ASPECTJRT", getLibPath().resolve("aspectjrt.jar"));
		libraries.put("JUNIT_5_API", getLibPath().resolve("junit-jupiter-api-5.6.2.jar"));
		libraries.put("JUNIT_5_PARAMS", getLibPath().resolve("junit-jupiter-params-5.6.2.jar"));
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public static Path getLibrary(String libName) 
	{
		return libraries.get(libName);
	}
	
	/**
	 * Generates an argument file with all dependencies that the application 
	 * needs. If any library has been added recently, the argument file will be
	 * updated.
	 * 
	 * @throws		IOException If an error occurs while writing the file
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm#GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	public static Path generateArgumentFile() throws IOException
	{
		FileUtil.createArgumentFile(
				ARGUMENT_FILE.getParent(), 
				ARGUMENT_FILE.getFileName().toString(), 
				List.copyOf(libraries.values())
		);
		
		return ARGUMENT_FILE;
	}
	
	/**
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param		appRoot Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getLibPath()
	{
		if (libPath != null)
			return libPath;
		
		libPath = Path.of(
				ExecutionFlow.getAppRootPath().toAbsolutePath().toString(), 
				"lib"
		);

		return libPath;
	}
}
