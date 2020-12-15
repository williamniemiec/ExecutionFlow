package executionFlow.io.processor.parser.trgeneration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import executionFlow.util.DataUtil;
import executionFlow.util.Pair;
import executionFlow.util.balance.CurlyBracketBalance;


/**
 * @author		Murilo Wolfart
 * @see			https://bitbucket.org/mwolfart/trgeneration/
 */
public class CodeCleaner {
	// Processed source code
	protected List<String> processedCode;
	// (line mode only) Mappings containing information on processed code x original code
	protected List<Map<Integer, List<Integer>>> lineMappings;
	// (line mode only) List containing lines that originally were empty or only contained comments
	private List<Integer> emptyLines;
	// Debug flag
	private boolean debug;
	
	// TODO make line mode optional in order to avoid unnecessary tasks
	
	public CodeCleaner(boolean debug) {
		lineMappings = new ArrayList<Map<Integer, List<Integer>>>();
		emptyLines = new ArrayList<Integer>();
	}
	
	public void clear() {
		emptyLines.clear();
		lineMappings.clear();
	}
	
	public void setDebug(boolean d) {
		debug = d;
	}
	
	public void cleanupCode(List<String> codeToCleanup) {
		processedCode = codeToCleanup;
		cleanup();
//		addDummyNodes();
		buildFullMap();
		buildReverseFullMap();
		
		if (debug) {
			dumpCode();
			dumpLastMap();
		}
	}
	
	public Map<Integer, List<Integer>> getCleanToOriginalCodeMapping() {
		// returns the last map in the list, which will be the mapping
		//  that contains the clean line id -> original line id map
		//  if the method is called after cleanupCode
		return lineMappings.get(lineMappings.size()-1);
	}
	
	// CURRENT FORMAT CONSTRAINTS:
	// TODO surrounding brackets aren't always necessary if blocks are simple
	//   however, nested blocks without brackets will cause problems.
	private int cleanup() {
		eliminateComments();
		if (debug) System.out.println("CLEANUP: Eliminated comments");
		trimLines();
		//eliminateAnnotations();
		if (debug) System.out.println("CLEANUP: Eliminated annotations");
		trimLines();
		removeBlankLines();
		if (debug) System.out.println("CLEANUP: Removed blank lines");
		// convertTernaries();
		formatBrackets();
		if (debug) System.out.println("CLEANUP: Formatted brackets");
		trimLines();
		convertEmptyForToWhile();
		separateLinesWithSemicolons();
		if (debug) System.out.println("CLEANUP: Separated lines with semicolons");
		trimLines();
		combineMultiLineStatements();
		if (debug) System.out.println("CLEANUP: Combined multi-line statements");
		trimLines();
		addBracketsToBlocks();
		if (debug) System.out.println("CLEANUP: Added brackets to necessary items");
		trimLines();
		convertForEachToFor();
		if (debug) System.out.println("CLEANUP: Converted forEachs to fors");
		trimLines();
		separateLinesWithSemicolons();
		if (debug) System.out.println("CLEANUP: Separated lines with semicolons");
		trimLines();
		convertForToWhile();
		if (debug) System.out.println("CLEANUP: Converted fors to whiles");
		trimLines();
		separateLinesWithSemicolons(); // separating again in case of continues
		if (debug) System.out.println("CLEANUP: Separated lines with semicolons");
		trimLines();
		separateCaseStatements();
		if (debug) System.out.println("CLEANUP: Separated case statements");
		trimLines();
//		prepareTryCatchBlocks();

		if (debug) System.out.println("CLEANUP DONE! Resulting code:");
		if (debug) dumpCode();
		return Defs.success;
	}
	
	private void formatBrackets() {
		// TODO trim lines each step? There are some spaces causing extra lines.
		//  actually, in the functions, do not split if its only spaces and tabs
		moveOpeningBrackets();
		trimLines();
		moveCodeAfterOpenedBracket();
		trimLines();
		moveClosingBrackets();
		trimLines();
		moveCodeAfterClosedBracket();
		// At this point, all opening brackets end a line and all closing brackets are on their own line
	}

	// Trim all lines (remove indents and other leading/trailing whitespace)
	private void trimLines() {
		for (int i=0; i<processedCode.size(); i++) {
			processedCode.set(i, processedCode.get(i).trim());
		}
	}
	
