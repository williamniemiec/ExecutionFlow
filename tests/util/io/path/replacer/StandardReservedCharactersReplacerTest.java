package util.io.path.replacer;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StandardReservedCharactersReplacerTest {
	
	//-----------------------------------------------------------------------
	//		Attributes
	//-----------------------------------------------------------------------
	private static final Replacer STANDARD_REPLACER;
	
	
	//-----------------------------------------------------------------------
	//		Initialization blocks
	//-----------------------------------------------------------------------
	static {
		STANDARD_REPLACER = ReservedCharactersReplacerFactory.getStandardReplacer();
	}

	
	//-----------------------------------------------------------------------
	//		Tests
	//-----------------------------------------------------------------------
	@Test
	public void testReplace() {
		assertEquals("()--;'-++", STANDARD_REPLACER.replace("<>/\\:\"|?*"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testReplaceNullString() {
		STANDARD_REPLACER.replace(null);
	}
	
	@Test
	public void testReplaceWithEmptyString() {
		assertEquals("", STANDARD_REPLACER.replace(""));
	}
	
	@Test
	public void testReplaceAlongWithNonReservedCharacter() {
		assertEquals("(hello world)", STANDARD_REPLACER.replace("<hello world>"));
	}
}
