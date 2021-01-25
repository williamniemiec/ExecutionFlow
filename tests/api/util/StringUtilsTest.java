package api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testImplodeTwoItems() {
		List<String> items = List.of("hello", "world", "!");
		
		assertEquals("hello-world-!", StringUtils.implode(items, "-"));
	}
	
	@Test
	public void testImplodeEmptyList() {
		List<String> items = List.of();
		
		assertEquals("", StringUtils.implode(items, "-"));
	}
	
	@Test
	public void testImplodeTwoItemsWithEmptyDelimiter() {
		List<String> items = List.of("hello", "world", "!");
		
		assertEquals("helloworld!", StringUtils.implode(items, ""));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testImplodeNullList() {
		StringUtils.implode(null, "-");
		fail("IllegalArgumentException should be thrown");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testImplodeNullDelimiter() {
		List<String> items = List.of("hello", "world", "!");
		
		StringUtils.implode(items, null);
		fail("IllegalArgumentException should be thrown");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testImplodeNullListAndDelimiter() {
		StringUtils.implode(null, null);
		fail("IllegalArgumentException should be thrown");
	}
}
