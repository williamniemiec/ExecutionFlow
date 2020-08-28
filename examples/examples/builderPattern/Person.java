package examples.builderPattern;


/**
 * Example class that uses Builder Pattern. It is a class about a person.
 */
public class Person 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private String firstName;
	private String lastName;
	private String email;
	private int age;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
 	private Person(String firstName, String lastName, String email, int age) 
	{
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.age = age;
	}
	
	
	//-------------------------------------------------------------------------
	//		Builder
	//-------------------------------------------------------------------------
	public static class PersonBuilder
	{
		private String firstName;
		private String lastName;
		private String email;
		private int age;
		
 		public PersonBuilder firstName(String firstName)
		{
			this.firstName = firstName;
			return this;
		}
		
 		public PersonBuilder lastName(String lastName)
		{
			this.lastName = lastName;
			return this;
		}
		
 		public PersonBuilder email(String email)
		{
			this.email = email;
			return this;
		}
		
 		public PersonBuilder age(int age)
		{
			this.age = age;
			return this;
		}
		
 		public Person build()
		{
			return new Person(firstName, lastName, email, age);
		}
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
 	public void print()
	{
		System.out.println("First name: "+firstName);
		System.out.println("Last name: "+lastName);
		System.out.println("Email: "+email);
		System.out.println("Age: "+age);
	}
}


