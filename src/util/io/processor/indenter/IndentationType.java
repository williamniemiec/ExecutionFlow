package util.io.processor.indenter;

public enum IndentationType {
	TAB('\t'),
	SPACE(' ');
	
	private char type;
	
	private IndentationType(char type) {
		this.type = type;
	}
	
	public char getType() {
		return type;
	}
}
