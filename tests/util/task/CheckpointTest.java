package util.task;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Test;

public class CheckpointTest {

	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private static Checkpoint checkpoint;
	
	
	//-------------------------------------------------------------------------
	//		Initialization blocks
	//-------------------------------------------------------------------------
	static {
		Path tmpDir = Path.of(System.getProperty("java.io.tmpdir"));
		
		checkpoint = new Checkpoint(tmpDir, "cp-test");
	}
	
	//-------------------------------------------------------------------------
	//		Test hooks
	//-------------------------------------------------------------------------
	@After
	public void afterEachTest() throws IOException {
		if (checkpoint.exists())
			checkpoint.disable();
	}
	
	
	//-------------------------------------------------------------------------
	//		Tests
	//-------------------------------------------------------------------------
	@Test
	public void testEnable() throws IOException {
		checkpoint.enable();
		
		assertTrue(checkpoint.isEnabled());
	}

	@Test
	public void testDisable() throws IOException {
		checkpoint.enable();
		checkpoint.disable();
		
		assertFalse(checkpoint.isEnabled());
	}
	
	@Test
	public void testExists() throws IOException {
		checkpoint.enable();
		
		assertTrue(checkpoint.exists());
	}
	
	@Test
	public void testDelete() throws IOException {
		checkpoint.enable();
		checkpoint.disable();
		checkpoint.delete();
		
		assertFalse(checkpoint.exists());
	}
}
