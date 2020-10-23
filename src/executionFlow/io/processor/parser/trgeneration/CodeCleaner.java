package executionFlow.io.processor.parser.cleanup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import executionFlow.util.DataUtil;


/**
 * Processes java file by making the following modifications:
 * <ul>
 * 	<li>Eliminates comments</li>
 * 	<li>Breaks code blocks contained within curly braces</li>
 * 	<li>Converts for each and for loops to while loop (disabled)</li>
 * 	<li>Separates case clauses</li>
 * 	<li>Breaks compound clauses</li>
 * </ul>
 * 
 * @author		Murilo Wolfart
 * @see			https://bitbucket.org/mwolfart/trgeneration/src/master/
 */
public class Cleanup 
{
	//-------------------------------------------------------------------------
	//		Attributes
	//-------------------------------------------------------------------------
	private List<String> sourceCode;
	private List<Integer> emptyLines;
	private List<Map<Integer, List<Integer>>> lineMappings;
	
	
	//-------------------------------------------------------------------------
	//		Constructor
	//-------------------------------------------------------------------------
	public Cleanup(List<String> sourceCode)
	{
		this.sourceCode = sourceCode;
		lineMappings = new ArrayList<Map<Integer, List<Integer>>>();
		emptyLines = new ArrayList<Integer>();
	}
	
	
	//-------------------------------------------------------------------------
	//		Methods
	//-------------------------------------------------------------------------
	public List<String> parse() {
		eliminateComments();
		trimLines();
		removeBlankLines();
		moveOpeningBraces();
		moveCodeAfterOpenedBrace();
		moveClosingBraces();
		moveCodeAfterClosedBrace();
		trimLines();
//		convertForEachToFor();
//		trimLines();
		separateLinesWithSemicolons();
		trimLines();
		combineMultiLineStatements();
		trimLines();
//		convertForToWhile();
//		trimLines();
		separateCaseStatements();
		trimLines();
		
		return sourceCode;
	}
	
	private void trimLines() {
		for (int i=0; i<sourceCode.size(); i++){
			sourceCode.set(i, sourceCode.get(i).trim());
		}
	}
	
	private void eliminateComments() {		
		for (int i=0; i<sourceCode.size(); i++) {
			int idxSingle = sourceCode.get(i).indexOf("//"); 
			int idxMulti = sourceCode.get(i).indexOf("/*");
			int idx = (idxSingle >= 0 && idxMulti >= 0) ? 
					Math.min(idxSingle, idxMulti) : Math.max(idxSingle, idxMulti);
			
			if (idx == -1) {
				continue;
			} else if (idx == idxSingle) {
				sourceCode.set(i, sourceCode.get(i).substring(0, idx)); 
			} else {
				i = eraseMultiLineComment(i, idx);
			}
		}
	}
		
	private int eraseMultiLineComment(int startLine, int idxStart) {
		String preceding = sourceCode.get(startLine).substring(0, idxStart);
		
		boolean closingBlockFound = false;
		int i = startLine, idxClosing = 0;
		
		while(!closingBlockFound) {
			idxClosing = sourceCode.get(i).indexOf("*/");
			if (idxClosing != -1) {
				closingBlockFound = true;
			} else if (i != startLine) {
				sourceCode.set(i, "");
			}
			i++;
		}
		int endLine = i-1;
		String trailing = sourceCode.get(endLine).substring(idxClosing+2);
		
		if (endLine == startLine) {
			sourceCode.set(startLine, preceding + trailing);
		} else {
			sourceCode.set(startLine, preceding);
			sourceCode.set(endLine, trailing);
		}
		
		return endLine;
	}
	
	private void removeBlankLines() {		
		int blankLines = 0;
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < sourceCode.size(); i++) {
			if (sourceCode.get(i).equals("")) {
				blankLines++;
				emptyLines.add(i);
			}
			if (sourceCode.get(i).equals("{")) {
				emptyLines.add(i);
			}
			// if first line of file is blank, point to 0.
			int targetLineId = Math.max(i-blankLines, 0);
			mapping.put(i, initArray(targetLineId));
		}
		
