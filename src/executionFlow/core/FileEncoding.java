package executionFlow.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Contains all supported file encodings.
 */
public enum FileEncoding 
{
	//-------------------------------------------------------------------------
	//		Enumerations
	//-------------------------------------------------------------------------
	UTF_8("UTF8", StandardCharsets.UTF_8), 
	ISO_8859_1("ISO-8859-1", StandardCharsets.ISO_8859_1);
	
	
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String text;
	private Charset charset;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private FileEncoding(String text, Charset charset)
	{
		this.text = text;
		this.charset = charset;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public String getText()
	{
		return text;
	}
	
	public Charset getStandardCharset()
	{
		return charset;
	}
}
