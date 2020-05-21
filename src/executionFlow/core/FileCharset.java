package executionFlow.core;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum FileCharset 
{
	UTF_8("UTF8", StandardCharsets.UTF_8), ISO_8859_1("ISO-8859-1", StandardCharsets.ISO_8859_1);
	
	private String text;
	private Charset charset;
		
	private FileCharset(String text, Charset charset)
	{
		this.text = text;
		this.charset = charset;
	}
	
	public String getText()
	{
		return text;
	}
	
	public Charset getStandardCharset()
	{
		return charset;
	}
}
