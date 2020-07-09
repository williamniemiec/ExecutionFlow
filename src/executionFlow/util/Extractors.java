package executionFlow.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Extracts project dependencies.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0
 * @since		2.0
 */
public class Extractors 
{
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	/**
	 * Extracts maven dependencies.
	 * 
	 * @return		Maven dependencies
	 * 
	 * @implNote 	It will get all JAR files from 
	 * <code>System.getProperty("user.home")\.m2\repository</code>
	 */
	public static List<Path> getMavenDependencies()
	{
		final List<Path> dependencies = new ArrayList<>();
		
		
		try {
			Files.walkFileTree(Path.of(System.getProperty("user.home") + "\\.m2\\repository"), new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
				{
					if (	file.getFileName().toString().endsWith(".jar") || 
							file.getFileName().toString().endsWith(".JAR")	) {
						dependencies.add(file);
					}
					
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) { }

		return dependencies;
	}
}
