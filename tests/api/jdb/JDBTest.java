package api.jdb;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class JDBTest {
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> commands;
	private String testMethodClassSignature;
	private int invocationLine;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public JDBTest() {
		testMethodClassSignature = "api.jdb.testfiles.Calculator";
		invocationLine = 8;
	}

	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void test() throws IOException, InterruptedException {
		JDB jdb = initializeJDB();
		
		String line = stopAfterBreakpoint(jdb);
		assertEquals("> 8    		sum(2, 3);", line);

		jdb.quit();
	}


	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	private JDB initializeJDB() {
		Path workingDirectory = Path.of(".", "bin").toAbsolutePath().normalize();
		Path sourcePath = workingDirectory
				.resolve(Path.of("..", "tests"))
				.normalize();
		List<Path> classpaths = List.of(workingDirectory);
		List<Path> sourcePaths = List.of(
				workingDirectory.relativize(sourcePath)
		);
		
		return new JDB.Builder()
				.workingDirectory(workingDirectory)
				.classPath(classpaths)
				.srcPath(sourcePaths)
				.build();
	}
	
	private String stopAfterBreakpoint(JDB jdb) throws IOException {
		jdb.run().send(buildInitCommand());
		
		String line = jdb.read();
		
		while (!isBreakpoint(line)) {
			line = jdb.read();
			System.out.println(line);
		}
		
		jdb.send("cont");
		
		line = jdb.read();
		System.out.println(line);
		
		return line;
	}
	
	private String[] buildInitCommand() {
		commands = new ArrayList<>();
		
		clearBreakpoints();
		initializeBreakpoint();
		initializeRunClass();
		
		return commands.toArray(new String[] {});
	}
	
	private void clearBreakpoints() {
		commands.add("clear");
	}
	
	private void initializeRunClass() {
		StringBuilder command = new StringBuilder();
		
		command.append("stop at");
		command.append(" ");
		command.append(testMethodClassSignature);
		command.append(":");
		command.append(invocationLine);
		
		commands.add(command.toString());
	}

	private void initializeBreakpoint() {
		StringBuilder command = new StringBuilder();
	
		command.append("run");
		command.append(" ");
		command.append(testMethodClassSignature);
		
		commands.add(command.toString());
	}
	
	private boolean isBreakpoint(String line) {
		return line.contains("Breakpoint");
	}
}