		lineMappings.add(mapping);
		sourceCode.removeAll(Collections.singleton(""));
	}	
	
	//move opening braces on their own line to the previous line
	// Note: curly bracket must be alone
	private void moveOpeningBraces() {
		int numRemovedLines = 0;
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();

		for (int i=0; i<sourceCode.size(); i++){
			int oldLineId = i+numRemovedLines;
			
			if (sourceCode.get(i).contains("catch(Throwable _"))
				continue;
			
			if (sourceCode.get(i).equals("{")){
				sourceCode.set(i-1, sourceCode.get(i-1) + "{");
				
				mapping.put(oldLineId, initArray(i-1)); 
				numRemovedLines++;
				
				sourceCode.remove(i);
				i--;
			} else {
				mapping.put(oldLineId, initArray(i));
			}
		}

		lineMappings.add(mapping);
	}

	//move any code after an opening brace to the next line
	private void moveCodeAfterOpenedBrace() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<sourceCode.size(); i++) {
			int oldLineId = i-numAddedLines;

			if (sourceCode.get(i).contains("catch(Throwable _"))
				continue;
			
			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			
			// find brace and check if there is code after
			int idx = Helper.getIndexOfReservedChar(sourceCode.get(i), "{");
			boolean hasCodeAfterBrace = (idx > -1 
					&& idx < sourceCode.get(i).length()-1);
			
			if (hasCodeAfterBrace){ 
				String preceding = sourceCode.get(i).substring(0, idx+1);
				String trailing = sourceCode.get(i).substring(idx+1);
				sourceCode.add(i+1, trailing); //insert the text right of the { as the next line
				sourceCode.set(i, preceding); //remove the text right of the { on the current line
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines++;
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}
		}
		
		lineMappings.add(mapping);
	}
	
	//move closing braces NOT starting a line to the next line
	private void moveClosingBraces() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<sourceCode.size(); i++){
			int oldLineId = i-numAddedLines;

			if (sourceCode.get(i).contains("catch(Throwable _"))
				continue;
			
			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			
			int idx = Helper.getIndexOfReservedChar(sourceCode.get(i), "}"); 
			if (idx > 1) { //this means the } is not starting a line
				String trailing = sourceCode.get(i).substring(idx);
				String preceding = sourceCode.get(i).substring(0, idx);
				sourceCode.add(i+1, trailing); //insert the text starting with the } as the next line
				sourceCode.set(i, preceding); //remove the text starting with the } on the current line
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines++;
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}
		}

		lineMappings.add(mapping);
	}
	
	//move any code after a closing brace to the next line
	private void moveCodeAfterClosedBrace() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<sourceCode.size(); i++){
			int oldLineId = i-numAddedLines;

			if (sourceCode.get(i).contains("catch(Throwable _"))
				continue;
			
			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			
			int idx = Helper.getIndexOfReservedChar(sourceCode.get(i), "}"); 
			if (idx > -1 && sourceCode.get(i).length() > 1) { //this means there is text after the {
				String trailing = sourceCode.get(i).substring(idx+1);
				String preceding = sourceCode.get(i).substring(0, idx+1);

				sourceCode.add(i+1, trailing); //insert the text right of the { as the next line
				sourceCode.set(i, preceding); //remove the text right of the { on the current line			
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines++;
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}
		}
		
		lineMappings.add(mapping);
	}

	//Separate sourceCode with containing semicolons except at the end
	private void separateLinesWithSemicolons() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i < sourceCode.size(); i++){
			int oldLineId = i-numAddedLines;
			List<Integer> targetLinesIds = new ArrayList<Integer>();
			List<String> statements = initArray(sourceCode.get(i).split(";"));
			
			if (sourceCode.get(i).contains("catch(Throwable _"))
				continue;
			
			// Temporary (it will be removed in the future)
			if (sourceCode.get(i).matches("[\\s\\t]*for[\\s\\t]*\\(.*"))
				continue;
			
			targetLinesIds.add(i);
			if (statements.size() > 1) {
				boolean lineEndsWithSemicolon = sourceCode.get(i).matches("^.*;$");
				sourceCode.set(i, statements.get(0) + ";");
				
				for (int j=1; j < statements.size(); j++){
					String pause = (j == statements.size()-1 && !lineEndsWithSemicolon ? "" : ";");
					sourceCode.add(i+j, statements.get(j) + pause);
					targetLinesIds.add(i+j);
				}
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines += statements.size()-1;
				i += statements.size()-1;	// can skip what we altered already
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}			
		}
		
		lineMappings.add(mapping);
	}
	
	private void combineMultiLineStatements() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int removedLines = 0;

		for (int i=0; i < sourceCode.size(); i++) {
			mapping.put(i+removedLines, initArray(i));
			String curLine = sourceCode.get(i);
			
			if (sourceCode.get(i).contains("catch(Throwable _"))
				continue;
			
			while (!Helper.lineContainsReservedChar(curLine, ";")
					&& !Helper.lineContainsReservedChar(curLine, "{") 
					&& !Helper.lineContainsReservedChar(curLine, "}")
					&& !((Helper.lineContainsReservedWord(curLine, "case") || Helper.lineContainsReservedWord(curLine, "default"))
							&& Helper.lineContainsReservedChar(curLine, ":"))
					){
				String separator = (curLine.charAt(curLine.length()-1) != ' '
									&& sourceCode.get(i+1).charAt(0) != ' ' ? " " : "");
				sourceCode.set(i, curLine + separator + sourceCode.get(i+1));
				sourceCode.remove(i+1);
				
				removedLines++;
				mapping.put(i+removedLines, initArray(i));
				curLine = sourceCode.get(i);
			}
		}
		
		lineMappings.add(mapping);
	}

	//separate case statements with next line
	private void separateCaseStatements() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<sourceCode.size(); i++){
			int oldLineId = i-numAddedLines;

			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			mapping.put(oldLineId, targetLinesIds);
			
			if (sourceCode.get(i).matches("^\\b(case|default)\\b.*:.*")){
				int idx = Helper.getIndexOfReservedChar(sourceCode.get(i), ":"); // TODO test if it works in all situations
				
				if (sourceCode.get(i).substring(idx+1).matches("[ \t]*\\{[ \t]*")) {
					continue;
				}
				
				if (idx < sourceCode.get(i).length()-1){
					sourceCode.add(i+1, sourceCode.get(i).substring(idx+1));
					sourceCode.set(i, sourceCode.get(i).substring(0, idx+1));
					numAddedLines++;
				}
			}
		}
		lineMappings.add(mapping);
	}
	
	//turn for loops into while loops
	private void convertForToWhile() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		List<Integer> loopsClosingLines = new ArrayList<Integer>();
		
		for (int i=0; i<sourceCode.size(); i++){			
			if (sourceCode.get(i).matches("^for.+$")){
				int depth = loopsClosingLines.size();
				int closingLine = findEndOfBlock(i+3);
				
				//move the initialization before the loop
				mapping.put(i+depth, initArray(i));
				int idx = sourceCode.get(i).indexOf("(");
				sourceCode.add(i, /* "%forcenode%" + */ sourceCode.get(i).substring(idx+1));
				i++; //adjust for insertion
				
				//move the iterator to just before the close
				mapping.put(i+1+depth, initArray(closingLine-1));
				idx = sourceCode.get(i+2).lastIndexOf(")");
				sourceCode.add(closingLine+1, /* "%forcenode%" + */ sourceCode.get(i+2).substring(0, idx) + ";");
				sourceCode.remove(i+2); //remove old line
				
				//replace for initialization with while
				mapping.put(i+depth, initArray(i));
				String testStatement = sourceCode.get(i+1).substring(0, sourceCode.get(i+1).length()-1).trim();
				sourceCode.set(i, "while (" + testStatement + "){");System.out.println("TEST: "+testStatement);
				sourceCode.remove(i+1); //remove old (test) line
				
				loopsClosingLines.add(closingLine);
			} else {
				int depth = loopsClosingLines.size();
				if (depth > 0 && i == loopsClosingLines.get(depth-1) - 1) {
					loopsClosingLines.remove(loopsClosingLines.size()-1);
				} else {
					mapping.put(i+depth, initArray(i));
				}
			}
		}
		
		lineMappings.add(mapping);
	}
	
	private void convertForEachToFor() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int addedLines = 0;
		
		for (int i=0; i<sourceCode.size(); i++){			
			if (sourceCode.get(i).matches("^for.+$")
					&& Helper.lineContainsReservedChar(sourceCode.get(i), ":")) {
				List<String> forEachInformation = extractForEachInfo(sourceCode.get(i));
				String type = forEachInformation.get(0);
				String varName = forEachInformation.get(1);
				String setName = forEachInformation.get(2);
				
				mapping.put(i - addedLines, new ArrayList<>(Arrays.asList(i, i+1)));
				sourceCode.set(i, "for (Iterator<" + type + "> it = " + setName + ".iterator(); it.hasNext(); ){");
				sourceCode.add(i+1, type + " " + varName + " = it.next();");
				addedLines++;
				i++;
			} else {
				mapping.put(i - addedLines, initArray(i));
			}
		}
		
		lineMappings.add(mapping);
	}
	
	// given a line containing a for each statement, collect the necessary info
	private List<String> extractForEachInfo(String line) {
		String buffer = "";
		List<String> info = new ArrayList<>();
		
		int i;
		for(i = 3; i < line.length() && info.size() < 2; i++) {
			if (line.charAt(i) != '(' && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
				buffer += line.charAt(i);
			} else if ((line.charAt(i) == ' ' || line.charAt(i) == '\t') && buffer.length() > 0) {
				info.add(buffer);
				buffer = "";
			}
		}
		
		while (line.charAt(i) == ':' || line.charAt(i) == ' ' || line.charAt(i) == '\t') i++;
		int start = i;
		int end = line.length() - 1;
		while (line.charAt(end) == '{' || line.charAt(end) == ' ' || line.charAt(end) == '\t') end--;
		info.add(line.substring(start, end));
		
		return info;
	}
	
	private int findEndOfBlock(int startingLine) {
		int curLineId = startingLine;
		int closingLine = -1;
		int depth = 0;
		
		while (curLineId < sourceCode.size() && closingLine == -1) {
			String curLine = sourceCode.get(curLineId);
			if (Helper.lineContainsReservedChar(curLine, "{")) {
				depth++;
			} else if (Helper.lineContainsReservedChar(curLine, "}") && depth > 0) {
				depth--;
			} else if (Helper.lineContainsReservedChar(curLine, "}")) {
				closingLine = curLineId;
			}
			curLineId++;
		}

		if (closingLine == -1){
			System.err.println("Braces are not balanced");
			System.err.println("When trying to find end of block starting at line " + (startingLine+1));
			System.err.println("Line content: " + sourceCode.get(startingLine));
			System.exit(2);
		}
		
		return closingLine;
	}
	
	private <T> ArrayList<T> initArray(T firstElement) {
		return new ArrayList<T>(Arrays.asList(firstElement));
	}
	
	private <T> ArrayList<T> initArray(T[] elements) {
		return new ArrayList<T>(Arrays.asList(elements));
	}
	
	
	//-------------------------------------------------------------------------
	//		Getters
	//-------------------------------------------------------------------------
	/**
	 * Gets the mapping of the original file with the modified file.
	 * 
	 * @return		Mapping with the following format:
	 * <ul>
	 * 	<li><b>Key:</b> Original source file line</li>
	 * 	<li><b>Value:</b> Modified source file line</li>
	 * </ul>
	 */
	public Map<Integer, Integer> getMapping()
	{
		Map<Integer, Integer> mapping = new HashMap<>();
		Set<Integer> updated = new HashSet<>();
		

		// Gets first line change
		for (Map.Entry<Integer, List<Integer>> lm : lineMappings.get(0).entrySet()) {
			mapping.put(lm.getKey(), lm.getValue().get(0));
		}
		
		// Updates line changes from the second
		for (int i=1; i<lineMappings.size(); i++) {
			// For each mapping
			for (Map.Entry<Integer, List<Integer>> lm : lineMappings.get(i).entrySet()) {
				// If there is a value in the mapping with the current key
				if (mapping.containsValue(lm.getKey())) {
					// Updates this value with the value contained in the current key
					for (Integer key : DataUtil.<Integer, Integer>findKeyFromValue(mapping, lm.getKey())) {
						if (!updated.contains(key)) {
							mapping.put(key, lm.getValue().get(0));
							updated.add(key);
						}
					}
				}
			}
			
			// Resets update set
			updated.clear();
		}
		
		return mapping;
	}
	
	public List<Map<Integer, List<Integer>>> getLineMappings()	{
		return lineMappings;
	}
}
