package util.data.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SessionTest {
	
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static final String FILENAME;
	private static final File WORKING_DIRECTORY;

	
	//-----------------------------------------------------------------------
	//		Initialization blocks
	//-----------------------------------------------------------------------
	static {
		FILENAME = "session-test";
		WORKING_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));
	}
	
	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@Test
	public void testCreateSession() {
		new Session(FILENAME, WORKING_DIRECTORY);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCreateSessionWithEmptyFilename() {
		new Session("", WORKING_DIRECTORY);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateSessionWithNullFilename() {
		new Session(null, WORKING_DIRECTORY);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateSessionWithNullWorkingDirectory() {
		new Session(FILENAME, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateSessionWithNullWorkingDirectoryAndFilename() {
		new Session(null, null);
	}
	
	@Test
	public void testSaveAndRead() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		Integer value = Integer.valueOf(0);
		
		s.save("key", value);
		
		assertEquals(value, s.read("key"));
		
		s.destroy();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullKey() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		Integer value = Integer.valueOf(0);
		
		s.save(null, value);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullValue() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
	
		s.save("key", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullKeyAndValue() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);

		s.save(null, null);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testReadFromNonexistentSession() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		
		s.read("key");
	}
	
	@Test(expected = IllegalStateException.class)
	public void testRemoveFromNonexistentSession() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		
		s.remove("key");
	}
	
	@Test(expected = IllegalStateException.class)
	public void testHasKeyFromNonexistentSession() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		
		s.hasKey("key");
	}
	
	@Test
	public void testSaveAndRemove() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		Integer value = Integer.valueOf(0);
		
		s.save("key", value);
		s.remove("key");
		
		assertFalse(s.hasKey("key"));
		
		s.destroy();
	}
	
	@Test
	public void testHasKey() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		Integer value = Integer.valueOf(0);
		
		s.save("key", value);
		
		assertTrue(s.hasKey("key"));
		
		s.destroy();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHasNullKey() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
	
		s.hasKey(null);
	}
	
	@Test
	public void testExists() throws IOException {
		Session s = new Session(FILENAME, WORKING_DIRECTORY);
		Integer value = Integer.valueOf(0);
		
		s.save("key", value);
		
		assertTrue(s.exists());
		
		s.destroy();
	}
	
	@Test
	public void testSaveAndReadShared() throws IOException {
		Integer value = Integer.valueOf(0);
		
		Session.saveShared("key", value);
		
		assertEquals(value, Session.readShared("key"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullKeyShared() throws IOException {
		Integer value = Integer.valueOf(0);
		
		Session.saveShared(null, value);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullValueShared() throws IOException {
		Session.saveShared("key", null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSaveNullKeyAndValueShared() throws IOException {
		Session.saveShared(null, null);
	}
	
	@Test
	public void testSaveAndRemoveShared() throws IOException {
		Integer value = Integer.valueOf(0);
		
		Session.saveShared("key", value);
		Session.removeShared("key");
		
		assertFalse(Session.hasKeyShared("key"));
	}
	
	@Test
	public void testHasKeyShared() throws IOException {
		Integer value = Integer.valueOf(0);
		
		Session.saveShared("key", value);
		
		assertTrue(Session.hasKeyShared("key"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testHasNullKeyShared() throws IOException {
		Session.hasKeyShared(null);
	}
}
