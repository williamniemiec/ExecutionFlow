package util.data.structure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PairTest {

	@Test
	public void testPairConstructor() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		
		assertEquals(Integer.valueOf(1), p.getFirst());
		assertEquals(Integer.valueOf(2), p.getSecond());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPairConstructorFirstNull() {
		Pair<Integer, Integer> p = new Pair<>(null, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPairConstructorSecondNull() {
		Pair<Integer, Integer> p = new Pair<>(null, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPairConstructorFirstAndSecondNull() {
		Pair<Integer, Integer> p = new Pair<>(null, null);
	}
	
	@Test
	public void testPairOf() {
		Pair<Integer, Integer> p = Pair.of(1, 2);
		
		assertEquals(Integer.valueOf(1), p.getFirst());
		assertEquals(Integer.valueOf(2), p.getSecond());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPairOfFirstNull() {
		Pair<Integer, Integer> p = Pair.of(null, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPairOfrSecondNull() {
		Pair<Integer, Integer> p = Pair.of(null, 2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testPairOfFirstAndSecondNull() {
		Pair<Integer, Integer> p = Pair.of(null, null);
	}
	
	@Test
	public void testGetFirst() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		
		assertEquals(Integer.valueOf(1), p.getFirst());
	}
	
	@Test
	public void testGetSecond() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		
		assertEquals(Integer.valueOf(2), p.getSecond());
	}
	
	@Test
	public void testSetFirst() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		p.setFirst(9);
		
		assertEquals(Integer.valueOf(9), p.getFirst());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetFirstNull() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		p.setFirst(null);
	}
	
	@Test
	public void testSetSecond() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		p.setSecond(9);
		
		assertEquals(Integer.valueOf(9), p.getSecond());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetSecondNull() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		p.setSecond(null);
	}
	
	@Test
	public void testEquals() {
		Pair<Integer, Integer> p = new Pair<>(1, 2);
		
		assertEquals(Pair.of(1, 2), p);
	}
}
