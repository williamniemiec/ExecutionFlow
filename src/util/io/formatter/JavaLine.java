package util.io.formatter;


/**
 * @author		Liam McLeod
 * @see			https://github.com/LiamMcLeod/JavaIndenter
 */
public class JavaLine {

	private String javaVar="";		// Java Code
	private String commentVar="";	// Code Comment
	private int javaLen=0;			// Length of Code

	public JavaLine(String strLine, int bracketCount){

		if (!strLine.equals("")){
		subSplit(strLine, bracketCount);
		}
	}

	private void subSplit(String strLine, int bracketCount){
		int i = 0, x = 0;
		String strIndent="";
		x = strLine.lastIndexOf('"');
		i = strLine.indexOf('/');

		if (i < x){
			i = strLine.indexOf('/', x);
		}
		// Check comment possible
		if (i+1 < strLine.length()){
			//check comment exists
			if (strLine.charAt(i+1) == '/') {
			// .substring split	
				if (strLine.contains("*/")) {
					int idxMultiSingleCommentEnd = strLine.lastIndexOf("*/");
					
					javaVar = strLine.substring(idxMultiSingleCommentEnd+1);
					commentVar = strLine.substring(0, idxMultiSingleCommentEnd+1);
				} 
				else {
					javaVar = strLine.substring(0, i);
					commentVar = strLine.substring(i, strLine.length());
				}
				
				
				// .trim trailing whitespace
				javaVar = javaVar.trim();

				// Check brackets
				if (javaVar.indexOf('{') > -1 || JavaIndenter.getBinFlag() == true){
					bracketCount--;
					//Comment but bracket?
				}
				//indent Code
				strIndent = indentJava(bracketCount);
				javaVar = strIndent + javaVar;
			}
			else {
				//No comment
				javaVar = strLine;
				javaVar = javaVar.trim();
				if ((javaVar.indexOf('{') > -1) && (javaVar.indexOf('}') > -1)){
					// No comments instance of bracket check?
				}
				else {
					if (javaVar.indexOf('{') > -1 || javaVar.contains("case")){
						bracketCount--;
					}
					else if (javaVar.contains("break")){
						bracketCount++;
					}
				}
				// Indent code
				strIndent = indentJava(bracketCount);
				javaVar = strIndent + javaVar;

				commentVar = "";
			}
		}
		else {
			// Comment impossible
			javaVar = strLine;
			javaVar = javaVar.trim();
			if ((javaVar.indexOf('{') > -1) && (javaVar.indexOf('}') > -1)){
				// check instances of bracket on line
			}
			else {
				if (javaVar.indexOf('{') > -1){
					// Check line is bracket?
					bracketCount--;
				}
			}
			// indent code
			strIndent = indentJava(bracketCount);
			javaVar = strIndent + javaVar;

			commentVar = "";
		}

		if (JavaIndenter.getBlockComment() == true){
			// detect \maybe



			javaVar = "";
			strIndent = indentJava(bracketCount);
			javaVar = strIndent + javaVar;
			strLine = strLine.trim();

			commentVar = strLine.substring(0, strLine.length());


		}
	}


	public int getJavaLineLength(){
		int length = javaVar.length();
		javaLen = javaVar.length();
	return length;
	}

    public static String countSpaces(int value){
    	String retVal = "";
    	  for (int x = 0; x < value; x++){
			  retVal += " ";
		  }
    return retVal;
    }

    private static String indentJava(int bracketCount){
    	String strIndent="";
		 for (int y = 0; y < bracketCount; y++){
			 strIndent += "     "; // or /t
	 	 }
	return strIndent;
    }

	public String returnLineWithCommentAt(int index){
		String stringOfSpaces="";
		int value=0;
		String strRetLine;
		//spaces counted here
		//value = index - javaVar.length();
		value = index - javaLen;
		 stringOfSpaces = countSpaces(value); //Replace 1
		 strRetLine = javaVar + stringOfSpaces + commentVar;

	return strRetLine;
	}

	public String toString(){
	return "JavaLine(String strLine, bracketCount)";
	}
}
