package executionFlow.dependency;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * Responsible for gathering project dependencies. 
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		5.2.0
 * @since		2.0.0
 */
public abstract class DependencyManager 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static List<DependencyExtractor> dependencyExtractors = new ArrayList<>();
	private static List<Path> dependencies;
	private static final Path ARGUMENT_FILE = 
			Path.of(System.getProperty("user.home"), ".ef_dependencies.txt");
	
	
	//-------------------------------------------------------------------------
	//		Initialization block
	//-------------------------------------------------------------------------
	static {
		dependencyExtractors.add(new MavenDependencyExtractor());
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Gathers all dependencies in a folder.
	 * 
	 * @throws		IOException If an error occurs when obtaining dependencies 
	 * or generating the argument file
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
			createArgumentFile();
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
	 * 
	 * @param		extractor Dependency extractor
	 */
	public static void register(DependencyExtractor extractor)
	{
		if (dependencyExtractors == null)
			dependencyExtractors = new ArrayList<>();
		
		if (!dependencyExtractors.contains(extractor))
			dependencyExtractors.add(extractor);
	}
	
	/**
	 * Checks whether dependencies have been obtained.
	 * 
	 * @return		If dependencies have been obtained
	 */
	public static boolean hasDependencies()
	{
		return dependencies != null;
	}
	
	/**
	 * Generates argument file of dependencies.
	 * 
	 * @throws		IOException If an error occurs while generating the argument
	 * file
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm#JSWOR-GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	private static void createArgumentFile() throws IOException
	{
		if ((dependencies == null) || (dependencies.size() == 0))
			return;
		
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARGUMENT_FILE.toFile()))) {
			Path dependency;
			int totalDependencies = dependencies.size();
			
			
			bw.write("\"\\");
			bw.newLine();
			
			for (int i=0; i<totalDependencies-1; i++) {
				dependency = dependencies.get(i);
				
				bw.write(dependency.toAbsolutePath().toString());
				bw.write(";");
				bw.newLine();
			}
			
			dependency = dependencies.get(totalDependencies-1);
			bw.write(dependency.toAbsolutePath().toString());
			bw.write("\"");
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets argument file of dependencies.
	 * 
	 * @return		Argument file
	 * 
	 * @see			https://docs.oracle.com/javase/9/tools/java.htm#JSWOR-GUID-4856361B-8BFD-4964-AE84-121F5F6CF111
	 */
	public static Path getArgumentFile()
	{
		return ARGUMENT_FILE;
	}
	
	public static List<Path> getDependencies()
	{
		return dependencies;
	}
}