	private void eliminateComments() {
		final String REGEX_LINE_COMMENT = "^(([\\s\\t]*\\/\\/.*)|.*\\/\\/[^;]+)$";
		
		for (int i=0; i<processedCode.size(); i++) {
			if (processedCode.get(i).contains("@") && processedCode.get(i).contains("//"))
				processedCode.set(i, processedCode.get(i).replaceAll("\\/\\/.+", ""));
			
			processedCode.set(i, removeMultiSingleLineComment(processedCode.get(i)));
			int idxLastOpenCurlyBracket = processedCode.get(i).lastIndexOf("{");
			int idxComment = idxLastOpenCurlyBracket == -1 ? -1 : processedCode.get(i).substring(idxLastOpenCurlyBracket).indexOf("//");
			int idxSingle = processedCode.get(i).matches(REGEX_LINE_COMMENT) ? processedCode.get(i).indexOf("//") : -1;
			
			// Removes comment after last open curly bracket
			if (idxSingle == -1 && idxComment != -1) {
				processedCode.set(i, processedCode.get(i).substring(0, idxLastOpenCurlyBracket+1));
			}
			
			int idxMulti = Helper.getIndexOfReservedSymbol(processedCode.get(i), "/\\*"); 
			if (idxMulti < 0 && processedCode.get(i).contains("/*")) {
				idxMulti = processedCode.get(i).indexOf("/*");
			}
			
			int idx = (idxSingle >= 0 && idxMulti >= 0) ? 
					Math.min(idxSingle, idxMulti) : Math.max(idxSingle, idxMulti);
					
					
			if (idx == -1) {
				continue;
			} else if (idx == idxSingle) {
				processedCode.set(i, processedCode.get(i).substring(0, idx));
			} else {
				i = eraseMultiLineComment(i, idx);
			}
		}
	}
		
	private String removeMultiSingleLineComment(String str) {
		int idxStart = str.indexOf("/*");
		int idxEnd = str.indexOf("*/");
		
		if (idxStart >= 0 && idxEnd > 0)
			str = str.substring(0, idxStart) + str.substring(idxEnd+2);
		
		return str;
	}

	private int eraseMultiLineComment(int startLine, int idxStart) {
		String preceding = processedCode.get(startLine).substring(0, idxStart);
		
		boolean closingBlockFound = false;
		int i = startLine, idxClosing = 0;
		
		while(!closingBlockFound) {
			idxClosing = processedCode.get(i).indexOf("*/");
			if (idxClosing != -1) {
				closingBlockFound = true;
			} else if (i != startLine) {
				processedCode.set(i, "");
			}
			i++;
		}
		int endLine = i-1;
		String trailing = processedCode.get(endLine).substring(idxClosing+2);
		
		if (endLine == startLine) {
			processedCode.set(startLine, preceding + trailing);
		} else {
			processedCode.set(startLine, preceding);
			processedCode.set(endLine, trailing);
		}
		
		return endLine;
	}
	
	private void eliminateAnnotations() {
		for (int i=0; i<processedCode.size(); i++) {
			boolean executionFlowAnnotation = processedCode.get(i).contains("executionFlow.");
			boolean testAnnotation = processedCode.get(i).contains("Test");
			boolean supressAnnotation = processedCode.get(i).contains("@SuppressWarnings");
			
			if (processedCode.get(i).matches("^@.*") && !executionFlowAnnotation && !testAnnotation && !supressAnnotation) {
				processedCode.set(i, "");
			}
		}		
	}
	
	private void removeBlankLines() {		
		int blankLines = 0;
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		
		for(int i = 0; i < processedCode.size(); i++) {
			if (processedCode.get(i).equals("")) {
				blankLines++;
				emptyLines.add(i);
			}
			if (processedCode.get(i).equals("{")) {
				emptyLines.add(i);
			}
			// if first line of file is blank, point to 0.
			int targetLineId = Math.max(i-blankLines, 0);
			mapping.put(i, Helper.initArray(targetLineId));
		}
		
		lineMappings.add(mapping);
		processedCode.removeAll(Collections.singleton(""));
	}
	
