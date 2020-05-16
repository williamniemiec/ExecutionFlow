package builderPattern;

import org.junit.Test;


/**
 * Tests classes that use Builder Pattern.
 */
public class BuilderPatternTest 
{
	@Test
	public void testBuilderPattern()
	{
		Contact contact = new Contact.ContactBuilder()
			.firstName("My first name")
			.lastName("My last name")
			.age(21)
			.email("test@gmail.com")
			.build();
		
		contact.print();
	}
}
