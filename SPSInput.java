package SPS1620;

/*
 *  SPSInput.java - source file input routines
 *
 *  IBM 1620 Jr Project, Computer History Museum, 2017-2018
 *
 *  To recreate the experience (visual, auditory, tactile, visceral) of running historic software on a 1960s-era computer.
 *
 *   Dave Babcock     - project lead, software, library
 *   John M. Bohn Jr. - software
 *   David Brock      - CHM sponsor
 *   Steve Casner     - hardware, software
 *   Joe Fredrick     - hardware, firmware
 *   Len Shustek      - CHM advisor
 *   Dag Spicer       - CHM advisor
 *   David Wise       - IBM 1620 expert
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;

public class SPSInput {
	
	// Methods
	
	public static void OpenSource() {
		
		if (++SPSData.sourceCurrentFile < SPSData.sourceFileCount) {
			SPSData.sourceFile = new File(SPSData.sourceFilenames[SPSData.sourceCurrentFile]);
		} else {
			SPSData.sourceFile = null;
			return;
		}
		
		try {
			SPSData.sourceReader = new BufferedReader(new FileReader(SPSData.sourceFile));
		} catch (FileNotFoundException e) {
			SPSData.pass = 0;
			SPSOutput.ReportError("cannot open source file (" + SPSData.sourceFilenames[SPSData.sourceCurrentFile] + ")");
			System.exit(1);
		}
		
		if (SPSData.sourceMultipleFiles) {
			SPSData.lineNumber = (SPSData.sourceCurrentFile + 1) << 18;
		} else {
			SPSData.lineNumber = 0;
		}
		
		if ((SPSData.pass == 2) && SPSData.sourceMultipleFiles) {
			SPSOutput.PrintFile();
		}
	}
	
	public static boolean ReadSource() {
		
		SPSData.inputLabel = "";
		SPSData.inputOperation = "";
		SPSData.inputOperands = "";
		SPSData.inputOperand = new String[0];
		SPSData.inputStatement = null;
		SPSData.inputMessages = "";
		
		String input = null;
		while (input == null) {
			if (SPSData.sourceFile == null) return false;
			try {
				input = SPSData.sourceReader.readLine();
				if (input == null) {
					CloseSource();
					OpenSource();
				}
			} catch (IOException e) {
				SPSOutput.ReportError("error reading source file (" + e.getMessage() + ")");
				CloseSource();
				OpenSource();
			}
		}
		SPSData.inputLine = SPSUtility.ExpandTabs(input.toUpperCase());
		++SPSData.lineNumber;
		++SPSData.lineCount;
		
		if (!SPSUtility.IsValidLine(SPSData.inputLine)) {
			SPSOutput.ReportError("invalid character(s) in statement");
			SPSData.inputStatement = SPSStatement.Get("????");
			return true;
		}
		
		if (SPSData.dendStatus != SPSData.DendStatus.NOT_SEEN) {
			SPSData.inputStatement = SPSStatement.Get("????");
			return true;
		}
					
		if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) {
			if (SPSUtility.IsEmpty(SPSUtility.Substring(SPSData.inputLine, 5, 75)) || (SPSUtility.CharAt(SPSData.inputLine, 5) == '*')) {
				SPSData.inputStatement = SPSStatement.Get("*");
				return true;
			} else {
				SPSData.inputLabel = SPSUtility.RTrim(SPSUtility.Substring(SPSData.inputLine, 5, 11));
				SPSData.inputOperation = SPSUtility.RTrim(SPSUtility.Substring(SPSData.inputLine, 11, 15));
				SPSData.inputOperands = SPSUtility.LTrim(SPSUtility.Substring(SPSData.inputLine, 15, 75));
			}
		} else {
			if (SPSUtility.IsEmpty(SPSData.inputLine) || (SPSUtility.CharAt(SPSData.inputLine, 0) == '*')) {
				SPSData.inputStatement = SPSStatement.Get("*");
				return true;
			} else {
				Matcher match = SPSData.PATTERN_FREEFORM.matcher(SPSData.inputLine);
				if (match.matches()) {
					if (match.group(1) != null) {
						SPSData.inputLabel = match.group(1);
					}
					if (match.group(2) != null) {
						SPSData.inputOperation = match.group(2);
					}
					if (match.group(4) != null) {
						SPSData.inputOperands = SPSUtility.LTrim(match.group(3));
					}
				}
			}
		}
		
		if (!SPSUtility.IsEmpty(SPSData.inputOperation)) {
			SPSData.inputStatement = SPSStatement.Get(SPSData.inputOperation);
			if (SPSData.inputStatement == null) {
			    char op1 = SPSUtility.CharAt(SPSData.inputOperation, 0);
			    if ((op1 >= '0') && (op1 <= '9')) {
					SPSOutput.ReportError("invalid numeric operation code (" + SPSData.inputOperation + ")");
				} else {
					SPSOutput.ReportError("invalid operation (" + SPSData.inputOperation + ")");
				}
				SPSData.inputStatement = SPSStatement.Get("????");
			} else if ((SPSData.inputStatement.Model == SPSData.SystemType.MODEL_2) && (SPSData.systemType != SPSData.SystemType.MODEL_2)) {
				SPSOutput.ReportError("model 2 instruction (" + SPSData.inputOperation + ")");
			}
		} else {
			SPSOutput.ReportError("missing operation");
			SPSData.inputStatement = SPSStatement.Get("????");
		}
		
		if ((SPSData.inputStatement.Type == SPSStatement.StatementType.DAC) ||
			(SPSData.inputStatement.Type == SPSStatement.StatementType.DSAC)) {
			SPSData.inputOperand = new String[3];
			int pos = SPSData.inputOperands.indexOf(',');
			int size = SPSData.inputOperands.length();
			if (pos == -1) {
				SPSData.inputOperand[0] = SPSUtility.Strip(SPSData.inputOperands);
				SPSData.inputOperand[1] = "";
				SPSData.inputOperand[2] = "";
			} else {
				SPSData.inputOperand[0] = SPSUtility.Strip(SPSData.inputOperands.substring(0, pos));
				if ((pos + 1) == size) {
					SPSData.inputOperand[1] = "";
					SPSData.inputOperand[2] = "";
				} else {
					int len = (int)SPSStatement.EvalOperand(SPSData.inputOperand[0], SPSData.lastAddress, false, true,
															SPSStatement.StatementCheck.NONE);
					SPSData.inputOperand[1] = SPSData.inputOperands.substring(pos + 1, Math.min(pos + len + 1, size));
					if ((pos + len + 1) >= size) {
						SPSData.inputOperand[2] = "";
					} else {
						String[] temp = SPSData.PATTERN_SPLIT.split(SPSData.inputOperands.substring(pos + len + 1), -1);
						if (!SPSUtility.IsEmpty(temp[0])) {
							SPSOutput.ReportError("operand value too long");
						}
						if (temp.length == 1) {
							SPSData.inputOperand[2] = "";
						} else {
							SPSData.inputOperand[2] = SPSUtility.Strip(temp[1]);
						}
					}
				}
			}
			
		} else if (SPSData.inputStatement.Type == SPSStatement.StatementType.DSA) {
			String[] temp = SPSData.PATTERN_SPLIT.split(SPSData.inputOperands, -1);
			int size = temp.length;
			if (temp.length > 10) {
				SPSOutput.ReportError("more than 10 operands");
				size = 10;
			}
			SPSData.inputOperand = new String[size];
			for (int i = 0; i < size; ++i) {
				SPSData.inputOperand[i] = SPSUtility.Strip(temp[i]);
			}
			
		} else {
			String[] temp = SPSData.PATTERN_SPLIT.split(SPSData.inputOperands + ",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,", -1);
			SPSData.inputOperand = new String[SPSData.inputStatement.OpCnt];
			for (int i = 0; i < SPSData.inputStatement.OpCnt; ++i) {
				SPSData.inputOperand[i] = SPSUtility.Strip(temp[i]);
			}
		}
		
		return true;
	}
	
	public static void CloseSource() {
	
		if (SPSData.sourceFile != null) {
			try {
				SPSData.sourceReader.close();
			} catch (IOException e) {
				// ignore error
			}
			SPSData.sourceFile = null;
		}
	}
}
