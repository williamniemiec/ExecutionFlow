package executionFlow.core.file.parser.files.complex;
public class test_switch
{
	public char switchCaseMethod(char letter)
	{ 
		letter = Character.toUpperCase(letter);
				
		switch (letter) 
		{
			case 'Á':
			case 'À':
			case 'Ã':
			case 'Â':
				letter = 'A';
				break;
			case 'É':
			case 'È':
			case 'Ê':
				letter = 'E';
				break;
			case 'Ì':
			case 'Í':
			case 'Î':
				letter = 'I';
				break;
			case 'Ò':
			case 'Ó':
			case 'Ô':
			case 'Õ':
				letter = 'O';
				break;
			case 'Ú':
			case 'Ù':
			case 'Û':
				letter = 'U';
				break;
		}
		
		return letter;
	}
}