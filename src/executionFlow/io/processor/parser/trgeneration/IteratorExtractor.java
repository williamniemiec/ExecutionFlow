package executionFlow.io.processor.parser.trgeneration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


public class IteratorExtractor
{
	public static <T> Iterator<T> extractIterator(Collection<T> l)
	{
		return l.iterator();
	}
	
	public static Iterator<Boolean> extractIterator(boolean[] array)
	{
		ArrayList<Boolean> list = new ArrayList<>();
		
		
		for (boolean e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Byte> extractIterator(byte[] array)
	{
		ArrayList<Byte> list = new ArrayList<>();
		
		
		for (byte e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Character> extractIterator(char[] array)
	{
		ArrayList<Character> list = new ArrayList<>();
		
		
		for (char e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Short> extractIterator(short[] array)
	{
		ArrayList<Short> list = new ArrayList<>();
		
		
		for (short e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Integer> extractIterator(int[] array)
	{
		ArrayList<Integer> list = new ArrayList<>();
		
		
		for (int e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Long> extractIterator(long[] array)
	{
		ArrayList<Long> list = new ArrayList<>();
		
		
		for (long e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Float> extractIterator(float[] array)
	{
		ArrayList<Float> list = new ArrayList<>();
		
		
		for (float e : array)
			list.add(e);
		
		return list.iterator();
	}
	
	public static Iterator<Double> extractIterator(double[] array)
	{
		ArrayList<Double> list = new ArrayList<>();
		
		
		for (double e : array)
			list.add(e);
		
		return list.iterator();
	}
}