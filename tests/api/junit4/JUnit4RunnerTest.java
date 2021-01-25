package api.junit4;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.Test;

public class JUnit4RunnerTest {

	@Test
	public void testRunStringUtilsTest() throws IOException, InterruptedException {
		Path workingDirectory = Path.of(".", "bin").toAbsolutePath().normalize();
		Path stringUtilsClassPath = workingDirectory.resolve(
				Path.of("api", "util", "StringUtilsTest.class")
		);
		List<Path> classpaths = List.of(
				workingDirectory.resolve(stringUtilsClassPath)
		);

		JUnit4Runner junit4Runner = new JUnit4Runner.Builder()
				.workingDirectory(workingDirectory)
				.classPath(classpaths)
				.classSignature("api.util.StringUtilsTest")
				.displayVersion(true)
				.build();
		
		junit4Runner.run();
	}
}
