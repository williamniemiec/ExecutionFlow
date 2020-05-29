package executionFlow.core.file;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * Contains all supported file encodings.
 * 
 * @author William Niemiec &lt; williamniemiec@hotmail.com &gt;
 * @since 1.4
 * @version 1.4
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
	private String name;
	private Charset charset;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	private FileEncoding(String name, Charset charset)
	{
		this.name = name;
		this.charset = charset;
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public String getName()
	{
		return name;
	}
	
	public Charset getStandardCharset()
	{
		return charset;
	}
}
