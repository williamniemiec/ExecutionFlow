package executionFlow.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class FileParserTest {
	@Test
	public void testFile() throws NoSuchAlgorithmException
	{
		String filename = "test_else.java";
		File f = new File(filename);
		try {
			Files.copy(
				f.toPath(), 
				new File(filename+".original").toPath(), 
				StandardCopyOption.REPLACE_EXISTING,
				StandardCopyOption.COPY_ATTRIBUTES
			);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		FileParser fp = new FileParser("test_else.java", null, "test_else_parsed");
		File out = new File(fp.parseFile());
		f.delete();
		out.renameTo(f);
	}
}
