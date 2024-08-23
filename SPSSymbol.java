package SPS1620;

/*
 *  SPSSymbolTable.java - symbol table support routines
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

public class SPSSymbol {
	
	// Data
	
	public int Address;
	public SPSStatement.StatementType Type;
	public int Defined;
	public LinkedList<Integer> References;
	
	private static Map<String, SPSSymbol> symbolTable = new TreeMap<String, SPSSymbol>();
	
	// Constructor
	
	public SPSSymbol(int address, SPSStatement.StatementType type, int defined) {
		this.Address = address;
		this.Type = type;
		this.Defined = defined;
		this.References = null;
	}
	
	// Methods
	
	public static void Add(String symbol, int address, int line) {
		if (SPSUtility.IsEmpty(symbol)) return;
		if (!SPSUtility.IsValidLabel(symbol)) {
			SPSOutput.ReportError("invalid label (" + symbol + ")");
			return;
		}
		
		String symbol2;
		if ((SPSData.symbolHead != ' ') && (symbol.length() < 6)) {
			symbol2 = SPSData.symbolHead + "$" + symbol;
		} else {
			symbol2 = symbol;
		}
		if (!symbolTable.containsKey(symbol2)) {
			symbolTable.put(symbol2, new SPSSymbol(address, SPSData.inputStatement.Type, line));
		} else {
			SPSOutput.ReportError("duplicate label (" + symbol2 + ")");
		}
	}
	
	public static void Verify(String symbol, int line) {
		if (SPSUtility.IsEmpty(symbol)) return;
		if (!SPSUtility.IsValidSymbol(symbol)) {
			SPSOutput.ReportError("invalid label (" + symbol + ")");
			return;
		}
		
		String symbol2;
		if ((SPSData.symbolHead != ' ') && (symbol.length() < 6)) {
			symbol2 = SPSData.symbolHead + "$" + symbol;
		} else {
			symbol2 = symbol;
		}
		if (symbolTable.containsKey(symbol2) && (symbolTable.get(symbol2).Defined != line)) {
			SPSOutput.ReportError("duplicate label (" + symbol2 + ")");
		}
	}
	
	public static int Lookup(String symbol, int line, boolean check) {
		if (SPSUtility.IsEmpty(symbol)) return 0;
		
		String symbol2;
		int pos = symbol.indexOf('$');
		if (pos == -1) {
			if (!SPSUtility.IsValidSymbol(symbol)) {
				SPSOutput.ReportError("invalid symbol (" + symbol + ")");
				return 0;
			}
			if ((SPSData.symbolHead != ' ') && (symbol.length() < 6)) {
				symbol2 = SPSData.symbolHead + "$" + symbol;
			} else {
				symbol2 = symbol;
			}
		} else if (pos == 0) {
			symbol2 = symbol.substring(1);
		} else if (pos == 1) {
			if (!SPSUtility.IsValidHead(symbol.charAt(0))) {
				SPSOutput.ReportError("invalid symbol head (" + symbol.charAt(0) + ")");
				return 0;
			}
			String temp = symbol.substring(2);
			if (!SPSUtility.IsValidSymbol(temp)) {
				SPSOutput.ReportError("invalid symbol (" + temp + ")");
				return 0;
			}
			symbol2 = symbol;
		} else {
			SPSOutput.ReportError("invalid symbol head (" + symbol.substring(0, pos) + ")");
			return 0;
		}
		
		if (symbolTable.containsKey(symbol2)) {
			SPSSymbol sym = symbolTable.get(symbol2);
			if (check && (sym.Defined > line)) {
				SPSOutput.ReportError("undefined [forward reference] symbol (" + symbol2 + ")");
				return 0;
			}
			if (sym.References == null) sym.References = new LinkedList<Integer>();
			if (!sym.References.contains(line)) {
				sym.References.add(line);
			}
			return sym.Address;
		} else {
			SPSOutput.ReportError("undefined symbol (" + symbol2 + ")");
			return 0;
		}
	}
	
	public static void Print() {

		if (SPSData.lstFile == null) return;
		
		SPSData.lstWriter.write("\f\n                                 Symbol Cross-Reference Table\n");
		SPSData.lstWriter.write("                                 ============================\n");
		
		if (SPSData.sourceMultipleFiles) {
			SPSData.lstWriter.write("\n\n  Id  Source File\n");
			SPSData.lstWriter.write("  --  --------------------------------------------------\n");
			for (int i = 0; i < SPSData.sourceFileCount; ++i) {
				SPSData.lstWriter.format("  %2d  %s\n", (i + 1), SPSData.sourceFilenames[i]);
			}
		}
		
		SPSData.lstWriter.write("\n\n  Symbol   Addr.  Type    Defined  References\n");
		SPSData.lstWriter.write("  -------  -----  ------  -------  -------------------------------------------------------------\n");
		
		Object[] keys = symbolTable.keySet().toArray();
		for (int i = 0; i < keys.length; ++i) {
			SPSSymbol sym = symbolTable.get(keys[i]);
			if (sym.Type == SPSStatement.StatementType.INSTRUCTION) {
				SPSData.lstWriter.format("  %-7s  %05d  <inst>  %7s", keys[i], sym.Address, SPSOutput.LineNumber(sym.Defined));
			} else {
				SPSData.lstWriter.format("  %-7s  %05d  %-6s  %7s", keys[i], sym.Address, sym.Type, SPSOutput.LineNumber(sym.Defined));
			}
			if (sym.References != null) {
				Collections.sort(sym.References);
				int cnt = 0;
				for (int line: sym.References) {
					if (++cnt == SPSData.SIZE_XREF_LINE) {
						SPSData.lstWriter.write("\n                                 ");
						cnt = 1;
					}
					SPSData.lstWriter.format("  %7s", SPSOutput.LineNumber(line));
				}
			}
			SPSData.lstWriter.write("\n");
		}
	}
}