	private void addBracketsToBlocks() {
		boolean mustReformatBrackets = false;
		for (int i = 0; i < processedCode.size(); i++) {
			String curLine = processedCode.get(i);
			
			if (curLine.contains("catch(Throwable _"))
				continue;
			
			int idxWord = Helper.getIndexOfReservedString(curLine, "(while|if|for|else)");
			if (idxWord == -1) continue;
			
			int idxOpen = Helper.getIndexAfterPosition(curLine, "(", idxWord);
			int idxClose = Helper.findMatchingParenthesis(curLine, idxOpen);
			int idx;
			
			if (idxOpen == -1 && idxClose == -1) {
				idx = idxWord+3; // else
			} else if (idxClose == -1) {
				i += 2;
				curLine = processedCode.get(i);
				idx = Helper.findMatchingParenthesis(curLine, -1); // for
			} else idx = idxClose;
			
			idx++;
			
			while (idx < curLine.length() && (curLine.charAt(idx) == ' ' || curLine.charAt(idx) == '\t')) {
				idx++;
			}
			
			if (idx < curLine.length() && (curLine.charAt(idx) != '{' & curLine.charAt(idx) != ';')) {
				int blockStart = idx;
				int blockEnd = Helper.getIndexAfterPosition(curLine, ";", blockStart) + 1;
				
				String newLine = curLine.substring(0, blockStart)
						+ "{ " + curLine.substring(blockStart, blockEnd)
						+ " }";	
				processedCode.set(i, newLine);
				mustReformatBrackets = true;
			}
		}
		if (mustReformatBrackets) {
			formatBrackets();
		}
	}
	
	// convert ternary operations into ifs
	// ifs are inserted in the same lines, so no mapping needs to be done
	
	// TODO since this is a hard operation to make, I'm leaving it for later
	/*
	private void convertTernaries() {
		int a = 1 > 2 ? 3 : 4;
		for(int i = 0; i < sourceCode.size(); i++) {
			String curLine = sourceCode.get(i);
			if (Helper.lineContainsReservedWord(curLine, "(.*?[^\\{\\}]*:.*)"));
		}
	}
	*/
	
	// Move opening brackets on their own line to the previous line
	// Note: curly bracket must be alone
	private void moveOpeningBrackets() {
		int numRemovedLines = 0;
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();

		for (int i=0; i<processedCode.size(); i++) {
			int oldLineId = i+numRemovedLines;
			
			if (processedCode.get(i).equals("{") && !processedCode.get(i).contains("catch(Throwable _")) {
				processedCode.set(i-1, processedCode.get(i-1) + "{");
				
				mapping.put(oldLineId, Helper.initArray(i-1)); 
				numRemovedLines++;
				
				processedCode.remove(i);
				i--;
			} else {
				mapping.put(oldLineId, Helper.initArray(i));
			}
		}

		lineMappings.add(mapping);
	}

