package executionFlow.dependency;

import java.nio.file.Path;
import java.util.List;


/**
 * Extracts project dependencies.
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public interface DependencyExtractor 
{
	/**
	 * Extracts dependencies from a project.
	 * 
	 * @return		Dependencies list
	 */
	public List<Path> extract();
}
