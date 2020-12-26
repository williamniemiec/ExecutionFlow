package examples.innerClass;


/**
 * Example class that contains a inner class.
 */
public class OuterClass 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String text
	;
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public OuterClass(String text) 
	{
		this.text = text;
	}
	

	//-------------------------------------------------------------------------
	//		Inner class
	//-------------------------------------------------------------------------
	public class InnerClass
	{
		private String text;
		
		
		public InnerClass(String text) 
		{
			this.text = text;
		}
		
		
		public String getText()
		{
			return this.text;
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public String getText()
	{
		return this.text;
	}
}
