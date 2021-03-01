package wniemiec.executionflow.lib;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wniemiec.executionflow.App;

/**
 * Responsible for gathering all libraries that the application needs.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		6.0.0
 * @since		5.2.0
 */
public class LibraryManager {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Map<String, Path> libraries = new HashMap<>();
	private static Path libPath;
	private static List<Path> javaClassPath;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private LibraryManager() {
	}
	
	
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
	public static Path getLibrary(String libName) {
		return libraries.get(libName);
	}
	
	/**
	 * Finds directory of application libraries and stores it in {@link #libPath}.
	 * 
	 * @param		appRoot Application root path
	 * 
	 * @implSpec	Lazy initialization
	 */
	public static Path getLibPath()	{
		if (libPath == null)
			initializeLibPath();
		
		return libPath;
	}

	private static void initializeLibPath() {
		libPath = Path.of(
				App.getAppRootPath().toAbsolutePath().toString(), 
				"lib"
		);
	}
	
	public static List<Path> getJavaClassPath()	{
		if (javaClassPath == null)
			initializeJavaClassPath();
		
		return javaClassPath;
	}

	private static void initializeJavaClassPath() {
		javaClassPath = new ArrayList<>();
		
		for (String path : System.getProperty("java.class.path").split("\\" + File.pathSeparator)) {
			javaClassPath.add(Path.of(path));
		}
	}
}
