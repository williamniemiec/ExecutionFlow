package builderPattern;


/**
 * Example class using Builder Pattern. It is a class about contacts.
 */
public class Contact 
{
	private String firstName;
	private String lastName;
	private String email;
	private int age;
	
	
	private Contact(String firstName, String lastName, String email, int age) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.age = age;
	}
	
	
	static class ContactBuilder
	{
		private String firstName;
		private String lastName;
		private String email;
		private int age;
		
		public ContactBuilder firstName(String firstName)
		{
			this.firstName = firstName;
			return this;
		}
		
		public ContactBuilder lastName(String lastName)
		{
			this.lastName = lastName;
			return this;
		}
		
		public ContactBuilder email(String email)
		{
			this.email = email;
			return this;
		}
		
		public ContactBuilder age(int age)
		{
			this.age = age;
			return this;
		}
		
		public Contact build()
		{
			return new Contact(firstName, lastName, email, age);
		}
	}
	
	public void print()
	{
		System.out.println("First name: "+firstName);
		System.out.println("Last name: "+lastName);
		System.out.println("Email: "+email);
		System.out.println("Age: "+age);
	}
}