	// Move any code after an opening bracket to the next line
	private void moveCodeAfterOpenedBracket() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<processedCode.size(); i++) {
			int oldLineId = i-numAddedLines;

			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			
			// find bracket and check if there is code after
			int idx = Helper.getIndexOfReservedChar(processedCode.get(i), "{");
			boolean hasCodeAfterBracket = (idx > -1 
					&& idx < processedCode.get(i).length()-1);
			
			if (hasCodeAfterBracket && !processedCode.get(i).contains("catch(Throwable _")) { 
				String preceding = processedCode.get(i).substring(0, idx+1);
				String trailing = processedCode.get(i).substring(idx+1);
				processedCode.add(i+1, trailing); //insert the text right of the { as the next line
				processedCode.set(i, preceding); //remove the text right of the { on the current line
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines++;
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}
		}
		
		lineMappings.add(mapping);
	}
	
	// Move closing brackets NOT starting a line to the next line
	private void moveClosingBrackets() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<processedCode.size(); i++) {
			int oldLineId = i-numAddedLines;

			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			
			int idx = Helper.getIndexOfReservedChar(processedCode.get(i), "}"); 
			if (idx > 1 && !processedCode.get(i).contains("catch(Throwable _")) { // this means the } is not starting a line
				String trailing = processedCode.get(i).substring(idx);
				String preceding = processedCode.get(i).substring(0, idx);
				processedCode.add(i+1, trailing); // insert the text starting with the } as the next line
				processedCode.set(i, preceding); // remove the text starting with the } on the current line
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines++;
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}
		}

		lineMappings.add(mapping);
	}
	
	// Move any code after a closing bracket to the next line
	private void moveCodeAfterClosedBracket() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<processedCode.size(); i++) {
			int oldLineId = i-numAddedLines;
			
			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			
			int idx = Helper.getIndexOfReservedChar(processedCode.get(i), "}"); 
			if (idx > -1 && processedCode.get(i).length() > 1 && !processedCode.get(i).contains("catch(Throwable _")) { // this means there is text after the }
				String trailing = processedCode.get(i).substring(idx+1);
				String preceding = processedCode.get(i).substring(0, idx+1);

				processedCode.add(i+1, trailing); // insert the text right of the } as the next line
				processedCode.set(i, preceding); // remove the text right of the } on the current line
				
				mapping.put(oldLineId, targetLinesIds);
				numAddedLines++;
			} else {
				mapping.put(oldLineId, targetLinesIds);
			}
		}
		
		lineMappings.add(mapping);
	}

	// Separate sourceCode with containing semicolons except at the end
	private void separateLinesWithSemicolons() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;

		for (int i=0; i < processedCode.size(); i++) {
			int oldLineId = i-numAddedLines;
			List<Integer> targetLinesIds = new ArrayList<Integer>();
			List<String> statements = Helper.splitByReserved(processedCode.get(i), ';');
			
			targetLinesIds.add(i);
			if (statements.size() > 1 && !processedCode.get(i).contains("catch(Throwable _")) {
				boolean lineEndsWithSemicolon = processedCode.get(i).matches("^.*;$");
				processedCode.set(i, statements.get(0) + ";");
				
				for (int j=1; j < statements.size(); j++) {
					String pause = (j == statements.size()-1 && !lineEndsWithSemicolon ? "" : ";");
					processedCode.add(i+j, statements.get(j) + pause);
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

		for (int i=0; i < processedCode.size(); i++) {			
			mapping.put(i+removedLines, Helper.initArray(i));
			String curLine = processedCode.get(i);
			
			while (!Helper.lineContainsReservedChar(curLine, ";")
					&& !Helper.lineContainsReservedChar(curLine, "{") 
					&& !Helper.lineContainsReservedChar(curLine, "}")
					&& !((Helper.lineContainsReservedWord(curLine, "case") || Helper.lineContainsReservedWord(curLine, "default"))
							&& Helper.lineContainsReservedChar(curLine, ":"))
					) {
				String separator = (curLine.charAt(curLine.length()-1) != ' '
									&& processedCode.get(i+1).charAt(0) != ' ' ? " " : "");
				
				if (!processedCode.get(i+1).contains("catch(Throwable _")) {
					processedCode.set(i, curLine + separator + processedCode.get(i+1));
					processedCode.remove(i+1);
					removedLines++;
				}
				
				mapping.put(i+removedLines, Helper.initArray(i));
				curLine = processedCode.get(i);
			}
		}
		
		lineMappings.add(mapping);
	}

	// Separate case statements with next line
	private void separateCaseStatements() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<processedCode.size(); i++) {
			int oldLineId = i-numAddedLines;

			// add current line to targets
			List<Integer> targetLinesIds = (mapping.containsKey(oldLineId) ? 
					mapping.get(oldLineId) : new ArrayList<Integer>());
			targetLinesIds.add(i);
			mapping.put(oldLineId, targetLinesIds);
			
			if (processedCode.get(i).matches("^\\b(case|default)\\b.*:.*")) {
				int idx = Helper.getIndexOfReservedChar(processedCode.get(i), ":"); // TODO test if it works in all situations
				
				if (processedCode.get(i).substring(idx+1).matches("[ \t]*\\{[ \t]*")) {
					continue;
				}
				
				if (idx < processedCode.get(i).length()-1) {
					processedCode.add(i+1, processedCode.get(i).substring(idx+1));
					processedCode.set(i, processedCode.get(i).substring(0, idx+1));
					numAddedLines++;
				}
			}
		}
		lineMappings.add(mapping);
	}
	
	private void prepareTryCatchBlocks() {
		for (int i=0; i<processedCode.size(); i++) {	
			if (processedCode.get(i).matches("^try.+$") 
					|| processedCode.get(i).matches("^catch.+$") 
					|| processedCode.get(i).matches("^finally.+$")) {
				processedCode.set(i, "%forcenode% " + processedCode.get(i));
			}
			else if (processedCode.get(i).equals("}")) {
				String startBlockLine = processedCode.get(Helper.findStartOfBlock(processedCode, i-1));
				if (startBlockLine.matches("^(%forcenode%)* try.+$") 
						|| startBlockLine.matches("^(%forcenode%)* catch.+$")
						|| startBlockLine.matches("^(%forcenode%)* finally.+$")) {
					processedCode.set(i-1, "%forceendnode% " + processedCode.get(i-1));
				}
			}
		}
	}
	
	private void convertEmptyForToWhile() {
		for (int i=0; i<processedCode.size(); i++) {
			if (processedCode.get(i).matches("^for[\\s\\t]*\\([\\s\\t]*;[\\s\\t]*;[\\s\\t]*\\).*$")) { // for(;;)
				Matcher m = Pattern.compile("for[\\s\\t]*\\([\\s\\t]*;[\\s\\t]*;[\\s\\t]*\\)").matcher(processedCode.get(i));
				m.find();
				processedCode.set(i, processedCode.get(i).replace(m.group(), "while(true)"));
			}
		}
	}
	
	//turn for loops into while loops
	private void convertForToWhile() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		List<Integer> loopsClosingLines = new ArrayList<Integer>();
		List<Pair<String, CurlyBracketBalance>> cbb = new ArrayList<>(); // Stack of pair(variable, curly bracket balance)
		Set<String> initVariables = new HashSet<>();
		List<String> multiVarInline = new ArrayList<>();
		
		
		for (int i=0; i<processedCode.size(); i++) {
			// Updates the scope visibility of variables already initialized		
			if (processedCode.get(i).matches("^for.+$")) {
				String REGEX_MULTI_INITILIZATION = "^.+(=.+,.+).+;$";
				int depth = loopsClosingLines.size();
				int closingLine = Helper.findEndOfBlock(processedCode, i+3);
				
				// Move the initialization before the loop
				mapping.put(i+depth, Helper.initArray(i));
				int idx = processedCode.get(i).indexOf("(");
				String initVar = processedCode.get(i).substring(idx+1).trim();
				boolean isVarDeclaration = initVar.matches("[\\s\\t]*([^\\s\\t]+)[\\s\\t]+([^\\s\\t]+)[\\s\\t]*=.+");
				String varLabel;
				

				if (isVarDeclaration) {
					if (initVar.contains(" "))
						varLabel = initVar.split("\\s")[1];
					else
						varLabel = initVar;
				}
				else {
					if (initVar.contains(" "))
						varLabel = initVar.split("\\s")[0];
					else
						varLabel = initVar;
				}

				if (varLabel.contains("="))
					varLabel = varLabel.split("=")[0];
				
				if (initVar.matches(REGEX_MULTI_INITILIZATION)) {
					Matcher m = Pattern.compile("[A-z$_0-9]+[\\s\\t]*=[^;]+").matcher(initVar);
					
					while (m.find()) {
						String[] vars = m.group().split(",");
						
						for (String var : vars) {
							String varName = var.split("=")[0].trim();
							
			
							multiVarInline.add(varName);
						}
					}
				}
				
				// Checks whether the variable has already been initialized
				if (multiVarInline.size() > 1) {
					for (String varname : multiVarInline) {
						if (initVariables.contains(varname)) {
							initVar = initVar.substring(initVar.indexOf(" ")+1).replaceAll(",", ";");
							break;
						}
					}
					
					multiVarInline.clear();
				}
				else if (isVarDeclaration && initVariables.contains(varLabel)) {
					initVar = initVar.substring(initVar.indexOf(" ")+1);
				}
				
				if (multiVarInline.size() > 0) {
					for (String varname : multiVarInline) {
						if (!initVariables.contains(varname)) {
							initVariables.add(varname);
							cbb.add(Pair.of(varname, new CurlyBracketBalance()));
						}
					}
				}
				else if (!initVariables.contains(varLabel)) {
					initVariables.add(varLabel);
					cbb.add(Pair.of(varLabel, new CurlyBracketBalance()));
				}
				
				processedCode.add(i, "%forcenode%" + initVar);
				i++; //adjust for insertion

				// Work with iterator step
				idx = processedCode.get(i+2).lastIndexOf(")");
				String iteratorStep = processedCode.get(i+2).substring(0, idx).replaceAll(",", ";");
				mapping.put(i+1+depth, new ArrayList<Integer>());
				
				// Clone the iterator to just before any continues present in the loop				
				List<Integer> continueLinesId = getContinuesInLoopBlock(i, closingLine+1);
				for(int lineId : continueLinesId) {
					List<Integer> targetLinesIds = mapping.get(i+1+depth);
					targetLinesIds.add(lineId);
					mapping.put(i+1+depth, targetLinesIds);
					processedCode.set(lineId, "%forcenode%" + iteratorStep + "; continue;");
				}
				
				// Move the iterator to just before the close
				List<Integer> targetLinesIds = mapping.get(i+1+depth);
				targetLinesIds.add(closingLine-1);
				mapping.put(i+1+depth, targetLinesIds);
				processedCode.add(closingLine+1, "%forcenode%" + iteratorStep + ";");
				processedCode.remove(i+2); //remove old line
				
				// Replace for initialization with while
				mapping.put(i+depth, Helper.initArray(i));
				String testStatement = processedCode.get(i+1).substring(0, processedCode.get(i+1).length()-1).trim();
				processedCode.set(i, "while (" + testStatement + ") {");
				processedCode.remove(i+1); // Remove old (test) line

				loopsClosingLines.add(closingLine);
			} else {
				int depth = loopsClosingLines.size();
				if (depth > 0 && i == loopsClosingLines.get(depth-1) - 1) {
					loopsClosingLines.remove(loopsClosingLines.size()-1);
				} else {
					mapping.put(i+depth, Helper.initArray(i));
				}
			}
			
			if (!cbb.isEmpty()) {
				for (Iterator<Pair<String, CurlyBracketBalance>> it = cbb.iterator(); it.hasNext(); ) {
					Pair<String, CurlyBracketBalance> item = it.next();
					
					item.second.parse(processedCode.get(i));
					
					if (item.second.getBalance() < 0) {
						initVariables.remove(item.first);
						it.remove();
					}
				}
			}
		}
		
		lineMappings.add(mapping);
	}
	
	private List<Integer> getContinuesInLoopBlock(int loopStartingLine, int loopClosingLine) {
		List<Integer> foundLineIds = new ArrayList<Integer>();
		for (int i=loopStartingLine+1; i<loopClosingLine; i++) {
			String curLine = processedCode.get(i);
			if (curLine.matches("^\\b(for|while|do)\\b.*")) {
				i = Helper.findEndOfBlock(processedCode, i);
			} else if (curLine.matches("^\\bcontinue\\b.*")) {
				foundLineIds.add(i);
			}
		}
		return foundLineIds;
	}
	
	private void convertForEachToFor() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int addedLines = 0;
		final String REGEX_PRIMITIVE_VAR= 
				".*(boolean|char|byte|short|int|float|long|double).*";
		
		
		for (int i=0; i<processedCode.size(); i++) {			
			if (processedCode.get(i).matches("^for.+$")
					&& Helper.lineContainsReservedChar(processedCode.get(i), ":")) {
				if (processedCode.get(i).contains("final ")) {
					processedCode.set(i, processedCode.get(i).replace("final ", ""));
				}
				
				List<String> forEachInformation = extractForEachInfo(processedCode.get(i));
				String type = forEachInformation.get(0);
				String varName = forEachInformation.get(1);
				String setName = forEachInformation.get(2);
				String itName = DataUtil.generateVarName();
				
				// Fix map generic type
				if (processedCode.get(i).contains("<")) {
					String insideFor = processedCode.get(i).substring(processedCode.get(i).indexOf("(")+1);
					int idxEnd = insideFor.split(":")[0].lastIndexOf(">");

					type = (idxEnd == -1) ? insideFor.split(":")[0].split(" ")[0] : insideFor.substring(0, idxEnd+1);
					forEachInformation = extractForEachInfo(processedCode.get(i).substring(0, processedCode.get(i).indexOf("<")) + processedCode.get(i).substring(processedCode.get(i).indexOf(">")+1) );
					varName = forEachInformation.get(1);
					setName = forEachInformation.get(2);
				}
				
				mapping.put(i - addedLines, new ArrayList<>(Arrays.asList(i, i+1)));
				
				if (type.matches(REGEX_PRIMITIVE_VAR)) {
					type = primitivesToObject(type);
				}
				
				boolean isMapEntry = type.contains("Map.Entry");
				boolean isGenericType = type.matches("[A-Z]");
				String typeIterator = type;

				if ((isMapEntry && type.contains("?")) || isGenericType) {
					typeIterator = "?";
				}

				processedCode.set(i, "for (java.util.Iterator<" + typeIterator + "> " + itName + 
						" = executionFlow.io.processor.parser.trgeneration.IteratorExtractor.extractIterator(" + setName + "); " + 
						itName + ".hasNext(); ) {");
				
				if (isMapEntry || isGenericType) {
					processedCode.add(i+1, type + " " + varName + " = (" + type + ")" + itName + ".next();");
				}
				else
					processedCode.add(i+1, type + " " + varName + " = " + itName + ".next();");
				
				addedLines++;
				i++;
			} else {
				mapping.put(i - addedLines, Helper.initArray(i));
			}
		}
		
		lineMappings.add(mapping);
	}
	
	private String primitivesToObject(String str)
	{
		str = replacePrimitive(str, "boolean", "Boolean");
		str = replacePrimitive(str, "byte", "Byte");
		str = replacePrimitive(str, "char", "Character");
		str = replacePrimitive(str, "short", "Short");
		str = replacePrimitive(str, "int", "Integer");
		str = replacePrimitive(str, "float", "Float");
		str = replacePrimitive(str, "long", "Long");
		str = replacePrimitive(str, "double", "Double");
		
		
		return str;
	}
	
	private String replacePrimitive(String str, String primitiveName, String objectName)
	{
		if (str.equals(primitiveName))
			return objectName;
		
		String regexPrimitive = ".*[^A-z]+" + primitiveName + "[^A-z]+.*";
		Pattern p = Pattern.compile(regexPrimitive);
		Matcher m = p.matcher(str);

		while (m.find()) {
			m = Pattern.compile(primitiveName).matcher(m.group());
			
			if (m.find()) {
				int idxStart = m.start();
				int idxEnd = m.end();
			
				str = str.substring(0, idxStart) + objectName + str.substring(idxEnd);
			}
		}
		
		return str;
	}

	private boolean isArrayVar(String setName, int currentIdx) {
		final String REGEX_INVOKED_DECLARATION = 
				"[\\s\\t]*(public|protected|private)[\\s\\t]+.+\\(.*\\)[\\s\\t]*\\{[\\s\\t]*$";
		final String REGEX_ARRAY_VAR = 
				"^.+(((\\.\\.\\.|\\[\\])[\\s\\t]+" + setName + ")|[\\s\\t]+" + setName + "\\[\\]).*$";
		boolean isConstructorDeclaration;
		boolean isMethodDeclaration;
		boolean isArrayVariable = false;
		String currentLine = processedCode.get(currentIdx);
		
		
		do {
			currentIdx--;
			currentLine = processedCode.get(currentIdx);
			
			isConstructorDeclaration = currentLine.contains("new ");
			isMethodDeclaration = currentLine.matches(REGEX_INVOKED_DECLARATION) && !isConstructorDeclaration;
		}
		while (!isMethodDeclaration && currentIdx > 0);
		
		if (currentIdx > 0) {
			isArrayVariable = currentLine.matches(REGEX_ARRAY_VAR);
		}
		
		return isArrayVariable;
	}

	// Given a line containing a for each statement, collect the necessary info
	private List<String> extractForEachInfo(String line) {
		String buffer = "";
		List<String> info = new ArrayList<>();
		
		
		line = line.replaceAll(",\\ ", ",");
		int i;
		for(i = 3; i < line.length() && info.size() < 2; i++) {
			if (line.charAt(i) != '(' && line.charAt(i) != ' ' && line.charAt(i) != '\t' && line.charAt(i) != ':') {
				buffer += line.charAt(i);
			} else if ((line.charAt(i) == ' ' || line.charAt(i) == '\t' || line.charAt(i) == ':') && buffer.length() > 0) {
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

	private void addDummyNodes() {
		Map<Integer, List<Integer>> mapping = new HashMap<Integer, List<Integer>>();
		int numAddedLines = 0;
		
		for (int i=0; i<processedCode.size(); i++) {
			String line = processedCode.get(i);
			
			if (Helper.lineContainsReservedChar(line, "}")) {
				//find the opening
				int openline = Helper.findStartOfBlock(processedCode, i-1);

				if ((processedCode.get(openline).toLowerCase().matches("^\\b(while|do|if|else)\\b.*")
						|| processedCode.get(openline).matches(Regex.methodSignature))
						&& processedCode.get(i-1).equals("}")) {
					processedCode.add(i, "dummy_node;");
					numAddedLines++;
					// i--; //adjust i due to insertion // I don't think this is needed.
				}
			}
			
			int oldLineId = i-numAddedLines;
			mapping.put(oldLineId, Helper.initArray(i));
		}
		
		lineMappings.add(mapping);
	}
	
	// Build mapping original -> final state
	private void buildFullMap() {
		Map<Integer, List<Integer>> firstMap = lineMappings.get(0);
		Map<Integer, List<Integer>> lastMap = new HashMap<Integer, List<Integer>>();
		
		for (Integer lineId : firstMap.keySet()) {
			List<Integer> targets = new ArrayList<Integer>();
			targets.addAll(getFinalTargetLineId(lineId, 0));
			lastMap.put(lineId, targets);
		}
		
		lineMappings.add(lastMap);
	}
	
	// Build mapping final -> original state
	private void buildReverseFullMap() {
		Map<Integer, List<Integer>> lastMap = lineMappings.get(lineMappings.size()-1);
		Map<Integer, List<Integer>> reverseMap = new HashMap<Integer, List<Integer>>();
		
		for(Integer key : lastMap.keySet()) {
			for (Integer tgt : lastMap.get(key)) {
				if (emptyLines.contains(key)) {
					continue;
				} else if (reverseMap.get(tgt) != null) {
					reverseMap.get(tgt).add(key);
				} else {
					reverseMap.put(tgt, Helper.initArray(key));
				}
			}
		}
		
		lineMappings.add(reverseMap);
	}
	
	// Given line id and depth, find its last id in final source code
	private List<Integer> getFinalTargetLineId(int id, int depth) {
		Map<Integer, List<Integer>> currentMap = lineMappings.get(depth);
		List<Integer> endLines = new ArrayList<Integer>(); 
		
		if (depth == lineMappings.size() - 1) {
			endLines.addAll(currentMap.get(id));
		} else {
			List<Integer> directTargets = currentMap.get(id);
			
			if (directTargets != null) {
				for (Integer target : directTargets) {
					endLines.addAll(getFinalTargetLineId(target, depth + 1));
				}
			}
		}
		
		return endLines;
	}

	public void dumpInfo() {
		for(int n=0; n<lineMappings.size(); n++) {
			System.out.println("Mapping #" + n);
			for(int key : lineMappings.get(n).keySet()) {				
				System.out.println(" | " + key + " -> " + lineMappings.get(n).get(key));
			}
		}
		dumpCode();
	}
	
	public void dumpCode() {
		String outlines = "\n***** Clean Source Code:\n\n";
		for (int i=0; i<processedCode.size(); i++) 
			outlines += i + ": " + processedCode.get(i) + "\n";
		System.out.printf("%s\n", outlines);
	}
	
	public void dumpLastMap() {
		System.out.println(" ***** Original to clean code map: ");
		Map<Integer, List<Integer>> map = getCleanToOriginalCodeMapping();
		for(int key : map.keySet()) {				
			System.out.println(" | " + key + " -> " + map.get(key));
		}
		System.out.println(" ***** End of map ");
	}
	
	public List<Map<Integer, List<Integer>>> getLineMappings()	{
		return lineMappings;
	}
	
	public List<String> getProcessedCode()	{
		return processedCode;
	}
}

