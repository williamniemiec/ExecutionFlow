package examples.others;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import examples.others.auxClasses.AuxClass;


public class MultiargsTest 
{
	@Test
	public void methodCallMultiLineArgsTest()
	{
		AuxClass ac = new AuxClass(1);
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), ac.identity(1,2,3,4,5));
	}

	@Test
	public void methodCallMultLineArgsWithBrokenLines()
	{
		AuxClass ac = new AuxClass(1);
		assertEquals(Arrays.asList(1, 2, 3, 4, 5), 
				ac.identity(
						1,
						2,
						3,
						4,
						5)
				);
	}
}