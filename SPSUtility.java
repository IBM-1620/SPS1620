package SPS1620;

/*
 *  SPSUtility.java - utility routines to evaluate operands, manipulate strings, and validate data
 *
 *  IBM 1620 Jr Project, Computer History Museum, 2017-2024
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

public class SPSUtility {
	
	// Methods
	
	public static boolean IsEmpty(String str) {
		if (str == null) return true;
		if (str.length() == 0) return true;
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) != ' ') return false;
		}
		return true;
	}
	
	public static boolean IsRMGM(byte digit) {
		byte val = (byte)(digit & SPSData.MASK_DIGIT);
		if ((val == SPSData.MEMORY_RM) || (val == SPSData.MEMORY_GM)) return true;
		return false;
	}
	
	public static String Substring(String str, int start) {
		if (IsEmpty(str)) return "";
		if (start >= str.length()) return "";
		return str.substring(start, str.length());
	}
	
	public static String Substring(String str, int start, int end) {
		if (IsEmpty(str)) return "";
		if (start >= str.length()) return "";
		return str.substring(start, Integer.min(end, str.length()));
	}

	public static boolean Contains(String str, String substr) {
		if (IsEmpty(str)) return false;
		return str.contains(substr);
	}
	
	public static String Strip(String str) {
		if (IsEmpty(str)) return "";
		String nstr = "";
		for (int i = 0; i < str.length(); ++i) {
			if ((str.charAt(i) != ' ') && (str.charAt(i) != '\t')) nstr += str.charAt(i);
		}
		return nstr;
	}
	
	public static String LTrim(String str) {
		if (IsEmpty(str)) return "";
		for (int i = 0; i < str.length(); ++i) {
			if ((str.charAt(i) != ' ') && (str.charAt(i) != '\t')) return str.substring(i);
		}
		return "";
	}
	
	public static String RTrim(String str) {
		if (IsEmpty(str)) return "";
		for (int i = (str.length() - 1); i >= 0; --i) {
			if ((str.charAt(i) != ' ') && (str.charAt(i) != '\t')) return str.substring(0, i + 1);
		}
		return "";
	}
	
	public static String Trim(String str) {
		if (IsEmpty(str)) return "";
		return str.trim();
	}
	
	public static String ExpandTabs(String str) {
		if (IsEmpty(str)) return "";
		if (SPSData.sourceFormat == SPSData.SourceFormat.FIXED) return str;
		if (str.indexOf('\t') == -1) return str;
		
		boolean single = (SPSData.tabTable.length == 1);
		int tabs = SPSData.tabTable[0];
		char[] chr = str.toCharArray();
		
		String str2 = "";
		for (int i = 0; i < chr.length; ++ i) {
			if (chr[i] != '\t') {
				str2 += chr[i];
			} else if (single) {
				int spaces = tabs - (str2.length() % tabs);
				for (int j = 0; j < spaces; ++j) {
					str2 += ' ';
				}
			} else {
				int pos = str2.length();
				int spaces = 1;
				for (int j = 0; j < SPSData.tabTable.length; ++j) {
					if (pos < SPSData.tabTable[j]) {
						spaces = SPSData.tabTable[j] - pos - 1;
						break;
					}
				}
				for (int j = 0; j < spaces; ++j) {
					str2 += ' ';
				}
			}
		}

		return str2;
	}
	
	public static char CharAt(String str, int pos) {
		if ((str == null) || (pos < 0) || (pos >= str.length())) return '\0';
	    return str.charAt(pos);
	}
	
	public static boolean IsValidLine(String str) {
		return SPSData.PATTERN_LINE.matcher(str).matches();
	}
	
	public static boolean IsValidHead(char head) {
		return (SPSData.VALID_HEAD.indexOf(head) != -1);
	}
	
	public static boolean IsValidLabel(String str) {
		if (SPSData.symbolDivide) {
			return SPSData.PATTERN_LABEL.matcher(str).matches() &&
				   !SPSData.PATTERN_NUMBER.matcher(str).matches();
		} else {
			return SPSData.PATTERN_LABELX.matcher(str).matches() &&
				   !SPSData.PATTERN_NUMBER.matcher(str).matches();
		}
	}
	
	public static boolean IsValidSymbol(String str) {
		if (SPSData.symbolDivide) {
			return (SPSData.PATTERN_SYMBOL.matcher(str).matches() ||
				    SPSData.PATTERN_SYMBOLH.matcher(str).matches()) &&
			       !SPSData.PATTERN_NUMBER.matcher(str).matches();
		} else {
			return (SPSData.PATTERN_SYMBOLX.matcher(str).matches() ||
				    SPSData.PATTERN_SYMBOLHX.matcher(str).matches()) &&
			       !SPSData.PATTERN_NUMBER.matcher(str).matches();
		}
	}
	
	public static boolean IsValidNumber(String str) {
		return SPSData.PATTERN_NUMBER.matcher(str).matches();
	}
	
	public static boolean IsValidNumberX(String str) {
		return SPSData.PATTERN_NUMBERX.matcher(str).matches();
	}
	
	public static boolean IsValidAlpha(String str) {
		return SPSData.PATTERN_ALPHA.matcher(str).matches();
	}
	
	public static boolean IsValidIndex(String str) {
		return SPSData.PATTERN_INDEX.matcher(str).matches();
	}
	
	public static boolean IsValidFlags(String str) {
		return SPSData.PATTERN_FLAGS.matcher(str).matches();
	}
}
