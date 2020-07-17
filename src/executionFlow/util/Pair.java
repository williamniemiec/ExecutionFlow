package executionFlow.util;


/**
 * Represents pairs of two elements. This implementation was based on std::lib
 * (from C++). You can see more here: 
 * {@link https://www.geeksforgeeks.org/pair-in-cpp-stl/}
 * 
 * @param		<T1> First element type
 * @param		<T2> Second element type
 * 
 * @author		William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @version		2.0.0
 * @since		2.0.0
 */
public class Pair<T1, T2> 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	public T1 first;
	public T2 second;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	/**
	 * Creates a pair containing two elements.
	 * 
	 * @param		first First element
	 * @param		second Second element
	 */
	public Pair(T1 first, T2 second)
	{
		this.first = first;
		this.second = second;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	@Override
	public String toString()
	{
		return "Pair[" + first + "," + second + "]";
	}
	
	@Override
	public int hashCode()
	{
		if (first == null) 
			return (second == null) ? 0 : second.hashCode() + 1;
        else if (second == null) 
        	return first.hashCode() + 2;
        else 
        	return first.hashCode() * 31 + second.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null)							{	return false;	}
		if (o.getClass() != this.getClass())	{	return false;	} 
		if (o == this)							{	return true;	}
		
		return	o instanceof Pair && 
				((Pair)o).first.equals(this.first) && 
				((Pair)o).second.equals(this.second);
	}
	
	/**
	 * Creates a pair from two objects.
	 * 
	 * @param		<T1> First element type
	 * @param		<T2> Second element type
	 * @param		first First element
	 * @param		second Second element
	 * 
	 * @return		Pair instance		
	 */
	public static <T1, T2> Pair<T1, T2> of(T1 first, T2 second) 
	{
        return new Pair<T1, T2>(first, second);
    }
}
