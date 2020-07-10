package executionFlow.dependencies;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import executionFlow.util.DataUtils;


/**
 * Responsible for gathering project dependencies. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public abstract class DependencyManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static List<DependencyExtractor> dependencyExtractors;
	private static List<Path> dependencies;
	private static final Path PATH_DEPENDENCIES = 
			Path.of(System.getProperty("user.home"), ".ef_dependencies");
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Gathers all dependencies in a folder.
	 * 
	 * @throws		IOException If an error occurs when obtaining a dependency
	 * @throws 		IllegalArgumentException If no dependency extractor has 
	 * been registered
	 */
	public static void fetch() throws IOException
	{
		if (dependencyExtractors == null || dependencyExtractors.isEmpty())
			throw new IllegalArgumentException("There must be at least one registered dependency extractor");
		
		if (dependencies == null) {
			dependencies = new ArrayList<>();
			pull();
			DataUtils.putFilesInFolder(dependencies, PATH_DEPENDENCIES);
		}
	}
	
	/**
	 * Gets path of all dependencies projects.
	 */
	private static void pull()
	{
		for (DependencyExtractor de : dependencyExtractors) {
			dependencies.addAll(de.extract());
		}
	}
	
	/**
	 * Registers a dependency extractor.
	 */
	public static void register(DependencyExtractor extractor)
	{
		if (dependencyExtractors == null)
			dependencyExtractors = new ArrayList<>();
		
		if (!dependencyExtractors.contains(extractor))
			dependencyExtractors.add(extractor);
	}
	
	/**
	 * Checks whether dependencies for Maven and Gradle projects have been
	 * obtained.
	 * 
	 * @return		If dependencies for Maven and Gradle projects have been
	 * obtained
	 */
	public static boolean hasDependencies()
	{
		return dependencies != null;
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	public static Path getPath()
	{
		return PATH_DEPENDENCIES;
	}
	
	public static List<Path> getDependencies()
	{
		return dependencies;
	}
}
